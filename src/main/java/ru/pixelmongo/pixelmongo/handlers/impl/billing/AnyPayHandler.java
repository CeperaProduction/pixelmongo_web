package ru.pixelmongo.pixelmongo.handlers.impl.billing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import ru.pixelmongo.pixelmongo.handlers.BillingHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.BillingData;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.BillingDataRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.BillingService;
import ru.pixelmongo.pixelmongo.utils.EncodeUtils;

@Component
public class AnyPayHandler implements BillingHandler{

    private static final String CURRENCY = "RUB";

    @Value("${billing.anypay.enabled}")
    private boolean enabled;

    @Value("${billing.anypay.hidden:false}")
    private boolean hidden;

    @Value("${billing.anypay.priority}")
    private int priority;

    @Value("${billing.anypay.test:false}")
    private boolean allowTest;

    @Value("${billing.anypay.url}")
    private String merchantUrl;

    @Value("${billing.anypay.id}")
    private String merchantId;

    @Value("${billing.anypay.ips}")
    private String trustedIpsStr;
    private List<IpAddressMatcher> trustedIps;

    @Value("${billing.anypay.key.secret}")
    private String secretKey;

    @Value("${billing.anypay.desc}")
    private String descPattern;

    @Autowired
    private BillingDataRepository bills;

    @Autowired
    private UserRepository users;

    @Autowired
    private DateFormatter df;

    private DateFormatter dfRemote = new DateFormatter("dd.MM.yyyy HH:mm:ss");

    @PostConstruct
    public void init() {
        trustedIps = new ArrayList<>();
        for(String ip : trustedIpsStr.split(",")) {
            ip = ip.trim();
            if(ip.isEmpty()) continue;
            trustedIps.add(new IpAddressMatcher(ip));
        }
    }

    @Override
    public String getName() {
        return "anypay";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isVisible() {
        return !hidden;
    }

    @Override
    public int getPriority() {
       return priority;
    }

    @Override
    public ResultMessage makeForm(User user, int sum, Locale loc, HttpServletRequest request,
            HttpServletResponse response) {
        BillingData bill = new BillingData(this, user, sum);
        bill = bills.save(bill);

        String sumStr = sum+".00";
        String descStr = MessageFormat.format(descPattern, user.getName(), sum);
        String sign = signForm(bill.getId(), sumStr);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(merchantUrl);
        uriBuilder.queryParam("merchant_id", merchantId);
        uriBuilder.queryParam("amount", sumStr);
        uriBuilder.queryParam("currency", CURRENCY);
        uriBuilder.queryParam("pay_id", bill.getId());
        uriBuilder.queryParam("desc", descStr);
        uriBuilder.queryParam("sign", sign);
        uriBuilder.queryParam("email", user.getEmail());

        Map<String, String> resData = new HashMap<>();
        resData.put("location", uriBuilder.toUriString());
        return new ResultDataMessage<Map<String, String>>(DefaultResult.OK, "Payment data created", resData);
    }

    private String signForm(int payId, String sumStr) {
        String[] data = new String[] {CURRENCY, sumStr, secretKey, merchantId, Integer.toString(payId)};
        return EncodeUtils.md5(String.join(":", data));
    }

    @Override
    public Object processWebHook(Map<String, String> params, String body, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        boolean test = params.getOrDefault("test", "").equals("1");
        if(test && !this.allowTest)
            return "Error: Test mode disabled";
        if(test) {
            BillingService.LOGGER.info("Got test billing notify request");
        }
        if(this.allowTest) {
            BillingService.LOGGER.info("Request params:\n"+toString(params));
        }
        String result = processWebHook(params, test, loc, request, response);
        if(this.allowTest)
            BillingService.LOGGER.info("Billing request result: "+result);
        return result;
    }

    private String processWebHook(Map<String, String> params, boolean test, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        String merchantId = params.get("merchant_id");
        String billIdStr = params.get("pay_id");
        String remoteIdStr = params.get("transaction_id");
        String currency = params.get("currency");
        String payMethod = params.get("method");
        String status = params.get("status");
        String sumStr = params.get("amount");
        String profitStr = params.get("profit");
        String sign = params.get("sign");
        String payDate = params.get("pay_date");

        if(!isTrusted(request))
            return "Error: Untrusted host";

        if(!checkBillingSign(sign, merchantId, sumStr, billIdStr, this.allowTest))
            return "Error: Wrong signature";

        if(!merchantId.equals(this.merchantId))
            return "Error: Wrong merchant";

        if(!currency.equals(AnyPayHandler.CURRENCY))
            return "Error: Wrong currency";

        BillingData bill;
        try {
            bill = bills.findById(Integer.parseInt(billIdStr)).get();
        }catch (Exception e) {
            return "Error: Billing data not found";
        }

        if(!getName().equals(bill.getHandler()))
            return "Error: This billing data is binded to another handler";

        if(bill.getStatus() == BillingData.STATUS_ERROR)
            return "Error: Wrong billing data status";

        if(bill.getStatus() == BillingData.STATUS_DONE) {
            return "Already paid at "+df.print(bill.getUpdated(), loc);
        }

        if(remoteIdStr != null) {
            try {
                bill.setBillingId(Long.parseLong(remoteIdStr));
            }catch(Exception ex) {
                BillingService.LOGGER.catching(ex);
            }
        }

        if(status.equals("paid")) {

            int sum;
            try {
                sum = (int)Float.parseFloat(sumStr);
                if(bill.getSum() != sum)
                    return "Error: Billing data sum and given amount are mismatched";
            }catch(Exception ex) {
                return "Error: Invalid amount value";
            }

            float profit;
            try {
                profit = Float.parseFloat(profitStr);
            }catch(Exception ex) {
                if(test) {
                    profit = 0;
                }else {
                    profit = sum;
                    BillingService.LOGGER.warn("Got pay notify request without profit value (pay_id="+bill.getId()+"). Setting profit to sum value.");
                }
            }

            User user = users.findById(bill.getUserId()).orElse(null);
            if(user == null)
                return "Error: User not found";

            Date date;
            try {
                date = dfRemote.parse(payDate, loc);
            }catch(Exception ex) {
                date = new Date();
            }

            bill.setUpdated(date);
            bill.setMessage(test ? "Test pay" : "Paid");
            bill.setProfit(profit);
            bill.setPayMethod(StringUtils.hasText(payMethod) ? payMethod : test ? "test" : "");
            bill.setStatus(BillingData.STATUS_DONE);
            bills.save(bill);

            user.setRealBalance(user.getRealBalance()+sum);
            users.save(user);

            return "OK";
        }else {

            bill.setUpdated(new Date());
            bill.setMessage("Request: "+status);
            bills.save(bill);

            return bill.getStatus() == BillingData.STATUS_CREATED ? "Ready to pay" : "Unable to pay";
        }
    }

    private boolean isTrusted(HttpServletRequest request) {
        if(this.trustedIps.isEmpty()) return true;
        return trustedIps.stream().anyMatch(ip->ip.matches(request));
    }

    private boolean checkBillingSign(String sign, String merchantId, String sumStr,
            String billIdStr, boolean test) {
        String[] data = new String[] {merchantId, sumStr, billIdStr, secretKey};
        String dataStr = String.join(":", data);
        String validSign = EncodeUtils.md5(dataStr);
        if(test) BillingService.LOGGER.info("Sign generation string: "+dataStr+
                " Valid sign: "+validSign+" Request sign: "+sign);
        return validSign.equals(sign);
    }

    private String toString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for(Entry<String, String> ent : map.entrySet()) {
            sb.append(ent.getKey()).append(" : ").append(ent.getValue());
            sb.append('\n');
        }
        return sb.toString();
    }

}

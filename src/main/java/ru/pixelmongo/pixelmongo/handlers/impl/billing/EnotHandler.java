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
import org.springframework.http.HttpStatus;
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
public class EnotHandler implements BillingHandler{

    private static final String CURRENCY = "RUB";

    @Value("${server.url}")
    private String serverUrl;

    @Value("${billing.enot.enabled}")
    private boolean enabled;

    @Value("${billing.enot.hidden:false}")
    private boolean hidden;

    @Value("${billing.enot.priority}")
    private int priority;

    @Value("${billing.enot.test:false}")
    private boolean allowTest;

    @Value("${billing.enot.url}")
    private String merchantUrl;

    @Value("${billing.enot.id}")
    private String merchantId;

    @Value("${billing.enot.ips:}")
    private String trustedIpsStr;
    private List<IpAddressMatcher> trustedIps;

    @Value("${billing.enot.key.public}")
    private String publicKey;

    @Value("${billing.enot.key.secret}")
    private String secretKey;

    @Value("${billing.enot.desc}")
    private String descPattern;

    @Autowired
    private BillingDataRepository bills;

    @Autowired
    private UserRepository users;

    @Autowired
    private DateFormatter df;

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
        return "enot";
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
        uriBuilder.queryParam("m", merchantId);
        uriBuilder.queryParam("oa", sumStr);
        uriBuilder.queryParam("cr", CURRENCY);
        uriBuilder.queryParam("o", bill.getId());
        uriBuilder.queryParam("c", descStr);
        uriBuilder.queryParam("s", sign);
        uriBuilder.queryParam("success_url", getSuccessUrl(bill.getId()));
        uriBuilder.queryParam("fail_url", getFailUrl(bill.getId()));

        Map<String, String> resData = new HashMap<>();
        resData.put("location", uriBuilder.toUriString());
        return new ResultDataMessage<Map<String, String>>(DefaultResult.OK, "Payment data created", resData);
    }

    private String signForm(int payId, String sumStr) {
        String[] data = new String[] {merchantId, sumStr, publicKey, Integer.toString(payId)};
        return EncodeUtils.md5(String.join(":", data));
    }

    private String getSuccessUrl(int billId) {
        String base = serverUrl;
        if(!base.endsWith("/")) base += "/";
        return base+"pay/success?pay_id="+billId;
    }

    private String getFailUrl(int billId) {
        String base = serverUrl;
        if(!base.endsWith("/")) base += "/";
        return base+"pay/fail?pay_id="+billId;
    }

    @Override
    public Object processWebHook(Map<String, String> params, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        boolean test = params.getOrDefault("test", "").equals("1");
        if(test && !this.allowTest) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Error: Test mode disabled";
        }
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
        String merchantId = params.get("merchant");
        String billIdStr = params.get("merchant_id");
        String remoteIdStr = params.get("intid");
        String currency = params.get("currency");
        String payMethod = params.get("method");
        String sumStr = params.get("amount");
        String profitStr = params.get("credited");
        String sign = params.get("sign_2");

        if(!isTrusted(request)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "Error: Untrusted host";
        }

        if(!checkBillingSign(sign, merchantId, sumStr, billIdStr, this.allowTest)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "Error: Wrong signature";
        }

        if(!merchantId.equals(this.merchantId)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Error: Wrong merchant";
        }

        if(!currency.equals(EnotHandler.CURRENCY)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Error: Wrong currency";
        }

        BillingData bill;
        try {
            bill = bills.findById(Integer.parseInt(billIdStr)).get();
        }catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Error: Billing data not found";
        }

        if(!getName().equals(bill.getHandler())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Error: This billing data is binded to another handler";
        }

        if(bill.getStatus() == BillingData.STATUS_ERROR) {
            response.setStatus(HttpStatus.LOCKED.value());
            return "Error: Wrong billing data status";
        }

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
        if(user == null) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return "Error: User not found";
        }

        Date date = new Date();

        bill.setUpdated(date);
        bill.setMessage(test ? "Test pay" : "Paid");
        bill.setProfit(profit);
        bill.setPayMethod(StringUtils.hasText(payMethod) ? payMethod : test ? "test" : "");
        bill.setStatus(BillingData.STATUS_DONE);
        bills.save(bill);

        user.setRealBalance(user.getRealBalance()+sum);
        users.save(user);

        return "Good";
    }

    private boolean isTrusted(HttpServletRequest request) {
        if(this.trustedIps.isEmpty()) return true;
        return trustedIps.stream().anyMatch(ip->ip.matches(request));
    }

    private boolean checkBillingSign(String sign, String merchantId, String sumStr,
            String billIdStr, boolean test) {
        String[] data = new String[] {merchantId, sumStr, secretKey, billIdStr};
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

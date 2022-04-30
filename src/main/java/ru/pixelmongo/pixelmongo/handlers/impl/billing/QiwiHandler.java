package ru.pixelmongo.pixelmongo.handlers.impl.billing;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.pixelmongo.pixelmongo.handlers.BillingHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.BillingData;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.qiwi.QiwiBill;
import ru.pixelmongo.pixelmongo.model.dto.qiwi.QiwiPayRequest;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.BillingDataRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.BillingService;
import ru.pixelmongo.pixelmongo.utils.EncodeUtils;

@Component
public class QiwiHandler implements BillingHandler{

    private static final String CURRENCY = "RUB";

    @Value("${server.url}")
    private String serverUrl;

    @Value("${billing.qiwi.enabled}")
    private boolean enabled;

    @Value("${billing.qiwi.hidden:false}")
    private boolean hidden;

    @Value("${billing.qiwi.priority}")
    private int priority;

    @Value("${billing.qiwi.test:false}")
    private boolean testMode;

    @Value("${billing.qiwi.url}")
    private String merchantUrl;

    @Value("${billing.qiwi.form}")
    private String formTheme;

    @Value("${billing.qiwi.key.public}")
    private String publicKey;

    @Value("${billing.qiwi.key.secret}")
    private String secretKey;

    @Value("${billing.qiwi.desc}")
    private String descPattern;

    @Value("${billing.qiwi.ips}")
    private String trustedIpsStr;
    private List<IpAddressMatcher> trustedIps;

    @Autowired
    private BillingDataRepository bills;

    @Autowired
    private UserRepository users;

    private DateFormatter dfForm = new DateFormatter("yyyy-MM-dd'T'HHmm");
    private DateFormatter dfRemote = new DateFormatter("yyyy-MM-dd'T'HH:mm:ssX");

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
    public String getName() {
        return "qiwi";
    }

    @Override
    public ResultMessage makeForm(User user, int sum, Locale loc, HttpServletRequest request,
            HttpServletResponse response) {

        BillingData bill = new BillingData(this, user, sum);
        bill = bills.save(bill);

        String sumStr = sum+".00";
        String descStr = MessageFormat.format(descPattern, user.getName(), sum);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(merchantUrl);
        uriBuilder.queryParam("billId", bill.getId());
        uriBuilder.queryParam("publicKey", publicKey);
        uriBuilder.queryParam("amount", sumStr);
        uriBuilder.queryParam("account", user.getName());
        uriBuilder.queryParam("email", user.getEmail());
        uriBuilder.queryParam("successUrl", getSuccessUrl(bill.getId()));
        uriBuilder.queryParam("comment", descStr);
        uriBuilder.queryParam("paySource", "qw");
        uriBuilder.queryParam("lifetime",
                dfForm.print(new Date(System.currentTimeMillis()+86400000), loc));
        uriBuilder.queryParam("customFields[themeCode]", formTheme);

        Map<String, String> resData = new HashMap<>();
        resData.put("location", uriBuilder.toUriString());
        return new ResultDataMessage<Map<String, String>>(DefaultResult.OK, "Payment data created", resData);
    }

    @Override
    public Object processWebHook(Map<String, String> params, Locale loc, HttpServletRequest request,
            HttpServletResponse response) {

        if(testMode) {
            BillingService.LOGGER.info("Qiwi request from "+request.getRemoteAddr());
        }

        if(!"POST".equalsIgnoreCase(request.getMethod()))
            return error(response, HttpStatus.METHOD_NOT_ALLOWED, "Wrong request method");

        if(!isTrusted(request))
            return error(response, HttpStatus.FORBIDDEN, "Untrusted host");

        QiwiPayRequest notify;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            notify = mapper.readValue(request.getReader(), QiwiPayRequest.class);
            if(testMode) {
                BillingService.LOGGER.info("Qiwi request data: "+mapper.writeValueAsString(QiwiPayRequest.class));
            }
        } catch (JsonParseException e) {
            return error(response, HttpStatus.BAD_REQUEST, "Invalid data");
        } catch (IOException e) {
            if(testMode)
                BillingService.LOGGER.catching(e);
            return error(response, HttpStatus.INTERNAL_SERVER_ERROR, "IO failure");
        }

        QiwiBill billInfo = notify.getBill();

        if(!checkSignature(billInfo, request.getHeader("X-Api-Signature-SHA256")))
            return error(response, HttpStatus.BAD_REQUEST, "Invalid signature");

        if(!billInfo.getAmount().getCurrency().equals(QiwiHandler.CURRENCY))
            return error("Wrong currency");

        BillingData bill;
        try {
            bill = bills.findById(Integer.parseInt(billInfo.getBillId())).get();
        }catch (Exception e) {
            return error("Billing data not found");
        }

        if(!getName().equals(bill.getHandler()))
            return error("This billing data is binded to another handler");

        if(bill.getStatus() == BillingData.STATUS_ERROR)
            return error("Wrong billing data status");

        if(bill.getStatus() == BillingData.STATUS_DONE) {
            return error("Already paid at "+dfRemote.print(bill.getUpdated(), loc));
        }

        String status = (billInfo.getStatus().getValue()+"").toLowerCase();

        if(status.equals("paid")) {

            int sum;
            try {
                sum = (int)Float.parseFloat(billInfo.getAmount().getValue());
                if(bill.getSum() != sum)
                    return error("Billing data sum and given amount are mismatched");
            }catch(Exception ex) {
                return error("Invalid amount value");
            }

            float profit = sum;

            User user = users.findById(bill.getUserId()).orElse(null);
            if(user == null)
                return error(response, HttpStatus.INTERNAL_SERVER_ERROR, "User not found");

            Date date;
            try {
                date = dfRemote.parse(billInfo.getStatus().getChangedDateTime(), loc);
            }catch(Exception ex) {
                date = new Date();
            }

            bill.setUpdated(date);
            bill.setMessage("Paid");
            bill.setProfit(profit);
            bill.setPayMethod("");
            bill.setStatus(BillingData.STATUS_DONE);
            bills.save(bill);

            user.setRealBalance(user.getRealBalance()+sum);
            users.save(user);


            return success();
        }else {

            bill.setUpdated(new Date());
            bill.setMessage("Request: "+status);
            bills.save(bill);

            return success(bill.getStatus() == BillingData.STATUS_CREATED ? "Ready to pay" : "Unable to pay");
        }
    }

    private String getSuccessUrl(int billId) {
        String base = serverUrl;
        if(!base.endsWith("/")) base += "/";
        return base+"pay/success?pay_id="+billId;
    }

    private Object error(String message) {
        return error(null, null, message);
    }

    private Object error(HttpServletResponse response, HttpStatus status, String message) {
        if(status != null) response.setStatus(status.value());
        Map<String, String> result = new HashMap<>();
        result.put("error", message);
        if(testMode) BillingService.LOGGER.info("Qiwi result: "+message);
        return result;
    }

    private Object success() {
        return success(null);
    }

    private Object success(String message) {
        Map<String, String> result = new HashMap<>();
        result.put("error", "0");
        if(message != null)
            result.put("message", message);
        if(testMode) BillingService.LOGGER.info("Qiwi result: "+(message == null ? "OK" : message));
        return result;
    }

    private boolean isTrusted(HttpServletRequest request) {
        if(this.trustedIps.isEmpty()) return true;
        return trustedIps.stream().anyMatch(ip->ip.matches(request));
    }

    private boolean checkSignature(QiwiBill bill, String sign) {
        if(sign == null) return false;
        String[] dataArr = new String[] {
                bill.getAmount().getCurrency(),
                bill.getAmount().getValue(),
                bill.getBillId(),
                bill.getSiteId(),
                bill.getStatus().getValue()
        };
        String dataStr = String.join("|", dataArr);
        String validSign = EncodeUtils.hmacSHA256(secretKey, dataStr);
        if(testMode) {
            BillingService.LOGGER.info("Qiwi hashing data: "+dataStr);
            BillingService.LOGGER.info("Qiwi sign: "+sign+", valid sign: "+validSign);
        }
        return validSign.equals(sign);
    }



}

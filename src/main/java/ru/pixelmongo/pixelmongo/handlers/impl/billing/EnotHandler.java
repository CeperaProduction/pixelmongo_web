package ru.pixelmongo.pixelmongo.handlers.impl.billing;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.pixelmongo.pixelmongo.handlers.BillingHandler;
import ru.pixelmongo.pixelmongo.model.dao.primary.BillingData;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.enot.EnotPaymentCreateRequest;
import ru.pixelmongo.pixelmongo.model.dto.enot.EnotPaymentCreateResponse;
import ru.pixelmongo.pixelmongo.model.dto.enot.EnotWebhookBody;
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
    private boolean testMode;

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

    @Autowired
    private RestTemplate rest;

    private ObjectMapper mapper = new ObjectMapper();

    public EnotHandler() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

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

        int billId = bill.getId();
        String descStr = MessageFormat.format(descPattern, user.getName(), sum);

        EnotPaymentCreateRequest createRequest = new EnotPaymentCreateRequest(merchantId,
                sum, CURRENCY, Integer.toString(billId), descStr, user.getEmail(), getSuccessUrl(billId), getFailUrl(billId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("x-api-key", publicKey);

        HttpEntity<String> httpEntity;
        try {
            httpEntity = new HttpEntity<>(mapper.writeValueAsString(createRequest), headers);
        } catch (JsonProcessingException e) {
            BillingService.LOGGER.error(e);
            return new ResultMessage(DefaultResult.ERROR, e.toString());
        }

        String createResponseString = rest.postForObject(URI.create(merchantUrl),
                httpEntity, String.class);

        EnotPaymentCreateResponse createResponse;
        try {
            createResponse = mapper.readValue(createResponseString, EnotPaymentCreateResponse.class);
        }catch (Exception e) {
            BillingService.LOGGER.error("Bad response received from Enot: {}", createResponseString);
            return new ResultMessage(DefaultResult.ERROR, createResponseString);
        }

        if(!createResponse.isStatusCheck()) {
            BillingService.LOGGER.error("Bad response received from Enot: {}", createResponseString);
            if(createResponse.getError() != null) {
                return new ResultMessage(DefaultResult.ERROR, createResponse.getError());
            }
            return new ResultMessage(DefaultResult.ERROR, createResponseString);
        }

        Map<String, String> resData = new HashMap<>();
        resData.put("location", createResponse.getData().getUrl());
        return new ResultDataMessage<Map<String, String>>(DefaultResult.OK, "Payment data created", resData);
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
    public Object processWebHook(Map<String, String> params, String bodyStr, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {

        if(!isTrusted(request)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "Error: Untrusted host";
        }

        if(!request.getMethod().equals("POST")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Error: Bad method";
        }

        EnotWebhookBody body;
        try {

            String sign = request.getHeader("x-api-sha256-signature");
            JsonNode bodyJsonNode = mapper.readTree(bodyStr);
            if(!bodyJsonNode.isObject()) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return "Error: Bad request (2)";
            }

            if(!checkBillingSign(sign, (ObjectNode) bodyJsonNode)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return "Error: Wrong signature";
            }
            body = mapper.treeToValue(bodyJsonNode, EnotWebhookBody.class);
        }catch(Exception e) {
            BillingService.LOGGER.warn("Can't read request of webhook from Enot", e);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Error: Bad request (1)";
        }

        return processWebHook(body, loc, request, response);
    }

    private String processWebHook(EnotWebhookBody body, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        String billIdStr = body.getOrderId();
        //String remoteIdStr = body.getInvoiceId();
        String currency = body.getCurrency();
        String payMethod = body.getPayService();
        String sumStr = body.getAmount();
        String profitStr = body.getCredited();
        int type = body.getType();
        int code = body.getCode();

        if(type != 1 && code != 1) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Error: Bad request (3)";
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

        /*
        if(remoteIdStr != null) {
            try {
                bill.setBillingId(Long.parseLong(remoteIdStr));
            }catch(Exception ex) {
                BillingService.LOGGER.catching(ex);
            }
        }
        */

        int sum;
        try {
            sum = (int)Float.parseFloat(sumStr);
            if(bill.getSum() != sum) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return "Error: Billing data sum and given amount are mismatched";
            }
        }catch(Exception ex) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Error: Invalid amount value";
        }

        float profit;
        try {
            profit = Float.parseFloat(profitStr);
        }catch(Exception ex) {
            profit = sum;
            BillingService.LOGGER.warn("Got pay notify request without profit value (pay_id={}). Setting profit to sum value.", bill.getId());
        }

        User user = users.findById(bill.getUserId()).orElse(null);
        if(user == null) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return "Error: User not found";
        }

        Date date = new Date();

        bill.setUpdated(date);
        bill.setMessage("Paid");
        bill.setProfit(profit);
        bill.setPayMethod(StringUtils.hasText(payMethod) ? payMethod : "");
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

    private boolean checkBillingSign(String sign, ObjectNode body) throws JsonProcessingException {
        Map<String, JsonNode> sortedMap = new TreeMap<>();
        body.fields().forEachRemaining(entry->sortedMap.put(entry.getKey(), entry.getValue()));
        String sortedBody = mapper.writeValueAsString(sortedMap);
        String calculatedSign = EncodeUtils.hmacSHA256(secretKey, sortedBody);
        return calculatedSign.equals(sign);
    }

}

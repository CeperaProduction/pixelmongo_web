package ru.pixelmongo.pixelmongo.model.dto.enot;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EnotWebhookBody {

    private String invoiceId;

    private String status;

    private String amount;

    private String credited;

    private String currency;

    private String orderId;

    private String payService;

    private String payerDetails;

    private int type;

    private String payTime;

    private int code;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPayService() {
        return payService;
    }

    public void setPayService(String payService) {
        this.payService = payService;
    }

    public String getPayerDetails() {
        return payerDetails;
    }

    public void setPayerDetails(String payerDetails) {
        this.payerDetails = payerDetails;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCredited() {
        return credited;
    }

    public void setCredited(String credited) {
        this.credited = credited;
    }

    @Override
    public String toString() {
        return "EnotWebhookBody [invoiceId=" + invoiceId + ", status=" + status + ", amount=" + amount + ", credited="
                + credited + ", currency=" + currency + ", orderId=" + orderId + ", payService=" + payService
                + ", payerDetails=" + payerDetails + ", type=" + type + ", payTime=" + payTime + ", code=" + code + "]";
    }

}

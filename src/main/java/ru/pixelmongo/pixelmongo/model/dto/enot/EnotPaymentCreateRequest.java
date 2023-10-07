package ru.pixelmongo.pixelmongo.model.dto.enot;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EnotPaymentCreateRequest {

    private String shopId;

    private int amount;

    private String currency;

    private String orderId;

    private String comment;

    private String email;

    private String successUrl;

    private String failUrl;

    public EnotPaymentCreateRequest() {}

    public EnotPaymentCreateRequest(String shopId, int amount, String currency, String orderId, String comment,
            String email, String successUrl, String failUrl) {
        super();
        this.shopId = shopId;
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
        this.comment = comment;
        this.email = email;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getFailUrl() {
        return failUrl;
    }

    public void setFailUrl(String failUrl) {
        this.failUrl = failUrl;
    }

    @Override
    public String toString() {
        return "EnotPaymentCreateRequest [shopId=" + shopId + ", amount=" + amount + ", currency=" + currency
                + ", orderId=" + orderId + ", comment=" + comment + ", email=" + email + ", successUrl=" + successUrl
                + ", failUrl=" + failUrl + "]";
    }

}

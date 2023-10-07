package ru.pixelmongo.pixelmongo.model.dto.enot;

public class EnotCreatedPaymentData {

    private String id;

    private String amount;

    private String currency;

    private String url;

    private String expired;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "EnotCreatedPaymentData [id=" + id + ", amount=" + amount + ", currency=" + currency + ", url=" + url
                + ", expired=" + expired + "]";
    }

}

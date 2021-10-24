package ru.pixelmongo.pixelmongo.model.dto.qiwi;

public class QiwiBill {

    private String siteId, billId;

    private QiwiAmount amount;

    private QiwiStatus status;

    private QiwiCustomer customer;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public QiwiAmount getAmount() {
        return amount;
    }

    public void setAmount(QiwiAmount amount) {
        this.amount = amount;
    }

    public QiwiStatus getStatus() {
        return status;
    }

    public void setStatus(QiwiStatus status) {
        this.status = status;
    }

    public QiwiCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(QiwiCustomer customer) {
        this.customer = customer;
    }



}

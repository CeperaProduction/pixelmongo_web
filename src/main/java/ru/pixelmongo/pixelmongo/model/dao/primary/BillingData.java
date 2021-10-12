package ru.pixelmongo.pixelmongo.model.dao.primary;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import ru.pixelmongo.pixelmongo.handlers.BillingHandler;

@Entity
@Table(name = "billing")
public class BillingData {

    public static final int STATUS_CREATED = 0;
    public static final int STATUS_DONE = 1;
    public static final int STATUS_ERROR = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "billing_id")
    private long billingId = 0;

    @Column(nullable = false, length = 63)
    private String handler;

    private int status = STATUS_CREATED;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name="user_name", length = 32, nullable = false)
    private String userName;

    private Date created;

    private Date updated;

    @Column(name = "pay_method")
    private String payMethod = "";

    private String message = "";

    @Column(nullable = false)
    private int sum;

    private float profit;

    public BillingData() {}

    public BillingData(BillingHandler handler, User user, int sum) {
        this.userId = user.getId();
        this.userName = user.getName();
        this.sum = sum;
        this.handler = handler.getName();
        this.created = new Date();
        this.updated = this.created;
    }

    public int getId() {
        return id;
    }

    public long getBillingId() {
        return billingId;
    }

    public void setBillingId(long billingId) {
        this.billingId = billingId;
    }

    public String getHandler() {
        return handler;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSum() {
        return sum;
    }

    public float getProfit() {
        return profit;
    }

    public String getProfitString() {
        return String.format("%1.2f", profit);
    }

    public void setProfit(float profit) {
        this.profit = profit;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Date getCreated() {
        return created;
    }


}

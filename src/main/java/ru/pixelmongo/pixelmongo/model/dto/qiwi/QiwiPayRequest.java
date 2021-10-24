package ru.pixelmongo.pixelmongo.model.dto.qiwi;

public class QiwiPayRequest {

    private QiwiBill bill;
    private String version;

    public QiwiBill getBill() {
        return bill;
    }

    public void setBill(QiwiBill bill) {
        this.bill = bill;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}

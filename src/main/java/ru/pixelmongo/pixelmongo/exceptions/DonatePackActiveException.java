package ru.pixelmongo.pixelmongo.exceptions;

import java.util.Date;

public class DonatePackActiveException extends IllegalStateException{

    private static final long serialVersionUID = -3460804525944212997L;

    private final int packId, backQueryId;
    private Date backExecuteDate;

    public DonatePackActiveException(int packId, int backQueryId, Date backExecuteDate) {
        super("Can't buy pack! It is still active!");
        this.packId = packId;
        this.backQueryId = backQueryId;
        this.backExecuteDate = backExecuteDate;
    }

    public int getPackId() {
        return packId;
    }

    public int getBackQueryId() {
        return backQueryId;
    }

    public Date getBackExecuteDate() {
        return backExecuteDate;
    }





}

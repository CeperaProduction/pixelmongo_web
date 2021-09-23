package ru.pixelmongo.pixelmongo.exceptions;

public class DonateNotEnoughtMoneyException extends RuntimeException{

    private static final long serialVersionUID = 9127802818139876229L;

    private final String userName;
    private final int needMoney, hasMoney;

    public DonateNotEnoughtMoneyException(String userName, int needMoney, int hasMoney) {
        super("User "+userName+" hasn't enought money. Need: "+needMoney+" Has: "+hasMoney);
        this.userName = userName;
        this.needMoney = needMoney;
        this.hasMoney = hasMoney;
    }

    public String getUserName() {
        return userName;
    }

    public int getNeedMoney() {
        return needMoney;
    }

    public int getHasMoney() {
        return hasMoney;
    }

}

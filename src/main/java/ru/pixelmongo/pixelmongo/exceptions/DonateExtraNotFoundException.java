package ru.pixelmongo.pixelmongo.exceptions;

public class DonateExtraNotFoundException extends RuntimeException{

    private static final long serialVersionUID = -3783835085533026589L;

    private final String extraTag;

    public DonateExtraNotFoundException(String extraTag) {
        super("Donate extra with tag '"+extraTag+"' not found");
        this.extraTag = extraTag;
    }

    public String getExtraTag() {
        return extraTag;
    }

}

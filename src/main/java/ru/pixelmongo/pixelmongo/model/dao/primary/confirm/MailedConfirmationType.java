package ru.pixelmongo.pixelmongo.model.dao.primary.confirm;

public enum MailedConfirmationType {
    CHANGE_PASSWORD,
    MAIL_CONFIRM,
    MAIL_CHANGE;

    public static class Values{

        public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";
        public static final String MAIL_CONFIRM = "MAIL_CONFIRM";
        public static final String MAIL_CHANGE = "MAIL_CHANGE";

    }
}

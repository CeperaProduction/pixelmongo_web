package ru.pixelmongo.pixelmongo.model;

public class PopupMessage {

    private String text = "";
    private Type type = Type.INFO;
    private int time = 10000;

    public PopupMessage() {}

    public PopupMessage(String text, Type type) {
        this.text = text;
        this.type = type;
    }

    public PopupMessage(String text, Type type, int timeMs) {
        this(text, type);
        this.time = timeMs;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public enum Type {
        INFO,
        OK,
        WARN,
        ERROR;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

}

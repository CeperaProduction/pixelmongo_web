package ru.pixelmongo.pixelmongo.model.dao.primary.donate;

public enum DonateDisplayType {

    DEFAULT(300, 300),
    BIG(600, 800),
    ROUNDED(300, 300);

    private final int widht, height;

    private DonateDisplayType(int width, int height) {
        this.widht = width;
        this.height = height;
    }

    public int getWidth() {
        return widht;
    }

    public int getHeight() {
        return height;
    }

}

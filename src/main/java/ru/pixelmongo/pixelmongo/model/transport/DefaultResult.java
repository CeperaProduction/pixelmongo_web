package ru.pixelmongo.pixelmongo.model.transport;

import java.util.function.Supplier;

public enum DefaultResult implements Supplier<String> {
    OK,
    ERROR;

    @Override
    public String get() {
        return name().toLowerCase();
    }

}

package ru.pixelmongo.pixelmongo.model.dto.forms.donate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonatePackBuyForm {

    private int server;

    private Map<String, List<String>> tokens = new HashMap<>();

    private int count = 1;

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    public Map<String, List<String>> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, List<String>> tokens) {
        this.tokens = tokens;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}

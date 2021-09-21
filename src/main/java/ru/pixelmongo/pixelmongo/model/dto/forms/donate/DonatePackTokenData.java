package ru.pixelmongo.pixelmongo.model.dto.forms.donate;

import java.util.ArrayList;
import java.util.List;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;

public class DonatePackTokenData {

    private DonatePackTokenType type;

    private String name;

    private List<String> options = new ArrayList<String>();

    public DonatePackTokenData() {}

    public DonatePackTokenData(DonatePackTokenType type, String name) {
        this.name = name;
        this.type = type;
    }

    public DonatePackTokenData(DonatePackTokenType type, String name, List<String> options) {
        this(type, name);
        if(options != null)
            this.options = options;
    }

    public DonatePackTokenType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setType(DonatePackTokenType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

}

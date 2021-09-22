package ru.pixelmongo.pixelmongo.model.dto.forms.donate;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Pattern;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.utils.DefaultPatterns;

public class DonatePackTokenData {

    private DonatePackTokenType type;

    @Pattern(regexp = DefaultPatterns.SIMPLE_TAG_PATTERN, message = "{admin.tag.invalid}")
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

    public String getOption(int index) {
        if(index < 0 || index >= options.size())
            return "";
        return options.get(index);
    }

    public List<List<String>> getOptionSections(int sectionSize){
        List<List<String>> result = new ArrayList<>();
        int size = options.size();
        if(size % sectionSize != 0)
            size += sectionSize - (size % sectionSize);
        for(int i = 0; i < size; i+=sectionSize) {
            List<String> spl = new ArrayList<>();
            for(int j = 0; j < sectionSize; j++)
                spl.add(getOption(i+j));
            result.add(spl);
        }
        return result;
    }

}

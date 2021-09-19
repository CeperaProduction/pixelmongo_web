package ru.pixelmongo.pixelmongo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;

public class StringListConverter implements AttributeConverter<List<String>, String>{

    public static final String SPLIT = ";";

    public static final String SPLIT_REPLACE = "\\.\\,";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if(attribute == null) return "";
        List<String> attr = new ArrayList<>();
        attribute.forEach(s->attr.add(s.replace(SPLIT, SPLIT_REPLACE)));
        return String.join(SPLIT, attr);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if(dbData == null) return new ArrayList<>();
        List<String> vals = Arrays.asList(dbData.split(SPLIT));
        List<String> vals2 = new ArrayList<>();
        vals.forEach(s->vals2.add(s.replace(SPLIT_REPLACE, SPLIT)));
        return vals2;
    }

}

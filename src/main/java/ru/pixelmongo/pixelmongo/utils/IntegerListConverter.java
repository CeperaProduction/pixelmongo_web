package ru.pixelmongo.pixelmongo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegerListConverter implements AttributeConverter<List<Integer>, String>{

    public static final Logger LOGGER = LogManager.getLogger();

    public static final String SPLIT = ";";

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        if(attribute == null || attribute.isEmpty()) return "";
        List<String> attr = new ArrayList<>();
        attribute.forEach(s->attr.add(s.toString()));
        return String.join(SPLIT, attr);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        if(dbData == null || dbData.isEmpty()) return new ArrayList<>();
        List<String> vals = Arrays.asList(dbData.split(SPLIT));
        List<Integer> vals2 = new ArrayList<>();
        vals.forEach(s->vals2.add(parseInt(s)));
        return vals2;
    }

    private int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        }catch(NumberFormatException ex) {
            LOGGER.catching(ex);
        }
        return 0;
    }

}

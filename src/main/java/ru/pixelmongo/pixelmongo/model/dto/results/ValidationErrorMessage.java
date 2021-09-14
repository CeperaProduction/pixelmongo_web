package ru.pixelmongo.pixelmongo.model.dto.results;

import java.util.HashMap;
import java.util.Map;

public class ValidationErrorMessage extends ResultDataMessage<Map<String, String>>{

    public ValidationErrorMessage(String message, Map<String, String> data) {
        super(()->"validation_error", message, data);
    }

    public ValidationErrorMessage(String message, String[] errorKeys, String[] errorValues) {
        super(()->"validation_error", message, toMap(errorKeys, errorValues));
    }

    private static Map<String, String> toMap(String[] keys, String[] values) {
        if(keys.length != values.length)
            throw new IllegalArgumentException("Keys and values arrays must be same lenght");
        Map<String, String> map = new HashMap<String, String>();
        for(int i = 0; i<keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

}

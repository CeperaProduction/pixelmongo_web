package ru.pixelmongo.pixelmongo.model.dto.results;

import java.util.function.Supplier;

public class ResultDataMessage<T> extends ResultMessage {
    
    private T data;

    public ResultDataMessage(Supplier<String> result, String message, T data) {
        super(result, message);
        this.data = data;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }

}

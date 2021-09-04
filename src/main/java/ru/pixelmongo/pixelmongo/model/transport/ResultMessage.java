package ru.pixelmongo.pixelmongo.model.transport;

import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResultMessage {

    private Supplier<String> result = ()->"";
    private String message = "";
    
    public ResultMessage(Supplier<String> result, String message) {
        this.result = result;
        this.message = message;
    }

    @JsonIgnore
    public Supplier<String> getResultSupplier() {
        return result;
    }
    
    public String getResult() {
        return result.get();
    }

    public void setResult(Supplier<String> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
    
    
    
}

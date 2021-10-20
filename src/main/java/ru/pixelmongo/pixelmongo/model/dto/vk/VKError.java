package ru.pixelmongo.pixelmongo.model.dto.vk;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VKError {

    private int code;

    private String message;

    @JsonProperty("error_code")
    public int getCode() {
        return code;
    }

    @JsonProperty("error_code")
    public void setCode(int code) {
        this.code = code;
    }

    @JsonProperty("error_msg")
    public String getMessage() {
        return message;
    }

    @JsonProperty("error_msg")
    public void setMessage(String message) {
        this.message = message;
    }



}

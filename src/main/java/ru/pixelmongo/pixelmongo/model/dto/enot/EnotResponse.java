package ru.pixelmongo.pixelmongo.model.dto.enot;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EnotResponse<T> {

    private T data;

    private int status;

    private boolean statusCheck;

    private String error;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isStatusCheck() {
        return statusCheck;
    }

    public void setStatusCheck(boolean statusCheck) {
        this.statusCheck = statusCheck;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "EnotResponse [data=" + data + ", status=" + status + ", statusCheck=" + statusCheck + ", error=" + error
                + "]";
    }

}

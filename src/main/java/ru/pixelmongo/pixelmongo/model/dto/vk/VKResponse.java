package ru.pixelmongo.pixelmongo.model.dto.vk;

public class VKResponse <T> {

    private T response;

    private VKError error;

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public VKError getError() {
        return error;
    }

    public void setError(VKError error) {
        this.error = error;
    }

}

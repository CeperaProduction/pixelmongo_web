package ru.pixelmongo.pixelmongo.model.dao;

public interface OrderedData<ID> {

    public ID getId();

    public int getOrdinary();

    public void setOrdinary(int ordinary);

}

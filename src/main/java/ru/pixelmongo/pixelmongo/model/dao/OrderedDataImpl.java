package ru.pixelmongo.pixelmongo.model.dao;

public class OrderedDataImpl<ID> implements OrderedData<ID> {

    private final ID id;
    private int ordinary;

    public OrderedDataImpl(ID id, int ordinary) {
        this.id = id;
        this.ordinary = ordinary;
    }

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public int getOrdinary() {
        return ordinary;
    }

    @Override
    public void setOrdinary(int ordinary) {
        this.ordinary = ordinary;
    }

}

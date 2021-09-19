package ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.utils.IntegerListConverter;
import ru.pixelmongo.pixelmongo.utils.StringListConverter;

@Entity
@DiscriminatorValue(DonatePackTokenType.Values.SELECT_VALUE)
public class DonatePackTokenSelectValue extends DonatePackToken{

    @Column(name = "data")
    @Convert(converter = StringListConverter.class)
    private List<String> values;

    @Column(name = "costs")
    @Convert(converter = IntegerListConverter.class)
    private List<Integer> costValues;

    public DonatePackTokenSelectValue(DonatePack pack, List<String> values, List<Integer> costValues) {
        super(pack);
        if(values.size() != costValues.size())
            throw new IllegalArgumentException("Values and costs lists must be same size");
        this.values = values;
        this.costValues = costValues;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<Integer> getCostValues() {
        return costValues;
    }

    public void setCostValues(List<Integer> costValues) {
        this.costValues = costValues;
    }

}

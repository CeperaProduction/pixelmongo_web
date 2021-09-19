package ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.utils.StringListConverter;

@Entity
@DiscriminatorValue(DonatePackTokenType.Values.RANDOM_VALUE)
public class DonatePackTokenRandomValue extends DonatePackToken{

    @Column(name = "data")
    @Convert(converter = StringListConverter.class)
    private List<String> values;

    public DonatePackTokenRandomValue(DonatePack pack, List<String> values) {
        super(pack);
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

}

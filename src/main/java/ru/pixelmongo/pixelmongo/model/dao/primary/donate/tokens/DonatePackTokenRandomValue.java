package ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.utils.StringListConverter;

@Entity
@DiscriminatorValue(DonatePackTokenType.Values.RANDOM_VALUE)
public class DonatePackTokenRandomValue extends DonatePackToken{

    @Column(name = "data")
    @Lob
    @Convert(converter = StringListConverter.class)
    private List<String> values;

    public DonatePackTokenRandomValue() {}

    public DonatePackTokenRandomValue(String token, DonatePack pack, List<String> values) {
        super(token, pack);
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

}

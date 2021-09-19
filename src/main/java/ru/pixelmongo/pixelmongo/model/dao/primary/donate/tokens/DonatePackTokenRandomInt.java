package ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;

@Entity
@DiscriminatorValue(DonatePackTokenType.Values.RANDOM_INT)
public class DonatePackTokenRandomInt extends DonatePackToken{

    private int min;

    private int max;

    public DonatePackTokenRandomInt() {}

    public DonatePackTokenRandomInt(DonatePack pack, int min, int max) {
        super(pack);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

}

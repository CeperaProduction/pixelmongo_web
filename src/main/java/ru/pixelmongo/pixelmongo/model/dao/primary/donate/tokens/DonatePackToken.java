package ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;

@Entity
@Table(name = "donate_packs_tokens")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 16)
public abstract class DonatePackToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "pack_id", nullable = false)
    private DonatePack pack;

    @Column(name="type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private DonatePackTokenType type;

    public DonatePackToken() {}

    public DonatePackToken(DonatePack pack) {
        this.pack = pack;
    }

    public int getId() {
        return id;
    }

    public DonatePack getPack() {
        return pack;
    }

    public void setPack(DonatePack pack) {
        this.pack = pack;
    }

    public DonatePackTokenType getType() {
        return type;
    }

}

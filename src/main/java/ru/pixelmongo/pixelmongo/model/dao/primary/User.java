package ru.pixelmongo.pixelmongo.model.dao.primary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 32, nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "email_confirmed", columnDefinition = "bit(1) not null default 0")
    private boolean emailConfirmed = false;

    //password hash
    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private UserGroup group;

    @Column(name = "balance", columnDefinition = "int(11) not null default 0")
    private int realBalance = 0;

    @Column(name = "balance_bonus", columnDefinition = "int(11) not null default 0")
    private int bonusBalance = 0;

    @Column(name="reg_date", nullable = false)
    private Date registrationDate;

    @Column(name="reg_ip", nullable = false)
    private String registrationIp;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @OrderBy("date DESC")
    private List<UserLoginRecord> loginRecords;

    @Column(name = "cape", columnDefinition = "bit(1) not null default 0")
    private boolean hasCape = false;

    @Column(name = "hd_skin", columnDefinition = "bit(1) not null default 0")
    private boolean hasHDSkin = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Staff> staffContainer;

    /**
     * Used only for cascade promo activations info removing
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<PromocodeActivation> promocodeActivations;

    public User() {}

    public User(String name, UserGroup group, String email, String passwordHash, String ip) {
        this.name = name;
        this.email = email;
        this.password = passwordHash;
        this.group = group;
        this.registrationIp = ip;
        this.registrationDate = new Date();
        this.loginRecords = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserGroup getGroup() {
        return group;
    }

    public void setGroup(UserGroup group) {
        this.group = group;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    /**
     * Hashed password
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Password must be hashed
     * @param passwordHash
     */
    public void setPassword(String passwordHash) {
        this.password = passwordHash;
    }

    public int getRealBalance() {
        return realBalance;
    }

    public int getBonusBalance() {
        return bonusBalance;
    }

    public void setRealBalance(int realBalance) {
        this.realBalance = realBalance;
    }

    public void setBonusBalance(int bonusBalance) {
        this.bonusBalance = bonusBalance;
    }

    public int getBalance() {
        return getRealBalance() + getBonusBalance();
    }

    public void changeBalance(int count) {
        if(count == 0) return;
        if(count > 0) setBonusBalance(getBonusBalance()+count);
        else {
            int mrc = -count - getBonusBalance();
            if(mrc <= 0) {
                setBonusBalance(-mrc);
            }else {
                setBonusBalance(0);
                setRealBalance(Math.max(getRealBalance()-mrc, 0));
            }
        }
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<UserLoginRecord> getLoginRecords() {
        return loginRecords;
    }

    public String getRegistrationIp() {
        return registrationIp;
    }

    public void setRegistrationIp(String registrationIp) {
        this.registrationIp = registrationIp;
    }

    public boolean hasCape() {
        return hasCape;
    }

    public boolean hasHDSkin() {
        return hasHDSkin;
    }

    public void setHasCape(boolean hasCape) {
        this.hasCape = hasCape;
    }

    public void setHasHDSkin(boolean hasHDSkin) {
        this.hasHDSkin = hasHDSkin;
    }

    public Optional<Staff> getStaffInfo(){
        for(Staff staff : staffContainer)
            return Optional.of(staff);
        return Optional.empty();
    }

    public boolean isAnonymous() {
        return false;
    }

}

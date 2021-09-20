package ru.pixelmongo.pixelmongo.model.dto.forms.donate;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateCategory;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackToken;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateCategoryRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateServerRepository;

public class DonatePackForm {

    @NotBlank(message = "{value.empty}")
    private String title;

    private String content = "";

    private List<String> commands = new ArrayList<>();

    private boolean countable = false;

    private int cost = 100;

    private int category;

    private boolean timed = false;

    private int existTime = 0;

    private List<String> backCommands = new ArrayList<>();

    private boolean hiddenGive = false;

    private boolean enabled = true;

    private int invSpace = 0;

    private boolean giveOffline = false;

    private List<Integer> servers = new ArrayList<>();

    //TODO DTO for tokens
    private List<DonatePackToken> tokens = new ArrayList<>();

    public DonatePackForm() {}

    public DonatePackForm(DonateCategory category) {
        this.category = category.getId();
    }

    public DonatePackForm(DonatePack pack) {
        this.title = pack.getTitle();
        this.content = pack.getContent();
        this.commands = pack.getCommands();
        this.countable = pack.isCountable();
        this.cost = pack.getCost();
        this.category = pack.getCategory().getId();
        this.timed = pack.isTimed();
        this.existTime = pack.getExistTime();
        this.backCommands = pack.getBackCommands();
        this.hiddenGive = pack.isHiddenGive();
        this.enabled = pack.isEnabled();
        this.invSpace = pack.getInvSpace();
        this.giveOffline = pack.isGiveOffline();
        pack.getServers().forEach(s->this.servers.add(s.getId()));
        //TODO make token DTOs
    }

    public void apply(DonatePack pack, DonateCategoryRepository categoryRepo,
            DonateServerRepository serverRepo) {
        pack.setTitle(this.title);
        pack.setContent(this.content);
        pack.setCommands(this.commands);
        pack.setCountable(this.countable);
        pack.setCost(this.cost);
        categoryRepo.findById(this.category).ifPresent(pack::setCategory);
        pack.setTimed(this.timed);
        pack.setExistTime(this.existTime);
        pack.setBackCommands(this.backCommands);
        pack.setHiddenGive(this.hiddenGive);
        pack.setEnabled(this.enabled);
        pack.setInvSpace(this.invSpace);
        pack.setGiveOffline(this.giveOffline);
        pack.getServers().clear();
        this.servers.forEach(sid->serverRepo.findById(sid).ifPresent(pack.getServers()::add));
        //TODO set tokens
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public boolean isCountable() {
        return countable;
    }

    public void setCountable(boolean countable) {
        this.countable = countable;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean isTimed() {
        return timed;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }

    public int getExistTime() {
        return existTime;
    }

    public void setExistTime(int existTime) {
        this.existTime = existTime;
    }

    public List<String> getBackCommands() {
        return backCommands;
    }

    public void setBackCommands(List<String> backCommands) {
        this.backCommands = backCommands;
    }

    public boolean isHiddenGive() {
        return hiddenGive;
    }

    public void setHiddenGive(boolean hiddenGive) {
        this.hiddenGive = hiddenGive;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getInvSpace() {
        return invSpace;
    }

    public void setInvSpace(int invSpace) {
        this.invSpace = invSpace;
    }

    public boolean isGiveOffline() {
        return giveOffline;
    }

    public void setGiveOffline(boolean giveOffline) {
        this.giveOffline = giveOffline;
    }

    public List<Integer> getServers() {
        return servers;
    }

    public void setServers(List<Integer> servers) {
        this.servers = servers;
    }

    public List<DonatePackToken> getTokens() {
        return tokens;
    }

    public void setTokens(List<DonatePackToken> tokens) {
        this.tokens = tokens;
    }

}

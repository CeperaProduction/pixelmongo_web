package ru.pixelmongo.pixelmongo.model.dto.forms.donate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateCategory;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateCategoryRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateServerRepository;
import ru.pixelmongo.pixelmongo.services.DonateService;

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

    private Map<String, DonatePackTokenData> tokens = new LinkedHashMap<>();

    public DonatePackForm() {}

    public DonatePackForm(DonateCategory category) {
        this.category = category.getId();
    }

    public DonatePackForm(DonatePack pack, DonateService donateService) {
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
        pack.getTokens().stream().map(donateService::makeTokenData)
            .forEach(td->tokens.put(td.getName(), td));
    }

    public void apply(DonatePack pack, DonateService donateService,
            DonateCategoryRepository categoryRepo,
            DonateServerRepository serverRepo) {
        pack.setTitle(this.title);
        pack.setContent(this.content);
        pack.setCommands(this.commands);
        pack.setCountable(this.countable && !this.timed);
        pack.setCost(Math.max(this.cost, 0));
        categoryRepo.findById(this.category).ifPresent(pack::setCategory);
        pack.setTimed(this.timed);
        pack.setExistTime(Math.max(this.existTime, 0));
        pack.setBackCommands(this.backCommands);
        pack.setHiddenGive(this.hiddenGive);
        pack.setEnabled(this.enabled);
        pack.setInvSpace(Math.max(this.invSpace, 0));
        pack.setGiveOffline(this.giveOffline);
        pack.getServers().clear();
        this.servers.forEach(sid->serverRepo.findById(sid).ifPresent(pack.getServers()::add));
        pack.getTokens().clear();
        this.tokens.values().stream().map(td->donateService.makeToken(td, pack))
            .forEach(pack.getTokens()::add);
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

    public Map<String, DonatePackTokenData> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, DonatePackTokenData> tokens) {
        this.tokens = tokens;
    }

    public List<DonatePackTokenData> getTokenList(){
        return new ArrayList<>(this.tokens.values());
    }

}

package ru.pixelmongo.pixelmongo.model.dto.forms.donate;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateServerRepository;
import ru.pixelmongo.pixelmongo.utils.DefaultPatterns;

public class DonatePageForm {

    @Pattern(regexp = DefaultPatterns.SIMPLE_TAG_PATTERN, message = "{admin.tag.invalid}")
    private String tag;

    @NotBlank(message = "{value.empty}")
    private String title;

    private boolean hidden = false;

    private boolean enabled = true;

    private List<Integer> servers = new ArrayList<>();

    public DonatePageForm() {}

    public DonatePageForm(DonatePage page) {
       this.tag = page.getTag();
       this.title = page.getTitle();
       this.hidden = page.isHidden();
       this.enabled = page.isEnabled();
       page.getServers().forEach(s->this.servers.add(s.getId()));
    }

    public void apply(DonatePage page, DonateServerRepository serversRepo) {
        page.setTag(this.tag);
        page.setTitle(this.title);
        page.setHidden(this.hidden);
        page.setEnabled(this.enabled);
        page.getServers().clear();
        this.servers.forEach(sid->serversRepo.findById(sid).ifPresent(page.getServers()::add));
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Integer> getServers() {
        return servers;
    }

    public void setServers(List<Integer> servers) {
        this.servers = servers;
    }



}

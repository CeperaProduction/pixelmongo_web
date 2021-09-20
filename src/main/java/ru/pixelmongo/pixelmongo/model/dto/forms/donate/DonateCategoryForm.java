package ru.pixelmongo.pixelmongo.model.dto.forms.donate;

import javax.validation.constraints.NotBlank;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateCategory;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePageRepository;

public class DonateCategoryForm {

    @NotBlank(message = "{value.empty}")
    private String title;

    private int page;

    private boolean enabled = true;

    public DonateCategoryForm() {}

    public DonateCategoryForm(DonatePage page) {
        this.page = page.getId();
    }

    public DonateCategoryForm(DonateCategory category) {
        this.title = category.getTitle();
        this.page = category.getPage().getId();
        this.enabled = category.isEnabled();
    }

    public void apply(DonateCategory category, DonatePageRepository pagesRepo) {
        category.setTitle(this.title);
        category.setEnabled(this.enabled);
        pagesRepo.findById(page).ifPresent(category::setPage);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }



}

package ru.pixelmongo.pixelmongo.model.dto;

public class PaginationElement {

    private String page;
    private Step step;
    private boolean active;
    private boolean disabled;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getStep() {
        return step == null ? null : step.name();
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public enum Step{
        next, back;
    }

}

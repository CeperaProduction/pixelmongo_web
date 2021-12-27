package ru.pixelmongo.pixelmongo.model.dto.forms;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import ru.pixelmongo.pixelmongo.model.dao.primary.Promocode;

public class PromocodeForm {

    @NotBlank(message = "{value.empty}")
    @Size(max = 255, message = "{value.too_long}")
    private String title = "";

    @NotBlank(message = "{value.empty}")
    @Size(max = 255, message = "{value.too_long}")
    private String code = "";

    @Min(value = 0, message = "{value.denied}")
    private int value;

    @Min(value = 1, message = "{value.denied}")
    private int maxUsages = 1;

    private Integer endTime;

    public PromocodeForm() {}

    public PromocodeForm(Promocode promocode) {
        this.title = promocode.getTitle();
        this.code = promocode.getCode();
        this.value = promocode.getValue();
        this.maxUsages = promocode.getMaxUses();
        if(promocode.getEndDate() != null)
            endTime = (int) (promocode.getEndDate().getTime()/1000L);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMaxUsages() {
        return maxUsages;
    }

    public void setMaxUsages(int maxUsages) {
        this.maxUsages = maxUsages;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

}

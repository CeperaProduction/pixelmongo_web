package ru.pixelmongo.pixelmongo.model.dto.forms;

import ru.pixelmongo.pixelmongo.model.dao.primary.Staff;
import ru.pixelmongo.pixelmongo.model.dao.primary.StaffDisplay;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;

public class StaffManageForm {

    private String user = "";

    private String title = "";

    private StaffDisplay display = new StaffDisplay();

    public StaffManageForm() {}

    public StaffManageForm(Staff staff) {
        User user = staff.getUser();
        if(user != null) this.user = user.getName();
        else this.user = "";
        this.title = staff.getTitle();
        this.display = staff.getDisplay();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public StaffDisplay getDisplay() {
        return display;
    }

    public void setDisplay(StaffDisplay display) {
        this.display = display;
    }



}

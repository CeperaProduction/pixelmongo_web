package ru.pixelmongo.pixelmongo.model.dao.primary;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@Embeddable
public class StaffDisplay {

    @Enumerated(EnumType.ORDINAL)
    private StaffDisplayForeground foreground = StaffDisplayForeground.NONE;

    @Enumerated(EnumType.ORDINAL)
    private StaffDisplayBackground background = StaffDisplayBackground.WHITE;

    public StaffDisplay() {}

    @JsonCreator
    public StaffDisplay(String str) {
        String[] ords = str.split("_");
        int ordFG = Integer.parseInt(ords[0]);
        int ordBG = Integer.parseInt(ords[1]);
        this.foreground = StaffDisplayForeground.values()[ordFG];
        this.background = StaffDisplayBackground.values()[ordBG];
    }

    public StaffDisplay(StaffDisplayForeground foreground, StaffDisplayBackground background) {
        this.foreground = foreground;
        this.background = background;
    }

    public StaffDisplayBackground getBackground() {
        return background;
    }

    public StaffDisplayForeground getForeground() {
        return foreground;
    }

    public String getStyleClassForeground() {
        return "display-fg-"+foreground.toString().toLowerCase().replace('_', '-');
    }

    public String getStyleClassBackground() {
        return "display-bg-"+background.toString().toLowerCase().replace('_', '-');
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof StaffDisplay)) return false;
        StaffDisplay other = (StaffDisplay) obj;
        return foreground.equals(other.foreground) && background.equals(other.background);
    }

    @Override
    public int hashCode() {
        int bgmax = StaffDisplayBackground.values().length;
        return foreground.ordinal()*bgmax + background.ordinal();
    }

    @Override
    @JsonValue
    public String toString() {
        return foreground.ordinal()+"_"+background.ordinal();
    }

    public static List<StaffDisplay> getAllVariants(){
        List<StaffDisplay> list = new ArrayList<>();
        for(StaffDisplayForeground fg : StaffDisplayForeground.values())
            for(StaffDisplayBackground bg : StaffDisplayBackground.values())
                list.add(new StaffDisplay(fg, bg));
        return list;

    }


}

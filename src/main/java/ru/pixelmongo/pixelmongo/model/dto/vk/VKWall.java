package ru.pixelmongo.pixelmongo.model.dto.vk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VKWall {

    private int count;

    private List<VKPost> items = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<VKPost> getItems() {
        return items;
    }

    public void setItems(List<VKPost> items) {
        ArrayList<VKPost> list = new ArrayList<VKPost>(items);
        Collections.sort(list, (p1, p2)->Integer.compare(p2.getDate(), p1.getDate()));
        this.items = list;
    }



}

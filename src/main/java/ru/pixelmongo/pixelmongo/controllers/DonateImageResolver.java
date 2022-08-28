package ru.pixelmongo.pixelmongo.controllers;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePack;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;
import ru.pixelmongo.pixelmongo.services.UploadService;

public class DonateImageResolver {

    private final UploadService upload;

    public DonateImageResolver(UploadService upload) {
        this.upload = upload;
    }

    public String get(DonatePage page) {
        if(page == null || page.getId() == 0) {
            return upload.getUploadPathURL("default_page.jpg", "donate");
        }
        return upload.getUploadPathURLIfExists("/static/img/donate/default_page.jpg", page.getId()+".jpg", "donate", "pages");
    }

    public String get(DonatePack pack) {
        if(pack == null || pack.getId() == 0) {
            return upload.getUploadPathURL("default_pack.jpg", "donate");
        }
        return upload.getUploadPathURLIfExists("/static/img/donate/default_pack.jpg", pack.getId()+".jpg", "donate", "packs");
    }



}
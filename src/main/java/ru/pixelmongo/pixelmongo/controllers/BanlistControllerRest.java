package ru.pixelmongo.pixelmongo.controllers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.sub.PlayerBanRecord;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.sub.PlayerBanRecordRepository;
import ru.pixelmongo.pixelmongo.services.UploadService;
import ru.pixelmongo.pixelmongo.services.UserService;
import ru.pixelmongo.pixelmongo.validation.FileNotEmpty;
import ru.pixelmongo.pixelmongo.validation.FileSize;
import ru.pixelmongo.pixelmongo.validation.FileType;

@RestController
@RequestMapping("/banlist")
public class BanlistControllerRest {

    public static final String PERM_UPLOAD="banlist.proof.upload";
    public static final String PERM_DELETE="banlist.proof.delete";
    public static final String PERM_ACCESS_ALL="banlist.proof.all";

    @Autowired
    private PlayerBanRecordRepository banlist;

    @Autowired
    private UploadService upload;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource msg;

    @PostMapping("/{ban}")
    public ResultMessage uploadProof(@PathVariable("ban") int banId, Locale loc,
            @RequestParam("file")
            @Validated
            @FileNotEmpty
            @FileType({"image/png", "image/jpg"})
            @FileSize(value = 4*1024*1024)
            MultipartFile mfile) {

        PlayerBanRecord ban = banlist.getActiveBan(banId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.ban", null, loc)));

        BanlistProofHelper helper = new BanlistProofHelper(userService, upload);
        if(!helper.canUpload(ban))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        final String ext1, ext2;
        if(mfile.getContentType().equals("image/png")) {
            ext1 = ".png";
            ext2 = ".jpg";
        }else {
            ext1 = ".jpg";
            ext2 = ".png";
        }
        String fileName = ban.getId()+ext1;
        upload.upload(fileName, mfile, "banlist");
        upload.deleteUploaded(ban.getId()+ext2, "banlist");

        Map<String, Object> data = new HashMap<>();
        data.put("location", upload.getUploadPathURL(fileName, "banlist"));
        return new ResultDataMessage<Map<String, Object>>(DefaultResult.OK,
                msg.getMessage("banlist.proof.saved", null, loc), data);
    }

    @DeleteMapping("/{ban}")
    public ResultMessage deleteProof(@PathVariable("ban") int banId, Locale loc) {

        PlayerBanRecord ban = banlist.getActiveBan(banId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.ban", null, loc)));

        BanlistProofHelper helper = new BanlistProofHelper(userService, upload);
        if(!helper.canDelete(ban))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        upload.deleteUploaded(ban.getId()+".png", "banlist");
        upload.deleteUploaded(ban.getId()+".jpg", "banlist");

        return new ResultMessage(DefaultResult.OK, msg.getMessage("banlist.proof.deleted", null, loc));
    }

    public static class BanlistProofHelper{

        private final UserService userService;
        private final UploadService uploadService;
        private final boolean upload, delete, all;

        public BanlistProofHelper(UserService userService, UploadService uploadService){
            this.userService = userService;
            this.uploadService = uploadService;
            upload = userService.hasPerm(PERM_UPLOAD);
            delete = userService.hasPerm(PERM_DELETE);
            all = userService.hasPerm(PERM_ACCESS_ALL);
        }

        public String get(PlayerBanRecord ban) {
            String uri = uploadService.getUploadPathURLIfExists(null, ban.getId()+".png", "banlist");
            if(uri == null) {
                uri = uploadService.getUploadPathURLIfExists(null, ban.getId()+".jpg", "banlist");
            }
            return uri;
        }

        public boolean canUpload(PlayerBanRecord ban) {
            return upload && (all || userService.getCurrentUser().getName().equals(ban.getAdmin()));
        }

        public boolean canDelete(PlayerBanRecord ban) {
            return delete && (all || userService.getCurrentUser().getName().equals(ban.getAdmin()));
        }

    }

}

package ru.pixelmongo.pixelmongo.controllers.admin.donate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateCategory;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateCategoryRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePackRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePageRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.OrdinaryUtilsService;
import ru.pixelmongo.pixelmongo.services.UploadService;
import ru.pixelmongo.pixelmongo.services.UserService;
import ru.pixelmongo.pixelmongo.utils.RandomUtils;

@RestController
@RequestMapping("/admin/donate/pages")
public class DonateContentControllerRest {

    @Autowired
    private OrdinaryUtilsService ordinary;

    @Autowired
    private DonatePageRepository pages;

    @Autowired
    private DonateCategoryRepository categories;

    @Autowired
    private DonatePackRepository packs;

    @Autowired
    private AdminLogService logs;

    @Autowired
    private UserService userService;

    @Autowired
    private UploadService upload;

    /*
     * Reordering
     */

    @PostMapping("/reorder")
    public ResultMessage reorderPages(@RequestParam("ids") String idsStr,
            HttpServletRequest request,
            HttpServletResponse response) {
        List<Integer> ids = getIds(idsStr);
        if(!ids.isEmpty()) {
            int changed = ordinary.reorder(this.pages, this.pages.findAllOrdinaries(), ids);
            if(changed > 0) {
                log("admin.log.donate.page.reorder", request);
            }
            return new ResultMessage(DefaultResult.OK, "Donate pages reordered");
        }
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        return new ResultMessage(DefaultResult.ERROR, "Wrong ids");
    }

    @PostMapping("/{page}/category/reorder")
    public ResultMessage reorderCategories(@PathVariable("page") String pageTag,
            @RequestParam("ids") String idsStr,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonatePage page = find(pages.findByTag(pageTag));

        List<Integer> ids = getIds(idsStr);
        if(!ids.isEmpty()) {
            int changed = ordinary.reorder(this.categories, this.categories.findAllOrdinaries(page), ids);
            if(changed > 0) {
                log("admin.log.donate.category.reorder", request, page.getTitle()+" #"+page.getId());
            }
            return new ResultMessage(DefaultResult.OK, "Donate categories reordered");
        }
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResultMessage(DefaultResult.ERROR, "Wrong ids");
    }

    @PostMapping("/{page}/category/{category}/reorder")
    public ResultMessage reorderPacks(@PathVariable("page") String pageTag,
            @PathVariable("category") int categoryId,
            @RequestParam("ids") String idsStr,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonateCategory category = find(categories.findById(categoryId));
        DonatePage page = category.getPage();

        if(!page.getTag().equals(pageTag))
            throw notFound();

        List<Integer> ids = getIds(idsStr);
        if(!ids.isEmpty()) {
            int changed = ordinary.reorder(this.packs, this.packs.findAllOrdinaries(category), ids);
            if(changed > 0) {
                log("admin.log.donate.pack.reorder", request, page.getTitle()+" #"+page.getId());
            }
            return new ResultMessage(DefaultResult.OK, "Donate packs reordered");
        }
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResultMessage(DefaultResult.ERROR, "Wrong ids");
    }

    /*
     *
     * Pack content images upload
     *
     */

    @PostMapping("/{page}/upload")
    public String uploadImage(@RequestParam("file") MultipartFile mfile,
            @PathVariable("page") String pageTag) {
        DonatePage page = find(pages.findByTag(pageTag));
        if(!mfile.getContentType().startsWith("image/"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must be an image");
        int i = mfile.getOriginalFilename().lastIndexOf('.');
        String ext = i == -1 ? "" : mfile.getOriginalFilename().substring(i);
        String name = RandomUtils.generateRandomKey(16)+ext;
        String[] path = new String[]{"donate", "packcontent", "page_"+page.getId()};
        upload.upload(name, mfile, path);
        return "{\"location\":\""+upload.getUploadPathURL(name, path)+"\"}";
    }


    /*
     * Utils
     */

    private List<Integer> getIds(String idsStr) {
        try {
            return Arrays.asList(idsStr.split(",")).stream()
                    .map(Integer::parseInt).collect(Collectors.toList());
        }catch(Exception ex) {}
        return Collections.emptyList();
    }

    private <T> T find(Optional<T> searchResult) {
        return searchResult.orElseThrow(this::notFound);
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
    }

    private void log(String langKey, HttpServletRequest request, Object... langValues) {
        logs.log(langKey, langValues, userService.getCurrentUser(), request.getRemoteAddr());
    }


}

package ru.pixelmongo.pixelmongo.controllers.admin.donate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateCategory;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonatePage;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ValidationErrorMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonateCategoryRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePackRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.donate.DonatePageRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.OrdinaryUtilsService;
import ru.pixelmongo.pixelmongo.services.UserService;
import ru.pixelmongo.pixelmongo.utils.RandomUtils;

@RestController
@RequestMapping("/admin/donate")
public class DonateAdminControllerRest {

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
    private MessageSource msg;

    @Autowired
    private UserService userService;

    @GetMapping("/servers/keygen")
    public ResultMessage keygen() {
        return new ResultMessage(DefaultResult.OK, RandomUtils.generateRandomKey());
    }

    @PostMapping("/pages/reorder")
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

    @PostMapping("/pages/{page}/category/reorder")
    public ResultMessage reorderCategories(@PathVariable("page") String pageTag,
            @RequestParam("ids") String idsStr,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonatePage page = find(pages.findByTag(pageTag), loc);

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

    @PostMapping("/pages/{page}/category/{category}/reorder")
    public ResultMessage reorderPacks(@PathVariable("page") String pageTag,
            @PathVariable("category") int categoryId,
            @RequestParam("ids") String idsStr,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {

        DonateCategory category = find(categories.findById(categoryId), loc);
        DonatePage page = category.getPage();

        if(!page.getTag().equals(pageTag))
            throw notFound(loc);

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

    private List<Integer> getIds(String idsStr) {
        try {
            return Arrays.asList(idsStr.split(",")).stream()
                    .map(Integer::parseInt).collect(Collectors.toList());
        }catch(Exception ex) {}
        return Collections.emptyList();
    }

    private <T> T find(Optional<T> searchResult, Locale loc) {
        return searchResult.orElseThrow(()->notFound(loc));
    }

    private ResponseStatusException notFound(Locale loc) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                msg.getMessage("error.status.404", null, loc));
    }

    private void log(String langKey, HttpServletRequest request, Object... langValues) {
        logs.log(langKey, langValues, userService.getCurrentUser(), request.getRemoteAddr());
    }

    /*
     * Exception handlers
     */

    @ExceptionHandler(ResponseStatusException.class)
    public ResultMessage handleExcetion(ResponseStatusException ex, HttpServletResponse response) {
        response.setStatus(ex.getRawStatusCode());
        return new ResultMessage(DefaultResult.ERROR, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultMessage handleExcetion(Exception ex) {
        return new ResultMessage(DefaultResult.ERROR, ex.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultDataMessage<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((err) -> {
            String field = ((FieldError) err).getField();
            String msg = err.getDefaultMessage();
            errors.put(field, msg);
        });
        return new ValidationErrorMessage("Validation error", errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultMessage handleMissingParametersException(MissingServletRequestParameterException ex){
        return new ResultMessage(DefaultResult.ERROR, ex.getLocalizedMessage());
    }


}

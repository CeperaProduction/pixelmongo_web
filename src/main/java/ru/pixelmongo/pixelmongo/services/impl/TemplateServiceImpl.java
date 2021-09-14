package ru.pixelmongo.pixelmongo.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ru.pixelmongo.pixelmongo.model.dao.User;
import ru.pixelmongo.pixelmongo.model.dao.UserPermission;
import ru.pixelmongo.pixelmongo.model.dto.PaginationElement;
import ru.pixelmongo.pixelmongo.model.dto.PaginationElement.Step;
import ru.pixelmongo.pixelmongo.services.TemplateService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Service("templateService")
public class TemplateServiceImpl implements TemplateService{

    @Autowired
    private DateFormatter df;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource msg;

    public List<PaginationElement> getPagination(int currentPage,
            int totalPages, int maxButtons) {
        if(currentPage < 1) currentPage = 1;
        if(totalPages < 1) totalPages = 1;
        if(currentPage > totalPages) currentPage = totalPages;
        if(maxButtons < 3) maxButtons = 3;
        PaginationElement prev = new PaginationElement();
        prev.setStep(Step.back);
        if(currentPage == 1){
            prev.setDisabled(true);
        }else {
            prev.setPage((currentPage-1)+"");
        }
        PaginationElement next = new PaginationElement();
        next.setStep(Step.next);
        if(currentPage == totalPages){
            next.setDisabled(true);
        }else {
            next.setPage((currentPage+1)+"");
        }
        int startPage = currentPage-maxButtons/2+1;
        int endPage = currentPage+maxButtons/2-1;
        if(maxButtons % 2 == 0) --endPage;
        if(endPage > totalPages) {
            startPage += totalPages - endPage;
            endPage = totalPages;
        }
        if(startPage < 1) {
            endPage -= startPage - 1;
            startPage = 1;
        }
        if(endPage > totalPages) {
            endPage = totalPages;
        }

        PaginationElement first = null;
        PaginationElement last = null;
        if(maxButtons > 4) {
            if(startPage > 1) {
                first = new PaginationElement();
                first.setPage("1");
                ++startPage;
            }
            if(endPage < totalPages) {
                last = new PaginationElement();
                last.setPage(totalPages+"");
                --endPage;
            }
        }
        List<PaginationElement> ps = new ArrayList<>();
        ps.add(prev);
        if(first != null) ps.add(first);
        for(int i = startPage; i <= endPage; i++) {
            PaginationElement p = new PaginationElement();
            p.setPage(i+"");
            if(i == currentPage) p.setActive(true);;
            ps.add(p);
        }
        if(last != null) ps.add(last);
        ps.add(next);
        return ps;
    }

    @Override
    public String printDate(Date date, Locale locale) {
        return date != null ? df.print(date, locale) : "---";
    }

    @Override
    public String printUserRegisterDate(User user) {
        if(user != null)
            return printDate(user.getRegistrationDate());
        return "---";
    }

    @Override
    public String printUserLastLoginDate(User user) {
        if(user != null) {
            Date date = userService.getLastLogin(user).map(r->r.getDate()).orElse(null);
            return printDate(date);
        }
        return "---";
    }

    @Override
    public String printUserLastLoginIp(User user) {
        if(user != null) {
            return userService.getLastLogin(user).map(r->r.getIp()).orElse("---");
        }
        return "---";
    }

    @Override
    public String addParameterToCurrentUrl(String parameter, String value) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
        return builder.replaceQueryParam(parameter, value).toUriString();
    }

    @Override
    public String printPerm(UserPermission perm, Locale loc) {
        return msg.getMessage("permission."+perm.getValue(), null, perm.getValue(), loc);
    }

}
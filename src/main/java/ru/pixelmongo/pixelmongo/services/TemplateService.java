package ru.pixelmongo.pixelmongo.services;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;

import ru.pixelmongo.pixelmongo.model.dao.User;
import ru.pixelmongo.pixelmongo.model.dao.UserPermission;
import ru.pixelmongo.pixelmongo.model.dto.PaginationElement;

public interface TemplateService {

    /**
     * Generates list of {@link PaginationElement} using specified parameters
     *
     * @param currentPage
     * @param totalPages
     * @param maxButtons - Max count of buttons. Includes prev. page and next page buttons.
     * @return
     */
    public List<PaginationElement> getPagination(int currentPage,
            int totalPages, int maxButtons);

    /**
     * See {@link TemplateService#getPagination(int, int, int)}
     *
     */
    public default void addPagination(Model model, int currentPage,
            int totalPages, int maxButtons) {
        List<PaginationElement> ps = getPagination(currentPage, totalPages, maxButtons);
        model.addAttribute("pagination", ps);
    }

    public String printDate(Date date, Locale locale);

    /**
     * Print date using current user locale
     * @param date
     * @return
     */
    public default String printDate(Date date) {
        Locale loc = LocaleContextHolder.getLocale();
        return printDate(date, loc);
    }

    public String printUserRegisterDate(User user);

    public String printUserLastLoginDate(User user);

    public String printUserLastLoginIp(User user);

    public String addParameterToCurrentUrl(String parameter, String value);

    public String printPerm(UserPermission perm, Locale loc);

    public default String printPerm(UserPermission perm) {
        Locale loc = LocaleContextHolder.getLocale();
        return printPerm(perm, loc);
    }

}

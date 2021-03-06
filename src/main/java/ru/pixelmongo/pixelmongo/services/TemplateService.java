package ru.pixelmongo.pixelmongo.services;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;

import ru.pixelmongo.pixelmongo.model.dao.primary.Promocode;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.UserLoginRecord;
import ru.pixelmongo.pixelmongo.model.dao.primary.UserPermission;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.DonateDisplayType;
import ru.pixelmongo.pixelmongo.model.dao.primary.donate.tokens.DonatePackTokenType;
import ru.pixelmongo.pixelmongo.model.dto.PaginationElement;

public interface TemplateService {

    /**
     * Absolute application base URL containing protocol, domain and path.
     * @return
     */
    public String getAbsoluteUrlBase();

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

    public String printTokenType(DonatePackTokenType tokenType, Locale loc);

    public default String printTokenType(DonatePackTokenType tokenType) {
        Locale loc = LocaleContextHolder.getLocale();
        return printTokenType(tokenType, loc);
    }

    public String printDisplayType(DonateDisplayType displayType, Locale loc);

    public default String printDisplayType(DonateDisplayType displayType) {
        Locale loc = LocaleContextHolder.getLocale();
        return printDisplayType(displayType, loc);
    }

    public <T> List<List<T>> splitList(List<T> list, int count);

    public String getAvatar(User user);

    public String getAvatar();

    public String printLoginSource(UserLoginRecord record, Locale loc);

    public default String printLoginSource(UserLoginRecord record) {
        Locale loc = LocaleContextHolder.getLocale();
        return printLoginSource(record, loc);
    }

    public String printUserLastLoginSource(User user, Locale loc);

    public default String printUserLastLoginSource(User user) {
        Locale loc = LocaleContextHolder.getLocale();
        return printUserLastLoginSource(user, loc);
    }

    public String getRememberMeParam();

    public boolean isRememberMeAuto();

    public String printPromocodeEndDate(Promocode promo, Locale loc);

    public default String printPromocodeEndDate(Promocode promo){
        Locale loc = LocaleContextHolder.getLocale();
        return printPromocodeEndDate(promo, loc);
    }

}

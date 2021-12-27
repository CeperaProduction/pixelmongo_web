package ru.pixelmongo.pixelmongo.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.exceptions.DonateExtraNotFoundException;
import ru.pixelmongo.pixelmongo.exceptions.DonateNotEnoughMoneyException;
import ru.pixelmongo.pixelmongo.exceptions.EmailNotConfirmedException;
import ru.pixelmongo.pixelmongo.exceptions.PromocodeAlreadyUsedException;
import ru.pixelmongo.pixelmongo.exceptions.PromocodeException;
import ru.pixelmongo.pixelmongo.exceptions.PromocodeExpiredException;
import ru.pixelmongo.pixelmongo.exceptions.PromocodeMaxUsesException;
import ru.pixelmongo.pixelmongo.exceptions.PromocodeNotFoundException;
import ru.pixelmongo.pixelmongo.exceptions.WrongImageSizeException;
import ru.pixelmongo.pixelmongo.model.dao.primary.Promocode;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dao.primary.confirm.MailedConfirmationType;
import ru.pixelmongo.pixelmongo.model.dto.forms.SkinUploadForm;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.DonateService;
import ru.pixelmongo.pixelmongo.services.MailedConfirmationService;
import ru.pixelmongo.pixelmongo.services.PlayerSkinService;
import ru.pixelmongo.pixelmongo.services.PromocodeService;
import ru.pixelmongo.pixelmongo.services.UserService;

@RestController
@RequestMapping("/profile")
public class ProfileControllerRest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository users;

    @Autowired
    private PlayerSkinService skinService;

    @Autowired
    private MessageSource msg;

    @Autowired
    private DonateService donate;

    @Autowired
    private MailedConfirmationService confirms;

    @Autowired
    private PromocodeService promocodes;

    @PostMapping("/skin")
    public ResultMessage uploadSkin(@Valid SkinUploadForm skinForm, BindingResult binding, Locale loc) {
        if(skinForm == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        User user = userService.getCurrentUser();
        if(user.isAnonymous())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        Map<String, ResultMessage> uploadResults = new HashMap<String, ResultMessage>();
        if(skinForm.getSkin() != null && !skinForm.getSkin().isEmpty()) {
            Supplier<String> resultStatus;
            String resultMessage;
            if(binding.hasFieldErrors("skin")) {
                resultStatus = DefaultResult.ERROR;
                resultMessage = collectErrors("skin.skin.errors", binding.getFieldErrors(), loc);
            }else {
                try {
                    skinService.uploadSkin(skinForm.getSkin(), user, user.hasHDSkin());
                    resultStatus = DefaultResult.OK;
                    resultMessage = msg.getMessage("skin.skin.changed", null, loc);
                }catch(WrongImageSizeException ex) {
                    resultStatus = DefaultResult.ERROR;
                    resultMessage = msg.getMessage("skin.skin.wrong_size", null, loc);
                }
            }
            uploadResults.put("skin", new ResultMessage(resultStatus, resultMessage));
        }

        if(skinForm.getCape() != null && !skinForm.getCape().isEmpty()) {
            Supplier<String> resultStatus;
            String resultMessage;
            if(!user.hasCape()) {
                resultStatus = DefaultResult.ERROR;
                resultMessage = msg.getMessage("skin.cape.no_access", null, loc);
            }else if(binding.hasFieldErrors("cape")) {
                resultStatus = DefaultResult.ERROR;
                resultMessage = collectErrors("skin.cape.errors", binding.getFieldErrors(), loc);
            }else {
                try {
                    skinService.uploadCape(skinForm.getCape(), user, user.hasHDSkin());
                    resultStatus = DefaultResult.OK;
                    resultMessage = msg.getMessage("skin.cape.changed", null, loc);
                }catch(WrongImageSizeException ex) {
                    resultStatus = DefaultResult.ERROR;
                    resultMessage = msg.getMessage("skin.cape.wrong_size", null, loc);
                }
            }
            uploadResults.put("cape", new ResultMessage(resultStatus, resultMessage));
        }

        if(uploadResults.isEmpty()) {
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("skin.not_selected", null, loc));
        }else {
            return new ResultDataMessage<Map<String, ResultMessage>>(DefaultResult.OK, "Processed", uploadResults);
        }
    }

    @DeleteMapping("/skin")
    public ResultMessage deleteSkin(@RequestParam("target") String target, Locale loc) {
        User user = userService.getCurrentUser();
        if(user.isAnonymous())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        switch(target) {
        case "skin":
            skinService.deleteSkin(user);
            return new ResultMessage(DefaultResult.OK, msg.getMessage("skin.skin.deleted", null, loc));
        case "cape":
            skinService.deleteCape(user);
            return new ResultMessage(DefaultResult.OK, msg.getMessage("skin.cape.deleted", null, loc));
        default: throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/buy/{extra}")
    public ResultMessage buyExtra(@PathVariable("extra") String extra, Locale loc) {
        User user = userService.getCurrentUser();
        if(user.isAnonymous())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        user = getStoredUser(user);
        try {
            return donate.buyExtra(extra, user, loc, false);
        }catch(DonateExtraNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad extra tag", ex);
        }
    }

    @PostMapping("/mailconfirm")
    public ResultMessage confirmMail(Locale loc, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.getCurrentUser();
        if(user.isAnonymous())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if(user.isEmailConfirmed())
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("mconfirm.mailconfirm.already", null, loc));
        long await = confirms.checkForSpam(user, MailedConfirmationType.MAIL_CONFIRM, request);
        if(await > 0) {
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("mconfirm.error.spam",
                    new Object[] {confirms.printAwaitTime(await)}, loc));
        }
        confirms.sendConfirmation(MailedConfirmationType.MAIL_CONFIRM, user, loc, request, user.getEmail());
        return new ResultMessage(DefaultResult.OK, msg.getMessage("mconfirm.mailconfirm.send", null, loc));
    }

    @PostMapping("/promocode")
    public ResultMessage promocode(@RequestParam(name="code") String code, Locale loc,
            HttpServletRequest request, HttpServletResponse response) {
        User user = userService.getCurrentUser();
        if(user.isAnonymous())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        if(promocodes.isBlocked(user, request))
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("promocodes.spam", null, loc));

        user = getStoredUser(user);

        try {
            Promocode promocode = promocodes.activate(user, code);
            users.save(user);
            Map<String, Integer> balanceData = new HashMap<String, Integer>();
            balanceData.put("balance", user.getBalance());
            return new ResultDataMessage<Map<String, Integer>>(DefaultResult.OK, msg.getMessage("promocodes.activated",
                    new Object[] {promocode.getTitle(), promocode.getValue()}, loc), balanceData);
        }catch(EmailNotConfirmedException ex) {
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("promocodes.email.not_confirmed", null, loc));
        }catch(PromocodeNotFoundException ex) {
            promocodes.onPromocodeFail(user, request);
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("promocodes.wrong", null, loc));
        }catch(PromocodeAlreadyUsedException ex) {
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("promocodes.used", null, loc));
        }catch(PromocodeMaxUsesException ex) {
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("promocodes.used.max", null, loc));
        }catch(PromocodeExpiredException ex) {
            return new ResultMessage(DefaultResult.ERROR, msg.getMessage("promocodes.expired", null, loc));
        }catch(PromocodeException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    private User getStoredUser(User user) {
        if(user.getClass() == User.class) return user;
        return users.getById(user.getId());
    }

    private String collectErrors(String messageKey, Iterable<FieldError> errors, Locale loc) {
        List<String> errorStrings = new ArrayList<>();
        errors.forEach(e->errorStrings.add(e.getDefaultMessage()));
        return msg.getMessage(messageKey, new Object[] {String.join(" ", errorStrings)}, loc);
    }

    @ExceptionHandler
    public ResultMessage handleNoMoney(DonateNotEnoughMoneyException e, Locale loc) {
        return new ResultMessage(DefaultResult.ERROR, msg.getMessage("donate.no_money", null, loc));
    }

}

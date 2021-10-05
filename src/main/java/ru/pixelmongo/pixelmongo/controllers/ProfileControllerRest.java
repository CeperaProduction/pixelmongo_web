package ru.pixelmongo.pixelmongo.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

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
import ru.pixelmongo.pixelmongo.exceptions.WrongImageSizeException;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.forms.SkinUploadForm;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultDataMessage;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.UserRepository;
import ru.pixelmongo.pixelmongo.services.DonateService;
import ru.pixelmongo.pixelmongo.services.PlayerSkinService;
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

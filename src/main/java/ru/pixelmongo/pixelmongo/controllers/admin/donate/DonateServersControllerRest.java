package ru.pixelmongo.pixelmongo.controllers.admin.donate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.pixelmongo.pixelmongo.controllers.RestControllerExceptionHandler;
import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.utils.RandomUtils;

@RestController
@RequestMapping("/admin/donate/servers")
public class DonateServersControllerRest extends RestControllerExceptionHandler{

    @Autowired
    public DonateServersControllerRest(MessageSource msg) {
        super(msg);
    }

    @GetMapping("/keygen")
    public ResultMessage keygen() {
        return new ResultMessage(DefaultResult.OK, RandomUtils.generateRandomKey());
    }

}

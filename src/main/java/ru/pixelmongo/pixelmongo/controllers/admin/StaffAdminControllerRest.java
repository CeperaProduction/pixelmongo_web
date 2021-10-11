package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.pixelmongo.pixelmongo.model.dto.results.DefaultResult;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.StaffRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.OrdinaryUtilsService;
import ru.pixelmongo.pixelmongo.services.UserService;

@RestController
@RequestMapping("/admin/staff/ajax")
public class StaffAdminControllerRest {

    @Autowired
    private StaffRepository staffs;

    @Autowired
    private AdminLogService logs;

    @Autowired
    private UserService userService;

    @Autowired
    private OrdinaryUtilsService ordinary;

    @PostMapping("/reorder")
    public ResultMessage reorder(@RequestParam("ids") String idsStr,
            HttpServletRequest request,
            HttpServletResponse response) {
        List<Integer> ids = null;
        try {
            ids = Arrays.asList(idsStr.split(",")).stream()
                    .map(Integer::parseInt).collect(Collectors.toList());
        }catch(Exception ex) {}
        if(ids != null && ids.size() > 1) {
            int changed = ordinary.reorder(this.staffs, this.staffs.findAllOrdinaries(), ids);
            if(changed > 0) {
                logs.log("admin.log.staff.reorder", null,
                        userService.getCurrentUser(), request.getRemoteAddr());
            }
            return new ResultMessage(DefaultResult.OK, "Staffs reordered");
        }
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResultMessage(DefaultResult.ERROR, "Wrong ids");
    }

}

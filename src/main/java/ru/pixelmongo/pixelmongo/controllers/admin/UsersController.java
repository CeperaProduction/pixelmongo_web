package ru.pixelmongo.pixelmongo.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.pixelmongo.pixelmongo.model.entities.User;
import ru.pixelmongo.pixelmongo.model.entities.UserGroup;
import ru.pixelmongo.pixelmongo.repositories.UserGroupRepository;
import ru.pixelmongo.pixelmongo.repositories.UserRepository;
import ru.pixelmongo.pixelmongo.services.TemplateService;

@Controller
@RequestMapping("/admin/users")
public class UsersController {

    @Autowired
    private UserRepository users;

    @Autowired
    private UserGroupRepository groups;

    @Autowired
    private TemplateService templateService;

    @GetMapping
    public String userList(@RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer group,
            Model model) {
        UserGroup userGroup = null;
        if(group != null)
            userGroup = groups.findById(group).orElse(null);
        Pageable pageable = PageRequest.of(page-1, 50);
        Page<User> usersPage = this.users.findAllSorted(search, userGroup, pageable);
        templateService.addPagination(model, page, usersPage.getTotalPages(), 9);
        model.addAttribute("search", StringUtils.hasText(search) ? search : "");
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("users_count", usersPage.getTotalElements());
        model.addAttribute("groups", groups.findAll());
        model.addAttribute("group_selected", userGroup == null ? 0 : userGroup.getId());
        return "admin/users";
    }

}

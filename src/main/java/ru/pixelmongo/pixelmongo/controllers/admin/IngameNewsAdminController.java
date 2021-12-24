package ru.pixelmongo.pixelmongo.controllers.admin;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import ru.pixelmongo.pixelmongo.model.dao.primary.IngameNews;
import ru.pixelmongo.pixelmongo.model.dao.primary.IngameNewsChannel;
import ru.pixelmongo.pixelmongo.model.dao.primary.User;
import ru.pixelmongo.pixelmongo.model.dto.PopupMessage;
import ru.pixelmongo.pixelmongo.model.dto.forms.IngameNewsChannelForm;
import ru.pixelmongo.pixelmongo.model.dto.forms.IngameNewsForm;
import ru.pixelmongo.pixelmongo.repositories.primary.IngameNewsChannelsRepository;
import ru.pixelmongo.pixelmongo.repositories.primary.IngameNewsRepository;
import ru.pixelmongo.pixelmongo.services.AdminLogService;
import ru.pixelmongo.pixelmongo.services.PopupMessageService;
import ru.pixelmongo.pixelmongo.services.UserService;

@Controller
@RequestMapping("/admin/ingamenews")
public class IngameNewsAdminController {

    @Autowired
    private IngameNewsRepository news;

    @Autowired
    private IngameNewsChannelsRepository channels;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource msg;

    @Autowired
    private PopupMessageService popup;

    @Autowired
    private AdminLogService logs;

    @GetMapping
    public String news(Model model) {
        model.addAttribute("news", news.findAllByOrderByCreateDateDesc());
        return "admin/ingamenews/news";
    }

    @GetMapping("/channels")
    public String channels(Model model) {
        model.addAttribute("channels", channels.findAll());
        return "admin/ingamenews/channels";
    }

    @GetMapping("/new")
    public String newNews(Model model) {
        IngameNews news = new IngameNews();
        IngameNewsForm form = new IngameNewsForm();
        model.addAttribute("method", "put");
        model.addAttribute("news", news);
        model.addAttribute("newsForm", form);
        model.addAttribute("channels", channels.findAll());
        return "admin/ingamenews/news_edit";
    }

    @PutMapping("/new")
    public String newNews(Model model,
            @ModelAttribute("newsForm") @Valid IngameNewsForm form,
            BindingResult binding,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        if(!binding.hasErrors()) {
            User user = userService.getCurrentUser();
            IngameNewsChannel channel = findChannel(form.getChannel(), loc);
            IngameNews news = new IngameNews(channel, form.getTitle(), form.getText(), user);
            news = this.news.save(news);
            logs.log("admin.log.ingamenews.create", new Object[] {news.getTitle()+" #"+news.getId()},
                    user, request.getRemoteAddr());
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.ingamenews.created", null, loc),
                            PopupMessage.Type.OK),
                    request, response);
            return "redirect:/admin/ingamenews/"+news.getId();
        }
        model.addAttribute("method", "put");
        model.addAttribute("news", new IngameNews());
        model.addAttribute("channels", channels.findAll());
        return "admin/ingamenews/news_edit";
    }

    @GetMapping("/{id}")
    public String editNews(@PathVariable(name = "id") int newsId, Model model, Locale loc) {
        IngameNews news = findNews(newsId, loc);
        IngameNewsForm form = new IngameNewsForm(news);
        model.addAttribute("method", "post");
        model.addAttribute("news", news);
        model.addAttribute("newsForm", form);
        model.addAttribute("channels", channels.findAll());
        return "admin/ingamenews/news_edit";
    }

    @PostMapping("/{id}")
    public String editNews(@PathVariable(name = "id") int newsId, Model model,
            @ModelAttribute("newsForm") @Valid IngameNewsForm form,
            BindingResult binding,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        IngameNews news = findNews(newsId, loc);
        if(!binding.hasErrors()) {
            User user = userService.getCurrentUser();
            IngameNewsChannel channel = findChannel(form.getChannel(), loc);
            news.setTitle(form.getTitle());
            news.setText(form.getText());
            news.setChannel(channel);
            news.setUpdateDate(new Date());
            news = this.news.save(news);
            logs.log("admin.log.ingamenews.edit", new Object[] {news.getTitle()+" #"+news.getId()},
                    user, request.getRemoteAddr());
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.ingamenews.edited", null, loc),
                            PopupMessage.Type.OK),
                    request, response);
            return "redirect:/admin/ingamenews/"+news.getId();
        }
        model.addAttribute("method", "post");
        model.addAttribute("news", news);
        model.addAttribute("channels", channels.findAll());
        return "admin/ingamenews/news_edit";
    }

    @DeleteMapping("/{id}")
    public String deleteNews(@PathVariable(name = "id") int newsId, Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        IngameNews news = findNews(newsId, loc);
        User user = userService.getCurrentUser();
        this.news.delete(news);
        logs.log("admin.log.ingamenews.delete", new Object[] {news.getTitle()+" #"+news.getId()},
                user, request.getRemoteAddr());
        popup.sendUsingCookies(
                new PopupMessage(
                        msg.getMessage("admin.ingamenews.deleted", null, loc),
                        PopupMessage.Type.WARN),
                request, response);
        return "redirect:/admin/ingamenews";
    }

    @GetMapping("/channels/new")
    public String newChannel(Model model) {
        IngameNewsChannel channel = new IngameNewsChannel();
        IngameNewsChannelForm form = new IngameNewsChannelForm();
        model.addAttribute("method", "put");
        model.addAttribute("channel", channel);
        model.addAttribute("channelForm", form);
        return "admin/ingamenews/channel_edit";
    }

    @PutMapping("/channels/new")
    public String newChannel(Model model,
            @ModelAttribute("channelForm") @Valid IngameNewsChannelForm form,
            BindingResult binding,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        if(!binding.hasErrors()) {
            User user = userService.getCurrentUser();
            IngameNewsChannel channel = new IngameNewsChannel(form.getName());
            channel = this.channels.save(channel);
            logs.log("admin.log.ingamenews.channel.create", new Object[] {
                    channel.getName()+" #"+channel.getId()}, user, request.getRemoteAddr());
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.ingamenews.channel.created", null, loc),
                            PopupMessage.Type.OK),
                    request, response);
            return "redirect:/admin/ingamenews/channels/"+channel.getId();
        }
        model.addAttribute("method", "put");
        model.addAttribute("channel", new IngameNewsChannelForm());
        return "admin/ingamenews/channel_edit";
    }

    @GetMapping("/channels/{id}")
    public String editChannel(@PathVariable(name = "id") int channelId, Model model, Locale loc) {
        IngameNewsChannel channel = findChannel(channelId, loc);
        IngameNewsChannelForm form = new IngameNewsChannelForm(channel);
        model.addAttribute("method", "post");
        model.addAttribute("channel", channel);
        model.addAttribute("channelForm", form);
        return "admin/ingamenews/channel_edit";
    }

    @PostMapping("/channels/{id}")
    public String editChannel(@PathVariable(name = "id") int channelId, Model model,
            @ModelAttribute("channelForm") @Valid IngameNewsChannelForm form,
            BindingResult binding,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        IngameNewsChannel channel = findChannel(channelId, loc);
        if(!binding.hasErrors()) {
            User user = userService.getCurrentUser();
            channel.setName(form.getName());
            channel = this.channels.save(channel);
            logs.log("admin.log.ingamenews.channel.edit", new Object[] {
                    channel.getName()+" #"+channel.getId()}, user, request.getRemoteAddr());
            popup.sendUsingCookies(
                    new PopupMessage(
                            msg.getMessage("admin.ingamenews.channel.edited", null, loc),
                            PopupMessage.Type.OK),
                    request, response);
            return "redirect:/admin/ingamenews/channels/"+channel.getId();
        }
        model.addAttribute("method", "post");
        model.addAttribute("channel", channel);
        return "admin/ingamenews/channel_edit";
    }

    @DeleteMapping("/channels/{id}")
    public String deleteChannel(@PathVariable(name = "id") int channelId, Model model,
            Locale loc,
            HttpServletRequest request,
            HttpServletResponse response) {
        IngameNewsChannel channel = findChannel(channelId, loc);
        User user = userService.getCurrentUser();
        this.channels.delete(channel);
        logs.log("admin.log.ingamenews.channel.delete", new Object[] {
                channel.getName()+" #"+channel.getId()}, user, request.getRemoteAddr());
        popup.sendUsingCookies(
                new PopupMessage(
                        msg.getMessage("admin.ingamenews.channel.deleted", null, loc),
                        PopupMessage.Type.WARN),
                request, response);
        return "redirect:/admin/ingamenews/channels";
    }

    private IngameNews findNews(int id, Locale loc) {
        return news.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.ingamenews", null, loc)));
    }

    private IngameNewsChannel findChannel(int id, Locale loc) {
        return channels.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        msg.getMessage("error.status.404.ingamenews.channel", null, loc)));
    }

    @ModelAttribute
    public void applyMode(Model model) {
        model.addAttribute("mode", "ingamenews");
    }

}

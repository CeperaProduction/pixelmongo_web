package ru.pixelmongo.pixelmongo.controllers.open;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.pixelmongo.pixelmongo.model.dao.primary.IngameNews;
import ru.pixelmongo.pixelmongo.model.dto.results.ResultMessage;
import ru.pixelmongo.pixelmongo.repositories.primary.IngameNewsRepository;

@RestController
@RequestMapping("/open/ingamenews")
public class IngameNewsController {

    @Autowired
    private IngameNewsRepository news;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public ResultMessage processRequest(@RequestParam(name = "action") String action,
            @RequestParam(name = "category", required = false) List<String> categories) {
        switch(action) {
        case "check": return new ResultMark(Result.SUCCESS, "Processed",
                news.getLastUpdate().orElseGet(()->new Date(0)));
        case "fetch":
            if(categories == null) categories = Collections.emptyList();
            return new ResultNews(Result.SUCCESS, "Processed",
                    news.getLastUpdate().orElseGet(()->new Date(0)),
                    news.getNewsInChannels(categories).stream()
                        .map(News::new).collect(Collectors.toList()));
        default: return new ResultMessage(Result.ERROR, "Wrong action");
        }
    }

    private enum Result implements Supplier<String>{
        SUCCESS, ERROR;

        @Override
        public String get() {
            return name().toLowerCase();
        }

    }

    public static class News {

        private String title, text, category, author;
        private int time, updated;

        public News(IngameNews news) {
            this.title = news.getTitle();
            this.text = news.getText();
            this.category = news.getChannel().getName();
            this.author = news.getAuthorName();
            this.time = (int) (news.getCreateDate().getTime()/1000L);
            this.updated = (int) (news.getUpdateDate().getTime()/1000L);
        }

        public String getTitle() {
            return title;
        }

        public String getText() {
            return text;
        }

        public String getCategory() {
            return category;
        }

        public String getAuthor() {
            return author;
        }

        public int getTime() {
            return time;
        }

        public int getUpdated() {
            return updated;
        }

    }

    public static class ResultMark extends ResultMessage {

        private int mark;

        public ResultMark(Result result, String message, Date mark) {
            super(result, message);
            this.mark = (int) (mark.getTime()/1000L);
        }

        public int getMark() {
            return mark;
        }

    }

    public static class ResultNews extends ResultMark {

        private List<News> news;

        public ResultNews(Result result, String message, Date mark, List<News> news) {
            super(result, message, mark);
            this.news = news;
        }

        public List<News> getNews() {
            return news;
        }

    }

}

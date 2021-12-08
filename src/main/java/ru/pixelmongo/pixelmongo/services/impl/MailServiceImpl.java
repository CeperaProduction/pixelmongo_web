package ru.pixelmongo.pixelmongo.services.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import ru.pixelmongo.pixelmongo.services.MailService;

public class MailServiceImpl implements MailService{

    @Autowired
    private JavaMailSender mail;

    @Autowired
    private SpringTemplateEngine templateEngine;

    private final String systemEmail;

    public MailServiceImpl(String systemEmail) {
        this.systemEmail = systemEmail;
    }

    @Override
    public void sendTemplatedMail(String email, String title, String template,
            Map<String, Object> context, MessagePostProcessor mailPostProcessor) throws MessagingException, IOException{
        MimeMessage message = mail.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        Context ctx = new Context();
        if(context != null)
            ctx.setVariables(context);

        String html = templateEngine.process(template, ctx);
        helper.setTo(email);
        helper.setSubject(title);
        helper.setText(html, true);
        helper.setFrom(systemEmail);

        if(mailPostProcessor != null)
            mailPostProcessor.process(helper);

        mail.send(message);
    }

}

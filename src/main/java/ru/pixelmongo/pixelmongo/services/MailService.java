package ru.pixelmongo.pixelmongo.services;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import javax.mail.MessagingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.MimeMessageHelper;

public interface MailService {

    public static final Logger LOGGER = LogManager.getLogger(MailService.class);

    public void sendTemplatedMail(String email, String title, String template,
            Map<String, Object> context, MessagePostProcessor mailPostProcessor) throws MessagingException, IOException;

    public static interface MessagePostProcessor extends Consumer<MimeMessageHelper>{

        @Override
        default void accept(MimeMessageHelper t) {
            try {
                process(t);
            }catch (MessagingException | IOException e) {
                LOGGER.catching(e);
            }
        }

        public void process(MimeMessageHelper helper) throws MessagingException, IOException;

    }

}

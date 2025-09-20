package com.teleeza.wallet.teleeza.rewarded_ads.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;

@Component
public class SendEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) {
        boolean sendMail = false;

        try {
            final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            final MimeMessageHelper message; // true = multipart
            message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setSubject(subject);
            message.setFrom("no-reply@teleeza.africa", "Teleeza");
            message.setTo(to);
            message.setText(body);

            // send mail
            this.javaMailSender.send(mimeMessage);
            sendMail = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

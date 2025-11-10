package com.shyamSofttech.studentManagement.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Slf4j
public class CustomMailSender {
    /*using following method for creating mail sender for user dynamically but
    * use password from App generated Password
    * use port and host from mail server
    * Use as A Util in whole project
    * You DON"T need setup application.yml for this*/

    public static JavaMailSender createJavaMailSender(String host, int port, String username, String password) {
        log.info("Inside createJavaMailSender of CustomMailSender");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        log.info("Use the generated App Password only");
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}

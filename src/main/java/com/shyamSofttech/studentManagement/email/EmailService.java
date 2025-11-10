package com.shyamSofttech.studentManagement.email;

import com.shyamSofttech.studentManagement.constant.ApiErrorCodes;
import com.shyamSofttech.studentManagement.exception.NoSuchElementFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {
    @Autowired
    private  JavaMailSender javaMailSender;
//    @Value("${mail.senderEmail}")
//    public String outlookEmail;
    public void sendPasswordResetEmail(String toEmail, String userName) {
        try {
            String subject = "Password Reset Notification";
            String messageBody = "Dear " + userName + ",\n\n" + "Your password has been successfully reset.";
            sendEmail(toEmail, subject, messageBody);
        } catch (Exception e) {
            throw new NoSuchElementFoundException(ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorCode(), ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorMessage());
        }
    }

    public void sendEmail(String toEmail, String subject, String msg) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(msg);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new NoSuchElementFoundException(ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorCode(), ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorMessage());
        }
    }
    public void sendEmailOtp(String otp, String toEmail){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Email verification");
            helper.setText("Verification code for your account is " + otp);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new NoSuchElementFoundException(ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorCode(), ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorMessage());
        }
    }

    public void sendEmailWithAttachment(String subjectName, File attachment, String toEmail) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject(subjectName);
            helper.setText("Please find attached report.");
            helper.addAttachment(subjectName + ".pdf", attachment);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new NoSuchElementFoundException(ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorCode(), ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorMessage());
        }
    }

    public void sendEmailWithImages(String subjectName, File[] imageFiles, String toEmail, String redirectUrl) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject(subjectName);

            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<h2 style='color:#2e6c80;'>Your Product Summary</h2>");
            htmlContent.append("<p>Here are the latest products from your order/report:</p>");

            for (File image : imageFiles) {
                htmlContent.append("<div style='margin-bottom:15px;'>")
                        .append("<img src='cid:")
                        .append(image.getName())
                        .append("' style='max-width:300px;border-radius:10px;box-shadow:2px 2px 6px #aaa;'>")
                        .append("</div>");
                helper.addInline(image.getName(), image);
            }

            htmlContent.append("<p><a href='")
                    .append(redirectUrl)
                    .append("' style='background:#007bff;color:white;padding:10px 15px;text-decoration:none;border-radius:5px;'>")
                    .append("View More on Website</a></p>");

            helper.setText(htmlContent.toString(), true); // true = HTML email

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new NoSuchElementFoundException(
                    ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorCode(),
                    ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorMessage());
        }
    }
    public void sendEmailWithImagesAndPdf(String subject, String htmlBody, File[] imageFiles, File pdfFile, String toEmail) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // enable HTML

            // Attach product images (max 4)
            if (imageFiles != null) {
                for (int i = 0; i < imageFiles.length && i < 4; i++) {
                    helper.addAttachment("product_" + (i + 1) + ".jpg", imageFiles[i]);
                }
            }

            // Attach PDF report
            if (pdfFile != null) {
                helper.addAttachment("stale_items_report.pdf", pdfFile);
            }

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new NoSuchElementFoundException(
                    ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorCode(),
                    ApiErrorCodes.ERROR_WHILE_SENDING_EMAIL.getErrorMessage()
            );
        }

    }



}

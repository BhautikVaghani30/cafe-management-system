package com.cafe.com.cafe.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtils {
    private static JavaMailSender emailSender = null;

    public EmailUtils(JavaMailSender emailSender) {
        EmailUtils.emailSender = emailSender;
    }

    public void sendSimpleMessage(String to, String subject, String text, List<String> list) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("codewithbhautik01@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        if (list != null && list.size() > 0) {
            helper.setCc(getCcArray(list));
        }

        helper.setText(text, true); // Set HTML content

        emailSender.send(message);
    }

    private String[] getCcArray(List<String> ccList) {
        String[] cc = new String[ccList.size()];

        for (int i = 0; i < ccList.size(); i++) {
            cc[i] = ccList.get(i);
        }
        return cc;
    }

    

    public void forgotMail(String to, String subject, String password) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage(); // mime messages
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("codewithbhautik01@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String htmlMsg = "<h1>Account Registration Confirmation</h1>" +
                "<p>Dear User,</p>" +
                "<p>We're writing to confirm that you've recently request for your registration password related your account. Here's your account information:</p>"
                +
                "<ul> <li>Email: " + to + "</li> <li>Password:" + password + "</li> </ul>" +
                "<p>Please keep this information confidential and do not share it with anyone. If you need to change your password in the future, you can do so by logging into your account.</p>"
                +
                "<p>If you have any questions or concerns, please don't hesitate to contact us.</p>" +
                "<p>Best regards,</p><p>Bhautik Vaghani<br>Namaste Cafe</p>"
                + "<br><a href=\"http://localhost:4200/\" style=\"display: inline-block; padding: 10px 20px; font-size: 16px; text-align: center; text-decoration: none; background-color: #4CAF50; color: #fff; border-radius: 5px; border: 2px solid #4CAF50; transition: background-color 0.3s;\">\r\n" + //
                                        "    Click here to login\r\n" + //
                                        "</a></p>";

        message.setContent(htmlMsg, "text/html");
        emailSender.send(message);
    }
    public void emailOTP(String to, String subject, int otp) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage(); // mime messages
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("codewithbhautik01@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String emailContent = "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;\">\n" +
        "  <div  style=\"width: 100%; max-width: 100%; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">\n" +
        "    <h1 style=\"color: #333;\">Your One-Time Password (OTP) for Sign-Up Verification</h1>\n" +
        "    <p>Dear " + to + ",</p>\n" +
        "    <p>Thank you for signing up with <strong>Namaste Village</strong>. To complete your registration, please use the following One-Time Password (OTP) to verify your email address:</p>\n" +
        "    <p style=\"font-size: 24px; font-weight: bold;\">OTP: " + otp + "</p>\n" +
        "    <p>Please enter this OTP on the sign-up page to verify your email address and complete your registration process.</p>\n" +
        "    <p>If you did not attempt to sign up for <strong>Namaste Village</strong>, please disregard this email.</p>\n" +
        "    <p>Thank you,<br>Namaste Village Team</p>\n" +
        "  </div>\n" +
        "</body>";

        message.setContent(emailContent, "text/html");
        emailSender.send(message);
    }

    public static Boolean sendBill(String to, String subject, File file) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("codewithbhautik01@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        
        // Read PDF file content and encode it as Base64
        byte[] fileContent = Files.readAllBytes(file.toPath());
        String base64EncodedPDF = Base64.getEncoder().encodeToString(fileContent);
        
        // Embed the Base64-encoded PDF content within the email body
        String emailContent = "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;\">\n" +
                "  <div  style=\"width: 100%; max-width: 100%; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">\n" +
                "    <h1 style=\"color: #333;\">Your One-Time Password (OTP) for Sign-Up Verification</h1>\n" +
                "    <p>Dear " + to + ",</p>\n" +
                "    <p>Thank you for signing up with <strong>Namaste Village</strong>. To complete your registration, please use the following One-Time Password (OTP) to verify your email address:</p>\n" +
                "    <embed width='500' height='400' src='data:application/pdf;base64," + base64EncodedPDF + "' type='application/pdf' />\n" +
                "    <p>Please enter this OTP on the sign-up page to verify your email address and complete your registration process.</p>\n" +
                "    <p>If you did not attempt to sign up for <strong>Namaste Village</strong>, please disregard this email.</p>\n" +
                "    <p>Thank you,<br>Namaste Village Team</p>\n" +
                "  </div>\n" +
                "</body>";
        
        // Set the email content and attach the PDF file
        helper.setText(emailContent, true);
        helper.addAttachment(file.getName(), file);
        
        emailSender.send(message);
        return true;
    }
    
}

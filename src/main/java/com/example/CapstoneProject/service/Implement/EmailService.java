package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.service.Interface.IEmailService;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void sendEmail(String to, String subject, String content) {
        if (to == null || to.isEmpty() || subject == null || subject.isEmpty() || content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Email parameters cannot be null or empty.");
        }
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("vunguyen.17082003@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            content = content.replace("\n", "<br>");
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

}

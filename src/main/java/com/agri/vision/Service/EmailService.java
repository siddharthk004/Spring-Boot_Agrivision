package com.agri.vision.Service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends an email with the specified OTP to the recipient.
     *
     * @param to  the recipient email address
     * @param otp the OTP to include in the email
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
            return true;
        } catch (Exception e) {
            System.err.println("Error while sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendEmailWithAttachment(String to, String subject, String message, File pdf) {
        try {
            // Create a new MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // Use MimeMessageHelper to handle attachments
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true); // true enables HTML content

            // Attach the PDF file
            if (pdf != null && pdf.exists()) {
                FileSystemResource file = new FileSystemResource(pdf);
                helper.addAttachment(pdf.getName(), file);
            }

            // Send the email
            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            System.err.println("Error while sending email with attachment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

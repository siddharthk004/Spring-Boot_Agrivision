package com.agri.vision.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
    public boolean sendEmail(String to,String subject, String message) {
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
}

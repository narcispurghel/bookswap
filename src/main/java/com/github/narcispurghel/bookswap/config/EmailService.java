package com.github.narcispurghel.bookswap.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Service
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    private final ExecutorService emailExecutor =
            Executors.newFixedThreadPool(5); // Max 5 thread-uri concurente

    private final Semaphore semaphore = new Semaphore(10); // Max 10 emailuri simultan

    public void sendEmailAsync(String to, String subject, String body) {
        emailExecutor.submit(() -> {
            try {
                semaphore.acquire();
                sendEmail(to, subject, body);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.release();
            }
        });
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("your-email@gmail.com");

            mailSender.send(message);
        } catch (Exception e) {
            // Log eroarea și eventual retry
        }
    }
}

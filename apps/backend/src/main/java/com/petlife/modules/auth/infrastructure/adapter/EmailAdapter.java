package com.petlife.modules.auth.infrastructure.adapter;

import com.petlife.modules.auth.domain.port.EmailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verifique sua conta PetLife");
        message.setText("Use o link a seguir para verificar sua conta: " +
            "https://petlife.com/verify?token=" + token);
        mailSender.send(message);
    }
}

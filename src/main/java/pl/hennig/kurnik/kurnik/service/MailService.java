package pl.hennig.kurnik.kurnik.service;

import pl.hennig.kurnik.kurnik.model.Token;

import javax.mail.MessagingException;

public interface MailService {

    void sendTokenMessage(Token token, String mail) throws MessagingException;

}

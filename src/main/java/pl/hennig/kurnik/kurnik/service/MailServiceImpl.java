package pl.hennig.kurnik.kurnik.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.hennig.kurnik.kurnik.model.Token;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
@Service
public class MailServiceImpl implements MailService{
    private JavaMailSender mailSender;
    @Autowired
    public MailServiceImpl(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }
    @Override
    public void sendTokenMessage(Token token, String mail) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        String link;
        String txt = null;
        if (token.getPurpose() == Token.Purpose.REGISTRATION) {
            link = "https://kurnikdlabiednychludzi.herokuapp.com/active?token=" + token.getToken();
            txt = "<h1>Aktywuj swoje konto używając tego linku</h1><br><a href=\"" + link + "\">Activate</a>";
        } else if (token.getPurpose() == Token.Purpose.FORGOT) {
            link = "https://kurnikdlabiednychludzi.herokuapp.com/reset?token=" + token.getToken();
            txt = "<h1>Zresetuj hasło używając tego linku</h1><br><a href=\"" + link + "\">Reset</a>";
        } else {
            return;
        }
        helper.setTo(mail);
        helper.setSubject("Witaj użytkowniku");
        helper.setText(txt, true);
        mailSender.send(msg);
    }
}

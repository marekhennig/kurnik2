package pl.hennig.kurnik.kurnik.gui;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import pl.hennig.kurnik.kurnik.model.Token;
import pl.hennig.kurnik.kurnik.model.User;
import pl.hennig.kurnik.kurnik.repository.TokenRepo;
import pl.hennig.kurnik.kurnik.repository.UserRepo;
import pl.hennig.kurnik.kurnik.service.MailService;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;


@Route("forgot")
public class PassForgotView extends VerticalLayout {

    private UserRepo userRepo;
    private MailService mailService;
    private TokenRepo tokenRepo;

    @Autowired
    public PassForgotView(UserRepo userRepo, MailService mailService, TokenRepo tokenRepo) {
        MenuGui menuGui = new MenuGui();
        add(menuGui.getMenuBarNotLogged());
        this.userRepo = userRepo;
        this.mailService = mailService;
        this.tokenRepo = tokenRepo;
        Binder<User> binder = new Binder<>(User.class);
        Label label = new Label("Password reset");
        EmailField emailField = new EmailField("Email");
        binder.forField(emailField)
                .withValidator(new RegexpValidator("^([a-zA-Z0-9_\\.\\-+])+@[a-zA-Z0-9-.]+\\.[a-zA-Z0-9-]{2,}$", "This is not email"))
                .bind(User::getEmail, User::setEmail);

        Button button = new Button("Verify");
        button.addClickListener(buttonClickEvent -> {
            if (emailValidator(emailField.getValue())) {
                Optional<User> user = userRepo.findUserByEmail(emailField.getValue());
                if (user.isPresent()) {
                    Token token = new Token(user.get(), LocalDateTime.now(), LocalDateTime.now().plusHours(1), Token.Purpose.FORGOT);
                    token.generateToken();
                    tokenRepo.save(token);
                    try {
                        mailService.sendTokenMessage(token, emailField.getValue());
                    } catch (MessagingException e) {
                        Notification notification = new Notification("We couldn't send an email");
                        notification.open();
                        UI.getCurrent().getPage().reload();
                        System.out.println("sadzas");
                    }
                    UI.getCurrent().navigate("login");

                } else {
                    Notification notification = new Notification("There is no user with that username", 3000);
                    notification.open();
                }
            }
        });
        add(label, emailField, button);
    }

    private Boolean emailValidator(String email) {
        if (email.matches("^([a-zA-Z0-9_\\.\\-+])+@[a-zA-Z0-9-.]+\\.[a-zA-Z0-9-]{2,}$")) {
            return true;
        }
        return false;
    }


}
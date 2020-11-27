package pl.hennig.kurnik.kurnik.gui;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.hennig.kurnik.kurnik.model.Token;
import pl.hennig.kurnik.kurnik.model.User;
import pl.hennig.kurnik.kurnik.repository.TokenRepo;
import pl.hennig.kurnik.kurnik.repository.UserRepo;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Route("reset")
public class PassResetView extends VerticalLayout implements HasUrlParameter<String> {
    private User user;
    private TokenRepo tokenRepo;
    private UserRepo userRepo;
    private Binder<User> binder = new Binder<>();
    private PasswordField passwordField = new PasswordField("New password");
    private PasswordField repasswordField = new PasswordField("Repeat new password");
    private PasswordEncoder passwordEncoder;
    @Autowired
    public PassResetView(TokenRepo tokenRepo, PasswordEncoder passwordEncoder, UserRepo userRepo) {
        MenuGui menuGui = new MenuGui();
        add(menuGui.getMenuBarLogged());
        this.passwordEncoder = passwordEncoder;
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
    }
    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> map = queryParameters.getParameters();
        if (map.isEmpty()) {
            try {
                this.user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                addLayout();
            } catch (Exception e) {
                UI.getCurrent().navigate("logout");
            }
        } else {
            String token = map.get("token").get(0);
            Optional<Token> userToken = tokenRepo.findTokenByToken(token);
            if (userToken.isPresent()) {
                if (userToken.get().getPurpose() == Token.Purpose.FORGOT) {
                    this.user = userToken.get().getUser();
                    addLayout();
                }
            } else {
                Notification notification = new Notification("We couldn't find user ", 3000);
                notification.open();
            }
        }
    }
    public void addLayout() {
        H1 header = new H1("Hello " + this.user.getUsername());
        header.getStyle().set("color", "#00688B");
        Label label = new Label("Password reset");
        binder.forField(passwordField)
                .withValidator(new RegexpValidator("Password should contain lowercase and uppercase letters and digits and be at least 8 characters long", "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,32}$"))
                .bind(User::getPassword, User::setPassword);
        binder.forField(repasswordField)
                .withValidator(this::rePasswordValidator, "Passwords should match")
                .bind(User::getPassword, User::setPassword);
        Button button = new Button("Submit");
        button.addClickListener(buttonClickEvent -> {
            if (passwordValidator(passwordField.getValue()) && rePasswordValidator(repasswordField.getValue())) {
                this.user.setPassword(passwordEncoder.encode(passwordField.getValue()));
                userRepo.save(this.user);
                UI.getCurrent().navigate("logout");
            }
            ;
        });
        add(header, label, passwordField, repasswordField, button);
    }
    private Boolean rePasswordValidator(String password) {
        if (password.equals(passwordField.getValue())) {
            return true;
        }
        return false;
    }
    private Boolean passwordValidator(String password) {
        if (password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,32}$")) {

            return true;
        }
        return false;
    }
}
package pl.hennig.kurnik.kurnik.gui;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.hennig.kurnik.kurnik.model.Token;
import pl.hennig.kurnik.kurnik.model.User;
import pl.hennig.kurnik.kurnik.repository.TokenRepo;
import pl.hennig.kurnik.kurnik.repository.UserRepo;
import pl.hennig.kurnik.kurnik.service.MailService;
import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;


@Route("registration")
public class RegistrationGui extends VerticalLayout {

    private UserRepo userRepo;
    private MailService mailService;
    private Binder<User> binder = new Binder<>();
    private PasswordField passwordField, rePasswordField;
    private TokenRepo tokenRepo;
    private Label usernameInDatabase = new Label();
    private Label emailInDatabase = new Label();
    private PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationGui(UserRepo userRepo, MailService mailService, TokenRepo tokenRepo, PasswordEncoder passwordEncoder) {
        MenuGui menuGui = new MenuGui();
        add(menuGui.getMenuBarNotLogged());
        this.userRepo = userRepo;
        this.mailService = mailService;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        Label label = new Label("There was an error");
        label.setVisible(false);
        TextField textFieldUsername = usernameField();
        TextField emailField = emailField() ;

        this.passwordField = passwordField();
        this.rePasswordField = rePasswordField();
        Button buttonSubmit = new Button("Submit");

        buttonSubmit.addClickListener(event->
        {
            usernameInDatabase.setVisible(false);
            emailInDatabase.setVisible(false);
            if(isValid(textFieldUsername.getValue(),emailField.getValue(),passwordField.getValue(),rePasswordField.getValue())) {
                try {
                    saveUser(textFieldUsername.getValue(), emailField.getValue(), passwordField.getValue());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });

        add(textFieldUsername,emailField,this.passwordField,this.rePasswordField,buttonSubmit,usernameInDatabase,emailInDatabase);

    }

    private TextField usernameField(){
        TextField textField = new TextField("Username");
        textField.setPlaceholder("Username");
        textField.setRequired(true);
        binder.forField(textField)
                .withValidator(
                        new RegexpValidator("Username should be at least 3 characters long","^(?=\\S+$).{3,32}"))
                .bind(User::getPassword,User::setPassword);
        return textField;

    }

    private TextField emailField(){
        TextField textField = new TextField("Email");
        textField.setPlaceholder("Email");
        textField.setRequired(true);
        binder.forField(textField)
                .withValidator(
                        new RegexpValidator("Invalid email","\t\n" +
                                "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))
                .bind(User::getPassword,User::setPassword);
        return textField;

    }

    private PasswordField passwordField(){
        PasswordField passwordField = new PasswordField();
        passwordField.setLabel("Password");
        passwordField.setPlaceholder("Enter password");
        passwordField.setRequired(true);

        binder.forField(passwordField)
                .withValidator(new RegexpValidator("Password should contain lowercase and uppercase letters and digits and be at least 8 characters long","^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,32}$"))
                .bind(User::getPassword,User::setPassword);

        return passwordField;

    }

    private PasswordField rePasswordField(){
        PasswordField passwordField = new PasswordField();
        passwordField.setLabel("Password");
        passwordField.setPlaceholder("Enter password");
        passwordField.setRequired(true);
        binder.forField(passwordField)
                .withValidator(this::rePasswordValidator,"Passwords should match")
                .bind(User::getPassword,User::setPassword);
        add(passwordField,passwordField);
        return passwordField;
    }


    private Boolean passwordValidator(String password){

        if(password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,32}$")){

            return true;
        }
        return false;
    }

    private Boolean rePasswordValidator(String password){

        if(password.equals(passwordField.getValue())){
            return true;
        }
        return false;
    }
    private Boolean usernameValidator(String username){
        if(username.matches("^(?=\\S+$).{3,32}")){

            return true;
        }
        return false;
    }

    private Boolean emailValidator(String email){
        if(email.matches("^([a-zA-Z0-9_\\.\\-+])+@[a-zA-Z0-9-.]+\\.[a-zA-Z0-9-]{2,}$")){
            return true;
        }
        return false;
    }

    private void saveUser(String username,String email, String password) throws MessagingException {


        User user = new User(username, email, passwordEncoder.encode(password), "User", false);

        Token userToken = new Token(user, LocalDateTime.now(), LocalDateTime.now().plusHours(24), Token.Purpose.REGISTRATION);
        userToken.generateToken();

        mailService.sendTokenMessage(userToken, user.getEmail());
        userRepo.save(user);
        tokenRepo.save(userToken);
    }

    private boolean emailInDataBaseValidator(String email){
        Optional<User> user = userRepo.findUserByEmail(email);
        if (user.isPresent()){
            emailInDatabase.setText("There is already user with that email");
            emailInDatabase.setVisible(true);
            return false;
        }
        return true;
    }

    private boolean usernameInDataBaseValidator(String username){
        Optional<User> user = userRepo.findUserByUsername(username);

        if (user.isPresent()){
            usernameInDatabase.setText("There is already user with that username");
            usernameInDatabase.setVisible(true);
            return false;
        }
        return true;
    }

    public Boolean isValid(String username, String email, String password, String rePassword){
        boolean emailindb = emailInDataBaseValidator(email);
        boolean usernameindb =  usernameInDataBaseValidator(username);
        if(passwordValidator(password) && rePasswordValidator(rePassword) && usernameValidator(username)&&emailValidator(email)&& emailindb && usernameindb ){

            return true;
        }

        return false;
    }

}
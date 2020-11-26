package pl.hennig.kurnik.kurnik.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.hennig.kurnik.kurnik.model.User;
import pl.hennig.kurnik.kurnik.repository.UserRepo;
import pl.hennig.kurnik.kurnik.service.SettingsService;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

@Route("settings")
public class SettingsGui extends VerticalLayout {
    private SettingsService settingsService;
    private User user;
    private UserRepo userRepo;

    public SettingsGui(SettingsService settingsService, UserRepo userRepo) {
        MenuGui menuGui = new MenuGui();
        add(menuGui.getMenuBarLogged());
        this.settingsService = settingsService;
        this.user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.userRepo = userRepo;

        Div divAvatar = new Div();
        divAvatar.add(new Label("Change/Add Avatar"));
        divAvatar.add(uploadAvatar());
        divAvatar.add(new Hr());


        Div divChangeEmail = new Div(new Label("Change email"));
        Button changeEmailButton = new Button("Change");
        changeEmailButton.addClickListener(buttonClickEvent -> changeEmail());
        changeEmailButton.addClickListener(buttonClickEvent -> {
            changeEmail();
        });
        divChangeEmail.add(new VerticalLayout(changeEmailButton), new Hr());

        Div changeReversDiv = new Div();
        BiMap<String, User.Revers> reversMap = HashBiMap.create();
        reversMap.put("Default", User.Revers.DEFAULT);
        reversMap.put("Dark", User.Revers.DARK);
        reversMap.put("Light", User.Revers.LIGHT);
        changeReversDiv.add(new Label("Change reverse"));
        HorizontalLayout changeReverseLayout = new HorizontalLayout();
        ComboBox<String> reverseList = new ComboBox<>();
        reverseList.setItems(reversMap.keySet());
        reverseList.setValue(reversMap.inverse().get(user.getRevers()));
        Button changeReverseButton = new Button("Change");
        changeReverseButton.addClickListener(buttonClickEvent -> {
            if (reversMap.keySet().contains(reverseList.getValue())) {
                user.setRevers(reversMap.get(reverseList.getValue()));
                userRepo.save(user);
            } else {
                Notification notification = new Notification("Wrong revers", 3000);
                notification.open();
            }
        });
        changeReverseLayout.add(reverseList, changeReverseButton, new Hr());
        changeReversDiv.add(changeReverseLayout, changeReverseButton);


        Div changePrivacyDiv = new Div();
        HorizontalLayout changePrivacyLayout = new HorizontalLayout();
        RadioButtonGroup<String> privacyOptions = new RadioButtonGroup<>();
        privacyOptions.setItems("Private", "Public");
        privacyOptions.setValue(user.isPrivate() ? "Private" : "Public");
        Button privacyChangeButton = new Button("Change");
        privacyChangeButton.addClickListener(buttonClickEvent -> {
            if (user.isPrivate() && privacyOptions.getValue().equals("Public")) {
                user.setPrivate(false);
                userRepo.save(user);
            } else if (!user.isPrivate() && privacyOptions.getValue().equals("Private")) {
                user.setPrivate(true);
                userRepo.save(user);
            }
            Notification notification = new Notification("Privacy options changed", 3000);
            notification.open();
        });
        changePrivacyLayout.add(privacyOptions, privacyChangeButton);
        changePrivacyDiv.add(changePrivacyLayout, new Hr());


        Div changePasswordDiv = new Div();
        Button changePasswordButton = new Button("Change Password", event -> {UI.getCurrent().navigate("reset");});
        changePasswordDiv.add(changePasswordButton, new Hr());


        add(divAvatar, divChangeEmail, changeReversDiv, changePrivacyDiv, changePasswordDiv);


    }


    private Upload uploadAvatar() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setDropLabel(new Label("Upload only jpg,png or bmp files"));
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/bmp");
        upload.addFinishedListener(succeededEvent -> settingsService.setAvatar(buffer, this.user.getUsername()));
        upload.setMaxFileSize(5000000);
        upload.addFailedListener(failedEvent -> {
            Notification notification = new Notification("Could't upload the file", 3000);
            notification.open();
        });
        return upload;
    }

    private void changeEmail() {
        Binder<User> binder = new Binder<>();

        Dialog dialog = new Dialog();
        VerticalLayout emailFields = new VerticalLayout();
        EmailField emailField = new EmailField("Email");
        EmailField reEmailField = new EmailField("repeat email");
        binder.forField(emailField).withValidator(this::emailValidator, "This is not email").bind(User::getEmail, User::setEmail);
        binder.forField(reEmailField).withValidator(event -> reEmailValidator(emailField.getValue(), reEmailField.getValue()), "emails don't match").bind(User::getEmail, User::setEmail);
        emailFields.add(emailField, reEmailField);
        Button buttonSubmit = new Button("Submit");
        buttonSubmit.addClickListener(buttonClickEvent -> {
            if (emailValidator(emailField.getValue()) && reEmailValidator(emailField.getValue(), reEmailField.getValue())) {
                user.setEmail(emailField.getValue());
                userRepo.save(user);
                dialog.close();
            }
        });
        dialog.add(emailFields, buttonSubmit);
        add(dialog);
        dialog.open();


    }

    private Boolean emailValidator(String email) {
        if (email.matches("^([a-zA-Z0-9_\\.\\-+])+@[a-zA-Z0-9-.]+\\.[a-zA-Z0-9-]{2,}$")) {
            return true;
        }
        return false;
    }

    private Boolean reEmailValidator(String email, String reEmail) {
        if (email.equals(reEmail)) {
            return true;
        }
        return false;
    }


}

package pl.hennig.kurnik.kurnik.gui;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.hennig.kurnik.kurnik.model.User;
import pl.hennig.kurnik.kurnik.repository.UserRepo;
import pl.hennig.kurnik.kurnik.service.ProfileService;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.AbstractStreamResource;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.hennig.kurnik.kurnik.model.User;
import pl.hennig.kurnik.kurnik.repository.UserRepo;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route("profile")
public class ProfileGui extends VerticalLayout implements HasUrlParameter<String> {

    private ProfileService profileService;
    private User user;
    private UserRepo userRepo;
    private boolean auth = false;
    private MenuBar menuBar;
    public ProfileGui(ProfileService profileService, UserRepo userRepo) {
        MenuGui menuGui = new MenuGui();
        add(menuGui.getMenuBarLogged());
        this.profileService = profileService;
        this.userRepo = userRepo;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> map = queryParameters.getParameters();
        if (map.isEmpty()) {

            Dialog dialog = new Dialog();
            TextField textField = new TextField();
            Button button = new Button("Search");
            button.addClickListener(buttonClickEvent -> {
                Optional<User> optionalUser = userRepo.findUserByUsername(textField.getValue());
                if (!optionalUser.isPresent()) {
                    textField.clear();
                    textField.focus();
                } else {
                    user = optionalUser.get();
                }
                addLayout();
                dialog.close();
            });
            dialog.add(new VerticalLayout(textField, button));
            add(dialog);
            dialog.open();
        } else {
            Optional<User> optionalUser = userRepo.findUserByUsername(map.get("username").get(0));
            if (!optionalUser.isPresent()) {
                UI.getCurrent().navigate("chat");
            } else {
                user = optionalUser.get();
                addLayout();
            }
        }

    }


    private void addLayout() {
        if (!user.isPrivate() || user == (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()) {
            auth = true;
        }
        Div avatarDiv = new Div();
        avatarDiv.add(profileService.avatar(user.getUsername()));
        add(avatarDiv);
        Div usernameDiv = new Div();
        usernameDiv.add(new Label(user.getUsername()));
        add(usernameDiv);
        if (auth) {
            Div emailDiv = new Div();
            emailDiv.add(user.getEmail());
            add(emailDiv);
        }


    }

}

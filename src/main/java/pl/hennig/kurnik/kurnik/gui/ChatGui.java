package pl.hennig.kurnik.kurnik.gui;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.FileCopyUtils;
import pl.hennig.kurnik.kurnik.model.ChatMessage;
import pl.hennig.kurnik.kurnik.model.MessageList;
import pl.hennig.kurnik.kurnik.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


@Route("chat")
@Push

public class ChatGui extends VerticalLayout {


    private UnicastProcessor<ChatMessage> publisher;
    private Flux<ChatMessage> messages;
    private List<String> blacklist;
    private String username;
    private MessageList messageList = new MessageList();


    public ChatGui(UnicastProcessor<ChatMessage> publisher,Flux<ChatMessage> messages ) {
        MenuGui menuGui = new MenuGui();
        add(menuGui.getMenuBarLogged());

        this.publisher = publisher;
        this.messages = messages;
        getBlacklist();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.username = user.getUsername();
        setSizeFull();


        H1 header = new H1("Kurnik chat");
        header.getElement().getThemeList().add("dark");
        add(header);
        showChat();

    }


    private void showChat() {

        add(messageList, createInputLayout());
        expand(messageList);

        messages.subscribe(this::addMessage);
    }

    private void addMessage(ChatMessage message) {

        if (message.getMessage().length() > 100) {
            Notification notification = new Notification("Message is too long", 3000);
            notification.open();
            return;
        }
        Label username = new Label();
        username.setText(message.getFrom() + " : ");
        if (message.getFrom().equals(this.username)) {
            username.getStyle().set("color", "red");
        }
        Label messageText = new Label();
        messageText.setText(message.getMessage());
        messageList.add(new HorizontalLayout(username, messageText));


    }
    private Component createInputLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");

        TextField messageField = new TextField();
        Button sendButton = new Button("Send");
        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layout.add(messageField, sendButton);
        layout.expand(messageField);

        sendButton.addClickListener(click -> {


            if ((blacklist.parallelStream().anyMatch(messageField.getValue()::contains))) {
                String badLanguageMessage = "I agree";
                Dialog dialog = new Dialog();
                dialog.open();
                Label label = new Label("I am bad person and deserve to be thrown to Iran");
                Label label1 = new Label("type: ");
                Label label2 = new Label(badLanguageMessage);


                TextField textField = new TextField();
                Button button = new Button("I'm disgusted with myself");
                button.addClickListener(buttonClickEvent -> {
                    if (textField.getValue().toLowerCase().equals(badLanguageMessage.toLowerCase())) {
                        dialog.close();
                        messageField.clear();
                        messageField.focus();
                    } else {
                        textField.clear();
                    }
                });
                dialog.add(new VerticalLayout(label, label1, label2, textField, button));
                add(dialog);

            } else if (messageField.getValue().trim().isEmpty()) {
                messageField.clear();
                messageField.focus();
            } else {

                publisher.onNext(new ChatMessage(username, messageField.getValue()));
                messageField.clear();
                messageField.focus();

            }

        });
        messageField.focus();

        return layout;
    }


    private void getBlacklist() {
        Resource resource = new ClassPathResource("blacklist.txt");

        try {
            InputStream inputStream = resource.getInputStream();
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            String data = new String(bdata, StandardCharsets.UTF_8);
            blacklist = Arrays.asList(data.split("\r?\n"));

        } catch (IOException e) {
            e.printStackTrace();

        }


    }


}


package pl.hennig.kurnik.kurnik.gui;


import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.Collections;

@Tag("sa-login-view")
@Route(value = LoginView.ROUTE)
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver { //
    public static final String ROUTE = "login";

    private LoginForm login = new LoginForm();

    public LoginView(){
        MenuGui menuGui = new MenuGui();
        add(menuGui.getMenuBarNotLogged());
        login.setAction("login");
        getElement().appendChild(login.getElement());
        login.addForgotPasswordListener(forgotPasswordEvent -> UI.getCurrent().navigate("forgot"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) { //
        if (!event.getLocation().getQueryParameters().getParameters().getOrDefault("error", Collections.emptyList()).isEmpty()) {
            login.setError(true); //
        }
    }
}
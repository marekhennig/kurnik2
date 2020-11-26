package pl.hennig.kurnik.kurnik.gui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MenuGui extends VerticalLayout {
    public MenuGui(){

    }
    public MenuBar getMenuBarLogged()
    {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Chat", e -> {
            UI.getCurrent().navigate("");
        });
        menuBar.addItem("Settings", e -> {
            UI.getCurrent().navigate("settings");
        });
        menuBar.addItem("Find Friend", e -> {
            UI.getCurrent().navigate("profile");
        });
        menuBar.addItem("Logout", e -> {
            UI.getCurrent().navigate("logout");
        });
        add(menuBar);
        return menuBar;
    }
    public MenuBar getMenuBarNotLogged()
    {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Login", e -> {
            UI.getCurrent().navigate("login");
        });
        menuBar.addItem("Register", e -> {
            UI.getCurrent().navigate("registration");
        });
        menuBar.addItem("Forgot Password", e -> {
            UI.getCurrent().navigate("forgot");
        });
        add(menuBar);
        return menuBar;
    }
}

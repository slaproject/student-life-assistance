package com.studentapp.frontend.view;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;

public class CentralView extends BorderPane {
    private final MenuBar menuBar;

    public CentralView() {
        menuBar = new MenuBar();

        Menu calendarMenu = new Menu("Calendar");
        MenuItem showCalendar = new MenuItem("Show Calendar");
        calendarMenu.getItems().add(showCalendar);
        menuBar.getMenus().addAll(calendarMenu);
        setTop(menuBar);
    }

    public void setMainContent(Node node) {
        setCenter(node);
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
} 
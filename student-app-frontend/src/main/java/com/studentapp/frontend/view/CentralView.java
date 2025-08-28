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

        // Add Finance menu
        Menu financeMenu = new Menu("Finance");
        MenuItem showFinance = new MenuItem("Budget Tracker");
        financeMenu.getItems().add(showFinance);

        menuBar.getMenus().addAll(calendarMenu, financeMenu);
        setTop(menuBar);
    }

    public void setMainContent(Node node) {
        setCenter(node);
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}

package com.studentapp.frontend;

import com.studentapp.frontend.controller.HelloController;
import com.studentapp.frontend.view.CalendarView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StudentAppFrontendApplication extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/auth-view.fxml"));
    Parent root = loader.load();
    primaryStage.setTitle("Student Life Assistance - Login/Signup");
    primaryStage.setScene(new Scene(root, 400, 300));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

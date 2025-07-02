package com.studentapp.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StudentAppFrontendApplication extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/studentapp/frontend/login-view.fxml"));
    Parent root = loader.load();
    primaryStage.setTitle("Student Life Assistance - Login");
    primaryStage.setScene(new Scene(root, 450, 400));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

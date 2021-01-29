package com.holland.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final ChoiceBox<Object> choiceBox = new ChoiceBox<>();
        choiceBox.setItems();

        Button btn1 = new Button("Say, Hello World");
        btn1.setOnAction(e -> System.out.println("hello world"));

        StackPane root = new StackPane();
        root.getChildren().add(btn1);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("First JavaFX Application");
        primaryStage.show();
    }
}

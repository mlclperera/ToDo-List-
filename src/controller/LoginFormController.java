package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class LoginFormController {

    public AnchorPane root;
    public TextField txtUserName;
    public PasswordField txtPassword;

    public static String LoginUserID;
    public static String LoginUserName;

    public void CreatedNewAccountOnMouseClick(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/CreateNewAccountForm.fxml"));
        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Create New Account");

    }

    public void btnLoginOnAction(ActionEvent actionEvent) throws IOException{

        String userNameText = txtUserName.getText();
        String passwordText = txtPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where name = ? and password = ?");
            preparedStatement.setObject(1, userNameText);
            preparedStatement.setObject(2, passwordText);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){

                LoginUserID = resultSet.getString(1);
                LoginUserName = resultSet.getString(2);

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/ToDoListForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("To Do List");

            }

            else {

                Alert alert = new Alert(Alert.AlertType.ERROR, "User Name or Password Incorrect");
                alert.showAndWait();
                txtPassword.clear();
                txtUserName.clear();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}

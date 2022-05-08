package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountController {
    public AnchorPane root;
    public PasswordField txtPassword;
    public PasswordField txtConfirmPassword;
    public Label lblAlert; // set alert if password is incorrect
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;
    public Label lblID;

    public void initialize(){

        lblAlert.setVisible(false);
        setDisable(true);

    }

    public void btnRegisterOnAction(ActionEvent actionEvent) throws IOException {

        String passwordText = txtPassword.getText();
        String confirmPasswordText = txtConfirmPassword.getText();

        if(passwordText.equals(confirmPasswordText)){



            register();

        }
        else {

            lblAlert.setVisible(true);
            txtPassword.setStyle("-fx-border-color: red");
            txtConfirmPassword.setStyle("-fx-border-color: red");
            txtPassword.requestFocus();
            txtPassword.clear();
            txtConfirmPassword.clear();
        }

        // set database connection
        Connection connection = DBConnection.getInstance().getConnection();
        //System.out.println(connection);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {

        lblAlert.setVisible(false);
        setDisable(false);
        txtUserName.requestFocus();
        autoGenerateID();
    }

    // Initial set disable components
    public void setDisable(boolean val){
        txtUserName.setDisable(val);
        txtEmail.setDisable(val);
        txtPassword.setDisable(val);
        txtConfirmPassword.setDisable(val);
        btnRegister.setDisable(val);
    }

    //set Auto Generate ID for new user
    public void autoGenerateID(){

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select uid from user order by uid desc limit 1");
            boolean isExist = resultSet.next(); // check there is already a user value

            if(isExist){

                String userID = resultSet.getString(1);
                userID = userID.substring(1, userID.length());
                int intID = Integer.parseInt(userID);
                intID++;

                if(intID < 10){
                    lblID.setText("U00" + intID);
                }else if(intID < 100){
                    lblID.setText("U0" + intID);
                }else {
                    lblID.setText("U" + intID);
                }

            }else {
                lblID.setText("U001");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    // Send data to database if password is correct
    public void register() throws IOException {

        String id = lblID.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        if(userName.trim().isEmpty()){
            txtUserName.requestFocus();
            txtUserName.setStyle("-fx-border-color: red");
        }else if(email.trim().isEmpty()){
            txtEmail.requestFocus();
            txtEmail.setStyle("-fx-border-color: red");
        }else if(password.trim().isEmpty()){
            txtPassword.requestFocus();
            txtPassword.setStyle("-fx-border-color: red");
        }else {
            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into user values (?, ?, ?, ?)");
                preparedStatement.setObject(1, id);
                preparedStatement.setObject(2, userName);
                preparedStatement.setObject(3, email);
                preparedStatement.setObject(4, password);

                preparedStatement.executeUpdate();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
        }
    }
}

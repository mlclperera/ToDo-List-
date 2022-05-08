package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoListFormController {

    public AnchorPane root;
    public Label lblTitle;
    public Label lblUserID;
    public Pane rootPane;
    public TextField txtTaskName;
    public ListView<ToDoTM> lstTodo;
    public TextField txtSelectedToDo;
    public Button btnUpdate;
    public Button btnDelete;

    public void initialize() throws SQLException {

        lblTitle.setText("Hi " + LoginFormController.LoginUserName + " Welcome to ToDo List");
        lblUserID.setText(LoginFormController.LoginUserID);
        rootPane.setVisible(false);
        loadList();
        setDisableCommon(true);

        lstTodo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {

                if(lstTodo.getSelectionModel().getSelectedItem() == null){
                    return;
                }
                setDisableCommon(false);
                rootPane.setVisible(false);

                txtSelectedToDo.setText(lstTodo.getSelectionModel().getSelectedItem().getDescription());
            }
        });
    }

    public void setDisableCommon(boolean val){
        txtSelectedToDo.setDisable(val);
        btnDelete.setDisable(val);
        btnUpdate.setDisable(val);
        txtSelectedToDo.clear();
    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {

        lstTodo.getSelectionModel().clearSelection();
        setDisableCommon(true);
        rootPane.setVisible(true);
        txtTaskName.requestFocus();
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to log out", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){

            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
        }

    }

    public void btnAddtoListOnAction(ActionEvent actionEvent) throws SQLException {

        String id = autoGenerateID();
        String description = txtTaskName.getText();
        String userID = lblUserID.getText();
        //System.out.println(id);
        loadList();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todo value (?,?,?)");
            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3, userID);

            preparedStatement.executeUpdate();
            //System.out.println("added");

            txtTaskName.clear();
            rootPane.setVisible(false);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public String autoGenerateID(){

        Connection connection = DBConnection.getInstance().getConnection();
        String id = "";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");
            boolean isExist = resultSet.next(); // check there is already a user value

            if(isExist){

                String toDoID = resultSet.getString(1);
                toDoID = toDoID.substring(1, toDoID.length());
                int intID = Integer.parseInt(toDoID);
                intID++;

                if(intID < 10){
                   id = "T00" + intID;
                }else if(intID < 100){
                    id = "T0" + intID;
                }else {
                    id = "T" + intID;
                }

            }else {
                id = "T001";
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

    // use's previous list must to load
    public void loadList() throws SQLException {

        ObservableList<ToDoTM> todos = lstTodo.getItems(); // obesrvable list
        todos.clear();

        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id = ?");
        preparedStatement.setObject(1,lblUserID.getText());

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){
            String id = resultSet.getString(1);
            String descripition = resultSet.getString(2);
            String user_id = resultSet.getString(3);

            todos.add(new ToDoTM(id, descripition,user_id));
        }


    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {

        String description = txtSelectedToDo.getText();
        String id = lstTodo.getSelectionModel().getSelectedItem().getId();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");
            preparedStatement.setObject(1, description);
            preparedStatement.setObject(2, id);

            preparedStatement.executeUpdate();
            loadList();
            setDisableCommon(true);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete this To-Do...", ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){

            String id = lstTodo.getSelectionModel().getSelectedItem().getId();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ?");
                preparedStatement.setObject(1, id);

                preparedStatement.executeUpdate();
                loadList();
                setDisableCommon(true);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}

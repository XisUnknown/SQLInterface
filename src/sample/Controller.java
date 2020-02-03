package sample;

import SQLConnectionPackage.ConnectionDB;
import SQLConnectionPackage.Tables;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Controller {

  private ObservableList items = FXCollections.observableArrayList();
  ObservableList savedQueriesList = FXCollections.observableArrayList();
  ObservableList tableList = FXCollections.observableArrayList();
  String db;
  TableColumn<String, String> colTable = new TableColumn<String, String>("TABLES");
  TableColumn<String, String> savedQueriesCol = new TableColumn<String, String>("Saved Queries");
  @FXML private TableView Result;
  @FXML private TableView TablesView;
  @FXML private TableView savedQueries;
  @FXML private TextArea input;
  @FXML private TextField dbInput;
  @FXML private TextField server;
  @FXML private TextField username;
  @FXML private TextField password;
  @FXML private Label conLabel;
  @FXML private Label queryInfo;
  @FXML private ComboBox selectBox;
  @FXML private CheckBox SaveQuery;

  @FXML
  public void initialize() {
    Tables.initializeSingleColumnTable(colTable,TablesView);
    Tables.initializeSingleColumnTable(savedQueriesCol,savedQueries);
    savedQueries.setItems(savedQueriesList);
  }
  public void connectServer() throws SQLException {
    ConnectionDB.getConnection(server.getText(),username.getText(),password.getText());
    conLabel.setText("connected");
    selectBox.getItems().removeAll(selectBox.getItems());
    ResultSet resultSet = ConnectionDB.getDatabases();
    while (resultSet.next()){
      selectBox.getItems().add(resultSet.getString("Database"));
    }
    conLabel.setText("Connected");
  }
  public void selectedDatabase() throws SQLException {
    ResultSet resultSet = ConnectionDB.getTables(selectBox.getValue().toString());
    System.out.println(resultSet);
    Tables.populateSingleColumnTable("TABLE_NAME",colTable,TablesView,tableList,resultSet);
  }
  public void displayQueryResult() throws SQLException {
    if (SaveQuery.isSelected()) {
      savedQueriesList.add(input.getText() + ";" + selectBox.getValue().toString());

      System.out.println(savedQueriesList);
      savedQueries.refresh();
    }
    ResultSet resultSet = ConnectionDB.getResultSetfromInput(selectBox.getValue().toString(),input.getText());
    Tables.populateMultiColumnTable(Result,items,resultSet);
  }
  public void displaypreviousQueryResult() throws SQLException {
    String buff = savedQueries.getSelectionModel().getSelectedItem().toString();
    String buff2[] = buff.split(";");

    ResultSet resultSet = ConnectionDB.getResultSetfromInput(buff2[1],buff2[0]);
    Tables.populateMultiColumnTable(Result,items,resultSet);
  }
  public void deleteSavedQuery() {
    savedQueries.getItems().remove(savedQueries.getSelectionModel().getSelectedItem());
  }
  public void closeConnection() throws SQLException {
    ConnectionDB.closeConnection();
    conLabel.setText("Disconnected");
  }
  public void exportFile() throws IOException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
    File selectedFile = fileChooser.showOpenDialog(null);
    //File selectedFile = new File("data3.txt");
    FileWriter fr = new FileWriter(selectedFile, true);
    BufferedWriter bw = new BufferedWriter(fr);
    for (int i = 0;i<savedQueries.getItems().size();i++){
      fr.write(savedQueries.getItems().get(i).toString());
      //System.out.println(savedQueries.getItems().get(i).toString());
    }
    fr.close();
  }
  public void importFile() throws IOException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
    File selectedFile = fileChooser.showOpenDialog(null);
    Scanner scanner = new Scanner(selectedFile);

    while (scanner.hasNextLine()){
      savedQueriesList.add(scanner.nextLine());
      System.out.println(savedQueriesList.get(0));
    }
    savedQueries.refresh();
    scanner.close();
  }
}

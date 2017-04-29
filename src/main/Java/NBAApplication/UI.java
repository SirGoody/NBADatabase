package NBAApplication;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.ResultSet;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javafx.scene.image.*;
import java.sql.*;
import java.io.*;
import java.text.*;

public class UI extends Application {

    private final TextArea outputField = new TextArea();
    private final Button searchButton = new Button("Search");
    private final Button applyFilter = new Button("Apply Filter");
    private ChoiceBox<String> choiceBox1 = new ChoiceBox<>();
    private ChoiceBox<String> choiceBox2 = new ChoiceBox<>();
    private TextField userInputSearch = new TextField();
    private String userInput = "";
    private BorderPane layout = new BorderPane();
    private VBox vboxInitial = new VBox();
    private VBox vboxFinal = new VBox();
    private Image image = new Image(getClass().getResourceAsStream("/NBALogoBackground.png"));
    private ImageView imv1 = new ImageView(image);

    private int tableIndex = 6;

    private String choice1;
    private String choice2;

    private String querySearch;

    private ObservableList<ObservableList> data;
    private TableView tableView;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        outputField.setEditable(false);
        outputField.setWrapText(true);

        userInputSearch.setMaxWidth(170);
        userInputSearch.setPromptText("Only use with Find filters.");

        searchButton.setOnAction(event -> performSearchButton());
        applyFilter.setOnAction(event -> applyFilterButton());

        choiceBox1.getItems().add("Players");
        choiceBox1.getItems().add("Teams");
        choiceBox1.getItems().add("Coaches");
        choiceBox1.getItems().add("Owners");
        choiceBox1.setValue("Players");

        userInputSearch.setEditable(true);

        tableView = new TableView();

        imv1.setFitHeight(106);
        imv1.setFitWidth(156);

        searchButton.setCursor(Cursor.HAND);
        applyFilter.setCursor(Cursor.HAND);

        vboxInitial = new VBox(choiceBox1, applyFilter);

        vboxFinal = new VBox(choiceBox2, userInputSearch, searchButton);
        vboxFinal.setVisible(false);

        layout.setBottom(tableView);
        layout.setLeft(vboxInitial);
        layout.setCenter(vboxFinal);
        layout.setRight(imv1);

        Scene scene = new Scene(layout);
        primaryStage.setWidth(1024);
        primaryStage.setHeight(540);
        primaryStage.setScene(scene);
        primaryStage.setTitle("2016 - 2017 Regular Season");
        primaryStage.show();
        
    }

    private void applyFilterButton(){
        choice1 = choiceBox1.getValue();

        if(choice1 == "Players")
        {
            vboxFinal.setVisible(true);
            choiceBox2.getItems().clear();
            choiceBox2.setValue("Display by Team");
            choiceBox2.getItems().add("Display by Team");
            choiceBox2.getItems().add("Display Player Stats");
            choiceBox2.getItems().add("Display Players Sorted by PPG");
            choiceBox2.getItems().add("Display Players Sorted by RPG");
            choiceBox2.getItems().add("Display Players Sorted by APG");
            choiceBox2.getItems().add("Display Players Sorted by Height");
            choiceBox2.getItems().add("Display Players Sorted by Weight");
            choiceBox2.getItems().add("Find Player by Last Name");
            choiceBox2.getItems().add("Find Player by Position");
            userInputSearch.clear();

        }

        if(choice1 == "Teams")
        {
            vboxFinal.setVisible(true);
            choiceBox2.getItems().clear();
            choiceBox2.setValue("General Search");
            choiceBox2.getItems().add("General Search");
            choiceBox2.getItems().add("Display Team Stats");
            choiceBox2.getItems().add("Find Team by Name");
            userInputSearch.clear();

        }

        if(choice1 == "Owners" || choice1 == "Coaches")
        {
            vboxFinal.setVisible(true);
            choiceBox2.getItems().clear();
            choiceBox2.setValue("General Search");
            choiceBox2.getItems().add("General Search");
            choiceBox2.getItems().add("Find by Team Name");
            userInputSearch.clear();
        }

    }

    private void performSearchButton() {
        outputField.clear();
        choice1 = choiceBox1.getValue();



        if(choice1 == "Owners")
        {

            choice2 = choiceBox2.getValue();

            if(choice2 == "General Search")
            {
                querySearch = "select Fname \"First\", Lname \"Last\", BDATE \"Birth Date\", Team_Name \"Team\" from Team_Owner ORDER BY Team_name";
                tableIndex = 4;
                vboxFinal.setVisible(false);

            }
            if(choice2 == "Find by Team Name")
            {
                userInput = userInputSearch.getText();
                querySearch = "SELECT Fname \"First\", Lname \"Last\", Team_Name \"Team\", BDATE \"Birth Date\" FROM Team_Owner WHERE Team_Name = " + "'" + userInput + "'";
                tableIndex = 4;
                vboxFinal.setVisible(false);
            }
        }

        if(choice1 == "Coaches")
        {

            choice2 = choiceBox2.getValue();

            if(choice2 == "General Search")
            {
                querySearch = "select Fname \"First\", Lname \"Last\", Team_Name \"Team\", BDATE \"Birth Date\" from Head_Coach ORDER BY Team_name";
                tableIndex = 3;
                vboxFinal.setVisible(false);
            }
            if(choice2 == "Find by Team Name")
            {
                userInput = userInputSearch.getText();
                querySearch = "SELECT Fname \"First\", Lname \"Last\", Team_Name \"Team\", BDATE \"Birth Date\" FROM Head_Coach WHERE Team_Name = " + "'" + userInput + "'";
                tableIndex = 4;
                vboxFinal.setVisible(false);
            }
        }

        if(choice1 == "Players")
        {

            choice2 = choiceBox2.getValue();

            if(choice2 == "Display by Team")
            {
                querySearch = "SELECT Fname \"First\", Lname \"Last\", Player_Team \"Team\", Player_Num \"Number\", " +
                        "Player_Pos \"Pos\", Bdate \"Birth Date\", Player_Height \"Height (in)\", Player_Weight \"Weight (lbs)\" FROM PLAYER ORDER BY Player_Team, Lname";
                tableIndex = 8;
                vboxFinal.setVisible(false);
            }

            if(choice2 == "Display Player Stats")
            {
                querySearch = "SELECT Fname \"First\", Lname \"Last\", Points_Per_Game \"PPG\", Rebounds_Per_Game \"RPG\", Assists_Per_Game \"APG\", " +
                        "Steals_Per_Game \"SPG\", Blocks_Per_Game \"BPG\", Turnovers_Per_Game \"TPG\" FROM PLAYER NATURAL JOIN PLAYER_STATS ORDER BY Points_Per_Game DESC";
                tableIndex = 8;
                vboxFinal.setVisible(false);
            }
            if(choice2 == "Find Player by Last Name")
            {
                userInput = userInputSearch.getText();
                querySearch = "select Fname \"First\", Lname \"Last\", Player_Team \"Team\", Player_Num \"Number\", Player_Pos \"Pos\", Bdate \"Birth Date\", " +
                        "Player_Height \"Height (in)\", Player_Weight \"Weight (lbs)\" from Player WHERE LNAME = " + "'" + userInput + "'";
                tableIndex = 8;
                vboxFinal.setVisible(false);
            }
            if(choice2 == "Find Player by Position")
            {
                userInput = userInputSearch.getText();
                querySearch = "select Fname \"First\", Lname \"Last\", Player_Team \"Team\", Player_Num \"Number\", Player_Pos \"Pos\", Bdate \"Birth Date\", " +
                        "Player_Height \"Height (in)\", Player_Weight \"Weight (lbs)\" from Player WHERE Player_Pos = " + "'" + userInput + "'";
                tableIndex = 8;
                vboxFinal.setVisible(false);
            }
            if(choice2 == "Display Players Sorted by PPG")
            {
                querySearch = "SELECT Fname \"First\", Lname \"Last\", Player_Team \"Team\", Player_Num \"Number\", Points_Per_Game \"PPG\" " +
                        "FROM PLAYER NATURAL JOIN PLAYER_STATS ORDER BY Points_Per_Game DESC";
                tableIndex = 5;
                vboxFinal.setVisible(false);
            }
            if(choice2 == "Display Players Sorted by RPG")
            {
                querySearch = "SELECT Fname \"First\", Lname \"Last\", Player_Team \"Team\", Player_Num \"Number\", Rebounds_Per_Game \"RPG\" " +
                        "FROM PLAYER NATURAL JOIN PLAYER_STATS ORDER BY Rebounds_Per_Game DESC";
                tableIndex = 5;
                vboxFinal.setVisible(false);
            }
            if(choice2 == "Display Players Sorted by APG")
            {
                querySearch = "SELECT Fname \"First\", Lname \"Last\", Player_Team \"Team\", Player_Num \"Number\", Assists_Per_Game \"APG\" " +
                        "FROM PLAYER NATURAL JOIN PLAYER_STATS ORDER BY Assists_Per_Game DESC";
                tableIndex = 5;
                vboxFinal.setVisible(false);
            }
            if(choice2 == "Display Players Sorted by Height")
            {
                querySearch = "select Fname \"First\" , Lname \"Last\", Player_Height \"Height (in)\" from PLAYER ORDER BY Player_Height DESC";
                tableIndex = 3;
                vboxFinal.setVisible(false);
            }
            if(choice2 == "Display Players Sorted by Weight")
            {
                querySearch = "select Fname \"First\" , Lname \"Last\", Player_Weight \"Weight (lbs)\" from PLAYER ORDER BY Player_Weight DESC";
                tableIndex = 3;
                vboxFinal.setVisible(false);
            }
        }

        if(choice1 == "Teams")
        {

            choice2 = choiceBox2.getValue();

            if(choice2 == "General Search")
            {
                querySearch = "select Team_Name \"Team Name\", Conference \"Conference\", Division \"Division\", Year_Founded \"Year Founded\", " +
                        "City_Name \"City\", State_name \"State\" from TEAM NATURAL JOIN TEAM_LOCATION";
                tableIndex = 6;
                vboxFinal.setVisible(false);
            }

            if(choice2 == "Display Team Stats")
            {
                querySearch = "SELECT Team_Name \"Team\", WINS, LOSSES FROM Team_Stats ORDER BY WINS DESC";
                tableIndex = 3;
                vboxFinal.setVisible(false);
            }

            if(choice2 == "Find Team by Name")
            {
                userInput = userInputSearch.getText();
                querySearch = "select Team_Name \"Team\", Conference, Division, Year_Founded \"Year Founded\", WINS, LOSSES from Team NATURAL JOIN TEAM_STATS " +
                        "WHERE TEAM_NAME = " + "'" + userInput + "'";
                tableIndex = 6;
                vboxFinal.setVisible(false);
            }
        }

        try {
            searchDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void searchDataBase()throws SQLException, IOException
    {
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());

        String serverName = "csor12c.dhcp.bsu.edu";
        String portNumber = "1521";
        String sid = "or12cdb";
        String url ="jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;

        data = FXCollections.observableArrayList();
        try{
            Connection conn =
                    DriverManager.getConnection (url,
                            "cwgoodman", "1180");
            Statement stmt = conn.createStatement ();
            ResultSet rset = stmt.executeQuery (querySearch);

            tableView.getItems().clear();
            tableView.getColumns().clear();

            for(int i=0 ; i<rset.getMetaData().getColumnCount(); i++){
                final int j = i;
                TableColumn col = new TableColumn(rset.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        String tableData = param.getValue().get(j).toString();

                        if(tableData.contains("00:00:00.0"))
                        {
                            tableData = tableData.replace("00:00:00.0","");
                        }
                        return new SimpleStringProperty(tableData);
                    }
                });

                tableView.getColumns().addAll(col);
                System.out.println("Column ["+i+"] ");
            }


            while(rset.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rset.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rset.getString(i));
                }
                System.out.println("Row [1] added "+row );
                data.add(row);

            }

            outputField.selectHome();
            outputField.deselect();

            // close the resultSet
            rset.close();
            // Close the statement
            stmt.close();
            // Close the connection
            conn.close();

            tableView.setItems(data);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }
}
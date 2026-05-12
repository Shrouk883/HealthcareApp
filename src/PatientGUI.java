import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class PatientGUI {

    private TableView<ObservableList<String>> tableView = new TableView<>();
    private TextArea logArea = new TextArea();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Manage Patients");

        TextField txtId = new TextField();
        txtId.setPromptText("Patient ID");
        TextField txtName = new TextField();
        txtName.setPromptText("Name");
        TextField txtDemographics = new TextField();
        txtDemographics.setPromptText("Demographics");
        TextField txtHistory = new TextField();
        txtHistory.setPromptText("Medical History");

        Button btnInsert = new Button("Add Patient");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        Button btnRefresh = new Button("Refresh");

        btnInsert.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnUpdate.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnRefresh.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        logArea.setEditable(false);
        logArea.setPrefHeight(150);

        setupTable();

        btnInsert.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                String demographics = txtDemographics.getText();
                String history = txtHistory.getText();

                String sql = "INSERT INTO PATIENT (PATIENT_ID_, NAME, DEMOGRAPHICS, HISTORY) VALUES (?, ?, ?, ?)";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.setString(2, name);
                    ps.setString(3, demographics);
                    ps.setString(4, history);
                    ps.executeUpdate();
                    logArea.appendText("✓ Patient " + id + " added!\n");
                    refreshTable();
                    clearFields(txtId, txtName, txtDemographics, txtHistory);
                }
            } catch (SQLException ex) {
                logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
            } catch (Exception ex) {
                logArea.appendText("✗ Invalid input\n");
            }
        });

        btnDelete.setOnAction(e -> {
            String selected = getSelectedId();
            if (selected != null) {
                try {
                    String sql = "DELETE FROM PATIENT WHERE PATIENT_ID_ = ?";
                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, Integer.parseInt(selected));
                        ps.executeUpdate();
                        logArea.appendText("✓ Patient " + selected + " deleted!\n");
                        refreshTable();
                    }
                } catch (SQLException ex) {
                    logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
                }
            }
        });

        btnRefresh.setOnAction(e -> refreshTable());

        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setVgap(10);
        inputGrid.setHgap(10);
        inputGrid.add(new Label("Patient ID:"), 0, 0);
        inputGrid.add(txtId, 1, 0);
        inputGrid.add(new Label("Name:"), 0, 1);
        inputGrid.add(txtName, 1, 1);
        inputGrid.add(new Label("Demographics:"), 0, 2);
        inputGrid.add(txtDemographics, 1, 2);
        inputGrid.add(new Label("History:"), 0, 3);
        inputGrid.add(txtHistory, 1, 3);

        HBox buttonBox = new HBox(10, btnInsert, btnUpdate, btnDelete, btnRefresh);

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(
                new Label("PATIENT MANAGEMENT"),
                inputGrid, buttonBox,
                new Label("Patient List:"), tableView,
                new Label("Log:"), logArea
        );

        Scene scene = new Scene(mainLayout, 900, 600);
        stage.setScene(scene);
        stage.show();

        refreshTable();
    }

    private void setupTable() {
        TableColumn<ObservableList<String>, String> colId = new TableColumn<>("Patient ID");
        TableColumn<ObservableList<String>, String> colName = new TableColumn<>("Name");
        TableColumn<ObservableList<String>, String> colDemo = new TableColumn<>("Demographics");
        TableColumn<ObservableList<String>, String> colHistory = new TableColumn<>("History");

        colId.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(0)));
        colName.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(1)));
        colDemo.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(2)));
        colHistory.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(3)));

        tableView.getColumns().addAll(colId, colName, colDemo, colHistory);
        tableView.setPrefHeight(300);
    }

    private void refreshTable() {
        tableView.getItems().clear();
        String sql = "SELECT PATIENT_ID_, NAME, DEMOGRAPHICS, HISTORY FROM PATIENT ORDER BY PATIENT_ID_";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("PATIENT_ID_")));
                row.add(rs.getString("NAME"));
                row.add(rs.getString("DEMOGRAPHICS"));
                row.add(rs.getString("HISTORY"));
                tableView.getItems().add(row);
            }
            logArea.appendText("✓ Loaded " + tableView.getItems().size() + " patients\n");

        } catch (SQLException e) {
            logArea.appendText("✗ DB Error: " + e.getMessage() + "\n");
        }
    }

    private String getSelectedId() {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            return tableView.getSelectionModel().getSelectedItem().get(0);
        }
        return null;
    }

    private void clearFields(TextField... fields) {
        for (TextField f : fields) f.clear();
    }
}
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class DoctorGUI {

    private TableView<ObservableList<String>> tableView = new TableView<>();
    private TextArea logArea = new TextArea();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Manage Doctors");

        TextField txtId = new TextField();
        txtId.setPromptText("Doctor ID");
        TextField txtBranchId = new TextField();
        txtBranchId.setPromptText("Branch ID");
        TextField txtName = new TextField();
        txtName.setPromptText("Name");
        TextField txtExpertise = new TextField();
        txtExpertise.setPromptText("Expertise");

        Button btnInsert = new Button("Add Doctor");
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

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selectedRow) -> {
            if (selectedRow != null) {
                txtId.setText(selectedRow.get(0));
                txtBranchId.setText(selectedRow.get(1));
                txtName.setText(selectedRow.get(2));
                txtExpertise.setText(selectedRow.get(3));
            }
        });

        btnInsert.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                int branchId = Integer.parseInt(txtBranchId.getText());
                String name = txtName.getText();
                String expertise = txtExpertise.getText();

                String sql = "INSERT INTO DOCTOR (DOCTOR_ID, BRANCH_ID_, NAME, EXPERTISE) VALUES (?, ?, ?, ?)";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.setInt(2, branchId);
                    ps.setString(3, name);
                    ps.setString(4, expertise);
                    ps.executeUpdate();
                    logArea.appendText("✓ Doctor " + id + " added!\n");
                    refreshTable();
                    clearFields(txtId, txtBranchId, txtName, txtExpertise);
                }
            } catch (SQLException ex) {
                logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
            } catch (Exception ex) {
                logArea.appendText("✗ Invalid input\n");
            }
        });

        btnUpdate.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                int branchId = Integer.parseInt(txtBranchId.getText());
                String name = txtName.getText();
                String expertise = txtExpertise.getText();

                String sql = "UPDATE DOCTOR SET BRANCH_ID_ = ?, NAME = ?, EXPERTISE = ? WHERE DOCTOR_ID = ?";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, branchId);
                    ps.setString(2, name);
                    ps.setString(3, expertise);
                    ps.setInt(4, id);

                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        logArea.appendText("✓ Doctor " + id + " updated!\n");
                        refreshTable();
                        clearFields(txtId, txtBranchId, txtName, txtExpertise);
                    } else {
                        logArea.appendText("✗ No doctor found with ID " + id + "\n");
                    }
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
                    String sql = "DELETE FROM DOCTOR WHERE DOCTOR_ID = ?";
                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, Integer.parseInt(selected));
                        ps.executeUpdate();
                        logArea.appendText("✓ Doctor " + selected + " deleted!\n");
                        refreshTable();
                    }
                } catch (SQLException ex) {
                    logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
                }
            }
        });

        btnRefresh.setOnAction(e -> {
            refreshTable();
            clearFields(txtId, txtBranchId, txtName, txtExpertise);
            tableView.getSelectionModel().clearSelection();
        });

        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setVgap(10);
        inputGrid.setHgap(10);
        inputGrid.add(new Label("Doctor ID:"), 0, 0);
        inputGrid.add(txtId, 1, 0);
        inputGrid.add(new Label("Branch ID:"), 0, 1);
        inputGrid.add(txtBranchId, 1, 1);
        inputGrid.add(new Label("Name:"), 0, 2);
        inputGrid.add(txtName, 1, 2);
        inputGrid.add(new Label("Expertise:"), 0, 3);
        inputGrid.add(txtExpertise, 1, 3);

        HBox buttonBox = new HBox(10, btnInsert, btnUpdate, btnDelete, btnRefresh);

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(
                new Label("DOCTOR MANAGEMENT"),
                inputGrid, buttonBox,
                new Label("Doctor List:"), tableView,
                new Label("Log:"), logArea
        );

        Scene scene = new Scene(mainLayout, 900, 600);
        stage.setScene(scene);
        stage.show();

        refreshTable();
    }

    private void setupTable() {
        TableColumn<ObservableList<String>, String> colId = new TableColumn<>("Doctor ID");
        TableColumn<ObservableList<String>, String> colBranch = new TableColumn<>("Branch ID");
        TableColumn<ObservableList<String>, String> colName = new TableColumn<>("Name");
        TableColumn<ObservableList<String>, String> colExpertise = new TableColumn<>("Expertise");

        colId.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(0)));
        colBranch.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(1)));
        colName.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(2)));
        colExpertise.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(3)));

        tableView.getColumns().addAll(colId, colBranch, colName, colExpertise);
        tableView.setPrefHeight(300);
    }

    private void refreshTable() {
        tableView.getItems().clear();
        String sql = "SELECT DOCTOR_ID, BRANCH_ID_, NAME, EXPERTISE FROM DOCTOR ORDER BY DOCTOR_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("DOCTOR_ID")));
                row.add(String.valueOf(rs.getInt("BRANCH_ID_")));
                row.add(rs.getString("NAME"));
                row.add(rs.getString("EXPERTISE"));
                tableView.getItems().add(row);
            }
            logArea.appendText("✓ Loaded " + tableView.getItems().size() + " doctors\n");

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

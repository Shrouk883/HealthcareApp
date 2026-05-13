import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class MedicationGUI {

    private TableView<ObservableList<String>> tableView = new TableView<>();
    private TextArea logArea = new TextArea();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Manage Medications");

        TextField txtId = new TextField();
        txtId.setPromptText("Medication ID");

        TextField txtName = new TextField();
        txtName.setPromptText("Medication Name");

        Button btnInsert = new Button("Add");
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

        // Autofill fields when selecting a row
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selectedRow) -> {
            if (selectedRow != null) {
                txtId.setText(selectedRow.get(0));
                txtName.setText(selectedRow.get(1));
            }
        });

        btnInsert.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();

                String sql = "INSERT INTO MEDICATION (MEDICATION_ID, MEDICATION_NAME) VALUES (?, ?)";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, id);
                    ps.setString(2, name);

                    ps.executeUpdate();

                    logArea.appendText("✓ Medication " + id + " added!\n");
                    refreshTable();
                    clearFields(txtId, txtName);
                    tableView.getSelectionModel().clearSelection();
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
                String name = txtName.getText();

                String sql = "UPDATE MEDICATION SET MEDICATION_NAME = ? WHERE MEDICATION_ID = ?";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setString(1, name);
                    ps.setInt(2, id);

                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        logArea.appendText("✓ Medication " + id + " updated!\n");
                        refreshTable();
                        clearFields(txtId, txtName);
                        tableView.getSelectionModel().clearSelection();
                    } else {
                        logArea.appendText("✗ No medication found with ID " + id + "\n");
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
                    String sql = "DELETE FROM MEDICATION WHERE MEDICATION_ID = ?";

                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(sql)) {

                        ps.setInt(1, Integer.parseInt(selected));

                        int rows = ps.executeUpdate();

                        if (rows > 0) {
                            logArea.appendText("✓ Medication " + selected + " deleted!\n");
                            refreshTable();
                            clearFields(txtId, txtName);
                            tableView.getSelectionModel().clearSelection();
                        } else {
                            logArea.appendText("✗ No medication found with ID " + selected + "\n");
                        }
                    }

                } catch (SQLException ex) {
                    logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
                }

            } else {
                logArea.appendText("✗ Please select a medication to delete\n");
            }
        });

        btnRefresh.setOnAction(e -> {
            refreshTable();
            clearFields(txtId, txtName);
            tableView.getSelectionModel().clearSelection();
        });

        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setVgap(10);
        inputGrid.setHgap(10);

        inputGrid.add(new Label("Medication ID:"), 0, 0);
        inputGrid.add(txtId, 1, 0);

        inputGrid.add(new Label("Name:"), 0, 1);
        inputGrid.add(txtName, 1, 1);

        HBox buttonBox = new HBox(10, btnInsert, btnUpdate, btnDelete, btnRefresh);

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(
                new Label("MEDICATION MANAGEMENT"),
                inputGrid,
                buttonBox,
                new Label("Medication List (Click to select):"),
                tableView,
                new Label("Log:"),
                logArea
        );

        Scene scene = new Scene(mainLayout, 700, 550);
        stage.setScene(scene);
        stage.show();

        refreshTable();
    }

    private void setupTable() {
        TableColumn<ObservableList<String>, String> colId = new TableColumn<>("Medication ID");
        TableColumn<ObservableList<String>, String> colName = new TableColumn<>("Medication Name");

        colId.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(0)));
        colName.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(1)));

        colId.setPrefWidth(150);
        colName.setPrefWidth(400);

        tableView.getColumns().addAll(colId, colName);
        tableView.setPrefHeight(300);
    }

    private void refreshTable() {
        tableView.getItems().clear();

        String sql = "SELECT MEDICATION_ID, MEDICATION_NAME FROM MEDICATION ORDER BY MEDICATION_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();

                row.add(String.valueOf(rs.getInt("MEDICATION_ID")));
                row.add(rs.getString("MEDICATION_NAME"));

                tableView.getItems().add(row);
            }

            logArea.appendText("✓ Loaded " + tableView.getItems().size() + " medications\n");

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
        for (TextField f : fields) {
            f.clear();
        }
    }
}

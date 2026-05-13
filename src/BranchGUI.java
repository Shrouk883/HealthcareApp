import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class BranchGUI {

    private TableView<ObservableList<String>> tableView = new TableView<>();
    private TextArea logArea = new TextArea();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Manage Branches");

        TextField txtId = new TextField();
        txtId.setPromptText("Branch ID");
        TextField txtAddress = new TextField();
        txtAddress.setPromptText("Address");
        TextField txtContact = new TextField();
        txtContact.setPromptText("Contact Details");

        Button btnInsert = new Button("Add Branch");
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
                txtAddress.setText(selectedRow.get(1));
                txtContact.setText(selectedRow.get(2));
            }
        });

        btnInsert.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String address = txtAddress.getText();
                String contact = txtContact.getText();

                String sql = "INSERT INTO BRANCH (BRANCH_ID_, _ADDRESS, CONTACT_DETAILS) VALUES (?, ?, ?)";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.setString(2, address);
                    ps.setString(3, contact);
                    ps.executeUpdate();
                    logArea.appendText("✓ Branch " + id + " added!\n");
                    refreshTable();
                    clearFields(txtId, txtAddress, txtContact);
                }
            } catch (SQLException ex) {
                logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
            } catch (Exception ex) {
                logArea.appendText("✗ Invalid ID\n");
            }
        });

        btnUpdate.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String address = txtAddress.getText();
                String contact = txtContact.getText();

                String sql = "UPDATE BRANCH SET _ADDRESS = ?, CONTACT_DETAILS = ? WHERE BRANCH_ID_ = ?";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, address);
                    ps.setString(2, contact);
                    ps.setInt(3, id);

                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        logArea.appendText("✓ Branch " + id + " updated!\n");
                        refreshTable();
                        clearFields(txtId, txtAddress, txtContact);
                    } else {
                        logArea.appendText("✗ No branch found with ID " + id + "\n");
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
                    String sql = "DELETE FROM BRANCH WHERE BRANCH_ID_ = ?";
                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, Integer.parseInt(selected));
                        ps.executeUpdate();
                        logArea.appendText("✓ Branch " + selected + " deleted!\n");
                        refreshTable();
                    }
                } catch (SQLException ex) {
                    logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
                }
            }
        });

        btnRefresh.setOnAction(e -> {
            refreshTable();
            clearFields(txtId, txtAddress, txtContact);
            tableView.getSelectionModel().clearSelection();
        });

        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setVgap(10);
        inputGrid.setHgap(10);
        inputGrid.add(new Label("Branch ID:"), 0, 0);
        inputGrid.add(txtId, 1, 0);
        inputGrid.add(new Label("Address:"), 0, 1);
        inputGrid.add(txtAddress, 1, 1);
        inputGrid.add(new Label("Contact:"), 0, 2);
        inputGrid.add(txtContact, 1, 2);

        HBox buttonBox = new HBox(10, btnInsert, btnUpdate, btnDelete, btnRefresh);

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(
                new Label("BRANCH MANAGEMENT"),
                inputGrid, buttonBox,
                new Label("Branch List:"), tableView,
                new Label("Log:"), logArea
        );

        Scene scene = new Scene(mainLayout, 800, 600);
        stage.setScene(scene);
        stage.show();

        refreshTable();
    }

    private void setupTable() {
        TableColumn<ObservableList<String>, String> colId = new TableColumn<>("ID");
        TableColumn<ObservableList<String>, String> colAddress = new TableColumn<>("Address");
        TableColumn<ObservableList<String>, String> colContact = new TableColumn<>("Contact");

        colId.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(0)));
        colAddress.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(1)));
        colContact.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(2)));

        tableView.getColumns().addAll(colId, colAddress, colContact);
        tableView.setPrefHeight(300);
    }

    private void refreshTable() {
        tableView.getItems().clear();
        String sql = "SELECT BRANCH_ID_, _ADDRESS, CONTACT_DETAILS FROM BRANCH";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("BRANCH_ID_")));
                row.add(rs.getString("_ADDRESS"));
                row.add(rs.getString("CONTACT_DETAILS"));
                tableView.getItems().add(row);
            }
            logArea.appendText("✓ Loaded " + tableView.getItems().size() + " branches\n");

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

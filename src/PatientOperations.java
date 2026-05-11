import java.sql.*;

public class PatientOperations {

    // INSERT PATIENT
    public static boolean insertPatient(int patientId,
                                        String name,
                                        String demographics,
                                        String history) {

        String sql =
                "INSERT INTO PATIENT " +
                        "(PATIENT_ID_, NAME, DEMOGRAPHICS, HISTORY) " +
                        "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setString(2, name);
            ps.setString(3, demographics);
            ps.setString(4, history);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Patient inserted successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }

        return false;
    }

    //UPDATE PATIENT
    public static boolean updatePatient(int patientId,
                                        String demographics,
                                        String history) {
        String sql =
                "UPDATE PATIENT " +
                        "SET DEMOGRAPHICS = ?, HISTORY = ? " +
                        "WHERE PATIENT_ID_ = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, demographics);
            ps.setString(2, history);
            ps.setInt(3, patientId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Patient updated successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
        }

        return false;
    }

    // DELETE PATIENT
    public static boolean deletePatient(int patientId) {

        String sql =
                "DELETE FROM PATIENT " +
                        "WHERE PATIENT_ID_ = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Patient deleted successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Delete failed: " + e.getMessage());
        }

        return false;
    }

    //SELECT ALL PATIENTS
    public static void selectAllPatients() {

        String sql =
                "SELECT * FROM PATIENT " +
                        "ORDER BY PATIENT_ID_";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== ALL PATIENTS =====");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("PATIENT_ID_") + " | " +
                                rs.getString("NAME") + " | " +
                                rs.getString("DEMOGRAPHICS") + " | " +
                                rs.getString("HISTORY")
                );
            }

        } catch (SQLException e) {
            System.err.println("Select failed: " + e.getMessage());
        }
    }

    public static void selectPatientsWithPrescriptionCount() {
        String sql =
                "SELECT " +
                        "P.PATIENT_ID_, P.NAME, P.DEMOGRAPHICS, P.HISTORY, " +
                        "COUNT(R.MEDICATION_ID) AS Total_Prescriptions " +
                        "FROM PATIENT P " +
                        "LEFT JOIN APPOINTMENT A ON P.PATIENT_ID_ = A.PATIENT_ID_ " +
                        "LEFT JOIN DIAGNOSIS D ON A.DIAGNOSIS_ID = D.DIAGNOSIS_ID " +
                        "LEFT JOIN REQUIRES R ON D.DIAGNOSIS_ID = R.DIAGNOSIS_ID " +
                        "GROUP BY P.PATIENT_ID_, P.NAME, P.DEMOGRAPHICS, P.HISTORY " +
                        "ORDER BY P.PATIENT_ID_";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- Patients Prescription Count ---");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("PATIENT_ID_") + " | " +
                                rs.getString("NAME") + " | " +
                                rs.getInt("Total_Prescriptions")
                );
            }

        } catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
        }
    }

    //SEARCH BY NAME
    public static void searchPatientByName(String patientName) {

        String sql =
                "SELECT * FROM PATIENT " +
                        "WHERE NAME LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + patientName + "%");

            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== SEARCH RESULTS =====");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("PATIENT_ID_") + " | " +
                                rs.getString("NAME") + " | " +
                                rs.getString("DEMOGRAPHICS") + " | " +
                                rs.getString("HISTORY")
                );
            }

        } catch (SQLException e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }
}
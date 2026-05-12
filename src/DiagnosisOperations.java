import java.sql.*;

public class DiagnosisOperations {

    // insert diagnosis
    public static boolean insertDiagnosis(int diagnosisId, int appointmentId, String description) {

        String insertSql = "INSERT INTO DIAGNOSIS (DIAGNOSIS_ID, APPOINTMENT_ID, DESCRIPTION) " +
                "VALUES (?, ?, ?)";

        String updateAppointmentSql = "UPDATE APPOINTMENT " +
                "SET DIAGNOSIS_ID = ? " +
                "WHERE APPOINTMENT_ID = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement insertPs = con.prepareStatement(insertSql);
                 PreparedStatement updatePs = con.prepareStatement(updateAppointmentSql)) {

                insertPs.setInt(1, diagnosisId);
                insertPs.setInt(2, appointmentId);
                insertPs.setString(3, description);

                int diagnosisRows = insertPs.executeUpdate();

                updatePs.setInt(1, diagnosisId);
                updatePs.setInt(2, appointmentId);

                int appointmentRows = updatePs.executeUpdate();

                if (diagnosisRows > 0 && appointmentRows > 0) {
                    con.commit();
                    System.out.println("Diagnosis inserted and appointment updated successfully.");
                    return true;
                }

                con.rollback();

            } catch (SQLException e) {
                con.rollback();
                System.err.println("insertDiagnosis failed: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }

        return false;
    }

    // insert treatment plan
    public static boolean insertTreatmentPlan(int diagnosisId, int medicationId, String dosage, int duration) {

        String sql = "INSERT INTO REQUIRES (DIAGNOSIS_ID, MEDICATION_ID, DOSAGE, DURATION) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, diagnosisId);
            ps.setInt(2, medicationId);
            ps.setString(3, dosage);
            ps.setInt(4, duration);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Treatment plan inserted successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("insertTreatmentPlan failed: " + e.getMessage());
        }

        return false;
    }
    
    // select diagnoses with their treatment plans
    public static void selectDiagnosisTreatmentPlans() {

        String sql = "SELECT DG.DIAGNOSIS_ID, DG.APPOINTMENT_ID, DG.DESCRIPTION, " +
                "M.MEDICATION_NAME, R.DOSAGE, R.DURATION " +
                "FROM DIAGNOSIS DG " +
                "LEFT JOIN REQUIRES R ON DG.DIAGNOSIS_ID = R.DIAGNOSIS_ID " +
                "LEFT JOIN MEDICATION M ON R.MEDICATION_ID = M.MEDICATION_ID " +
                "ORDER BY DG.DIAGNOSIS_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== DIAGNOSIS TREATMENT PLANS =====");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("DIAGNOSIS_ID") + " | " +
                                rs.getInt("APPOINTMENT_ID") + " | " +
                                rs.getString("DESCRIPTION") + " | " +
                                rs.getString("MEDICATION_NAME") + " | " +
                                rs.getString("DOSAGE") + " | " +
                                rs.getString("DURATION")
                );
            }

        } catch (SQLException e) {
            System.err.println("selectDiagnosisTreatmentPlans failed: " + e.getMessage());
        }
    }
}
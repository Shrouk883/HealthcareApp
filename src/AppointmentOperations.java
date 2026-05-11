import java.sql.*;

public class AppointmentOperations {

    //INSERT
    public static boolean insertAppointment(int appointmentId,
                                            int diagnosisId,
                                            int doctorId,
                                            int patientId,
                                            String date,
                                            String time) {

        String sql = "INSERT INTO APPOINTMENT " +
                "(APPOINTMENT_ID, DIAGNOSIS_ID, DOCTOR_ID, PATIENT_ID_, DATE, TIME) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);
            ps.setInt(2, diagnosisId);
            ps.setInt(3, doctorId);
            ps.setInt(4, patientId);
            ps.setString(5, date);
            ps.setString(6, time);

            ps.executeUpdate();
            System.out.println("Appointment inserted successfully");
            return true;

        } catch (SQLException e) {
            System.err.println("insertAppointment failed: " + e.getMessage());
            return false;
        }
    }

    //UPDATE
    public static boolean updateAppointmentDoctor(int appointmentId, int doctorId) {

        String sql = "UPDATE APPOINTMENT SET DOCTOR_ID = ? WHERE APPOINTMENT_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setInt(2, appointmentId);

            int rows = ps.executeUpdate();
            System.out.println("Appointments updated: " + rows);
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("updateAppointment failed: " + e.getMessage());
            return false;
        }
    }

    //DELETE
    public static boolean deleteAppointment(int appointmentId) {

        String sql = "DELETE FROM APPOINTMENT WHERE APPOINTMENT_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);

            int rows = ps.executeUpdate();
            System.out.println("Appointment deleted: " + rows);
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("deleteAppointment failed: " + e.getMessage());
            return false;
        }
    }

    //SELECT ALL
    public static void selectAppointments() {

        String sql = "SELECT APPOINTMENT_ID, PATIENT_ID_, DOCTOR_ID, DIAGNOSIS_ID, DATE, TIME " +
                "FROM APPOINTMENT ORDER BY APPOINTMENT_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- All Appointments ---");
            System.out.printf("%-5s %-10s %-10s %-10s %-15s %-10s%n",
                    "ID", "Patient", "Doctor", "Diagnosis", "Date", "Time");
            System.out.println("-".repeat(70));

            while (rs.next()) {
                System.out.printf("%-5d %-10d %-10d %-10d %-15s %-10s%n",
                        rs.getInt("APPOINTMENT_ID"),
                        rs.getInt("PATIENT_ID_"),
                        rs.getInt("DOCTOR_ID"),
                        rs.getInt("DIAGNOSIS_ID"),
                        rs.getString("DATE"),
                        rs.getString("TIME"));
            }

        } catch (SQLException e) {
            System.err.println("selectAppointments failed: " + e.getMessage());
        }
    }
}
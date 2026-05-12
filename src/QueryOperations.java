import java.sql.*;

public class QueryOperations {

    private static void printQueryResult(ResultSet rs) throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rs.getString(i));

                if (i < columnCount) {
                    System.out.print(" | ");
                }
            }

            System.out.println();
        }
    }

    // Q1: specialty with highest consultations last month
    public static void selectTopSpecialtyLastMonth() {

        String sql =
                "SELECT TOP 1 D.EXPERTISE AS Specialty, " +
                        "COUNT(A.APPOINTMENT_ID) AS Total_Consultations " +
                        "FROM APPOINTMENT A " +
                        "JOIN DOCTOR D ON A.DOCTOR_ID = D.DOCTOR_ID " +
                        "WHERE MONTH(A.[DATE]) = MONTH(DATEADD(MONTH, -1, GETDATE())) " +
                        "AND YEAR(A.[DATE]) = YEAR(DATEADD(MONTH, -1, GETDATE())) " +
                        "GROUP BY D.EXPERTISE " +
                        "ORDER BY Total_Consultations DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== Q1: TOP SPECIALTY LAST MONTH =====");
            printQueryResult(rs);

        } catch (SQLException e) {
            System.err.println("selectTopSpecialtyLastMonth failed: " + e.getMessage());
        }
    }

    // Q2: practitioners with 0 consultations last month
    public static void selectDoctorsWithoutConsultationsLastMonth() {

        String sql =
                "SELECT D.DOCTOR_ID, D.[NAME], D.EXPERTISE " +
                        "FROM DOCTOR D " +
                        "WHERE D.DOCTOR_ID NOT IN ( " +
                        "SELECT DISTINCT A.DOCTOR_ID " +
                        "FROM APPOINTMENT A " +
                        "WHERE MONTH(A.[DATE]) = MONTH(DATEADD(MONTH, -1, GETDATE())) " +
                        "AND YEAR(A.[DATE]) = YEAR(DATEADD(MONTH, -1, GETDATE())) " +
                        ")";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== Q2: DOCTORS WITH NO CONSULTATIONS LAST MONTH =====");
            printQueryResult(rs);

        } catch (SQLException e) {
            System.err.println("selectDoctorsWithoutConsultationsLastMonth failed: " + e.getMessage());
        }
    }

    // Q3: patient with highest variety of medications last month
    public static void selectPatientWithHighestMedicationVarietyLastMonth() {

        String sql =
                "SELECT TOP 1 P.PATIENT_ID_, P.[NAME], " +
                        "COUNT(DISTINCT R.MEDICATION_ID) AS Distinct_Medications " +
                        "FROM PATIENT P " +
                        "JOIN APPOINTMENT A ON P.PATIENT_ID_ = A.PATIENT_ID_ " +
                        "JOIN DIAGNOSIS DG ON A.DIAGNOSIS_ID = DG.DIAGNOSIS_ID " +
                        "JOIN REQUIRES R ON DG.DIAGNOSIS_ID = R.DIAGNOSIS_ID " +
                        "WHERE MONTH(A.[DATE]) = MONTH(DATEADD(MONTH, -1, GETDATE())) " +
                        "AND YEAR(A.[DATE]) = YEAR(DATEADD(MONTH, -1, GETDATE())) " +
                        "GROUP BY P.PATIENT_ID_, P.[NAME] " +
                        "ORDER BY Distinct_Medications DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== Q3: PATIENT WITH HIGHEST MEDICATION VARIETY =====");
            printQueryResult(rs);

        } catch (SQLException e) {
            System.err.println("selectPatientWithHighestMedicationVarietyLastMonth failed: " + e.getMessage());
        }
    }

    // Q4: Branch with heighst number of distinct patients last month
    public static void selectBranchWithMostDistinctPatientsLastMonth() {

        String sql =
                "SELECT TOP 1 B.BRANCH_ID_, B.[_ADDRESS], " +
                        "COUNT(DISTINCT A.PATIENT_ID_) AS Distinct_Patients " +
                        "FROM BRANCH B " +
                        "JOIN DOCTOR D ON B.BRANCH_ID_ = D.BRANCH_ID_ " +
                        "JOIN APPOINTMENT A ON D.DOCTOR_ID = A.DOCTOR_ID " +
                        "WHERE MONTH(A.[DATE]) = MONTH(DATEADD(MONTH, -1, GETDATE())) " +
                        "AND YEAR(A.[DATE]) = YEAR(DATEADD(MONTH, -1, GETDATE())) " +
                        "GROUP BY B.BRANCH_ID_, B.[_ADDRESS] " +
                        "ORDER BY Distinct_Patients DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== Q4: BRANCH WITH MOST DISTINCT PATIENTS =====");
            printQueryResult(rs);

        } catch (SQLException e) {
            System.err.println("selectBranchWithMostDistinctPatientsLastMonth failed: " + e.getMessage());
        }
    }

    // Q5: all branch dignosis details
    public static void selectDiagnosisDetailsForBranchLastMonth(int branchId) {

        String sql =
                "SELECT B.[_ADDRESS], " +
                        "P.[NAME] AS Patient_Name, " +
                        "D.[NAME] AS Doctor_Name, " +
                        "A.[DATE], " +
                        "DG.DIAGNOSIS_ID, " +
                        "DG.[DESCRIPTION] " +
                        "FROM BRANCH B " +
                        "JOIN DOCTOR D ON B.BRANCH_ID_ = D.BRANCH_ID_ " +
                        "JOIN APPOINTMENT A ON D.DOCTOR_ID = A.DOCTOR_ID " +
                        "JOIN DIAGNOSIS DG ON A.DIAGNOSIS_ID = DG.DIAGNOSIS_ID " +
                        "JOIN PATIENT P ON A.PATIENT_ID_ = P.PATIENT_ID_ " +
                        "WHERE B.BRANCH_ID_ = ? " +
                        "AND MONTH(A.[DATE]) = MONTH(DATEADD(MONTH, -1, GETDATE())) " +
                        "AND YEAR(A.[DATE]) = YEAR(DATEADD(MONTH, -1, GETDATE()))";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, branchId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== Q5: DIAGNOSIS DETAILS FOR BRANCH " + branchId + " =====");
            printQueryResult(rs);

            rs.close();

        } catch (SQLException e) {
            System.err.println("selectDiagnosisDetailsForBranchLastMonth failed: " + e.getMessage());
        }
    }

    // Q6: all patients full profiles number of prescriptions issued
    public static void selectPatientsWithTotalPrescriptions() {

        String sql =
                "SELECT P.PATIENT_ID_, P.[NAME], P.DEMOGRAPHICS, P.HISTORY, " +
                        "COUNT(R.MEDICATION_ID) AS Total_Prescriptions " +
                        "FROM PATIENT P " +
                        "LEFT JOIN APPOINTMENT A ON P.PATIENT_ID_ = A.PATIENT_ID_ " +
                        "LEFT JOIN DIAGNOSIS DG ON A.DIAGNOSIS_ID = DG.DIAGNOSIS_ID " +
                        "LEFT JOIN REQUIRES R ON DG.DIAGNOSIS_ID = R.DIAGNOSIS_ID " +
                        "GROUP BY P.PATIENT_ID_, P.[NAME], P.DEMOGRAPHICS, P.HISTORY " +
                        "ORDER BY P.PATIENT_ID_";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== Q6: PATIENTS WITH TOTAL PRESCRIPTIONS =====");
            printQueryResult(rs);

        } catch (SQLException e) {
            System.err.println("selectPatientsWithTotalPrescriptions failed: " + e.getMessage());
        }
    }
}

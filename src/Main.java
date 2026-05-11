public class Main {

    public static void main(String[] args) {


        //PATIENTS
        System.out.println("=== PATIENTS ===");

        PatientOperations.insertPatient(10001, "Ali Ahmed", "Male, Cairo", "Healthy");
        PatientOperations.insertPatient(10002, "Sara Mohamed", "Female, Giza", "Asthma");
        PatientOperations.selectAllPatients();
        PatientOperations.updatePatient(10001, "Male, Cairo", "Updated History");
        PatientOperations.deletePatient(10002);
        PatientOperations.selectAllPatients();

        //DOCTORS
        System.out.println("\n=== DOCTORS ===");

        DoctorOperations.insertDoctor(10, 1, "Dr. Ahmed Hassan", "Cardiology");
        DoctorOperations.insertDoctor(20, 2, "Dr. Mona Ali", "Dermatology");
        DoctorOperations.selectDoctors();
        DoctorOperations.updateDoctor(20, "Dr. Mona Ali", "Neurology");
        DoctorOperations.deleteDoctor(20);
        DoctorOperations.selectDoctors();

        //MEDICATIONS
        System.out.println("\n=== MEDICATIONS ===");

        MedicationOperations.insertMedication(10, "Aspirin");
        MedicationOperations.insertMedication(11, "Paracetamol");
        MedicationOperations.selectMedications();
        MedicationOperations.updateMedication(10, "Ibuprofen");
        MedicationOperations.deleteMedication(11);
        MedicationOperations.selectMedications();

        //APPOINTMENTS
        System.out.println("\n=== APPOINTMENTS ===");

        AppointmentOperations.insertAppointment(
                10001, 1, 1, 101, "2026-05-11", "09:00"
        );

        AppointmentOperations.insertAppointment(
                10002, 2, 2, 102, "2026-05-12", "10:30"
        );

        AppointmentOperations.selectAppointments();
        AppointmentOperations.updateAppointmentDoctor(1002, 1);
        AppointmentOperations.deleteAppointment(1002);
        MedicationOperations.selectMedications();


        //JOIN QUERY
        System.out.println("\n=== PRESCRIPTION COUNT ===");

        PatientOperations.selectPatientsWithPrescriptionCount();
    }
}
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    private static final String SELECT_COLUMNS =
            "id, full_name, patient_type, phone, village, date_registered, last_visit, next_visit, "
                    + "CASE WHEN next_visit < CURDATE() AND status <> 'Completed' "
                    + "THEN 'Defaulter' ELSE status END AS status";

    public List<Patient> findPatients(String searchText, boolean defaultersOnly) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT ")
                .append(SELECT_COLUMNS)
                .append(" FROM patients WHERE 1 = 1");

        if (defaultersOnly) {
            sql.append(" AND next_visit < CURDATE() AND status <> 'Completed'");
        }
        if (searchText != null && !searchText.trim().isEmpty()) {
            sql.append(" AND (full_name LIKE ? OR phone LIKE ? OR village LIKE ?)");
        }
        sql.append(" ORDER BY id");

        List<Patient> patients = new ArrayList<>();
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            if (searchText != null && !searchText.trim().isEmpty()) {
                String searchPattern = "%" + searchText.trim() + "%";
                statement.setString(1, searchPattern);
                statement.setString(2, searchPattern);
                statement.setString(3, searchPattern);
            }

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    patients.add(new Patient(
                            results.getInt("id"),
                            results.getString("full_name"),
                            results.getString("patient_type"),
                            results.getString("phone"),
                            results.getString("village"),
                            results.getDate("date_registered").toLocalDate(),
                            results.getDate("last_visit").toLocalDate(),
                            results.getDate("next_visit").toLocalDate(),
                            results.getString("status")
                    ));
                }
            }
        }
        return patients;
    }

    public void addPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients "
                + "(full_name, patient_type, phone, village, date_registered, last_visit, next_visit, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setPatientValues(statement, patient);
            statement.executeUpdate();
        }
    }

    public void updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE patients SET full_name = ?, patient_type = ?, phone = ?, village = ?, "
                + "date_registered = ?, last_visit = ?, next_visit = ?, status = ? WHERE id = ?";
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setPatientValues(statement, patient);
            statement.setInt(9, patient.getId());
            if (statement.executeUpdate() == 0) {
                throw new SQLException("The selected patient record no longer exists.");
            }
        }
    }

    public void deletePatient(int id) throws SQLException {
        try (Connection connection = Db.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM patients WHERE id = ?")) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("The selected patient record no longer exists.");
            }
        }
    }

    private void setPatientValues(PreparedStatement statement, Patient patient) throws SQLException {
        statement.setString(1, patient.getFullName());
        statement.setString(2, patient.getPatientType());
        statement.setString(3, patient.getPhone());
        statement.setString(4, patient.getVillage());
        statement.setDate(5, Date.valueOf(patient.getDateRegistered()));
        statement.setDate(6, Date.valueOf(patient.getLastVisit()));
        statement.setDate(7, Date.valueOf(patient.getNextVisit()));
        statement.setString(8, patient.getStatus());
    }
}
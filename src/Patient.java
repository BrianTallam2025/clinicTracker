import java.time.LocalDate;

public class Patient {
    private int id;
    private String fullName;
    private String patientType;
    private String phone;
    private String village;
    private LocalDate dateRegistered;
    private LocalDate lastVisit;
    private LocalDate nextVisit;
    private String status;

    public Patient(int id, String fullName, String patientType, String phone, String village,
                   LocalDate dateRegistered, LocalDate lastVisit, LocalDate nextVisit, String status) {
        this.id = id;
        this.fullName = fullName;
        this.patientType = patientType;
        this.phone = phone;
        this.village = village;
        this.dateRegistered = dateRegistered;
        this.lastVisit = lastVisit;
        this.nextVisit = nextVisit;
        this.status = status;
    }

    public Patient(String fullName, String patientType, String phone, String village,
                   LocalDate dateRegistered, LocalDate lastVisit, LocalDate nextVisit, String status) {
        this(0, fullName, patientType, phone, village, dateRegistered, lastVisit, nextVisit, status);
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPatientType() {
        return patientType;
    }

    public String getPhone() {
        return phone;
    }

    public String getVillage() {
        return village;
    }

    public LocalDate getDateRegistered() {
        return dateRegistered;
    }

    public LocalDate getLastVisit() {
        return lastVisit;
    }

    public LocalDate getNextVisit() {
        return nextVisit;
    }

    public String getStatus() {
        return status;
    }
}
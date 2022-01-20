package SchedulingApp.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 */
public class AppointmentRedux {
    private IntegerProperty appointmentId = new SimpleIntegerProperty();
    private IntegerProperty customerId = new SimpleIntegerProperty();
    private IntegerProperty userId = new SimpleIntegerProperty();
    private StringProperty title = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private StringProperty location = new SimpleStringProperty();
    private StringProperty contact = new SimpleStringProperty();
    private StringProperty type = new SimpleStringProperty();
    private ZonedDateTime start;
    private ZonedDateTime end;
    private LocalDateTime createDate;
    private Date startDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    private Customer customer = new Customer();
    private int contactId;
    private String customerName;

    /**
     * Empty constructor for creating an appointment
     */
    public AppointmentRedux() {

    }

    /**
     * @return returns the appointmentID
     */
    public final int getAppointmentId() {
        return appointmentId.get();
    }

    /**
     * @param appointmentId takes the appointmentID as an int to set the appointmentID
     */
    public final void setAppointmentId(int appointmentId) {
        this.appointmentId.set(appointmentId);
    }

    /**
     * @return returns the appointmentID as an integer property to be set in the tables
     */
    public IntegerProperty appointmentIdProperty() {
        return appointmentId;
    }

    /**
     * @return retrieves the customerID
     */
    public final int getCustomerId() {
        return customerId.get();
    }

    /**
     * @param customerId sets customerID as a parameter for the appointment
     */
    public final void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    /**
     * @return returns the customerID as an IntegerProperty to be set in the tables
     */
    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    /**
     * @return returns the UserId
     */
    public final int getUserId() {
        return userId.get();
    }

    /**
     * @param userId sets the UserID as a parameter for the appointment
     */
    public final void setUserId(int userId) {
        this.userId.set(userId);
    }

    /**
     * @return returns the UserID as an Integer property to be set in the tables
     */
    public IntegerProperty userIdProperty() {
        return userId;
    }

    /**
     * @return retrieves the title for the appointment
     */
    public String getTitle() {
        return title.get();
    }

    /**
     * @param title Sets the title as a parameter for the appointment
     */
    public void setTitle(String title) {
        this.title.set(title);
    }

    /**
     * @return returns the title as a StringProperty to be set in the tables
     */
    public StringProperty titleProperty() {
        return title;
    }

    /**
     * @return returns the description for the appointment
     */
    public String getDescription() {
        return description.get();
    }

    /**
     * @param description sets the description as a parameter for the appointment
     */
    public void setDescription(String description) {
        this.description.set(description);
    }

    /**
     * @return returns the description as a StringProperty to be set in the tables
     */
    public StringProperty descriptionProperty() {
        return description;
    }

    /**
     * @return returns the location of the appointment
     */
    public String getLocation() {
        return location.get();
    }

    /**
     * @param location sets the location as a parameter for the appointment
     */
    public void setLocation(String location) {
        this.location.set(location);
    }

    /**
     * @return returns the location as a StringProperty to be set in the tables
     */
    public StringProperty locationProperty() {
        return location;
    }

    /**
     * @return returns the Contact of the appointment
     */
    public String getContact() {
        return contact.get();
    }

    /**
     * @param contact sets the Contact as a parameter for the appointment
     */
    public void setContact(String contact) {
        this.contact.set(contact);
    }

    /**
     * @return returns the Contact as a StringProperty to be set in the tables
     */
    public StringProperty contactProperty() {
        return contact;
    }

    /**
     * @return returns the type of appointment
     */
    public String getType() {
        return type.get();
    }

    /**
     * @param type sets the type as a parameter for the appointment
     */
    public void setType(String type) {
        this.type.set(type);
    }

    /**
     * @return returns the type as a StringProperty to be set in the tables
     */
    public StringProperty typeProperty() {
        return type;
    }

    /**
     * @return returns the start date/time of the appointment
     */
    public ZonedDateTime getStart() {
        return start;
    }

    /**
     * @param start sets the start date/time as a parameter of the appointment
     */
    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    /**
     * @return returns the end date/time of the appointment
     */
    public ZonedDateTime getEnd() {
        return end;
    }

    /**
     * @param end sets the end date/time as a parameter of the appointment
     */
    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    /**
     * @return returns the date the appointment was created on
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate sets the createDate as a parameter of the appointment
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * @return returns the userName of the user that created the appointment
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy sets the userName of the user that created the appointment as a parameter of the appointment
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return returns the timestamp of the most recent update of the appointment
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate sets the timestamp of the most recent update of the appointment
     */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return returns the userName of the user who most recently updated the appointment
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * @param lastUpdatedBy sets the userName of the user who most recently updated the appointment
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * @return Returns the Customer that has been added to the appointment
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @param customer sets the Customer as a parameter of the appointment
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * @return returns the name of the customer that has been added to the appointment
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName sets the name of the customer that has been added to the appointment
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return returns the ContactID of the contact that has been assigned to the appointment
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * @param contactId sets the ContactID as a parameter of the appointment
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * @return returns True if all of the parameters are met for the appointment, false if required information of the appointment is missing
     */
    public boolean isValidInput() {
        ResourceBundle rb = ResourceBundle.getBundle("Appointment", Locale.getDefault());
        if(this.customer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("errorAddingAppointment"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorCustomer"));
            alert.showAndWait();
            return false;
        }
        else if(this.title.get().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("errorAddingAppointment"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorTitle"));
            alert.showAndWait();
            return false;
        }
        else if(this.description.get().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("errorAddingAppointment"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorDescription"));
            alert.showAndWait();
            return false;
        }
        else if(this.location.get().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("errorAddingAppointment"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorLocation"));
            alert.showAndWait();
            return false;
        }
        else if(this.contact.get().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("errorAddingAppointment"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorContact"));
            alert.showAndWait();
            return false;
        }
        else if(this.type.get().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("errorAddingAppointment"));
            alert.setHeaderText(rb.getString("errorAddingAppointment"));
            alert.setContentText(rb.getString("errorType"));
            alert.showAndWait();
            return false;
        }
        if(!isValidTime())  {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * @return returns True if the selected Date/Time meets all of the criteria, false if the Date/Time needs to be changed
     */
    public boolean isValidTime() {
        ResourceBundle rb = ResourceBundle.getBundle("Appointment", Locale.getDefault());
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate apptStartDate = this.start.toLocalDate();
        LocalTime apptStartTime = this.start.toLocalTime();
        LocalDate apptEndDate = this.end.toLocalDate();
        LocalTime apptEndTime = this.end.toLocalTime();
        int weekDay = apptStartDate.getDayOfWeek().getValue();

        if(!apptStartDate.isEqual(apptEndDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("invalidDateTime"));
            alert.setHeaderText(rb.getString("invalidDateTime"));
            alert.setContentText(rb.getString("errorDifferentDate"));
            alert.showAndWait();
            return false;
        }
        else if(weekDay == 6 || weekDay == 7) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("invalidDateTime"));
            alert.setHeaderText(rb.getString("invalidDateTime"));
            alert.setContentText(rb.getString("errorDateIsWeekend"));
            alert.showAndWait();
            return false;
        }
        else if(apptStartTime.isBefore(midnight.plusHours(8))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("invalidDateTime"));
            alert.setHeaderText(rb.getString("invalidDateTime"));
            alert.setContentText(rb.getString("errorStartEndOutsideHours"));
            alert.showAndWait();
            return false;
        }
        else if(apptEndTime.isAfter(midnight.plusHours(22))) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("invalidDateTime"));
            alert.setHeaderText(rb.getString("invalidDateTime"));
            alert.setContentText(rb.getString("errorStartEndOutsideHours"));
            alert.showAndWait();
            return false;
        }
        else if(apptStartDate.isBefore(LocalDate.now()) || apptStartTime.isBefore(LocalTime.MIDNIGHT)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("invalidDateTime"));
            alert.setHeaderText(rb.getString("invalidDateTime"));
            alert.setContentText(rb.getString("errorStartBeforeNow"));
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * @return returns True if the appointment does not overlap with any existing appointments, False if there is an overlap
     */
    public boolean isNotOverlapping() {
        ObservableList<AppointmentRedux> overlappingAppt = DatabaseManager.getOverlappingAppts(this.start.toLocalDateTime(), this.end.toLocalDateTime());
        if(overlappingAppt.size() > 0) {
            return false;
        }
        return true;
    }
































}

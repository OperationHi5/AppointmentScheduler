/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.ViewController;

import SchedulingApp.Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

/** Allows the modification of an appointment */
public class ModifyAppointmentController {

    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String db = "WJ05TZp";
    private static final String url = "jdbc:mysql://wgudb.ucertify.com:3306/" + db;
    private static final String user = "U05TZp";
    private static final String pass = "53688602409";

    @FXML
    private Label modifyAppointmentWindowLabel;
    @FXML
    private Label appointmentCustomerLabel;
    @FXML
    private ComboBox<Customer> appointmentCustomerComboBox;
    @FXML
    private Label appointmentTitleLabel;
    @FXML
    private TextField appointmentTitleField;
    @FXML
    private Label appointmentDescriptionLabel;
    @FXML
    private TextField appointmentDescriptionField;
    @FXML
    private Label appointmentLocationLabel;
    @FXML
    private TextField appointmentLocationField;
    @FXML
    private Label appointmentTypeLabel;
    @FXML
    private TextField appointmentTypeField;
    @FXML
    private Label appointmentContactLabel;
    @FXML
    private ComboBox<String> appointmentContactPicker;
    @FXML
    private Label appointmentDateLabel;
    @FXML
    private DatePicker appointmentStartDatePicker;
    @FXML
    private Label appointmentStartTimeLabel;
    @FXML
    private Spinner<LocalTime> appointmentStartTime;
    @FXML
    private Label appointmentEndDateLabel;
    @FXML
    private DatePicker appointmentEndDatePicker;
    @FXML
    private Label appointmentEndTimeLabel;
    @FXML
    private Spinner<LocalTime> appointmentEndTime;
    @FXML
    private Button saveModifyAppointmentButton;
    @FXML
    private Button cancelModifyAppointmentButton;

    @FXML
    private final DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    private final DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("hh:mm a");

    private static ZoneId  zoneId = ZoneId.systemDefault();

    @FXML
    public static AppointmentRedux appointment = new AppointmentRedux();

    private static Customer customer;

    /** Initialize an appointment */
    private static AppointmentRedux selectedAppointment;

    /** Initializes an ObservableList to hold the customers that were added to the appointment */
    private ObservableList<Customer> currentCustomers = FXCollections.observableArrayList();

    private String getAllContactsQuery = "SELECT Contact_Name FROM contacts";


    /** Set labels to the local language, default is English */
    @FXML
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        modifyAppointmentWindowLabel.setText(rb.getString("lblModifyAppointment"));
        appointmentCustomerLabel.setText(rb.getString("lblCustomer"));
        appointmentTitleLabel.setText(rb.getString("lblTitle"));
        appointmentTitleField.setPromptText(rb.getString("lblTitle"));
        appointmentDescriptionLabel.setText(rb.getString("lblDescription"));
        appointmentDescriptionField.setPromptText(rb.getString("lblDescription"));
        appointmentLocationLabel.setText(rb.getString("lblLocation"));
        appointmentLocationField.setPromptText(rb.getString("lblLocation"));
        appointmentTypeLabel.setText(rb.getString("lblType"));
        appointmentTypeField.setPromptText(rb.getString("lblType"));
        appointmentContactLabel.setText(rb.getString("lblContact"));
        appointmentDateLabel.setText(rb.getString("lblDate"));
        appointmentStartTimeLabel.setText(rb.getString("lblStartTime"));
        appointmentEndTimeLabel.setText(rb.getString("lblEndTime"));
        saveModifyAppointmentButton.setText(rb.getString("btnSave"));
        cancelModifyAppointmentButton.setText(rb.getString("btnCancel"));
    }

    /** Gets the customerId from the related Appointment
     * @param appointmentId takes the appointmentID as input and uses it to get the CustomerID from the associated appointment
     * @return
     * */
    private int getCustomerId(int appointmentId) {
        int customerId;
        try (Connection connection = DriverManager.getConnection(url,user,pass);
             Statement statement = connection.createStatement()) {
            ResultSet results = statement.executeQuery("SELECT Customer_ID FROM appointments WHERE Appointment_ID = " + appointmentId);
            results.next();
            customerId = results.getInt(1);
            return customerId;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * Gets the inputted information from the fields to modify the appointment
     */
    public void getAppointmentInfo() {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        try {
            appointment.setCustomer(appointmentCustomerComboBox.getValue());
            appointment.setCustomerId(appointmentCustomerComboBox.getValue().getCustomerId());
            appointment.setTitle(appointmentTitleField.getText());
            appointment.setDescription(appointmentDescriptionField.getText());
            appointment.setLocation(appointmentLocationField.getText());
            appointment.setContact(appointmentContactPicker.getSelectionModel().getSelectedItem());
            appointment.setType(appointmentLocationField.getText());
            appointment.setStart(ZonedDateTime.of(LocalDate.parse(appointmentStartDatePicker.getValue().toString(), formatDate),
                    LocalTime.parse(appointmentStartTime.getValue().toString()), zoneId));
            appointment.setEnd(ZonedDateTime.of(LocalDate.parse(appointmentEndDatePicker.getValue().toString(),formatDate),
                    LocalTime.parse(appointmentEndTime.getValue().toString()), zoneId));
            appointment.setAppointmentId(selectedAppointment.getAppointmentId());
        }
        catch (NullPointerException e) {
            Alert nullAlert = new Alert(Alert.AlertType.ERROR);
            nullAlert.setTitle(rb.getString("errorModifyingAppointment"));
            nullAlert.setHeaderText(rb.getString("errorModifyingAppointment"));
            nullAlert.setContentText(rb.getString("errorNoCustomer"));
            nullAlert.showAndWait();
        }
    }


    /**
     * @param event takes the information that has been inputted, validates that all of the required information is there, and ensures that the appointment doesn't overlap
     *              and submits it to update the information in the database.
     */
    @FXML
    void saveModifyAppointment(ActionEvent event) {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
        saveAlert.setTitle(rb.getString("appointmentSave"));
        saveAlert.setHeaderText(rb.getString("appointmentSaveHeader"));
        saveAlert.setContentText(rb.getString("appointmentSaveContext"));
        saveAlert.showAndWait();
        if(saveAlert.getResult() == ButtonType.OK) {
            try {
                getAppointmentInfo();
                appointment.isValidInput();
                appointment.isNotOverlapping();
                if(!appointment.isValidInput()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(rb.getString("errorModifyingAppointment"));
                    alert.setHeaderText(rb.getString("errorModifyingAppointment"));
                    alert.setContentText(rb.getString("errorModifyingAppointmentInfo"));
                    alert.showAndWait();
                }
                else if(!appointment.isNotOverlapping()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(rb.getString("errorAppointmentOverlap"));
                    alert.setHeaderText(rb.getString("errorAppointmentOverlapFound"));
                    alert.setContentText(rb.getString("errorChangeAppointmentTime"));
                    alert.showAndWait();
                }
                else if(appointment.isValidInput() && appointment.isNotOverlapping()) {
                    DatabaseManager.modifyAppointment(appointment);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
                    Parent root = loader.load();
                    Stage mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    mainWindow.setTitle("Appointment Scheduling App");
                    mainWindow.setScene(new Scene(root));
                    mainWindow.show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            saveAlert.close();
        }
    }

    /** Cancel modifying the appointment
     * @param event takes an actionEvent as input and uses it to cancel the modification of the appointment and return to the appointment summary window
     * */
    @FXML
    private void cancelModifyingAppointment(ActionEvent event) {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        /** Shows an alert to confirm cancelling */
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(rb.getString("confirmCancel"));
        alert.setHeaderText(rb.getString("confirmCancel"));
        alert.setContentText(rb.getString("confirmCancelModifyingMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        /** Checks if the OK button was selected and returns to the appointment summary window */
        if(result.get() == ButtonType.OK) {
            try {
                Parent mainWindowParent = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
                Scene mainWindowScene = new Scene(mainWindowParent);
                Stage mainWindowStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainWindowStage.setScene(mainWindowScene);
                mainWindowStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            alert.close();
        }
    }

    /**
     * Sets the fields in the window to the information that was previously added for the appointment
     */
    public void setSelectedAppointmentInfo() {
        selectedAppointment = MainWindowController.getSelectedAppointment();
        appointmentCustomerComboBox.setValue(selectedAppointment.getCustomer());
        appointmentTitleField.setText(selectedAppointment.getTitle());
        appointmentDescriptionField.setText(selectedAppointment.getDescription());
        appointmentLocationField.setText(selectedAppointment.getLocation());
        appointmentContactPicker.setValue(selectedAppointment.getContact());
        appointmentTypeField.setText(selectedAppointment.getType());
        appointmentStartDatePicker.setValue(selectedAppointment.getStart().toLocalDate());
        appointmentStartTime.setValueFactory(spinnerStart);
        spinnerStart.setValue(selectedAppointment.getStart().toLocalTime());
        appointmentEndDatePicker.setValue(selectedAppointment.getEnd().toLocalDate());
        appointmentEndTime.setValueFactory(spinnerEnd);
        spinnerEnd.setValue(selectedAppointment.getEnd().toLocalTime());
    }

    /**
     * Retrieves a list of all of the customers in the database to be passed into the combobox
     */
    public void getAllCustomers() {
        DatabaseManager.updateCustomerList();
        ObservableList<Customer> allCustomers = CustomerList.getCustomerList();
        appointmentCustomerComboBox.setItems(allCustomers);
    }

    public void setDefaultDateTime() {
        appointmentStartDatePicker.setValue(LocalDate.now());
        appointmentEndDatePicker.setValue(LocalDate.now());
        appointmentStartTime.setValueFactory(spinnerStart);
        spinnerStart.setValue(LocalTime.of(8, 00));
        appointmentEndTime.setValueFactory(spinnerEnd);
        spinnerEnd.setValue(LocalTime.of(17,00));
    }


    /**
     * Converts the Customer Name into a value that can be passed into the combobox
     */
    public void convertCustomer() {
        appointmentCustomerComboBox.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                return customer.getCustomerName();
            }

            @Override
            public Customer fromString(String string) {
                return appointmentCustomerComboBox.getValue();
            }
        });
    }

    /** Searches the database for all Contacts and adds them to an observable arraylist to be passed to the Combo Box
     * @return returns a list of all contacts to be passed into the Contact Selector ComboBox
     * */
    public ObservableList<String> getAllContacts() {
        ObservableList<String> contactList = FXCollections.observableArrayList();
        try(Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement statement = connection.prepareStatement(getAllContactsQuery)) {
            ResultSet contactResults = statement.executeQuery();
            while(contactResults.next()) {
                String contact = contactResults.getString(1);
                contactList.add(contact);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return contactList;
    }

    SpinnerValueFactory spinnerStart = new SpinnerValueFactory<LocalTime>() {
        {
            setConverter(new LocalTimeStringConverter(formatTime, null));
        }
        @Override
        public void decrement(int steps) {
            LocalTime time = (LocalTime) getValue();
            setValue(time.minusHours(steps));
            setValue(time.minusMinutes(16-steps));
        }

        @Override
        public void increment(int steps) {
            LocalTime time = (LocalTime) getValue();
            setValue(time.plusHours(steps));
            setValue(time.plusMinutes(steps + 14));
        }
    };

    SpinnerValueFactory spinnerEnd = new SpinnerValueFactory<LocalTime>() {
        {
            setConverter(new LocalTimeStringConverter(formatTime, null));
        }
        @Override
        public void decrement(int steps) {
            LocalTime time = (LocalTime) getValue();
            setValue(time.minusHours(steps));
            setValue(time.minusMinutes(16-steps));
        }

        @Override
        public void increment(int steps) {
            LocalTime time = (LocalTime) getValue();
            setValue(time.plusHours(steps));
            setValue(time.plusMinutes(steps + 14));
        }
    };



    /** Sets the contact list to the contact picker combobox */
    @FXML
    public void setContacts() {
        appointmentContactPicker.setItems(getAllContacts());
    }

    /** Initialize the Window Elements */
    public void initialize() {
        /** Setting the local language */
        setLanguage();
        setContacts();
        setSelectedAppointmentInfo();
        convertCustomer();
        getAllCustomers();
    }

}

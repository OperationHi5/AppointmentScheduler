/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.ViewController;

import SchedulingApp.Model.*;
import SchedulingApp.Model.AppointmentRedux;
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
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

/** Handles the addition of an appointment */
public class AddAppointmentController {

    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String db = "WJ05TZp";
    private static final String url = "jdbc:mysql://wgudb.ucertify.com:3306/" + db;
    private static final String user = "U05TZp";
    private static final String pass = "53688602409";

    @FXML
    private Label appointmentEndDateLabel;
    @FXML
    private ComboBox<Customer> appointmentCustomerComboBox;
    @FXML
    private Label appointmentCustomerLabel;
    @FXML
    private Label addAppointmentWindowLabel;
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
    private DatePicker appointmentEndDatePicker;
    @FXML
    private Label appointmentStartTimeLabel;
    @FXML
    private Label appointmentEndTimeLabel;
    @FXML
    private Button deleteCustomerFromAppointmentButton;
    @FXML
    private Button saveAppointmentButton;
    @FXML
    private Button cancelAddingAppointmentButton;
    @FXML
    private Spinner<LocalTime> appointmentStartTime;
    @FXML
    private Spinner<LocalTime> appointmentEndTime;

    @FXML
    private final DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    private final DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("hh:mm a");

    private static ZoneId zoneId = ZoneId.systemDefault();


    private static Customer customer;

    private String getAllContactsQuery = "SELECT Contact_Name FROM contacts";

    /** Creating a list of the customers assigned to this appointment */
    private ObservableList<Customer> currentCustomers = FXCollections.observableArrayList();
    @FXML
    public static AppointmentRedux appointmentRedux = new AppointmentRedux();

    /** Setting the labels to the local language, English is the default */
    @FXML
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        addAppointmentWindowLabel.setText(rb.getString("lblAddAppointment"));
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
        saveAppointmentButton.setText(rb.getString("btnSave"));
        cancelAddingAppointmentButton.setText(rb.getString("btnCancel"));
    }

    /**
     * Converts the Customer Name to a value that can be passed into the combobox
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
     * @return this returns a list of all contacts in the database to be passed to the contact picker combo box
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

    /**
     * Retrieves a list of all customers to populate the combobox
     */
    public void getAllCustomers() {
        DatabaseManager.updateCustomerList();
        ObservableList<Customer> allCustomers = CustomerList.getCustomerList();
        appointmentCustomerComboBox.setItems(allCustomers);
        appointmentCustomerComboBox.setPromptText("Select a Customer:");
    }

    /** sets the contact list to the contact combo box */
    @FXML
    public void setContacts() {
        appointmentContactPicker.setItems(getAllContacts());
    }

    /**
     * Sets the starting Date/Time of the DatePicker and Time Spinners
     */
    public void setDefaultDateTime() {
        appointmentStartDatePicker.setValue(LocalDate.now());
        appointmentEndDatePicker.setValue(LocalDate.now());
        appointmentStartTime.setValueFactory(spinnerStart);
        spinnerStart.setValue(LocalTime.of(8, 00));
        appointmentEndTime.setValueFactory(spinnerEnd);
        spinnerEnd.setValue(LocalTime.of(17,00));
    }

    /** Cancel adding a new appointment
     * @param event takes an ActionEvent as input and cancels the addition of the appointment
     * */
    @FXML
    private void cancelAddingAppointment(ActionEvent event) {
        /** Creates a confirmation box for cancelling the addition of the new customer */
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(rb.getString("confirmCancel"));
        alert.setHeaderText(rb.getString("confirmCancel"));
        alert.setContentText(rb.getString("confirmCancelAddingMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        /** Checks to see if the OK button was clicked and returns to the main window if it was */
        if(result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
                Parent root = loader.load();
                Stage mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainWindow.setTitle("Appointment Scheduling App");
                mainWindow.setScene(new Scene(root));
                mainWindow.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves the info of the appointment that has been inputted.
     */
    public void getAppointmentInfo() {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        try {
            appointmentRedux.setCustomer(appointmentCustomerComboBox.getValue());
            appointmentRedux.setCustomerId(appointmentCustomerComboBox.getValue().getCustomerId());
            appointmentRedux.setTitle(appointmentTitleField.getText());
            appointmentRedux.setDescription(appointmentDescriptionField.getText());
            appointmentRedux.setLocation(appointmentLocationField.getText());
            appointmentRedux.setContact(appointmentContactPicker.getSelectionModel().getSelectedItem());
            appointmentRedux.setType(appointmentLocationField.getText());
            appointmentRedux.setStart(ZonedDateTime.of(LocalDate.parse(appointmentStartDatePicker.getValue().toString(), formatDate),
                                                        LocalTime.parse(appointmentStartTime.getValue().toString()), zoneId));
            appointmentRedux.setEnd(ZonedDateTime.of(LocalDate.parse(appointmentEndDatePicker.getValue().toString(),formatDate),
                                                        LocalTime.parse(appointmentEndTime.getValue().toString()), zoneId));
        }
        catch (NullPointerException e) {
            Alert nullAlert = new Alert(Alert.AlertType.ERROR);
            nullAlert.setTitle(rb.getString("errorAddingAppointment"));
            nullAlert.setHeaderText(rb.getString("errorAddingAppointment"));
            nullAlert.setContentText(rb.getString("errorNoCustomer"));
            nullAlert.showAndWait();
        }
    }

    /**
     * @param event Takes the information that has been inputted, validates that all required information is there and that the appointment doesn't overlap and submits it to be
     *              added to the database
     */
    @FXML
    void saveAppointment(ActionEvent event) {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
        Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
        saveAlert.setTitle(rb.getString("appointmentSave"));
        saveAlert.setHeaderText(rb.getString("appointmentSaveHeader"));
        saveAlert.setContentText(rb.getString("appointmentSaveContext"));
        saveAlert.showAndWait();
        if(saveAlert.getResult() == ButtonType.OK) {
            try {
                getAppointmentInfo();
                appointmentRedux.isValidInput();
                appointmentRedux.isNotOverlapping();
                if(!appointmentRedux.isValidInput()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(rb.getString("errorAddingAppointment"));
                    alert.setHeaderText(rb.getString("errorAddingAppointment"));
                    alert.setContentText(rb.getString("errorAddingAppointmentInfo"));
                    alert.showAndWait();
                }
                else if(!appointmentRedux.isNotOverlapping()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(rb.getString("errorAppointmentOverlap"));
                    alert.setHeaderText(rb.getString("errorAppointmentOverlapFound"));
                    alert.setContentText(rb.getString("errorChangeAppointmentTime"));
                    alert.showAndWait();
                }
                else if(appointmentRedux.isValidInput() && appointmentRedux.isNotOverlapping()) {
                    DatabaseManager.addAppointment(appointmentRedux);
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



    /** Initializes the window elements */
    @FXML
    public void initialize() {
        setLanguage();
        setContacts();
        getAllCustomers();
        convertCustomer();
        setDefaultDateTime();
    }













































}

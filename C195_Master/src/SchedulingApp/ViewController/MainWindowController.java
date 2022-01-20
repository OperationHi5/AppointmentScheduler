/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.ViewController;

import SchedulingApp.Model.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/** Handles all of the elements for the main window of the application and all of the various functions of the app */
public class MainWindowController {

    @FXML
    private TableView<AppointmentRedux> tvAppointments;
    @FXML
    private TableColumn<AppointmentRedux, Integer> tvAppointmentIdColumn;
    @FXML
    private TableColumn<AppointmentRedux, String> tvAppointmentTitleColumn;
    @FXML
    private TableColumn<AppointmentRedux, String> tvAppointmentDescriptionColumn;
    @FXML
    private TableColumn<AppointmentRedux, String> tvAppointmentLocationColumn;
    @FXML
    private TableColumn<AppointmentRedux, String> tvAppointmentContactColumn;
    @FXML
    private TableColumn<AppointmentRedux, String> tvAppointmentTypeColumn;
    @FXML
    private TableColumn<AppointmentRedux, String> tvAppointmentStartColumn;
    @FXML
    private TableColumn<AppointmentRedux, String> tvAppointmentEndColumn;
    @FXML
    private TableColumn<AppointmentRedux, String> tvAppointmentCustomerIdColumn;
    @FXML
    private AnchorPane mainWindowPane;
    @FXML
    private Button addAppointmentButton;
    @FXML
    private Button modifyAppointmentButton;
    @FXML
    private Button deleteAppointmentButton;
    @FXML
    private Button reportsButton;
    @FXML
    private RadioButton monthlyCalendarButton;
    @FXML
    private Label monthlyCalendarButtonLabel;
    @FXML
    private RadioButton weeklyCalendarButton;
    @FXML
    private Label weeklyCalendarButtonLabel;
    @FXML
    public TableView<Customer> tvCustomers;
    @FXML
    private TableColumn<Customer, String> tvCustomerIdColumn;
    @FXML
    private TableColumn<Customer, String> tvCustomersNameColumn;
    @FXML
    private TableColumn<Customer, String> tvCustomersAddressColumn;
    @FXML
    private TableColumn<Customer, String> tvCustomersPostalCodeColumn;
    @FXML
    private TableColumn<Customer, String> tvCustomerDivisionColumn;
    @FXML
    private TableColumn<Customer, String> tvCustomersCountryColumn;
    @FXML
    private TableColumn<Customer, String> tvCustomersPhoneColumn;
    @FXML
    private Button addCustomerButton;
    @FXML
    private Button modifyCustomerButton;
    @FXML
    private Button deleteCustomerButton;
    @FXML
    private Button exitButton;

    @FXML
    private final DateTimeFormatter formatDT = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

    private static Customer selectedCustomer;

    private static AppointmentRedux selectedAppointment;

    /** Tells whether the calendar is in monthly or weekly view. Starts in monthly view */
    private boolean monthlyView = true;

    /** Sets labels to the local language. English is the default */
    @FXML
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("MainWindow", Locale.getDefault());
        weeklyCalendarButtonLabel.setText(rb.getString("weeklyCalendarButtonLabel"));
        monthlyCalendarButtonLabel.setText(rb.getString("monthlyCalendarButtonLabel"));
        addAppointmentButton.setText(rb.getString("addAppointmentButton"));
        modifyAppointmentButton.setText(rb.getString("modifyAppointmentButton"));
        deleteAppointmentButton.setText(rb.getString("deleteAppointmentButton"));
        reportsButton.setText(rb.getString("reportsButton"));
        addCustomerButton.setText(rb.getString("addCustomerButton"));
        modifyCustomerButton.setText(rb.getString("modifyCustomerButton"));
        deleteCustomerButton.setText(rb.getString("deleteCustomerButton"));
        exitButton.setText(rb.getString("exitButton"));
        tvAppointmentIdColumn.setText(rb.getString("tvAppointmentIdColumn"));
        tvAppointmentTitleColumn.setText(rb.getString("tvAppointmentTitleColumn"));
        tvAppointmentDescriptionColumn.setText(rb.getString("tvAppointmentDescriptionColumn"));
        tvAppointmentLocationColumn.setText(rb.getString("tvAppointmentLocationColumn"));
        tvAppointmentContactColumn.setText(rb.getString("tvAppointmentContactColumn"));
        tvAppointmentTypeColumn.setText(rb.getString("tvAppointmentTypeColumn"));
        tvAppointmentStartColumn.setText(rb.getString("tvAppointmentStartColumn"));
        tvAppointmentEndColumn.setText(rb.getString("tvAppointmentEndColumn"));
        tvAppointmentCustomerIdColumn.setText(rb.getString("tvAppointmentCustomerIdColumn"));
        tvCustomerIdColumn.setText(rb.getString("tvCustomerIdLabel"));
        tvCustomersNameColumn.setText(rb.getString("tvCustomerNameLabel"));
        tvCustomersAddressColumn.setText(rb.getString("tvAddressLabel"));
        tvCustomersPostalCodeColumn.setText(rb.getString("tvPostalCodeLabel"));
        tvCustomerDivisionColumn.setText(rb.getString("tvDivisionLabel"));
        tvCustomersCountryColumn.setText(rb.getString("tvCountryLabel"));
        tvCustomersPhoneColumn.setText(rb.getString("tvPhoneLabel"));

    }

    /** Opens the add appointment window
     * @param event takes an ActionEvent as input and uses it to open the Add Appointment Window
     * */
    @FXML
    private void openAddAppointment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddAppointment.fxml"));
            Parent root = loader.load();
            Stage mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainWindow.setTitle("Add New Appointment");
            mainWindow.setScene(new Scene(root));
            mainWindow.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Opens the Add new Customer Window
     * @param event takes an ActionEvent as input and uses it to open the Add New Customer window
     * */
    @FXML
    private void addNewCustomer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddCustomer.fxml"));
            Parent root = loader.load();
            Stage addCustomer = (Stage) ((Node) event.getSource()).getScene().getWindow();
            addCustomer.setTitle("Add New Customer");
            addCustomer.setScene(new Scene(root));
            addCustomer.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Opens the reports window
     * @param event takes an ActionEvent as input and uses it to open the reports window
     * */
    @FXML
    private void openReports(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Reports.fxml"));
            Parent root = loader.load();
            Stage mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainWindow.setTitle("Reports");
            mainWindow.setScene(new Scene(root));
            mainWindow.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Switches the calendar between monthly and weekly views
     * */
    @FXML
    private void toggleCalendarView() {
        if (weeklyCalendarButton.isSelected()) {
            tvAppointments.setItems(DatabaseManager.getAppointmentsByMonth());
        }
        else {
            tvAppointments.setItems(DatabaseManager.getAppointmentsByWeek());
        }
    }

    @FXML
    private void setMonthlyCalendar() {
        if (monthlyCalendarButton.isSelected()) {
            tvAppointments.setItems(DatabaseManager.getAppointmentsByMonth());
        }
    }

    @FXML
    private void setWeeklyCalendar() {
        if (weeklyCalendarButton.isSelected()) {
            tvAppointments.setItems(DatabaseManager.getAppointmentsByWeek());
        }
    }

    /** Updates the Customer Table */
    public void updateCustomerTableView() {
        DatabaseManager.updateCustomerList();
        tvCustomers.setItems(CustomerList.getCustomerList());
    }

    /** Opens the Modify Appointment Window
     * @param event takes an ActionEvent as input and uses it to open the appointment modification window
     * */
    @FXML
    private void modifyAppointment(ActionEvent event) {
        /** Gets the selected appointment from the table */
        selectedAppointment = tvAppointments.getSelectionModel().getSelectedItem();
        /** Checks to see if an appointment was selected */
        if (selectedAppointment == null) {
            /** Creates an alert saying that an appointment must be selected */
            ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingAppointment"));
            alert.setContentText(rb.getString("errorPleaseSelectAnAppointment"));
            alert.showAndWait();
        }
        else {
            try {
                /** Opens the Appointment modification window */
                Parent mainWindowParent = FXMLLoader.load(getClass().getResource("ModifyAppointment.fxml"));
                Scene mainWindowScene = new Scene(mainWindowParent);
                Stage mainWindowStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainWindowStage.setScene(mainWindowScene);
                mainWindowStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Delete the selected appointment
     * @param event takes an actionEvent as input and uses it to delete the selected appointment
     * */
    @FXML
    private void deleteAppointment(ActionEvent event) {
        /** Get the selected appointment from the table */
        AppointmentRedux appointmentToBeDeleted = tvAppointments.getSelectionModel().getSelectedItem();
        /** Makes sure an appointment was selected */
        if(appointmentToBeDeleted == null) {
            /** Shows an alert that says an appointment must be selected */
            ResourceBundle rb = ResourceBundle.getBundle("AddModifyAppointment", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorDeletingAppointment"));
            alert.setContentText(rb.getString("errorDeletingAppointmentMessage"));
            alert.showAndWait();
            return;
        }
        /** Submits the appointment to be deleted */
        DatabaseManager.deleteAppointment(appointmentToBeDeleted);
        tvAppointments.setItems(DatabaseManager.updateAppointmentList());
    }

    /** Opens the Modify Customer Window
     * @param event takes an actionEvent as input and opens the window to modify the selected customer
     * */
    @FXML
    public void openModifyCustomerWindow(ActionEvent event) {
        ResourceBundle rb = ResourceBundle.getBundle("MainWindow", Locale.getDefault());
        selectedCustomer = tvCustomers.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alert nullAlert = new Alert(Alert.AlertType.ERROR);
            nullAlert.setTitle(rb.getString("error"));
            nullAlert.setHeaderText(rb.getString("errorModifyingCustomer"));
            nullAlert.setContentText(rb.getString("errorModifyingCustomerMessage"));
            nullAlert.showAndWait();
        }
        else {
            try {
                /** Opens the window to modify the customer */
                Parent modifyCustomerWindow = FXMLLoader.load(getClass().getResource("ModifyCustomer.fxml"));
                Scene modifyCustomerScene = new Scene(modifyCustomerWindow);
                Stage modifyCustomerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                modifyCustomerStage.setTitle("Modify Customer");
                modifyCustomerStage.setScene(modifyCustomerScene);
                modifyCustomerStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Deletes selected customer from database after checking if there are existing appointments for that customer and prompting to delete the appointments first
     * @param event takes an actionEvent as input and runs the method to delete a customer from the database
     * */
    @FXML
    public void deleteCustomerFromDatabase(ActionEvent event) {
        ResourceBundle rb = ResourceBundle.getBundle("MainWindow", Locale.getDefault());
        Customer deletedCustomer = tvCustomers.getSelectionModel().getSelectedItem();
        if(deletedCustomer == null) {
            Alert nullAlert = new Alert(Alert.AlertType.ERROR);
            nullAlert.setTitle(rb.getString("error"));
            nullAlert.setHeaderText(rb.getString("errorDeletingCustomer"));
            nullAlert.setContentText(rb.getString("errorDeletingCustomerMessage"));
            nullAlert.showAndWait();
        }
        /** Shows a message if the customer was successfully deleted */
        else if(DatabaseManager.deleteCustomer(deletedCustomer)) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle(rb.getString("customerDeleted"));
            successAlert.setHeaderText(rb.getString("customerDeleted"));
            successAlert.setContentText(rb.getString("customerDeletedMessage"));
            successAlert.showAndWait();
            updateCustomerTableView();
            toggleCalendarView();
        }
        /** Shows a message if the customer was not deleted */
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("customerNotDeleted"));
            alert.setHeaderText(rb.getString("customerNotDeleted"));
            alert.setContentText(rb.getString("customerNotDeletedMessage"));
            alert.showAndWait();
        }

    }

    /** Gets the selected customer to be modified or deleted
     * @return returns the selected customer to be used by the modify or delete functions
     * */
    @FXML
    public static Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    /** Gets the selected appointment to be returned to the modification window
     * @return returns the selected appointment to be passed to the modification or delete functions
     * */
    @FXML
    public static AppointmentRedux getSelectedAppointment() {
        return selectedAppointment;
    }

    /** Initializes the window elements */
    @FXML
    public void initialize() throws ParseException {
        /** Sets the local language */
        setLanguage();
        DatabaseManager.updateAppointmentList();
        ToggleGroup calendarButtons = new ToggleGroup();
        monthlyCalendarButton.setToggleGroup(calendarButtons);
        weeklyCalendarButton.setToggleGroup(calendarButtons);
        monthlyCalendarButton.setSelected(true);
        setMonthlyCalendar();
        //toggleCalendarView();
        tvAppointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        /** Multiple lambdas to efficiently set table values */
        tvAppointmentTitleColumn.setCellValueFactory(cellData -> {return cellData.getValue().titleProperty(); });
        tvAppointmentDescriptionColumn.setCellValueFactory(cellData -> {return cellData.getValue().descriptionProperty(); });
        tvAppointmentLocationColumn.setCellValueFactory(cellData -> {return cellData.getValue().locationProperty(); });
        tvAppointmentContactColumn.setCellValueFactory(cellData -> {return cellData.getValue().contactProperty(); });
        tvAppointmentTypeColumn.setCellValueFactory(cellData -> {return cellData.getValue().typeProperty(); });
        tvAppointmentStartColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(formatDT)));
        tvAppointmentEndColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(formatDT)));
        tvAppointmentCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tvCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tvCustomersNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tvCustomersAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        tvCustomersPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        tvCustomerDivisionColumn.setCellValueFactory(new PropertyValueFactory<>("division"));
        tvCustomersCountryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        tvCustomersPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        updateCustomerTableView();
        tvCustomers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    /** Closes the application on click of the exit button
     * @param event takes an Action event as input and closes the application after a confirmation box pops up
     * */
    @FXML
    private void closeApp(ActionEvent event) {
        /** Creates an alert to confirm exiting the application */
        ResourceBundle rb = ResourceBundle.getBundle("MainWindow", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(rb.getString("confirmExit"));
        alert.setHeaderText(rb.getString("confirmExit"));
        alert.setContentText(rb.getString("confirmExitMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        /** If OK button was selected exits the program */
        if(result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }
}

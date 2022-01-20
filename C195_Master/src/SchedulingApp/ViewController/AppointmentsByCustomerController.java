/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.ViewController;

import SchedulingApp.Model.AppointmentRedux;
import SchedulingApp.Model.DatabaseManager;
import javafx.beans.property.SimpleStringProperty;
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
import java.io.IOException;
import java.sql.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/** Handles the Report that generates the list of appointments by Customer */
public class AppointmentsByCustomerController {

    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String db = "WJ05TZp";
    private static final String url = "jdbc:mysql://wgudb.ucertify.com:3306/" + db;
    private static final String user = "U05TZp";
    private static final String pass = "53688602409";


    @FXML
    private Label appointmentByCustomerLabel;
    @FXML
    private Label customerComboBoxLabel;
    @FXML
    private ComboBox<String> customerComboBox;
    @FXML
    private TableView<AppointmentRedux> customerAppointmentScheduleView;
    @FXML
    private TableColumn<AppointmentRedux,Integer> appointmentIdColumn;
    @FXML
    private TableColumn<AppointmentRedux,String> appointmentTitleColumn;
    @FXML
    private TableColumn<AppointmentRedux,String> appointmentTypeColumn;
    @FXML
    private TableColumn<AppointmentRedux,String> appointmentDescriptionColumn;
    @FXML
    private TableColumn<AppointmentRedux, String> appointmentStartColumn;
    @FXML
    private TableColumn<AppointmentRedux, String> appointmentEndColumn;
    @FXML
    private Button exitButton;

    private String selectedCustomer;

    private String getAllCustomersQuery = "SELECT Customer_Name FROM customers";

    private ObservableList<AppointmentRedux> customerAppointmentList = FXCollections.observableArrayList();

    @FXML
    private final DateTimeFormatter formatDT = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

    private static ZoneId zoneId = ZoneId.systemDefault();


    /** Sets labels to local language, Default is English */
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("Reports", Locale.getDefault());
        appointmentByCustomerLabel.setText(rb.getString("appointmentByCustomerLabel"));
        customerComboBoxLabel.setText(rb.getString("customerComboBoxLabel"));
        appointmentIdColumn.setText(rb.getString("appointmentIdColumn"));
        appointmentTitleColumn.setText(rb.getString("appointmentTitleColumn"));
        appointmentTypeColumn.setText(rb.getString("appointmentTypeColumn"));
        appointmentDescriptionColumn.setText(rb.getString("appointmentDescriptionColumn"));
        appointmentStartColumn.setText(rb.getString("appointmentStartColumn"));
        appointmentEndColumn.setText(rb.getString("appointmentEndColumn"));
        exitButton.setText(rb.getString("exitButton"));
    }

    /** Searches the database for all Customers and adds them to an observable arraylist to be passed to the Combo Box
     * @return returns a list of all customers to be passed into the Select Customer comboBox for selection
     * */
    public ObservableList<String> getAllCustomers() {
        ObservableList<String> contactList = FXCollections.observableArrayList();
        try(Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement statement = connection.prepareStatement(getAllCustomersQuery)) {
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

    /** Sets the Contacts to the Contact Combo Box */
    @FXML
    public void setContacts() {

        customerComboBox.setItems(getAllCustomers());
    }

    /** Gets the customer appointments and sets them to the table view */
    @FXML
    private void getCustomerAppointments() {
        selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();
        customerAppointmentList = DatabaseManager.generateAppointmentScheduleByCustomer(selectedCustomer);
        customerAppointmentScheduleView.setItems(customerAppointmentList);
    }


    /** Exit reports window
     * @param event takes an ActionEvent as input and uses it to exit this window and return to the reports window
     * */
    private void exit(ActionEvent event) {
        try {
            /** Return to the main window */
            Parent mainWindowParent = FXMLLoader.load(getClass().getResource("Reports.fxml"));
            Scene mainWindowScene = new Scene(mainWindowParent);
            Stage mainWindowStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainWindowStage.setScene(mainWindowScene);
            mainWindowStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Initializes Window Elements */
    public void initialize() {
        setLanguage();
        setContacts();
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        /** Lambdas to efficiently set values to the table columns */
        appointmentStartColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(formatDT)));
        appointmentEndColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(formatDT)));
        /** Lambda to efficiently assign action to exit button */
        exitButton.setOnAction(event -> exit(event));
    }
}

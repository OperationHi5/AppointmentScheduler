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
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/** Handles the Window for the Report that shows the Schedule by Contact */
public class ConsultantAppointmentScheduleController {

    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String db = "WJ05TZp";
    private static final String url = "jdbc:mysql://wgudb.ucertify.com:3306/" + db;
    private static final String user = "U05TZp";
    private static final String pass = "53688602409";


    @FXML
    private Label appointmentByConsultantLabel;
    @FXML
    private Label contactComboBoxLabel;
    @FXML
    private ComboBox<String> contactComboBox;
    @FXML
    private TableView<AppointmentRedux> contactAppointmentScheduleView;
    @FXML
    private TableColumn<AppointmentRedux,Integer> appointmentIdColumn;
    @FXML
    private TableColumn<AppointmentRedux,String> appointmentTitleColumn;
    @FXML
    private TableColumn<AppointmentRedux,String> appointmentTypeColumn;
    @FXML
    private TableColumn<AppointmentRedux,String> appointmentDescriptionColumn;
    @FXML
    private TableColumn<AppointmentRedux,String> appointmentStartColumn;
    @FXML
    private TableColumn<AppointmentRedux,String> appointmentEndColumn;
    @FXML
    private TableColumn<AppointmentRedux,Integer> appointmentCustomerIdColumn;
    @FXML
    private Button exitButton;

    private String selectedContact;

    private String getAllContactsQuery = "SELECT Contact_Name FROM contacts";

    private ObservableList<AppointmentRedux> contactAppointmentList = FXCollections.observableArrayList();

    @FXML
    private final DateTimeFormatter formatDT = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");


    /** Sets labels to local language, default is English */
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("Reports", Locale.getDefault());
        appointmentByConsultantLabel.setText(rb.getString("appointmentByConsultantLabel"));
        contactComboBoxLabel.setText(rb.getString("contactComboBoxLabel"));
        appointmentIdColumn.setText(rb.getString("appointmentIdColumn"));
        appointmentTitleColumn.setText(rb.getString("appointmentTitleColumn"));
        appointmentTypeColumn.setText(rb.getString("appointmentTypeColumn"));
        appointmentDescriptionColumn.setText(rb.getString("appointmentDescriptionColumn"));
        appointmentStartColumn.setText(rb.getString("appointmentStartColumn"));
        appointmentEndColumn.setText(rb.getString("appointmentEndColumn"));
        appointmentCustomerIdColumn.setText(rb.getString("appointmentCustomerIdColumn"));
        exitButton.setText(rb.getString("exitButton"));
    }

    /** Searches the database for all Contacts and adds them to an observable arraylist to be passed to the Combo Box
     * @return returns a list of all contacts in the database to be passed to the Select Contact comboBox
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

    /** Gets the appointments for the selected contact and sets them to the table view */
    @FXML
    private void getContactAppointments() {
        selectedContact = contactComboBox.getSelectionModel().getSelectedItem();
        contactAppointmentList = DatabaseManager.generateScheduleForConsultants(selectedContact);
        contactAppointmentScheduleView.setItems(contactAppointmentList);
    }

    /** Sets the Contacts to the Contact Combo Box */
    @FXML
    public void setContacts() {
        contactComboBox.setItems(getAllContacts());
    }

    /** Exit reports window
     * @param event takes an ActionEvent as input and uses it to exit the window and return to the reports window
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

    /** Initializes the window elements */
    @FXML
    public void initialize() {
        setLanguage();
        setContacts();
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        /** Used lambdas to efficiently assign values to the table columns */
        appointmentStartColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(formatDT)));
        appointmentEndColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(formatDT)));
        appointmentCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        /** Lambda to efficiently assign action to exit button */
        exitButton.setOnAction(event -> exit(event));
    }
}

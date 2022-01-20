/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.ViewController;

import SchedulingApp.Model.Customer;
import SchedulingApp.Model.CustomerList;
import SchedulingApp.Model.DatabaseManager;
import SchedulingApp.ViewController.MainWindowController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/** Handles the modification of a customer */
public class ModifyCustomerController {


    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String db = "WJ05TZp";
    private static final String url = "jdbc:mysql://wgudb.ucertify.com:3306/" + db;
    private static final String user = "U05TZp";
    private static final String pass = "53688602409";

    @FXML
    private Label modifyCustomerTitleLabel;
    @FXML
    private Label customerNameLabel;
    @FXML
    private TextField customerNameField;
    @FXML
    private Label customerAddressLabel;
    @FXML
    private TextField customerAddressField;
    @FXML
    private Label customerPostalCodeLabel;
    @FXML
    private TextField postalCodeField;
    @FXML
    private Label customerCountryLabel;
    @FXML
    private ComboBox<String> countryComboBox = new ComboBox<>();
    @FXML
    private Label customerFirstLevelLabel;
    @FXML
    private ComboBox<String> firstLevelDivComboBox = new ComboBox<>();
    @FXML
    private Label customerPhoneLabel;
    @FXML
    private TextField customerPhoneField;
    @FXML
    private Button saveCustomerButton;
    @FXML
    private Button cancelAddCustomerButton;

    private Customer selectedCustomer = new Customer();

    private String getCountriesSQLQuery = "SELECT Country FROM countries WHERE Country_ID = 231 OR Country_ID = 38 OR Country_ID = 230";

    private String getCountryIdQuery = "SELECT COUNTRY_ID FROM countries WHERE Country = ";

    private String getFirstLevelDivQuery = "SELECT Division FROM first_level_divisions WHERE COUNTRY_ID = ";


    /** Sets labels to local language, default is English */
    @FXML
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyCustomer", Locale.getDefault());
        modifyCustomerTitleLabel.setText(rb.getString("modifyCustomerLabel"));
        customerNameLabel.setText(rb.getString("customerNameLabel"));
        customerNameField.setPromptText(rb.getString("customerNameLabel"));
        customerAddressLabel.setText(rb.getString("addressLabel"));
        customerAddressField.setPromptText(rb.getString("addressFieldLabel"));
        customerPostalCodeLabel.setText(rb.getString("postalCodeLabel"));
        postalCodeField.setPromptText(rb.getString("postalCodeLabel"));
        customerCountryLabel.setText(rb.getString("countryLabel"));
        customerFirstLevelLabel.setText(rb.getString("firstLevelDivisionLabel"));
        customerPhoneLabel.setText(rb.getString("phoneLabel"));
        customerPhoneField.setPromptText(rb.getString("phoneLabel"));
        saveCustomerButton.setText(rb.getString("saveButton"));
        cancelAddCustomerButton.setText(rb.getString("cancelButton"));
    }

    /** Saves the modified attributes of the customer and submits to the database
     * @param event takes an ActionEvent as input to handle the saving of the modified customer information
     * */
    @FXML
    private void saveModifyCustomer(ActionEvent event) {
        /** Gets the values from the fields */
        int customerId = selectedCustomer.getCustomerId();
        String customerName = customerNameField.getText();
        String customerAddress = customerAddressField.getText();
        String customerCountry = countryComboBox.getSelectionModel().getSelectedItem();
        String customerFirstDivision = firstLevelDivComboBox.getSelectionModel().getSelectedItem();
        String customerPostalCode = postalCodeField.getText();
        String customerPhone = customerPhoneField.getText();
        /** Validates that there is information in the given fields */
        String errorMessage = Customer.isCustomerValid(customerName, customerAddress, customerFirstDivision, customerPostalCode, customerPhone);
        /** If an error message is given, creates an error message box */
        if (errorMessage.length() > 0) {
            ResourceBundle rb = ResourceBundle.getBundle("AddModifyCustomer", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingCustomer"));
            alert.setContentText(errorMessage);
            alert.showAndWait();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyCustomer.fxml"));
                Parent root = loader.load();
                Stage addCustomer = (Stage) ((Node) event.getSource()).getScene().getWindow();
                addCustomer.setTitle("Add New Customer");
                addCustomer.setScene(new Scene(root));
                addCustomer.show();
                customerNameField.setText(customerName);
                customerAddressField.setText(customerAddress);
                countryComboBox.getSelectionModel().select(customerCountry);
                firstLevelDivComboBox.getSelectionModel().select(customerFirstDivision);
                postalCodeField.setText(customerPostalCode);
                customerPhoneField.setText(customerPhone);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /** Sends the updated customer information to the database and returns to the main window */
        try {
            DatabaseManager.modifyCustomer(customerId, customerName, customerAddress, customerPostalCode, customerPhone, customerFirstDivision);
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

    /** Cancel modifying a customer
     * @param event takes an actionEvent as input to handle the cancellation of modifications to the customer
     * */
    @FXML
    private void cancelModifyCustomer(ActionEvent event) {
        /** Creates a confirmation box for cancelling the modification of the customer */
        ResourceBundle rb = ResourceBundle.getBundle("AddModifyCustomer", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(rb.getString("confirmCancel"));
        alert.setHeaderText(rb.getString("confirmCancel"));
        alert.setContentText(rb.getString("confirmCancelModifyMessage"));
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


    /** Searches the database for all countries and adds them to an observable arraylist to be passed to the Combo Box
     * @return returns a list of all countries in the database to be passed into the country selector comboBox
     * */
    public ObservableList<String> getAllCountries() {
        ObservableList<String> countryList = FXCollections.observableArrayList();
        try(Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement statement = connection.prepareStatement(getCountriesSQLQuery)) {
            ResultSet countryResults = statement.executeQuery();
            while(countryResults.next()) {
                String country = countryResults.getString(1);
                countryList.add(country);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return countryList;
    }

    /** Passes the list of countries to the combo box for selection */
    public void setCountries() {
        countryComboBox.setItems(getAllCountries());
    }

    /** Retrieves the countryId from the country selected in the Combo Box
     * @return returns the countryId from the selected country
     * */
    private int getCountryId() {
        int countryId = 0;
        try(Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement statement = connection.prepareStatement(getCountryIdQuery + "'" + countryComboBox.getSelectionModel().getSelectedItem() + "'")) {
            ResultSet results = statement.executeQuery();
            while(results.next()) {
                countryId = results.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return countryId;
    }

    /** Searches the database for all first level divisions associated with the selected countryId
     * @return returns a list of the firstLevelDivisions (State/Provinces) that is filtered by the selected Country
     * */
    public ObservableList<String> getFirstLevelDivisions() {
        ObservableList<String> divisionList = FXCollections.observableArrayList();
        countryComboBox.getSelectionModel().getSelectedItem();
        try(Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement statement = connection.prepareStatement(getFirstLevelDivQuery + getCountryId())) {
            ResultSet divisionResults = statement.executeQuery();
            while(divisionResults.next()) {
                String division = divisionResults.getString(1);
                divisionList.add(division);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return divisionList;
    }

    /** Pass the list of first level divisions to the combo box for selection */
    @FXML
    public void setFirstLevelDivisions() {
        firstLevelDivComboBox.setItems(getFirstLevelDivisions());
    }

    /** Initializes the window elements */
    public void initialize() {
        /** sets the local language */
        setLanguage();
        setCountries();
        selectedCustomer = MainWindowController.getSelectedCustomer();
        customerNameField.setText(selectedCustomer.getCustomerName());
        customerAddressField.setText(selectedCustomer.getAddress());
        postalCodeField.setText(selectedCustomer.getPostalCode());
        countryComboBox.getSelectionModel().select(selectedCustomer.getCountry());
        firstLevelDivComboBox.getSelectionModel().select(selectedCustomer.getDivision());
        customerPhoneField.setText(selectedCustomer.getPhone());
    }
}

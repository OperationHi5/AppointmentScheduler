/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.ViewController;

import SchedulingApp.Model.AppointmentRedux;
import SchedulingApp.Model.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

import static SchedulingApp.Model.DatabaseManager.checkLogInCredentials;

/** Handles the process of allowing a user to log in to the application */
public class LogInController {

    @FXML
    private Label logInLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private Label usernameLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label passwordLabel;
    @FXML
    private Button submitButton;
    @FXML
    private Label logInErrorLabel;
    @FXML
    private Label logInSuccessLabel;
    @FXML
    private Label userLogInLocationLabel;


    /** A signal for whether or not a database error message needs to display */
    public static int databaseError = 0;

    /** Set labels to local language, default language is English */
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("LogIn", Locale.getDefault());
        logInLabel.setText(rb.getString("titleLabel"));
        usernameLabel.setText(rb.getString("usernameLabel"));
        passwordLabel.setText(rb.getString("passwordLabel"));
        submitButton.setText(rb.getString("submitButton"));
        userLogInLocationLabel.setText(rb.getString("userLogInLocationLabel") + ": " + getUserTimeZone());
    }

    /** Submit provided credentials to be validated
     * @param event takes an ActionEvent as input to submit the entered credentials for validation
     * @throws IOException throws this exception if there is an error opening the main window
     * */
    @FXML
    private void submitLogIn(ActionEvent event) throws IOException {
        /** Retrieves inputted fields and clears password field */
        String userName = usernameField.getText();
        String password = passwordField.getText();
        passwordField.setText("");
        ResourceBundle rb = ResourceBundle.getBundle("LogIn", Locale.getDefault());
        /** Gives an error message if either the username or password fields are left empty */
        if(userName.equals("") || password.equals("")) {
           logInErrorLabel.setText(rb.getString("noUserPassLabel"));
           return;
        }
        /** Validate credentials against database */
        boolean correctLogin = checkLogInCredentials(userName, password);
        /** Check if inputted credentials were correct */
        if(correctLogin) {
            try {
                AppointmentRedux upcomingAppointment = DatabaseManager.getUpcomingAppointment();
                if(upcomingAppointment.getAppointmentId() != 0) {
                    Alert appointmentAlert = new Alert(Alert.AlertType.INFORMATION);
                    appointmentAlert.setTitle(rb.getString("upcomingAppointment"));
                    appointmentAlert.setHeaderText(rb.getString("upcomingAppointment"));
                    appointmentAlert.setContentText(rb.getString("upcomingAppointmentText")
                            + "\non " + upcomingAppointment.getStart().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
                            + "\nat " + upcomingAppointment.getStart().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.FULL))
                            + " with client " + upcomingAppointment.getCustomerName() + ".");
                    appointmentAlert.showAndWait();
                    if (appointmentAlert.getResult() == ButtonType.OK) {
                        /** Show main window */
                        logInSuccessLabel.setText(rb.getString("logInSuccessLabel"));
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
                        Parent root = loader.load();
                        Stage mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        mainWindow.setTitle("Appointment Scheduling App");
                        mainWindow.setScene(new Scene(root));
                        mainWindow.show();
                    }
                    else {
                        appointmentAlert.close();
                    }
                }
                else {
                    /** Show main window */
                    Alert noAppointment = new Alert(Alert.AlertType.INFORMATION);
                    noAppointment.setTitle(rb.getString("noAppointment"));
                    noAppointment.setHeaderText(rb.getString("noAppointment"));
                    noAppointment.setContentText(rb.getString("noAppointmentScheduled"));
                    noAppointment.showAndWait();
                    if (noAppointment.getResult() == ButtonType.OK) {
                        logInSuccessLabel.setText(rb.getString("logInSuccessLabel"));
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
                        Parent root = loader.load();
                        Stage mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        mainWindow.setTitle("Appointment Scheduling App");
                        mainWindow.setScene(new Scene(root));
                        mainWindow.show();
                    }

                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        /** Check if a databaseError has been set */
        else if (databaseError > 0) {
            /** Show a connection error message */
            logInErrorLabel.setText(rb.getString("connectionErrorLabel"));
        }
        else {
            /** Show incorrect username/password error message */
            logInErrorLabel.setText(rb.getString("wrongUserPassLabel"));
        }
    }

    /** Gets the user's local time zone to be displayed in the log in form
     * @return returns the User's Time Zone to be set on the label to show the user's location on the Login form
     * */
    @FXML
    private String getUserTimeZone() {
        ZoneId zoneId = ZoneId.systemDefault();
        String userTimeZone = zoneId.toString();
        return userTimeZone;
    }

    /** Adds a number for each error logging in to the database */
    @FXML
    public static void incrementDatabaseError() {
        databaseError++;
    }

    /** Initializes the window elements
     * @throws IOException throws this exception if there is an error initializing the window
     * */
    @FXML
    public void initialize() throws IOException {
        /** Sets the local language */
        setLanguage();
    }
}

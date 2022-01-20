/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.ViewController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/** Displays the reports that can be generated and handles opening the respective windows */
public class ReportsController {

    @FXML
    private Label reportsLabel;
    @FXML
    private Label appointmentTypesByMonthReportLabel;
    @FXML
    private Label consultantScheduleReportLabel;
    @FXML
    private Label customerScheduleReportLabel;
    @FXML
    private Button generateAppointmentTypeByMonthButton;
    @FXML
    private Button generateConsultantScheduleButton;
    @FXML
    private Button generateCustomerScheduleButton;
    @FXML
    private Button exitReportsButton;

    /** Sets labels to local language, default is English */
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("Reports", Locale.getDefault());
        reportsLabel.setText(rb.getString("reportsLabel"));
        appointmentTypesByMonthReportLabel.setText(rb.getString("appointmentTypesByMonthLabel"));
        consultantScheduleReportLabel.setText(rb.getString("consultantScheduleLabel"));
        customerScheduleReportLabel.setText(rb.getString("customerScheduleLabel"));
        generateAppointmentTypeByMonthButton.setText(rb.getString("generateButton"));
        generateConsultantScheduleButton.setText(rb.getString("generateButton"));
        generateCustomerScheduleButton.setText(rb.getString("generateButton"));
        exitReportsButton.setText(rb.getString("exitButton"));
    }

    /** Opens the report that generates appointments by month and type
     * @param event takes an actionEvent as input and uses it to open this report window
     * */
    @FXML
    public void openAppointmentsByTypeWindow(ActionEvent event) {
        try {
            Parent mainWindowParent = FXMLLoader.load(getClass().getResource("AppointmentsByTypeReport.fxml"));
            Scene mainWindowScene = new Scene(mainWindowParent);
            Stage mainWindowStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainWindowStage.setScene(mainWindowScene);
            mainWindowStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Opens the report that generates appointment schedule for each contact
     * @param event takes an ActionEvent as Input and uses it to open this report window
     * */
    @FXML
    public void openConsultantAppointmentScheduleWindow(ActionEvent event) {
        try {
            Parent mainWindowParent = FXMLLoader.load(getClass().getResource("ConsultantAppointmentSchedule.fxml"));
            Scene mainWindowScene = new Scene(mainWindowParent);
            Stage mainWindowStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainWindowStage.setScene(mainWindowScene);
            mainWindowStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Opens the report that generates appointment schedule for each customer
     * @param event takes an actionEvent as input and uses it to open this report window
     * */
    @FXML
    public void openCustomerAppointmentScheduleWindow(ActionEvent event) {
        try {
            Parent mainWindowParent = FXMLLoader.load(getClass().getResource("AppointmentsByCustomer.fxml"));
            Scene mainWindowScene = new Scene(mainWindowParent);
            Stage mainWindowStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            mainWindowStage.setScene(mainWindowScene);
            mainWindowStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Exit reports window
     * @param event takes an actionEvent as input and uses it to exit the reports window and return to the main window
     * */
    private void exit(ActionEvent event) {
        try {
            /** Return to the main window */
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

    /** Initializes Window Elements */
    public void initialize() {
        /** Sets the local language */
        setLanguage();
        /** Using lambdas to efficiently Assign action to run report buttons */
        generateConsultantScheduleButton.setOnAction(event -> openConsultantAppointmentScheduleWindow(event));
        /** Using lambdas to efficiently Assign action to run report buttons */
        generateCustomerScheduleButton.setOnAction(event -> openCustomerAppointmentScheduleWindow(event));
        /** Using lambdas to efficiently Assign action to run report buttons */
        exitReportsButton.setOnAction(event -> exit(event));
    }
}

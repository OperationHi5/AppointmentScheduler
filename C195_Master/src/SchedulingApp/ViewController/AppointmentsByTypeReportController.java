/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.ViewController;

import SchedulingApp.Model.DatabaseManager;
import SchedulingApp.Model.MonthType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

/** Handles the Appointments by Type Report Window */
public class AppointmentsByTypeReportController {
    @FXML
    private Label appointmentTypeByMonthTitleLabel;
    @FXML
    private TableView<MonthType> appointmentTypeByMonthView;
    @FXML
    private TableColumn<MonthType, String> appointmentMonthCountColumn;
    @FXML
    private TableColumn<MonthType, Integer> appointmentTypeCountColumn;
    @FXML
    private Button exitButton;


    /** Sets local language for labels, default is English */
    @FXML
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("Reports", Locale.getDefault());
        appointmentTypeByMonthTitleLabel.setText(rb.getString("appointmentTypeByMonthTitleLabel"));
        appointmentMonthCountColumn.setText(rb.getString("appointmentMonthCountColumn"));
        appointmentTypeCountColumn.setText(rb.getString("appointmentTypeCountColumn"));
        exitButton.setText(rb.getString("exitButton"));
    }

    /** Updates the TypeByMonth TableView */
    public void updateAppointmentTypeByMonthView() throws ParseException {
        appointmentTypeByMonthView.setItems(DatabaseManager.generateAppointmentTypeByMonthReport());
    }

    /** Exit the Appointment Summary Window
     * @param event takes an ActionEvent as input and uses it to close the window and return to the reports window
     * */
    @FXML
    private void exit(ActionEvent event) {
        /** Returns to the reports window */
        try {
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

    /** Initializes the Window elements */
    @FXML
    public void initialize() throws ParseException {
        setLanguage();
        updateAppointmentTypeByMonthView();
        appointmentMonthCountColumn.setCellValueFactory(new PropertyValueFactory<>("yearMonth"));
        appointmentTypeCountColumn.setCellValueFactory(new PropertyValueFactory<>("typeCount"));
        /** Lambda to efficiently assign action to the exit button */
        exitButton.setOnAction(event -> exit(event));
    }
}

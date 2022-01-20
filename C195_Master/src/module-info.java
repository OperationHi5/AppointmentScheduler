/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */
module C195.Master {

    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;

    opens SchedulingApp.ViewController;
    opens SchedulingApp;
    opens SchedulingApp.Model;
}
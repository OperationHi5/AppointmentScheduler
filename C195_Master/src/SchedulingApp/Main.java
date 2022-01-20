/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.TimeZone;

public class Main extends Application {

    /**
     * @param primaryStage takes the primary stage as input to load the initial window of the application
     * @throws Exception throws an exception if there is an error initializing the application
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        // These lines allow for overriding the default system language to french, and overriding the default timezone
        //Locale.setDefault(new Locale.Builder().setLanguage("fr").build());
        //TimeZone.setDefault(TimeZone.getTimeZone("PST"));
        Parent root = FXMLLoader.load(getClass().getResource("ViewController/LogIn.fxml"));
        primaryStage.setTitle("Scheduling App");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    /**
     * @param args launches the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}

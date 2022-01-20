/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** Allows for the creation of a Customer List to be displayed in the tables in the User Interface */
public class CustomerList {

    /**
     * @return creates a list of all the customers that have been added to the database
     */
    private static ObservableList<Customer> customerList = FXCollections.observableArrayList();


    /**
     * @return allows the customerList to be returned for use
     */
    public static ObservableList<Customer> getCustomerList() {
        return customerList;
    }
}

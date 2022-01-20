/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.Model;

import java.util.Locale;
import java.util.ResourceBundle;

/** Allows for the Creation of a Customer in the Application */
public class Customer {

    private int customerId;
    private String customerName;
    private String address;
    private String postalCode;
    private String division;
    private String country;
    private String phone;


    /**
     * @param customerId takes customerId as input to build the customer
     * @param customerName takes customerName as input to build the customer
     * @param address takes address as input to build the customer
     * @param postalCode takes postalcode as input to build the customer
     * @param division takes division as input to build the customer
     * @param country takes country as input to build the customer
     * @param phone takes phone as input to build the customer
     */
    public Customer(int customerId, String customerName, String address, String postalCode, String division, String country, String phone) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.division = division;
        this.country = country;
        this.phone = phone;
    }

    /** Empty Constructor to allow for Customer Attributes to be manually set in DatabaseManager */
    public Customer() {

    }


    /**
     * @return allows the customer id to be returned
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * @return allows the customer name to be returned
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @return allows the address to be returned
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return allows the postalcode to be returned
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @return allows the division to be returned
     */
    public String getDivision() {
        return division;
    }

    /**
     * @return allows the country to be returned
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return allows the phone to be returned
     */
    public String getPhone() {
        return phone;
    }


    /**
     * @param customerId allows for the customer id to be set
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * @param customerName allows for the customer name to be set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @param address allows for the address to be set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @param postalCode allows for the postalcode to be set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @param division allows for the division to be set
     */
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * @param country allows for the country to be set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @param phone allows for the phone to be set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }


    /**
     * @param customerName takes customerName as input for validation
     * @param address takes address as input for validation
     * @param division takes division as input for validation
     * @param postalCode takes postalcode as input for validation
     * @param phone takes phone as input for validation
     * @return returns an error message if any of the inputs are empty or contain incorrect values
     */
    public static String isCustomerValid(String customerName, String address, String division, String postalCode, String phone) {
        ResourceBundle rb = ResourceBundle.getBundle("Customer", Locale.getDefault());
        String errorMessage = "";
        if (customerName.length() == 0) {
            errorMessage = errorMessage + (rb.getString("errorCustomerName"));
        }
        if (address.length() == 0) {
            errorMessage = errorMessage + (rb.getString("errorAddress"));
        }
        if (division.length() == 0) {
            errorMessage = errorMessage + (rb.getString("errorCountry"));
        }
        if (postalCode.length() == 0) {
            errorMessage = errorMessage + (rb.getString("errorPostalCode"));
        }
        if (phone.length() == 0) {
            errorMessage = errorMessage + (rb.getString("errorPhone"));
        }
        return errorMessage;
    }
}

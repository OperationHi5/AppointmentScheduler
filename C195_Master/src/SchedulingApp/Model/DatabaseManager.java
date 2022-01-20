/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.Model;

import SchedulingApp.ViewController.LogInController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

/** Allows for the handling of the bulk of the Database Operations */
public class DatabaseManager {

    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String db = "WJ05TZp";
    private static final String url = "jdbc:mysql://wgudb.ucertify.com:3306/" + db;
    private static final String user = "U05TZp";
    private static final String pass = "53688602409";

    private static ZoneId zoneId = ZoneId.systemDefault();
    private static String currentUser;
    private static int openCount = 0;


    private static String insertIntoCustomersQuery = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID)";

    private static String insertIntoAppointmentsQuery = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID)";


    /**
     * @param userName takes username as input for login validation
     * @param password takes password as input for login validation
     * @return returns true or false depending on if the credentials were valid
     * @throws IOException throws this exception if there is an error loading the mainwindow
     */
    public static boolean checkLogInCredentials(String userName, String password) throws IOException {
        int userId = getUserId(userName);
        boolean correctPassword = checkPassword(userId, password);
        if(correctPassword) {
            setCurrentUser(userName);
            try {
                Path path = Paths.get("Login_Activity.txt");
                Files.write(path, Arrays.asList("User " + currentUser + " logged in at " + Date.from(Instant.now()).toString() + "."), StandardCharsets.UTF_8,
                        Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        else {
            Path path = Paths.get("Login_Activity.txt");
            Files.write(path, Arrays.asList("User Failed to log in at " + Date.from(Instant.now()).toString() + "."),
                    StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
            return false;
        }
    }

    public static AppointmentRedux getUpcomingAppointment() {
        String getUpcomingAppointmentQuery = "SELECT customers.Customer_Name, appointments.* FROM appointments "
                + "JOIN customers ON appointments.Customer_ID = customers.Customer_ID "
                + "WHERE Start > ? AND Start <= (? + INTERVAL 15 MINUTE)";

        AppointmentRedux upcomingAppointment = new AppointmentRedux();
        try {
            Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement statement = connection.prepareStatement(getUpcomingAppointmentQuery);
            ZonedDateTime localZDT = ZonedDateTime.now(zoneId);
            ZonedDateTime zdtUTC = localZDT.withZoneSameInstant(ZoneId.of("UTC"));
            LocalDateTime localUTC = zdtUTC.toLocalDateTime();
            statement.setTimestamp(1, Timestamp.valueOf(localUTC));
            statement.setTimestamp(2, Timestamp.valueOf(localUTC));
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Customer customer = new Customer();
                customer.setCustomerName(results.getString("Customer_Name"));
                upcomingAppointment.setCustomer(customer);
                upcomingAppointment.setAppointmentId(results.getInt("Appointment_ID"));
                upcomingAppointment.setCustomerId(results.getInt("Customer_ID"));
                upcomingAppointment.setCustomerName(results.getString("Customer_Name"));
                upcomingAppointment.setUserId(results.getInt("User_ID"));
                upcomingAppointment.setTitle(results.getString("Title"));
                LocalDateTime startUTC = results.getTimestamp("Start").toLocalDateTime();
                ZonedDateTime startZDT = ZonedDateTime.ofInstant(startUTC.toInstant(ZoneOffset.UTC),zoneId);
                upcomingAppointment.setStart(startZDT);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return upcomingAppointment;
    }


    /**
     * @param userName takes the username as input to return the userId
     * @return returns the userId for use
     */
    public static int getUserId(String userName) {
        try{
            Class.forName(driver);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        int userId = -1;
        /** Using a try-with-resources + catch for connecting to database and handling server connection issues */
        try (Connection connection = DriverManager.getConnection(url,user,pass);
             Statement statement = connection.createStatement()) {

            /** Retrieves the userId for the username that was entered */
            ResultSet userIdSet = statement.executeQuery("SELECT User_ID FROM users WHERE User_Name = '" + userName + "'");

            /** Sets the userId to a unique vale + retrieves int from the ResultSet */
            if(userIdSet.next()) {
                userId = userIdSet.getInt("User_ID");
            }
            userIdSet.close();
        }
        catch (SQLException e) {

            /** Increments the databaseError count so an error message can be displayed if necessary */
            LogInController.incrementDatabaseError();
        }
        return userId;
    }


    /**
     * @param userId takes the userId as input to validate the password that was entered
     * @param password takes the password as input to ensure it matches the inputted userId
     * @return returns true or false if the password matched the userId
     */
    private static boolean checkPassword(int userId, String password) {
        /** Using a try-with-resources + catch for connecting to database and handling server connection issues */
        try (Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement()) {
            /** Retrieves the password for the given userId */
            ResultSet passwordSet = statement.executeQuery("SELECT Password FROM users WHERE User_ID = " + userId);

            /** Initializes database password and retrieves the String from ResultSet */
            String databasePassword = null;
            if(passwordSet.next()) {
                databasePassword = passwordSet.getString("Password");
            }
            else {
                return false;
            }
            passwordSet.close();
            /** Validates databasePassword against the password entered and returns a boolean */
            if(databasePassword.equals(password)) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * @param customerName takes customer name as input to submit the new customer to the database
     * @param address takes the address as input to submit the new customer to the database
     * @param postalCode takes the postalcode as input to submit the new customer to the database
     * @param phone takes the phone as input to submit the new customer to the database
     * @param division takes the division as input to submit the new customer to the database
     */
    public static void addNewCustomer (String customerName, String address, String postalCode, String phone, String division) {
        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        int divisionId = getDivisionId(division);
        if(isExistingCustomer(customerName, phone)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingCustomer"));
            alert.setContentText(rb.getString("errorCustomerAlreadyExists"));
            alert.showAndWait();
        }
        else {
            try(Connection connection = DriverManager.getConnection(url,user,pass);
                Statement statement = connection.createStatement()) {
                statement.executeUpdate(insertIntoCustomersQuery + "VALUES (" + "'" + customerName + "'" + ", " + "'" + address + "'" + ", "
                        + "'" + postalCode + "'" + ", " + "'" + phone + "'" + ", " + "CURRENT_DATE, " + "'" + currentUser + "'" + ", " +
                        "CURRENT_TIMESTAMP, " + "'" + currentUser + "'" + ", " + divisionId + ")");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @param customerId takes customerId as input for the customer to be modified
     * @param customerName takes the customername as input for the customer to be modified
     * @param address takes the address as input for the customer to be modified
     * @param postalCode takes the postalcode as input for the customer to be modified
     * @param phone takes the phone as input for the customer to be modified
     * @param division takes the division as input for the customer to be modified
     */
    public static void modifyCustomer (int customerId, String customerName, String address, String postalCode, String phone, String division) {
        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        int divisionId = getDivisionId(division);
        if(isExistingCustomer(customerName, phone)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddingCustomer"));
            alert.setContentText(rb.getString("errorCustomerAlreadyExists"));
            alert.showAndWait();
        }
        else {
            /** Try with resources block to handle the connection to the database */
            try(Connection connection = DriverManager.getConnection(url,user,pass);
                Statement statement = connection.createStatement()) {
                statement.executeUpdate("UPDATE customers SET Customer_Name = " + "'" + customerName + "'" + ", " + "Address = " + "'" + address + "'" + ", " +
                                        "Postal_Code = " + "'" + postalCode + "'" + ", " + "Phone = " + "'" + phone + "'" + ", " + "Division_ID = " + divisionId +
                                        " WHERE Customer_ID = " + customerId + "");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @return doesn't return anything. Updates the customer list to be used by other functions of the app
     */
    public static void updateCustomerList() {
        /** Trying with Resources for database connection */
        try (Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement()) {
            /** Retrieving the customer list and clearing it */
            ObservableList<Customer> customerList = new CustomerList().getCustomerList();
            customerList.clear();
            /** Creates a list of customerIds for every customer in the database */
            ResultSet customerIdResults = statement.executeQuery("SELECT Customer_ID FROM customers");
            ArrayList<Integer> customerIdList = new ArrayList<>();
            while(customerIdResults.next()) {
                customerIdList.add(customerIdResults.getInt(1));
            }
            /** Creates a new Customer Object for each customerId in the list and adds the customer to the customerList */
            for(int customerId : customerIdList) {
                /** Creates a new Customer Object */
                Customer customer = new Customer();
                /** Retrieves the customer information from the database and sets it to the customer */
                ResultSet customerResultSet = statement.executeQuery("SELECT Customer_Name, Address, Postal_Code, Division_ID, Phone FROM customers WHERE Customer_ID = " + customerId);
                customerResultSet.next();
                String customerName = customerResultSet.getString(1);
                String address = customerResultSet.getString(2);
                String postalCode = customerResultSet.getString(3);
                int divisionId = customerResultSet.getInt(4);
                String phone = customerResultSet.getString(5);
                customer.setCustomerId(customerId);
                customer.setCustomerName(customerName);
                customer.setAddress(address);
                customer.setPostalCode(postalCode);
                customer.setPhone(phone);
                ResultSet divisionName = statement.executeQuery("SELECT Division FROM first_level_divisions WHERE Division_ID = " + divisionId);
                divisionName.next();
                String division = divisionName.getString(1);
                customer.setDivision(division);
                ResultSet countryIdSet = statement.executeQuery("SELECT COUNTRY_ID FROM first_level_divisions WHERE Division_ID = " + divisionId);
                countryIdSet.next();
                int countryId = countryIdSet.getInt(1);
                ResultSet countryNameSet = statement.executeQuery("SELECT Country FROM countries WHERE Country_ID = " + countryId);
                countryNameSet.next();
                String country = countryNameSet.getString(1);
                customer.setCountry(country);
                customerList.add(customer);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<AppointmentRedux> getAppointmentsByWeek() {
        ObservableList<AppointmentRedux> appointmentsByWeek = FXCollections.observableArrayList();
        String getAppointmentsByWeekQuery = "SELECT customers.*, appointments.* FROM customers "
                + "RIGHT JOIN appointments ON customers.Customer_ID = appointments.Customer_ID "
                + "WHERE Start BETWEEN NOW() AND (SELECT ADDDATE(NOW(), INTERVAL 7 DAY))";
        try {
            Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement statement = connection.prepareStatement(getAppointmentsByWeekQuery);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Customer selectedCustomer = getCustomerById(results.getInt("Customer_ID"));
                AppointmentRedux getWeeklyAppointments = new AppointmentRedux();
                getWeeklyAppointments.setCustomer(selectedCustomer);
                getWeeklyAppointments.setAppointmentId(results.getInt("Appointment_ID"));
                getWeeklyAppointments.setTitle(results.getString("Title"));
                getWeeklyAppointments.setDescription(results.getString("Description"));
                getWeeklyAppointments.setLocation(results.getString("Location"));
                getWeeklyAppointments.setType(results.getString("Type"));
                getWeeklyAppointments.setCustomerId(results.getInt("Customer_ID"));
                getWeeklyAppointments.setContactId(results.getInt("Contact_ID"));
                getWeeklyAppointments.setCustomerName(results.getString("Customer_Name"));
                getWeeklyAppointments.setCreatedBy(results.getString("Created_By"));
                getWeeklyAppointments.setUserId(results.getInt("User_ID"));
                LocalDateTime startUTC = results.getTimestamp("Start").toLocalDateTime();
                LocalDateTime endUTC = results.getTimestamp("End").toLocalDateTime();
                ZonedDateTime startLocal = ZonedDateTime.ofInstant(startUTC.toInstant(ZoneOffset.UTC), zoneId);
                ZonedDateTime endLocal = ZonedDateTime.ofInstant(endUTC.toInstant(ZoneOffset.UTC), zoneId);
                Statement secondStatement = connection.createStatement();
                ResultSet contactResults = secondStatement.executeQuery("SELECT Contact_Name from contacts WHERE Contact_ID = " + getWeeklyAppointments.getContactId());
                contactResults.next();
                String contactName = contactResults.getString(1);
                getWeeklyAppointments.setContact(contactName);
                getWeeklyAppointments.setStart(startLocal);
                getWeeklyAppointments.setEnd(endLocal);
                appointmentsByWeek.add(getWeeklyAppointments);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentsByWeek;
    }

    public static ObservableList<AppointmentRedux> getAppointmentsByMonth() {
        ObservableList<AppointmentRedux> appointmentsByMonth = FXCollections.observableArrayList();
        String getAppointmentsByMonthQuery = "SELECT customers.*, appointments.* FROM customers "
                + "RIGHT JOIN appointments ON customers.Customer_ID = appointments.Customer_ID "
                + "WHERE Start BETWEEN NOW() AND (SELECT ADDDATE(NOW(), INTERVAL 30 DAY))";
        try {
            Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement statement = connection.prepareStatement(getAppointmentsByMonthQuery);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Customer selectedCustomer = getCustomerById(results.getInt("Customer_ID"));
                AppointmentRedux getMonthlyAppointments = new AppointmentRedux();
                getMonthlyAppointments.setCustomer(selectedCustomer);
                getMonthlyAppointments.setAppointmentId(results.getInt("Appointment_ID"));
                getMonthlyAppointments.setTitle(results.getString("Title"));
                getMonthlyAppointments.setDescription(results.getString("Description"));
                getMonthlyAppointments.setLocation(results.getString("Location"));
                getMonthlyAppointments.setType(results.getString("Type"));
                getMonthlyAppointments.setCustomerId(results.getInt("Customer_ID"));
                getMonthlyAppointments.setContactId(results.getInt("Contact_ID"));
                getMonthlyAppointments.setCustomerName(results.getString("Customer_Name"));
                getMonthlyAppointments.setCreatedBy(results.getString("Created_By"));
                getMonthlyAppointments.setUserId(results.getInt("User_ID"));
                LocalDateTime startUTC = results.getTimestamp("Start").toLocalDateTime();
                LocalDateTime endUTC = results.getTimestamp("End").toLocalDateTime();
                ZonedDateTime startLocal = ZonedDateTime.ofInstant(startUTC.toInstant(ZoneOffset.UTC), zoneId);
                ZonedDateTime endLocal = ZonedDateTime.ofInstant(endUTC.toInstant(ZoneOffset.UTC), zoneId);

                Statement secondStatement = connection.createStatement();
                ResultSet contactResults = secondStatement.executeQuery("SELECT Contact_Name from contacts WHERE Contact_ID = " + getMonthlyAppointments.getContactId());
                contactResults.next();
                String contactName = contactResults.getString(1);
                getMonthlyAppointments.setContact(contactName);
                getMonthlyAppointments.setStart(startLocal);
                getMonthlyAppointments.setEnd(endLocal);
                appointmentsByMonth.add(getMonthlyAppointments);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentsByMonth;
    }


    /**
     * @return does not return anything, but updates the appointment list to be used by other functions of the application
     */
    public static ObservableList<AppointmentRedux> updateAppointmentList() {
        ObservableList<AppointmentRedux> upcomingAppointmentsList = FXCollections.observableArrayList();
        String getUpcomingAppointmentsQuery = "SELECT customers.*, appointments.* FROM customers "
                + "RIGHT JOIN appointments ON customers.Customer_ID = appointments.Customer_ID "
                + "WHERE Start >= CURRENT_TIMESTAMP ";
        try {
            Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement statement = connection.prepareStatement(getUpcomingAppointmentsQuery);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Customer selectedCustomer = getCustomerById(results.getInt("Customer_ID"));
                AppointmentRedux upcomingAppointments = new AppointmentRedux();
                upcomingAppointments.setCustomer(selectedCustomer);
                upcomingAppointments.setAppointmentId(results.getInt("Appointment_ID"));
                upcomingAppointments.setTitle(results.getString("Title"));
                upcomingAppointments.setDescription(results.getString("Description"));
                upcomingAppointments.setLocation(results.getString("Location"));
                upcomingAppointments.setType(results.getString("Type"));
                upcomingAppointments.setCustomerId(results.getInt("Customer_ID"));
                upcomingAppointments.setContactId(results.getInt("Contact_ID"));
                upcomingAppointments.setCustomerName(results.getString("Customer_Name"));
                upcomingAppointments.setCreatedBy(results.getString("Created_By"));
                upcomingAppointments.setUserId(results.getInt("User_ID"));
                LocalDateTime startUTC = results.getTimestamp("Start").toLocalDateTime();
                LocalDateTime endUTC = results.getTimestamp("End").toLocalDateTime();
                ZonedDateTime startLocal = ZonedDateTime.ofInstant(startUTC.toInstant(ZoneOffset.UTC), zoneId);
                ZonedDateTime endLocal = ZonedDateTime.ofInstant(endUTC.toInstant(ZoneOffset.UTC), zoneId);

                Statement secondStatement = connection.createStatement();
                ResultSet contactResults = secondStatement.executeQuery("SELECT Contact_Name from contacts WHERE Contact_ID = " + upcomingAppointments.getContactId());
                contactResults.next();
                String contactName = contactResults.getString(1);
                upcomingAppointments.setContact(contactName);
                upcomingAppointments.setStart(startLocal);
                upcomingAppointments.setEnd(endLocal);
                upcomingAppointmentsList.add(upcomingAppointments);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return upcomingAppointmentsList;
    }

    public static Customer getCustomerById(int customerId) {
        String getCustomerByIdQuery = "SELECT * FROM customers WHERE Customer_ID = ?";
        Customer getCustomerQuery = new Customer();

        try {
            Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement statement = connection.prepareStatement(getCustomerByIdQuery);
            statement.setInt(1, customerId);
            ResultSet results = statement.executeQuery();

            if(results.next()) {
                getCustomerQuery.setCustomerId(results.getInt("Customer_ID"));
                getCustomerQuery.setCustomerName(results.getString("Customer_Name"));
                getCustomerQuery.setAddress(results.getString("Address"));
                getCustomerQuery.setPostalCode(results.getString("Postal_Code"));
                getCustomerQuery.setPhone(results.getString("Phone"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return getCustomerQuery;
    }

    /**
     * @param appointmentToBeDeleted takes a selected appointment and submits it to be deleted
     */
    public static void deleteAppointment(AppointmentRedux appointmentToBeDeleted) {
        int appointmentId = appointmentToBeDeleted.getAppointmentId();
        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        /** Shows an alert to confirm deleting the appointment entry */
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(rb.getString("confirmDelete"));
        alert.setHeaderText(rb.getString("confirmDeleteAppointment"));
        alert.setContentText(rb.getString("confirmDeleteAppointmentMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        /** Checks if the OK button was clicked and deletes the appointment if it was */
        if(result.get() == ButtonType.OK) {
            /** Try with resources for database connection */
            try (Connection connection = DriverManager.getConnection(url,user,pass);
                 Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM appointments WHERE Appointment_ID = " + appointmentId);
            }
            catch (Exception e) {
                /** Creates an alert that notifies the user that a database connection is required for this to function */
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle(rb.getString("error"));
                alert2.setHeaderText(rb.getString("errorModifyingAppointment"));
                alert2.setContentText(rb.getString("errorRequiresDatabase"));
                alert2.showAndWait();
            }
            /** Updates the appointmentList to remove the deleted appointment */
            updateAppointmentList();
        }
    }


    /**
     * @param customer takes a customer for deletion as input and checks to see if there are appointments for that customer and whether the user wants to delete them
     */
    public static void deleteCustomerAppointments(Customer customer) {
        int customerId = customer.getCustomerId();
        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        /** Shows an alert to confirm deleting all of the customer's appointments */
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(rb.getString("confirmDelete"));
        alert.setHeaderText(rb.getString("confirmDeleteCustomerAppointments"));
        alert.setContentText(rb.getString("confirmDeleteAllCustomerAppointments"));
        Optional<ButtonType> result = alert.showAndWait();
        /** Checks to see if the OK button was clicked and deletes all of the customer's appointments */
        if(result.get() == ButtonType.OK) {
            /** Try with resources for database connection */
            try(Connection connection = DriverManager.getConnection(url,user,pass);
                Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM appointments WHERE Customer_ID = " + customerId);
            }
            catch (Exception e) {
                /** Creates an alert that notifies the user that a database connection is required for this to function */
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle(rb.getString("error"));
                alert2.setHeaderText(rb.getString("errorModifyingAppointment"));
                alert2.setContentText(rb.getString("errorRequiresDatabase"));
                alert2.showAndWait();
            }
            /** Updates the appointmentList to remove the deleted appointments */
            updateAppointmentList();
        }
    }


    /**
     * @param customer takes a selected customer to be deleted from the database
     * @return returns true or false depending on whether or not the customer was deleted
     */
    public static boolean deleteCustomer(Customer customer) {
        int customerId = customer.getCustomerId();
        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        /** Shows an alert to confirm deleting the customer */
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(rb.getString("confirmRemove"));
        alert.setHeaderText(rb.getString("confirmDeletingCustomer"));
        alert.setContentText(rb.getString("confirmDeletingCustomerMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        /** Checks to see if the button was clicked before checking to see if there are appointments associated with customer */
        if(result.get() == ButtonType.OK) {
            /** Try with resources for database connection */
            try(Connection connection = DriverManager.getConnection(url,user,pass);
                Statement statement = connection.createStatement()) {
                ResultSet appointmentResults = statement.executeQuery("SELECT  Appointment_ID FROM appointments WHERE Customer_ID = " + customerId);
                appointmentResults.next();
                int appointmentId = appointmentResults.getInt(1);
                if(appointmentId > 0) {
                    /** Shows an alert that notifies the user that there are appointments for this customer that must be deleted before the customer can be deleted */
                    Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
                    alert2.setTitle(rb.getString("errorExistingAppointments"));
                    alert2.setHeaderText(rb.getString("errorExistingCustomerAppointments"));
                    alert2.setContentText(rb.getString("errorExistingCustomerAppointmentsMessage"));
                    Optional<ButtonType> result2 = alert2.showAndWait();
                    /** Checks to see if the OK button was clicked before continuing with deletion of appointments */
                    if(result2.get() == ButtonType.OK) {
                        deleteCustomerAppointments(customer);
                        updateAppointmentList();
                        statement.executeUpdate("DELETE FROM customers WHERE Customer_ID = " + customerId);
                        return true;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            return false;
        }
        updateCustomerList();
        return true;
    }


    /**
     * @param customerName takes customerName as input to see if the customer already exists in the database
     * @param phone takes phone as input to see if the customer alread exists in the database
     * @return
     */
    public static boolean isExistingCustomer(String customerName, String phone) {
        int customerId;
        try (Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement()) {
            ResultSet results = statement.executeQuery("SELECT Customer_ID FROM customers WHERE Customer_Name = " + "'" + customerName + "'" +
                                                        " AND Phone = " + "'" + phone + "'");
            while(results.next()) {
                customerId = results.getInt(1);
                if(customerId > 0) {

                    return true;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @return returns a list of all of the MonthType combos to viewed in the report
     */
    public static ObservableList<MonthType> generateAppointmentTypeByMonthReport() throws ParseException {
        updateAppointmentList();
        ObservableList<MonthType> monthTypeList = FXCollections.observableArrayList();
        ResourceBundle rb = ResourceBundle.getBundle("DBManager", Locale.getDefault());
        /** Gets the appointments from the list so the dates and types can be retrieved */
        ArrayList<String> monthsWithAppointments = new ArrayList<>();
        /** Checks the year and month of each appointment and adds new year-month combos to the arraylist */
        for (AppointmentRedux appointment : updateAppointmentList()) {
            LocalDate apptLocal = appointment.getStart().toLocalDate();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date appointmentDate = dateFormat.parse(apptLocal.toString());
            System.out.println(appointmentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(appointmentDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            String yearMonth = year + "-" + month;
            if(month < 10) {
                yearMonth = year + "-0" + month;
            }
            if(!monthsWithAppointments.contains(yearMonth)) {
                monthsWithAppointments.add(yearMonth);
            }
        }
        /** Sort the yearMonths */
        Collections.sort(monthsWithAppointments);
        for (String yearMonth : monthsWithAppointments) {
            /** Get year and month values again */
            int year = Integer.parseInt(yearMonth.substring(0,4));
            int month = Integer.parseInt(yearMonth.substring(5,7));
            /** Initialize typeCount int */
            int typeCount = 0;
            ArrayList<String> type = new ArrayList<>();
            for (AppointmentRedux appointment : updateAppointmentList()) {
                /** Get the appointment date */
                LocalDate apptLocal = appointment.getStart().toLocalDate();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date appointmentDate = dateFormat.parse(apptLocal.toString());
                System.out.println(appointmentDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(appointmentDate);
                /** Get the appointment year and month values */
                int appointmentYear = calendar.get(Calendar.YEAR);
                int appointmentMonth = calendar.get(Calendar.MONTH) + 1;
                /** If year and month match get the appointment type */
                if(year == appointmentYear && month == appointmentMonth) {
                    String appointmentType = appointment.getType();
                    /** If appointment type is not in arrayList, add it and increment the typecount */
                    if (!type.contains(appointmentType)) {
                        type.add(appointmentType);
                        typeCount++;
                    }
                }
            }
            MonthType monthType = new MonthType(yearMonth, typeCount);
            monthTypeList.add(monthType);
        }
        return monthTypeList;
    }


    /**
     * @param contact takes a selected contact as input and pulls all of their appointments to be viewed in the report
     * @return returns a list of the contact's appointments to be viewed in the report
     */
    public static ObservableList<AppointmentRedux> generateScheduleForConsultants(String contact) {
        updateAppointmentList();
        ObservableList<AppointmentRedux> contactAppointmentList = FXCollections.observableArrayList();
        ArrayList<String> contactsWithAppointments = new ArrayList<>();
        /** Checks the Contact of each appointment. Adds new Contacts to ArrayList */
        for(AppointmentRedux appointment : updateAppointmentList()) {
            String appointmentContact = appointment.getContact();
            /** Checks if the appointmentContact matches the selected contact */
            if(contact.equals(appointmentContact)) {
                int customerId;
                int appointmentId = appointment.getAppointmentId();
                String title = appointment.getTitle();
                String description = appointment.getDescription();
                String type = appointment.getType();
                String customerName = appointment.getCustomerName();
                /** Try with resources for database connection */
                try(Connection connection = DriverManager.getConnection(url,user,pass);
                    Statement statement = connection.createStatement()) {
                    ResultSet customerIdResults = statement.executeQuery("SELECT Customer_ID FROM customers WHERE Customer_Name = " + "'" + customerName + "'");
                    customerIdResults.next();
                    customerId = customerIdResults.getInt(1);
                    AppointmentRedux contactAppointment = new AppointmentRedux();
                    contactAppointment.setContact(appointmentContact);
                    contactAppointment.setAppointmentId(appointmentId);
                    contactAppointment.setTitle(title);
                    contactAppointment.setType(type);
                    contactAppointment.setDescription(description);
                    contactAppointment.setStart(appointment.getStart());
                    contactAppointment.setEnd(appointment.getEnd());
                    contactAppointment.setCustomerId(customerId);
                    contactAppointmentList.add(contactAppointment);
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return contactAppointmentList;
    }


    /**
     * @param customerName takes customerName as input to pull all appointments associated with that customer
     * @return returns a list of appointments to be viewed in the report
     */
    public static ObservableList<AppointmentRedux> generateAppointmentScheduleByCustomer(String customerName) {
        updateAppointmentList();
        ObservableList<AppointmentRedux> customerAppointmentList = FXCollections.observableArrayList();
        /** Checks the customer of each appointment, adds new Customers to arraylist */
        ObservableList<AppointmentRedux> appointmentList = FXCollections.observableArrayList();
        appointmentList = updateAppointmentList();
        /** Lambda to efficiently search all of the appointments to see if existing customer names match the inputted name */
        appointmentList.stream().forEach(appointment -> {
            String appointmentCustomerName = appointment.getCustomerName();
            if(customerName.equals(appointmentCustomerName)) {
                int appointmentId = appointment.getAppointmentId();
                String title = appointment.getTitle();
                String description = appointment.getDescription();
                String type = appointment.getType();
                AppointmentRedux customerAppointment = new AppointmentRedux();
                customerAppointment.setAppointmentId(appointmentId);
                customerAppointment.setTitle(title);
                customerAppointment.setDescription(description);
                customerAppointment.setType(type);
                customerAppointment.setStart(appointment.getStart());
                customerAppointment.setEnd(appointment.getEnd());
                customerAppointmentList.add(customerAppointment);
            }
        });
        return customerAppointmentList;
    }

    /** This is the new one
     * @param start takes the start time of the new appointment
     * @param end takes the end time of the new appointment
     * @return returns a list of any existing appointments that overlap with the new appointment start/end */
    public static ObservableList<AppointmentRedux> getOverlappingAppts(LocalDateTime start, LocalDateTime end) {
        ObservableList<AppointmentRedux> getOverlappedAppts = FXCollections.observableArrayList();
        String getOverlappingApptsQuery = "SELECT * FROM appointments " + "WHERE (Start >= ? AND End <= ?) "
                                          + "OR (Start <= ? AND End >=?) "
                                          + "OR (Start BETWEEN ? AND ? OR End BETWEEN ? AND ?)";
        try {
            LocalDateTime startLDT = start.atZone(zoneId).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            System.out.println(startLDT);
            LocalDateTime endLDT = end.atZone(zoneId).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            System.out.println(endLDT);
            Connection connection = DriverManager.getConnection(url,user,pass);
            PreparedStatement preparedStatement = connection.prepareStatement(getOverlappingApptsQuery);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startLDT));
            preparedStatement.setTimestamp(2,Timestamp.valueOf(endLDT));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(startLDT));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(endLDT));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(startLDT));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(endLDT));
            preparedStatement.setTimestamp(7, Timestamp.valueOf(startLDT));
            preparedStatement.setTimestamp(8, Timestamp.valueOf(endLDT));
            ResultSet results = preparedStatement.executeQuery();

                while(results.next()) {
                    AppointmentRedux overlappedAppt = new AppointmentRedux();
                    overlappedAppt.setAppointmentId(results.getInt("Appointment_ID"));
                    overlappedAppt.setTitle(results.getString("Title"));
                    overlappedAppt.setDescription(results.getString("Description"));
                    overlappedAppt.setLocation(results.getString("Location"));
                    //overlappedAppt.setContact(results.getString("Contact_Name"));
                    overlappedAppt.setType(results.getString("Type"));
                    LocalDateTime startUTC = results.getTimestamp("Start").toLocalDateTime();
                    LocalDateTime endUTC = results.getTimestamp("End").toLocalDateTime();
                    ZonedDateTime startLocal = ZonedDateTime.ofInstant(startUTC.toInstant(ZoneOffset.UTC), zoneId);
                    ZonedDateTime endLocal = ZonedDateTime.ofInstant(endUTC.toInstant(ZoneOffset.UTC), zoneId);
                    overlappedAppt.setStart(startLocal);
                    overlappedAppt.setEnd(endLocal);
                    getOverlappedAppts.add(overlappedAppt);
                }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return getOverlappedAppts;
    }

    /** The New Method
     * @param appointment takes the information of the new appointment and adds it to the database*/
    public static void addAppointment(AppointmentRedux appointment) {
        String addAppointmentQuery = String.join(" ",
                "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, "
                            + "Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) ",
                            "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?)");

        try {
            String contact = appointment.getContact();
            Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement();
            ResultSet contactIdResults = statement.executeQuery("SELECT Contact_ID FROM contacts WHERE Contact_Name = " + "'" + contact + "'");
            contactIdResults.next();
            int contactId = contactIdResults.getInt(1);
            PreparedStatement preparedStatement = connection.prepareStatement(addAppointmentQuery);
            preparedStatement.setObject(1, appointment.getTitle());
            preparedStatement.setObject(2, appointment.getDescription());
            preparedStatement.setObject(3, appointment.getLocation());
            preparedStatement.setObject(4, appointment.getType());
            ZonedDateTime startZDT = appointment.getStart().withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime endZDT = appointment.getEnd().withZoneSameInstant(ZoneId.of("UTC"));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(startZDT.toLocalDateTime()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(endZDT.toLocalDateTime()));
            preparedStatement.setObject(7, currentUser);
            preparedStatement.setObject(8, currentUser);
            preparedStatement.setInt(9, appointment.getCustomerId());
            preparedStatement.setInt(10, getUserId(currentUser));
            preparedStatement.setInt(11, contactId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        updateAppointmentList();
    }

    /**
     * @param appointment takes the modified appointment information and submits the updated information to the database
     */
    public static void modifyAppointment(AppointmentRedux appointment) {
        String modifyAppointmentQuery = String.join(" ", "UPDATE appointments",
                "SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Last_Update=NOW(), Last_Updated_By=?, Customer_ID=?, User_ID=?, Contact_ID=?",
                "WHERE Appointment_ID=?");
        try {
            String contact = appointment.getContact();
            Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement();
            ResultSet contactIdResults = statement.executeQuery("SELECT Contact_ID FROM contacts WHERE Contact_Name = " + "'" + contact + "'");
            contactIdResults.next();
            int contactId = contactIdResults.getInt(1);
            PreparedStatement preparedStatement = connection.prepareStatement(modifyAppointmentQuery);
            preparedStatement.setObject(1, appointment.getTitle());
            preparedStatement.setObject(2, appointment.getDescription());
            preparedStatement.setObject(3, appointment.getLocation());
            preparedStatement.setObject(4, appointment.getType());
            ZonedDateTime startZDT = appointment.getStart().withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime endZDT = appointment.getEnd().withZoneSameInstant(ZoneId.of("UTC"));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(startZDT.toLocalDateTime()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(endZDT.toLocalDateTime()));
            preparedStatement.setObject(7, currentUser);
            preparedStatement.setObject(8, appointment.getCustomerId());
            preparedStatement.setInt(9, getUserId(currentUser));
            preparedStatement.setInt(10, contactId);
            preparedStatement.setInt(11, appointment.getAppointmentId());

            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param division takes the division as input to get the divisionID of that division
     * @return returns the DivisionID of the selected Division (state or province)
     */
    private static int getDivisionId(String division) {
        try (Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement()) {
            ResultSet results = statement.executeQuery("SELECT Division_ID FROM first_level_divisions WHERE Division = " + "'" + division + "'");
            System.out.println(results);
            while(results.next()) {
                int divisionId = results.getInt(1);
                return divisionId;
            }
            return -1;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        /** returns -1 if there is no Division selected */
        return -1;
    }




    /**
     * @param userName takes the username as input to determine the current user
     */
    private static void setCurrentUser(String userName) {
        currentUser = userName;
    }

}

Scheduling Application for WGU C195 Final
Jacob Clingler, jclingl@wgu.edu, Version 1.0, 12/29/2020
Using IntelliJ Ultimate 2020.3, JDK Amazon Corretto jdk11.0.9_12, JavaFX-SDK-11.0.2

Application Can Be run by importing the project into your IDE and running the application
1.I left behind two lines of code in the 'main' class that allows for easily changing the language to French and changing the default timezone. Commented out by default
2.Log in using Username 'test' and password 'test'
3.After successful login the main window opens to display a tableview that toggles between weekly and monthly views to view any appointments that are scheduled in those time frames
4.Buttons are displayed on the right hand side of the window to allow navigation through the various functions of the application

Section B Project Requirements: Used one type of lambda on several Controllers to assign actions to buttons, notated with comments, a specific use was in the AppointmentsByCustomerController at line 149
                                Used another lambda in SchedulingApp.Model.DatabaseManager at line 725 to search a list for matching customer names

Section A3f Project Requirements: the 3rd report is a report that will generate all appointments for a specific customer
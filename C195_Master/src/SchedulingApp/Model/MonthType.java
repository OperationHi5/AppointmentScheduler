/**
 * Appointment Scheduling App for C195
 * @author Jacob Clingler Student ID#: #000968521
 * jclingl@wgu.edu
 */

package SchedulingApp.Model;

/** Allows for the creation of a Month and Type object for the Appointments By Type Report */
public class MonthType {
    private String yearMonth;
    private int typeCount;


    /**
     * @param yearMonth uses a year and month string to build a MonthType for reporting
     * @param typeCount makes a count of the type of appointment for reporting
     */
    public MonthType(String yearMonth, int typeCount) {
        this.yearMonth = yearMonth;
        this.typeCount = typeCount;
    }

    /**
     * @return allows the year and month string to be returned
     */
    public String getYearMonth() {
        return yearMonth;
    }

    /**
     * @return allows the type of appointment count to be returned
     */
    public int getTypeCount() {
        return typeCount;
    }


    /**
     * @param yearMonth allows the yearMonth string to be set
     */
    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    /**
     * @param typeCount allows the typeCount to be set
     */
    public void setTypeCount(int typeCount) {
        this.typeCount = typeCount;
    }
}

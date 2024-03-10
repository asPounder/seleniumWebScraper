import java.time.LocalDate;

/**
 * The TimetableData class represents timetable data.
 */
public class TimetableData {
    /** The timetable data represented as a 2D array. */
    public String[][] timetable;
    /** The date associated with the timetable data. */
    public LocalDate date;

    /**
     * Constructs a TimetableData object with the given timetable and date.
     *
     * @param timetable The timetable data as a 2D array.
     * @param date      The date associated with the timetable data.
     */
    public TimetableData(String[][] timetable, LocalDate date) {
        this.timetable = timetable;
        this.date = date;
    }

    /**
     * Constructs a TimetableData object with null timetable and date.
     */
    public TimetableData() {
        this(null, null);
    }
}

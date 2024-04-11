import java.time.LocalDate;

/**
 * The helper TimetableData class.
 */
public class TimetableData {
    /** 
     * The timetable itself represented as an String array of arrays of length 2.
     * Formated as [subject, time]
     */
    public String[][] timetable;
    /** The timestamp associated with the timetable. */
    public LocalDate timestamp;

    /**
     * Constructs a TimetableData object with the given timetable and timestamp.
     */
    public TimetableData(String[][] timetable, LocalDate timestamp) {
        this.timetable = timetable;
        this.timestamp = timestamp;
    }

    /**
     * Constructs a TimetableData object with null timetable and timestamp.
     */
    public TimetableData() {
        this(null, null);
    }
}

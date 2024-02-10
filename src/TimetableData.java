import java.time.LocalDate;

public class TimetableData {
    public String[][] timetable;
    public LocalDate date;

    public TimetableData(String[][] timetable, LocalDate date) {
        this.timetable = timetable;
        this.date = date;
    }

    public TimetableData() {
        this(null, null);
    }
}

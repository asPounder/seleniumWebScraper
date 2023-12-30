package exceptions;

public class NoTimetableException extends RuntimeException{
    public NoTimetableException(String str) {
        super(str);
    }
}

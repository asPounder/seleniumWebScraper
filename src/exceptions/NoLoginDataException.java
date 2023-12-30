package exceptions;

public class NoLoginDataException extends RuntimeException{
    public NoLoginDataException(String str) {
        super(str);
    }
}
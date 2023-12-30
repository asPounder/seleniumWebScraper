package exceptions;

public class LoginFailureException extends RuntimeException{
    public LoginFailureException(String str) {
        super(str);
    }
}

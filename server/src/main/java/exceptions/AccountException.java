package exceptions;

public class AccountException extends RuntimeException{
    public AccountException(String msg, Exception cause) {
        super(msg, cause);
    }
    public AccountException(String msg) {
        super(msg);
    }
}

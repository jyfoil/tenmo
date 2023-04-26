package exceptions;

public class DaoException extends RuntimeException {

    public DaoException(String msg, Exception cause) {
        super(msg, cause);
    }
    public DaoException(String msg) {
        super(msg);
    }
}

package exceptions;

public class TransferException extends RuntimeException {
    public TransferException(String msg, Exception cause) {
        super(msg, cause);
    }
    public TransferException(String msg) {
        super(msg);
    }
}

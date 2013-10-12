public class RegisterIllegalArgumentException extends IllegalArgumentException {
    public RegisterIllegalArgumentException() {
        super();
    }

    public RegisterIllegalArgumentException(String s) {
        super(s);
    }

    public RegisterIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegisterIllegalArgumentException(Throwable cause) {
        super(cause);
    }
}
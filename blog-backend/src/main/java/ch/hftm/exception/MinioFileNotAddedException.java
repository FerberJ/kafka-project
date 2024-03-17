package ch.hftm.exception;

public class MinioFileNotAddedException extends Exception {
    public MinioFileNotAddedException() {
        super();
    }

    public MinioFileNotAddedException(String message) {
        super(message);
    }

    public MinioFileNotAddedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioFileNotAddedException(Throwable cause) {
        super(cause);
    }
}

package goduoel.com.kakaointern.data.error;

public class RetryException extends Exception {

    private RetryType retryType;

    public RetryType getRetryType() {
        return retryType;
    }

    public RetryException(int i, RetryType retryType) {
        super("재시도 (" + i + "/3)");
        this.retryType = retryType;
    }

    public RetryException(String message, RetryType retryType) {
        super(message);
        this.retryType = retryType;
    }
}

package candybar.lib.items;

public class BillingResult {
    private final int responseCode;
    private final String debugMessage;

    public static final int OK = 0;
    public static final int USER_CANCELED = 1;
    public static final int SERVICE_DISCONNECTED = 2;
    public static final int ERROR = -1;

    public BillingResult(int responseCode, String debugMessage) {
        this.responseCode = responseCode;
        this.debugMessage = debugMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public static class Builder {
        private int responseCode;
        private String debugMessage;

        public Builder setResponseCode(int responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder setDebugMessage(String debugMessage) {
            this.debugMessage = debugMessage;
            return this;
        }

        public BillingResult build() {
            return new BillingResult(responseCode, debugMessage);
        }
    }
} 
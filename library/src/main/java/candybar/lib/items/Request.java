package candybar.lib.items;

import android.content.ComponentName;

public class Request {
    private final String name;
    private final String activity;
    private final String packageName;
    private final String orderId;
    private final String productId;
    private final String requestedOn;
    private boolean isRequested;
    private boolean isAvailableForRequest;
    private String infoText;
    private String fileName;
    private String iconBase64;

    public Request(String name, String activity, String packageName, String orderId, String productId, String requestedOn) {
        this.name = name;
        this.activity = activity;
        this.packageName = packageName;
        this.orderId = orderId;
        this.productId = productId;
        this.requestedOn = requestedOn;
        this.isRequested = false;
        this.isAvailableForRequest = true;
        this.infoText = "";
    }

    public String getName() {
        return name;
    }

    public String getActivity() {
        return activity;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public String getRequestedOn() {
        return requestedOn;
    }

    public boolean isRequested() {
        return isRequested;
    }

    public void setRequested(boolean requested) {
        isRequested = requested;
    }

    public boolean isAvailableForRequest() {
        return isAvailableForRequest;
    }

    public void setAvailableForRequest(boolean availableForRequest) {
        isAvailableForRequest = availableForRequest;
    }

    public String getInfoText() {
        return infoText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText != null ? infoText : "";
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setIconBase64(String iconBase64) {
        this.iconBase64 = iconBase64;
    }

    public String getIconBase64() {
        return iconBase64;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String activity;
        private String packageName;
        private String orderId;
        private String productId;
        private String requestedOn;
        private boolean isRequested;
        private boolean isAvailableForRequest = true;
        private String infoText = "";

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder activity(String activity) {
            this.activity = activity;
            return this;
        }

        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder productId(String productId) {
            this.productId = productId;
            return this;
        }

        public Builder requestedOn(String requestedOn) {
            this.requestedOn = requestedOn;
            return this;
        }

        public Builder requested(boolean requested) {
            this.isRequested = requested;
            return this;
        }

        public Builder availableForRequest(boolean availableForRequest) {
            this.isAvailableForRequest = availableForRequest;
            return this;
        }

        public Builder infoText(String infoText) {
            this.infoText = infoText;
            return this;
        }

        public Request build() {
            Request request = new Request(name, activity, packageName, orderId, productId, requestedOn);
            request.setRequested(isRequested);
            request.setAvailableForRequest(isAvailableForRequest);
            request.setInfoText(infoText);
            return request;
        }
    }

    public static class Property {
        private final String name;
        private final String orderId;
        private final String productId;
        private ComponentName componentName;

        public Property(String name, String orderId, String productId) {
            this.name = name;
            this.orderId = orderId;
            this.productId = productId;
        }

        public String getName() {
            return name;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getProductId() {
            return productId;
        }

        public ComponentName getComponentName() {
            return componentName;
        }

        public void setComponentName(ComponentName componentName) {
            this.componentName = componentName;
        }
    }
}

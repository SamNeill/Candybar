package candybar.lib.items;

import java.util.List;

public class Purchase {
    private final String orderId;
    private final String purchaseToken;
    private final long purchaseTime;
    private final List<String> products;
    private final boolean isAcknowledged;
    private final int purchaseState;

    public Purchase(String orderId, String purchaseToken, long purchaseTime, 
                   List<String> products, boolean isAcknowledged, int purchaseState) {
        this.orderId = orderId;
        this.purchaseToken = purchaseToken;
        this.purchaseTime = purchaseTime;
        this.products = products;
        this.isAcknowledged = isAcknowledged;
        this.purchaseState = purchaseState;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public List<String> getProducts() {
        return products;
    }

    public boolean isAcknowledged() {
        return isAcknowledged;
    }

    public int getPurchaseState() {
        return purchaseState;
    }

    public static class Builder {
        private String orderId;
        private String purchaseToken;
        private long purchaseTime;
        private List<String> products;
        private boolean isAcknowledged;
        private int purchaseState;

        public Builder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder setPurchaseToken(String purchaseToken) {
            this.purchaseToken = purchaseToken;
            return this;
        }

        public Builder setPurchaseTime(long purchaseTime) {
            this.purchaseTime = purchaseTime;
            return this;
        }

        public Builder setProducts(List<String> products) {
            this.products = products;
            return this;
        }

        public Builder setAcknowledged(boolean acknowledged) {
            isAcknowledged = acknowledged;
            return this;
        }

        public Builder setPurchaseState(int purchaseState) {
            this.purchaseState = purchaseState;
            return this;
        }

        public Purchase build() {
            return new Purchase(orderId, purchaseToken, purchaseTime, 
                              products, isAcknowledged, purchaseState);
        }
    }
} 
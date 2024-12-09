package candybar.lib.items;

public class ProductDetails {
    private final String productId;
    private final String title;
    private final String description;
    private final String price;
    private final String type;

    public ProductDetails(String productId, String title, String description, String price, String type) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.type = type;
    }

    public String getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public static class Builder {
        private String productId;
        private String title;
        private String description;
        private String price;
        private String type;

        public Builder setProductId(String productId) {
            this.productId = productId;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setPrice(String price) {
            this.price = price;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public ProductDetails build() {
            return new ProductDetails(productId, title, description, price, type);
        }
    }
} 
package candybar.lib.items;

import androidx.annotation.NonNull;

import candybar.lib.items.ProductDetails;

/*
 * CandyBar - Material Dashboard
 *
 * Copyright (c) 2014-2016 Dani Mahardhika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class InAppBilling {

    private final String mProductId;
    private int mProductCount;
    private ProductDetails mProductDetails;
    private String mPrice;
    private String mProductName;

    public static final int DONATE = 0;
    public static final int PREMIUM_REQUEST = 1;

    public InAppBilling(String productId) {
        mProductId = productId;
    }

    public InAppBilling(@NonNull ProductDetails productDetails, String productId) {
        mProductDetails = productDetails;
        mProductId = productId;
        mProductName = productDetails.getTitle();
        mPrice = productDetails.getPrice();
    }

    public InAppBilling(@NonNull ProductDetails productDetails, String productId, int productCount) {
        mProductDetails = productDetails;
        mProductId = productId;
        mProductCount = productCount;
        mProductName = productDetails.getTitle();
        mPrice = productDetails.getPrice();
    }

    public InAppBilling(String productId, int productCount) {
        mProductId = productId;
        mProductCount = productCount;
    }

    public InAppBilling(String productId, String price) {
        mProductId = productId;
        mPrice = price;
    }

    public InAppBilling(String productId, String price, String productName) {
        mProductId = productId;
        mPrice = price;
        mProductName = productName;
    }

    public InAppBilling(String productId, String price, String productName, int productCount) {
        mProductId = productId;
        mPrice = price;
        mProductName = productName;
        mProductCount = productCount;
    }

    public String getProductId() {
        return mProductId;
    }

    public int getProductCount() {
        return mProductCount;
    }

    public void setProductCount(int count) {
        mProductCount = count;
    }

    public ProductDetails getProductDetails() {
        return mProductDetails;
    }

    public void setProductDetails(ProductDetails productDetails) {
        mProductDetails = productDetails;
        if (productDetails != null) {
            mProductName = productDetails.getTitle();
            mPrice = productDetails.getPrice();
        }
    }

    public String getPrice() {
        if (mPrice != null) {
            return mPrice;
        }
        if (mProductDetails != null) {
            return mProductDetails.getPrice();
        }
        return "";
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getProductName() {
        if (mProductName != null) {
            return mProductName;
        }
        if (mProductDetails != null) {
            return mProductDetails.getTitle();
        }
        return mProductId;
    }

    public void setProductName(String productName) {
        mProductName = productName;
    }
}

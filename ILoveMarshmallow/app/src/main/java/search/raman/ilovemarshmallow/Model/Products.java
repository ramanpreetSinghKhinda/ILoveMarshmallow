package search.raman.ilovemarshmallow.Model;

import java.net.URL;

/********************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This class is model base class for Products information
 ********************************************************************************************************************************/
public class Products {
    private String asinId, productName, brandName, productUrl, originalPrice, productPrice;
    private int productRating;
    private URL imageUrl;


    public Products() {

    }

    public Products(String asinId, String productName, String brandName) {
        this.asinId = asinId;
        this.productName = productName;
        this.brandName = brandName;
    }

    public Products(String asinId, String productName, String brandName, URL imageUrl, String productUrl, String originalPrice, String productPrice, int productRating) {
        this.asinId = asinId;
        this.productName = productName;
        this.brandName = brandName;
        this.imageUrl = imageUrl;
        this.productUrl = productUrl;
        this.originalPrice = originalPrice;
        this.productPrice = productPrice;
        this.productRating = productRating;
    }

    public String getAsinId() {
        return asinId;
    }


    public String getProductName() {
        if (null != productName)
            return productName;

        return "Product Name";
    }


    public String getBrandName() {
        return brandName;
    }


    public URL getImageUrl() {
        return imageUrl;

    }


    public String getProductUrl() {
        return productUrl;
    }


    public String getOriginalPrice() {
        return originalPrice;
    }

    public String getProductPrice() {
        if (null != productPrice)
            return productPrice;
        else
            return "$ 0";
    }

    public int getProductRating() {
        return productRating;
    }

    public void setAsinId(String asinId) {
        this.asinId = asinId;
    }


    public void setProductName(String productName) {
        this.productName = productName;
    }


    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }


    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }


    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }


    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductRating(int productRating) {
        this.productRating = productRating;
    }

}
package search.raman.ilovemarshmallow.Model;

import java.net.URL;
import java.util.ArrayList;

/********************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This class is model class for Individual Product information and extends features from base Products class
 ********************************************************************************************************************************/
public class Item extends Products {
    private String defaultProductType, description;
    private ArrayList<String> gender;
    private URL defaultImageUrl;

    public Item() {
        super();
    }

    public Item(String asinId, String defaultProductType, String productName, String brandName, URL defaultImageUrl, ArrayList<String> gender, String description) {
        super(asinId, productName, brandName);

        this.defaultProductType = defaultProductType;
        this.defaultImageUrl = defaultImageUrl;
        this.gender = gender;
        this.description = description;
    }

    public String getDefaultProductType() {
        return defaultProductType;
    }

    public URL getDefaultImageUrl() {
        return defaultImageUrl;
    }

    public ArrayList<String> getGenderArray() {
        return gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDefaultProductType(String defaultProductType) {
        this.defaultProductType = defaultProductType;
    }


    public void setDefaultImageUrl(URL defaultImageUrl) {
        this.defaultImageUrl = defaultImageUrl;
    }


    public void setGenderArray(ArrayList<String> gender) {
        this.gender = gender;
    }


    public void setDescription(String description) {
        this.description = description;
    }

}
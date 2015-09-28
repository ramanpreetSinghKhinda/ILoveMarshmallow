package search.raman.ilovemarshmallow.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import search.raman.ilovemarshmallow.Gcm.GcmActivity;
import search.raman.ilovemarshmallow.Model.Item;
import search.raman.ilovemarshmallow.R;
import search.raman.ilovemarshmallow.Service.ProductGetService;
import search.raman.ilovemarshmallow.Utilities.Globals;
import search.raman.ilovemarshmallow.Utilities.Utility;

/********************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This class is responsible for showing detailed product information on the screen
 ********************************************************************************************************************************/
public class DetailViewActivity extends AppCompatActivity {
    private String clearNotificationTo, currentUserEmailId, asinId, productName, brandName, originalPrice,
            productPrice, description, defaultProductType, productImageUrlString, descDetail;

    private EditText friendEmailId, loveMessage;
    private URL defaultImageUrl, productImageUrl;
    private ImageView detailImageView;
    private RatingBar mProductRating;
    private int productRating;

    private Item mItem;
    private ProductGetService sh;
    private AsyncTask<String, String, Bitmap> mAsyncTask;
    private AsyncTask<String, String, Bitmap> mImageAsyncTask;

    private Toolbar detailToolbar;
    private TextView txtToolbarTitle;
    private Typeface romanticTypeface, boldRomanticTypeface;

    private Button btnShare;
    private ArrayList<String> genderArray = new ArrayList<String>();
    private StringBuffer descString = new StringBuffer();
    private TextView txtProductName, txtBrandName, txtProductPrice, txtDescription, txtDescriptionDetail;

    private GcmActivity gcmObject;
    private Utility utility;
    private ProgressBar loadingImageSpinner;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (null != intent) {
            asinId = intent.getStringExtra(Globals.ASIN_ID);
            productName = intent.getStringExtra(Globals.PRODUCT_NAME);
            productImageUrlString = intent.getStringExtra(Globals.PRODUCT_IMAGE_URL);
            brandName = intent.getStringExtra(Globals.BRAND_NAME);
            productPrice = intent.getStringExtra(Globals.PRODUCT_PRICE);
            productRating = intent.getIntExtra(Globals.PRODUCT_RATING, 0);
            currentUserEmailId = intent.getStringExtra(Globals.CURRENT_USER_EMAIL_ID);
        }

        detailToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        detailImageView = (ImageView) findViewById(R.id.detailImageView);
        txtProductName = (TextView) findViewById(R.id.product_name);
        txtBrandName = (TextView) findViewById(R.id.brand_name);
        txtProductPrice = (TextView) findViewById(R.id.product_price);
        mProductRating = (RatingBar) findViewById(R.id.product_rating);
        txtDescription = (TextView) findViewById(R.id.description);
        txtDescriptionDetail = (TextView) findViewById(R.id.descDetail);
        loadingImageSpinner = (ProgressBar) findViewById(R.id.loadingImageSpinner);

        // this is for notification sync feature
        // A user may have multiple devices logged in with same email id
        // If the user have seen one notification on one of the device than the notification will automatically get removed from his/her other devices
        clearNotificationTo = intent.getStringExtra(Globals.SEND_CLEAR_NOTIFICATION_TO);

        initiateDetailView();

        // Call sendNotificationClearMessage() API for notifying to other Android devices which are Logged in with same user id, that message is seen
        // Will function only when user tapped the notification and app is launched first time or resumed after getting killed
        gcmObject.sendNotificationClearMessage(clearNotificationTo);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // override onNewIntent() and call the sendNotificationClearMessage() API
    // Will function every time when notification is tapped by the user
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        asinId = intent.getStringExtra(Globals.ASIN_ID);
        productName = intent.getStringExtra(Globals.PRODUCT_NAME);
        productImageUrlString = intent.getStringExtra(Globals.PRODUCT_IMAGE_URL);
        brandName = intent.getStringExtra(Globals.BRAND_NAME);
        productPrice = intent.getStringExtra(Globals.PRODUCT_PRICE);
        productRating = intent.getIntExtra(Globals.PRODUCT_RATING, 0);
        currentUserEmailId = intent.getStringExtra(Globals.CURRENT_USER_EMAIL_ID);

        // this is for notification sync feature
        // A user may have multiple devices logged in with same email id
        // If the user have seen one notification on one of the device than the notification will automatically get removed from his/her other devices
        clearNotificationTo = intent.getStringExtra(Globals.SEND_CLEAR_NOTIFICATION_TO);

        initiateDetailView();

        // Call sendNotificationClearMessage() API for notifying to other Android devices which are Logged in with same user id, that message is seen
        gcmObject.sendNotificationClearMessage(clearNotificationTo);
    }

    public void showToast(String msg, int time) {
        Toast.makeText(this, msg, time).show();
    }

    private void initiateDetailView() {
        utility = new Utility();
        gcmObject = GcmActivity.getInstance(this);

        initiateToolbarTabs();
        setDetails();
        getProductDetails(asinId);
    }

    private void initiateToolbarTabs() {
        romanticTypeface = Typeface.createFromAsset(getAssets(), Globals.ROMANTIC_FONT);
        boldRomanticTypeface = Typeface.create(romanticTypeface, Typeface.BOLD);

        if(!detailToolbar.getMenu().hasVisibleItems())
            detailToolbar.inflateMenu(R.menu.detail_view_menu);

        txtToolbarTitle = (TextView) detailToolbar.findViewById(R.id.app_toolbar_title);
        txtToolbarTitle.setTypeface(boldRomanticTypeface);
        txtToolbarTitle.setText(brandName);

        detailToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white);
        detailToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        detailToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItem = item.getItemId();
                switch (menuItem) {
                    case R.id.action_share:
                        shareProduct();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void shareProduct() {
        final Dialog shareProductDialog = new Dialog(this);
        shareProductDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        shareProductDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        shareProductDialog.setContentView(R.layout.share_product_dialog);
        shareProductDialog.show();

        btnShare = (Button) shareProductDialog.findViewById(R.id.btnShare);
        friendEmailId = (EditText) shareProductDialog.findViewById(R.id.friendEmailId);
        loveMessage = (EditText) shareProductDialog.findViewById(R.id.loveMessage);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receiverEmailId = friendEmailId.getText().toString();

                if (!(utility.isValidEmail(receiverEmailId))) {
                    showToast("Email id is not valid", Toast.LENGTH_SHORT);
                } else if (gcmObject.sendNotification(currentUserEmailId, receiverEmailId, loveMessage.getText().toString(), asinId, productName, productImageUrlString, brandName, productPrice, productRating)) {
                    // Call the sendNotification() API for sending GCM Push Notification to a particular user
                    showToast("Message Sent Successfully", Toast.LENGTH_LONG);
                    shareProductDialog.dismiss();
                }

            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            drawProductName();

        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            drawProductName();
        }
    }

    // this method will use dyanamic drawing of product name
    // so that it should not overlap with other views when the text is drawn on the screen
    private void drawProductName() {
        Paint mPaint = new Paint();
        mPaint.setTypeface(Typeface.DEFAULT);
        mPaint.setTextSize(utility.pxFromDp(this, 22)); // have this the same as your text size
        mPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));


        int screenWidth = (int) utility.getScreenWidth(getWindowManager().getDefaultDisplay());
        int productPriceWidth = (int) mPaint.measureText(productPrice, 0, productPrice.length());
        int extraMarginsOnEnds = (int) utility.pxFromDp(this, 40);

        int width = screenWidth - (productPriceWidth + extraMarginsOnEnds);
        txtProductName.setWidth(width);
        txtProductName.setText(productName);

    }

    private void setDescription() {
        descString.append(Html.fromHtml(mItem.getDescription(), null, utility.new MyTagHandler()));

        int first = descString.indexOf("•");
        int second = descString.indexOf("•", first + 1);
        int third = utility.nthLastIndexOf(descString.toString(), '•', 3);

        description = descString.substring(first, second).replace("• ", "").replace("\n", "");
        descDetail = descString.substring(third, descString.length());
        txtDescription.setText(description);
        txtDescriptionDetail.setText(descString);
    }

    private void setDetails() {
        drawProductName();
        txtBrandName.setText(brandName);
        txtProductPrice.setText(productPrice);

        if (productRating == 0)
            mProductRating.setVisibility(View.INVISIBLE);
        else {
            mProductRating.setVisibility(View.VISIBLE);
            mProductRating.setRating(productRating);
        }
    }

    private void getProductDetails(final String asinId) {
        mAsyncTask = new AsyncTask<String, String, Bitmap>() {
            @Override
            protected void onPreExecute() {
                sh = new ProductGetService();
                loadingImageSpinner.setVisibility(View.VISIBLE);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(Globals.PRODUCT_URL + params[0], ProductGetService.GET);
                Bitmap itemView = null;

                try {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonMainObj = (JSONObject) jsonParser.parse(jsonStr);

                    mItem = new Item();

                    if (null != jsonMainObj.get("defaultProductType")) {
                        defaultProductType = (String) jsonMainObj.get("defaultProductType");
                        mItem.setDefaultProductType(defaultProductType);
                        Log.v(Globals.TAG, " defaultProductType : " + mItem.getDefaultProductType());
                    }

                    if (null != jsonMainObj.get("defaultImageUrl")) {
                        defaultImageUrl = new URL((String) jsonMainObj.get("defaultImageUrl"));
                        mItem.setDefaultImageUrl(defaultImageUrl);
                        Log.v(Globals.TAG, " defaultImageUrl : " + mItem.getDefaultImageUrl());

                    }

                    if (null != jsonMainObj.get("description")) {
                        description = (String) jsonMainObj.get("description");
                        mItem.setDescription(description);
                        Log.v(Globals.TAG, " description : " + mItem.getDescription());
                    }

                    if (null != jsonMainObj.get("genders")) {
                        JSONArray jsonGenderArray = (JSONArray) jsonMainObj.get("genders");

                        int length = jsonGenderArray.size();
                        int counter = 0;
                        while (counter < length) {
                            genderArray.add((String) jsonGenderArray.get(counter));
                            ++counter;
                        }
                        mItem.setGenderArray(genderArray);
                        Log.v(Globals.TAG, " genderArray: " + mItem.getGenderArray());

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.v(Globals.TAG, "ProductGetService couldn't get any data from the url");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return itemView;
            }

            @Override
            protected void onPostExecute(Bitmap resultItemView) {
                super.onPostExecute(resultItemView);
                setDescription();
                downLoadImageTask(mItem.getDefaultImageUrl(), false);
            }
        };
        mAsyncTask.execute(asinId);
    }

    private void downLoadImageTask(URL imageUrl, final Boolean isNotification) {
        final URL myImageUrl = imageUrl;
        mImageAsyncTask = new AsyncTask<String, String, Bitmap>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Bitmap doInBackground(String... data) {
                HttpURLConnection connection = null;
                InputStream input = null;
                try {
                    connection = (HttpURLConnection) myImageUrl
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    input = connection.getInputStream();
                    BufferedInputStream buffer = new BufferedInputStream(input);
                    Bitmap myBitmap = BitmapFactory.decodeStream(buffer);

                    return myBitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (null != connection) {
                        connection.disconnect();
                    }
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (isNotification) {
//                    pushNotification(result);
                } else {
                    loadingImageSpinner.setVisibility(View.GONE);

                    if (result == null) {
                        result = BitmapFactory.decodeResource(getResources(), R.drawable.image_placeholder);
                    }
                    setBitmap(result);
                }
            }

        };
        mImageAsyncTask.execute();
    }

    public void setBitmap(Bitmap bitmap) {
        detailImageView.setImageBitmap(bitmap);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

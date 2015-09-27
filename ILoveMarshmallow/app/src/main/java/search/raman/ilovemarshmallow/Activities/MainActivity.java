package search.raman.ilovemarshmallow.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.MalformedURLException;
import java.net.URL;

import search.raman.ilovemarshmallow.Adapter.GridItemsAdapter;
import search.raman.ilovemarshmallow.Model.Products;
import search.raman.ilovemarshmallow.R;
import search.raman.ilovemarshmallow.Service.ProductGetService;
import search.raman.ilovemarshmallow.Utilities.Globals;
import search.raman.ilovemarshmallow.Utilities.Utility;

/********************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This is the MainActivity and is responsible for searching the products using api calls to server
 ********************************************************************************************************************************/
public class MainActivity extends AppCompatActivity {
    private ScrollView trendingScrollView;
    private RelativeLayout recyclerLayout;
    private RecyclerView recyclerProductView;
    private CardView cardSearch;
    private GridItemsAdapter productsGridAdapter;
    private Products[] mProducts;
    private ProductGetService productGetService;
    private AsyncTask<String, Boolean, Boolean> mProductSearchAsyncTask;

    private ImageView imgBackOnSearch, clearSearch;
    private EditText editTextSearch;
    private Toolbar appToolbar;
    private TextView txtToolbarTitle;
    private Typeface romanticTypeface, boldRomanticTypeface;

    private Button btnShopMensShirts, btnShopDuneLondon, btnShopWomenJackets, btnShopWomenJeans, btnShopWomenHeels;
    private Dialog progressDialog;
    private URL imageUrl;
    private Utility utility;
    private int productRating;
    private String currentUserEmailId, userName, asinId, productName, brandName, productUrl, originalPrice, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utility = new Utility();
        productsGridAdapter = new GridItemsAdapter(this);

        cardSearch = (CardView) findViewById(R.id.card_search);
        imgBackOnSearch = (ImageView) findViewById(R.id.img_back_on_search);
        editTextSearch = (EditText) cardSearch.findViewById(R.id.editText_search);
        clearSearch = (ImageView) findViewById(R.id.clear_search);

        //trending collections view
        trendingScrollView = (ScrollView) findViewById(R.id.trending_scroll_view);
        btnShopMensShirts = (Button) findViewById(R.id.btn_shop_mens_shirts);
        btnShopDuneLondon = (Button) findViewById(R.id.btn_shop_dune_london);
        btnShopWomenJackets = (Button) findViewById(R.id.btn_shop_women_jackets);
        btnShopWomenJeans = (Button) findViewById(R.id.btn_shop_women_jeans);
        btnShopWomenHeels = (Button) findViewById(R.id.btn_shop_women_heels);

        recyclerLayout = (RelativeLayout) findViewById(R.id.recycler_layout);
        recyclerProductView = (RecyclerView) findViewById(R.id.recycler_product_view);
        recyclerProductView.setHasFixedSize(true);

        recyclerLayout.setVisibility(View.GONE);
        trendingScrollView.setVisibility(View.VISIBLE);

        InitiateToolbarTabs();
        InitiateSearch();
        HandleSearch();
        HandleTrendingCollections();
    }

    private void HandleTrendingCollections() {
        btnShopMensShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchProduct("shirts");
            }
        });

        btnShopDuneLondon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchProduct("Dune");
            }
        });

        btnShopWomenJackets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchProduct("jackets");
            }
        });

        btnShopWomenJeans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchProduct("jeans");
            }
        });

        btnShopWomenHeels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchProduct("heels");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void InitiateToolbarTabs() {
        try {
            currentUserEmailId = getIntent().getStringExtra(Globals.CURRENT_USER_EMAIL_ID);
            userName = currentUserEmailId.substring(0, currentUserEmailId.indexOf('@'));
        } catch (StringIndexOutOfBoundsException e){
            userName = currentUserEmailId;
        }

        romanticTypeface = Typeface.createFromAsset(getAssets(), Globals.ROMANTIC_FONT);
        boldRomanticTypeface = Typeface.create(romanticTypeface, Typeface.BOLD);

        appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        appToolbar.inflateMenu(R.menu.main_menu);
        txtToolbarTitle = (TextView) appToolbar.findViewById(R.id.app_toolbar_title);
        txtToolbarTitle.setTypeface(boldRomanticTypeface);

        txtToolbarTitle.setText("Hi " + userName);
    }

    public void handleSearchToolBar() {
        if (cardSearch.getVisibility() == View.VISIBLE) {
            hideSoftKeyboard();
            cardSearch.setVisibility(View.GONE);
            cardSearch.setAnimation(AnimationUtils.loadAnimation(this.getApplicationContext(), android.R.anim.fade_out));
            cardSearch.getAnimation().setDuration(300);
            cardSearch.setEnabled(false);
        } else {
            cardSearch.setAnimation(AnimationUtils.loadAnimation(this.getApplicationContext(), android.R.anim.fade_in));
            cardSearch.getAnimation().setDuration(300);
            cardSearch.setEnabled(true);
            cardSearch.setVisibility(View.VISIBLE);
            editTextSearch.setText("");
            editTextSearch.requestFocus();
            showSoftKeyboard(editTextSearch);
        }
    }

    private void showTrendingView() {
        if (recyclerLayout.getVisibility() == View.VISIBLE) {
            recyclerLayout.setVisibility(View.GONE);
        }

        if (trendingScrollView.getVisibility() == View.GONE) {
            trendingScrollView.setVisibility(View.VISIBLE);
        }
    }


    private void InitiateSearch() {
        appToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItem = item.getItemId();
                switch (menuItem) {
                    case R.id.action_search:
                        handleSearchToolBar();
                        break;

                    case R.id.action_trending:
                        showTrendingView();
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextSearch.getText().toString().length() == 0) {
                    clearSearch.setImageResource(R.mipmap.ic_search);
                } else {
                    clearSearch.setImageResource(R.mipmap.ic_close);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextSearch.getText().toString().length() == 0) {

                } else {
                    editTextSearch.setText("");
                    hideSoftKeyboard();
                }
            }
        });
    }

    private void HandleSearch() {
        imgBackOnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSearchToolBar();
            }
        });

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (editTextSearch.getText().toString().trim().length() > 0) {
                        handleSearchToolBar();
                        searchProduct(editTextSearch.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    private void showProgressDialog() {
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.dialog_view);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void showToast(String msg, int time) {
        Toast.makeText(this, msg, time).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (recyclerLayout.getVisibility() == View.VISIBLE)
                drawCardGrids();

        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (recyclerLayout.getVisibility() == View.VISIBLE)
                drawCardGrids();
        }
    }

    private Boolean success = false;

    private void searchProduct(final String item) {
        mProductSearchAsyncTask = new AsyncTask<String, Boolean, Boolean>() {
            @Override
            protected void onPreExecute() {
                productGetService = new ProductGetService();
                showProgressDialog();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                String jsonStr = null;
                try {
                    jsonStr = productGetService.makeServiceCall(Globals.SEARCH_URL + params[0], ProductGetService.GET);

                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonMainObj = (JSONObject) jsonParser.parse(jsonStr);
                    JSONArray results = (JSONArray) jsonMainObj.get("results");

                    int countOfProducts = results.size();
                    mProducts = new Products[countOfProducts];
                    int counter = 0;
                    while (counter < countOfProducts) {
                        JSONObject resultProducts = (JSONObject) results.get(counter);
                        mProducts[counter] = new Products();

                        if (null != resultProducts.get("asin")) {
                            asinId = (String) resultProducts.get("asin");
                            mProducts[counter].setAsinId(asinId);
                            Log.v(Globals.TAG, " mProducts[counter].getAsinId() : " + mProducts[counter].getAsinId());
                        }

                        if (null != resultProducts.get("productName")) {
                            productName = (String) resultProducts.get("productName");
                            mProducts[counter].setProductName(productName);
                            Log.v(Globals.TAG, " mProducts[counter].getProductName() : " + mProducts[counter].getProductName());
                        }

                        if (null != resultProducts.get("brandName")) {
                            brandName = (String) resultProducts.get("brandName");
                            mProducts[counter].setBrandName(brandName);
                            Log.v(Globals.TAG, " mProducts[counter].getBrandName() : " + mProducts[counter].getBrandName());
                        }

                        if (null != resultProducts.get("imageUrl")) {
                            imageUrl = new URL((String) resultProducts.get("imageUrl"));
                            mProducts[counter].setImageUrl(imageUrl);
                            Log.v(Globals.TAG, " mProducts[counter].getImageUrl() : " + mProducts[counter].getImageUrl());
                        }

                        if (null != resultProducts.get("productUrl")) {
                            productUrl = (String) resultProducts.get("productUrl");
                            mProducts[counter].setProductUrl(productUrl);
                            Log.v(Globals.TAG, " mProducts[counter].getProductUrl() : " + mProducts[counter].getProductUrl());
                        }

                        if (null != resultProducts.get("originalPrice")) {
                            originalPrice = (String) resultProducts.get("originalPrice");
                            mProducts[counter].setOriginalPrice(originalPrice);
                            Log.v(Globals.TAG, " mProducts[counter].getOriginalPrice() : " + mProducts[counter].getOriginalPrice());
                        }

                        if (null != resultProducts.get("price")) {
                            price = (String) resultProducts.get("price");
                            mProducts[counter].setPrice(price);
                            Log.v(Globals.TAG, " mProducts[counter].getProductPrice() : " + mProducts[counter].getProductPrice());
                        }

                        if (null != resultProducts.get("productRating")) {
                            productRating = ((Double) resultProducts.get("productRating")).intValue();
                            mProducts[counter].setProductRating(productRating);
                            Log.v(Globals.TAG, " mProducts[counter].getroductRating() : " + mProducts[counter].getProductRating());
                        }

                        success = true;
                        ++counter;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                    success = false;
                    Log.v(Globals.TAG, "ProductGetService couldn't get any data from the url");
                } catch (MalformedURLException e) {
                    success = false;
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    success = false;
                    e.printStackTrace();
                } catch (Exception e) {
                    success = false;
                    e.printStackTrace();
                }

                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                hideProgressDialog();
                if (success) {
                    if (trendingScrollView.getVisibility() == View.VISIBLE) {
                        trendingScrollView.setVisibility(View.GONE);
                    }

                    recyclerLayout.setVisibility(View.VISIBLE);
                    productsGridAdapter.notifyDataSetChanged();
                    drawCardGrids();
                } else {
                    showToast(getResources().getString(R.string.product_not_found), Toast.LENGTH_LONG);
                }
            }

            @Override
            protected void onCancelled() {
                hideProgressDialog();

            }
        };
        mProductSearchAsyncTask.execute(item);
    }

    // this method will use dynamic grid drawing
    // and will calculate the number of grids to be drawn based upon the screen size and orientation
    public void drawCardGrids() {
        int cardWidth = (int) utility.pxFromDp(this, 170);
        int cardHeight = (int) utility.pxFromDp(this, 265);

        int screenWidth = (int) utility.getScreenWidth(getWindowManager().getDefaultDisplay());
        int num = screenWidth / cardWidth;

        int totalSpacing = screenWidth - cardWidth * num;
        int itemSpacing = totalSpacing / (num + 1);

        productsGridAdapter = new GridItemsAdapter(mProducts, cardWidth, cardHeight, itemSpacing);

        recyclerProductView.setLayoutManager(new GridLayoutManager(this, num));
        recyclerProductView.setAdapter(productsGridAdapter);
        productsGridAdapter.setOnItemClickListener(new GridItemsAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position, Products mProduct) {
                Log.v(Globals.TAG, "Touched item : " + position + ", AsinId : " + mProduct.getAsinId());

                Intent intent = new Intent(MainActivity.this, DetailViewActivity.class);
                intent.putExtra(Globals.ASIN_ID, mProduct.getAsinId());
                intent.putExtra(Globals.PRODUCT_NAME, mProduct.getProductName());
                intent.putExtra(Globals.BRAND_NAME, mProduct.getBrandName());
                intent.putExtra(Globals.PRODUCT_IMAGE_URL, mProduct.getImageUrl().toString());
                intent.putExtra(Globals.PRODUCT_PRICE, mProduct.getProductPrice());
                intent.putExtra(Globals.PRODUCT_RATING, mProduct.getProductRating());
                intent.putExtra(Globals.CURRENT_USER_EMAIL_ID, currentUserEmailId);

                startActivity(intent);
            }
        });
    }
}
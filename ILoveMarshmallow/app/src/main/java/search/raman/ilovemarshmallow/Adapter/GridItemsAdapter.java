package search.raman.ilovemarshmallow.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import search.raman.ilovemarshmallow.Model.Products;
import search.raman.ilovemarshmallow.R;

/********************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This class is responsible for showing data on each grid
 ********************************************************************************************************************************/
public class GridItemsAdapter extends RecyclerView.Adapter<GridItemsAdapter.ViewHolder> {
    private int cardWidth, cardHeight, itemSpacing;
    private Context mContext;
    private Products[] mProducts;
    private AsyncTask<String, Void, Bitmap> mAsyncTask;
    private ArrayList<String> asinIds = new ArrayList<String>();

    public GridItemsAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public GridItemsAdapter(Products[] mProducts) {
        super();

        this.mProducts = mProducts;
    }

    public GridItemsAdapter(Products[] mProducts, int cardWidth, int cardHeight, int itemSpacing) {
        super();

        this.mProducts = mProducts;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.itemSpacing = itemSpacing;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_layout, viewGroup, false);


        CardView.LayoutParams param = new CardView.LayoutParams(cardWidth, cardHeight);
        param.setMargins(itemSpacing / 2, 20, itemSpacing / 2, 20);

        CardView card = (CardView) v.findViewById(R.id.product_card);
        card.setLayoutParams(param);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        downLoadImageTask(mProducts[i].getImageUrl(), viewHolder);

        setAsinIds(mProducts[i].getAsinId());
        viewHolder.productName.setText(mProducts[i].getProductName());
        viewHolder.brandName.setText(mProducts[i].getBrandName());
        viewHolder.productPrice.setText(String.valueOf(mProducts[i].getProductPrice()));

        if (mProducts[i].getProductRating() > 0) {
            viewHolder.productRating.setVisibility(View.VISIBLE);
            viewHolder.productRating.setRating(mProducts[i].getProductRating());
        } else {
            viewHolder.productRating.setVisibility(View.INVISIBLE);
        }
    }

    private void downLoadImageTask(URL url, ViewHolder viewHolder) {
        final ViewHolder view = viewHolder;
        final URL imgUrl = url;

        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Bitmap doInBackground(String... data) {
                try {
                    InputStream in = imgUrl.openStream();
                    Bitmap itemView = BitmapFactory.decodeStream(in);

                    return itemView;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                setBitmap(view, result);

            }

        }.execute();
    }

    private void setBitmap(ViewHolder view, Bitmap result) {
        if (result == null) {
            result = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_placeholder);
        }
        view.productPhoto.setImageBitmap(result);
    }

    @Override
    public int getItemCount() {
        return mProducts.length;
    }


    private OnItemClickListener mItemClickListener;

    public void setAsinIds(String asinId) {
        this.asinIds.add(asinId);
    }

    public String getAsinId(int index) {
        return asinIds.get(index);
    }


    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, Products mProduct);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView productName, brandName, productPrice;
        private RatingBar productRating;
        private ImageView productPhoto;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.productPhoto = (ImageView) itemView.findViewById(R.id.product_photo);
            this.productName = (TextView) itemView.findViewById(R.id.product_name);
            this.brandName = (TextView) itemView.findViewById(R.id.brand_name);
            this.productPrice = (TextView) itemView.findViewById(R.id.product_price);
            this.productRating = (RatingBar) itemView.findViewById(R.id.product_rating);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getPosition(), mProducts[getPosition()]);
            }
        }
    }

}
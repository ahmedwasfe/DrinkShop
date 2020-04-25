package com.ahmet.drinkshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmet.drinkshop.Model.Banner;
import com.ahmet.drinkshop.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SliderBannerAdapter extends SliderViewAdapter<SliderBannerAdapter.BannerHolder> {


    private Context mContext;
    private List<Banner> mListBanner;
    private LayoutInflater inflater;

    public SliderBannerAdapter(Context mContext, List<Banner> mListBanner) {
        this.mContext = mContext;
        this.mListBanner = mListBanner;

        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public BannerHolder onCreateViewHolder(ViewGroup parent) {

        View layoutView = inflater.inflate(R.layout.raw_banner_slider, parent, false);
        return new BannerHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(BannerHolder viewHolder, int position) {

        Picasso.get()
                .load(mListBanner.get(position).getLink())
                .placeholder(R.drawable.drink_shop_bg)
                .into(viewHolder.mImgBanner);
        viewHolder.mTxtBannerName.setText(mListBanner.get(position).getName());

    }

    @Override
    public int getCount() {
        return mListBanner.size();
    }

    class BannerHolder extends SliderViewAdapter.ViewHolder{

        @BindView(R.id.img_banner)
        ImageView mImgBanner;
        @BindView(R.id.txt_banner_name)
        TextView mTxtBannerName;

        public BannerHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

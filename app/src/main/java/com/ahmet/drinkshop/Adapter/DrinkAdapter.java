package com.ahmet.drinkshop.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.drinkshop.RoomDatabase.Model.Cart;
import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Interface.IRecyclerItemClickListener;
import com.ahmet.drinkshop.Model.Drink;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.RoomDatabase.Model.Favorite;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkHolder> {

    private Context mContext;
    private List<Drink> mListDrink;
    private LayoutInflater inflater;

    public DrinkAdapter(Context mContext, List<Drink> mListDrink) {
        this.mContext = mContext;
        this.mListDrink = mListDrink;

        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public DrinkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = inflater.inflate(R.layout.raw_drink, parent, false);

        return new DrinkHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkHolder holder, int position) {

        holder.mTxtDrinkPrice.setText(new StringBuilder("$").append(mListDrink.get(position).getPrice()).toString());

        Picasso.get()
                .load(mListDrink.get(position).getLink())
                .placeholder(R.drawable.drink_shop_bg)
                .into(holder.mImgDrink);
        holder.mTxtDrinkName.setText(mListDrink.get(position).getName());

        holder.setIRecyclerItemClickListener(new IRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(mContext, "" + mListDrink.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.mImgAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogAddToCart(position);
            }
        });

         // Favorite System
        if (Common.mFavoriteRepository.isFavoriteExist(Integer.parseInt(mListDrink.get(position).getID())) == 1)
            holder.mImgAddToFavorite.setImageResource(R.drawable.ic_in_favorite);
        else
            holder.mImgAddToFavorite.setImageResource(R.drawable.ic_out_favorite);

        holder.mImgAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.mFavoriteRepository.isFavoriteExist(Integer.parseInt(mListDrink.get(position).getID())) != 1){
                    addOrRemoveFavorite(mListDrink.get(position), true);
                    holder.mImgAddToFavorite.setImageResource(R.drawable.ic_in_favorite);
                    Toast.makeText(mContext, "added", Toast.LENGTH_SHORT).show();
                }else{
                    addOrRemoveFavorite(mListDrink.get(position), false);
                    holder.mImgAddToFavorite.setImageResource(R.drawable.ic_out_favorite);
                    Toast.makeText(mContext, "Remove", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addOrRemoveFavorite(Drink drink, boolean isAdd) {

        Favorite favorite = new Favorite();
        favorite.setId(drink.getID());
        favorite.setName(drink.getName());
        favorite.setPrice(drink.getPrice());
        favorite.setImage(drink.getLink());
        favorite.setCategoryId(drink.getCategoryId());

        if (isAdd)
            Common.mFavoriteRepository.addToFavoritr(favorite);
        else
            Common.mFavoriteRepository.clearFavorite(favorite);
    }

    private void showDialogAddToCart(int position) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View itemView = inflater.inflate(R.layout.layout_dialog, null);

        ImageView mImageProduct = itemView.findViewById(R.id.img_cart_product);
        TextView mTxtProductName = itemView.findViewById(R.id.txt_cart_product_name);
        ElegantNumberButton quntityCount = itemView.findViewById(R.id.btn_cart_product_quantity_count);

        EditText mInputComment = itemView.findViewById(R.id.input_comment);

        RadioButton mRadioSizeM = itemView.findViewById(R.id.radio_size_m);
        RadioButton mRadioSizeL = itemView.findViewById(R.id.radio_size_l);

        mRadioSizeM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mSizeOfCup = 0;

            }
        });

        mRadioSizeL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mSizeOfCup = 1;

            }
        });

        RadioButton mRadioSugar100 = itemView.findViewById(R.id.radio_sugar_100);
        RadioButton mRadioSugar70 = itemView.findViewById(R.id.radio_sugar_70);
        RadioButton mRadioSugar50 = itemView.findViewById(R.id.radio_sugar_50);
        RadioButton mRadioSugar30 = itemView.findViewById(R.id.radio_sugar_30);
        RadioButton mRadioSugarFree = itemView.findViewById(R.id.radio_sugar_free);

        mRadioSugar100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mSugar = 100;

            }
        });

        mRadioSugar70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mSugar = 70;

            }
        });

        mRadioSugar50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mSugar = 50;

            }
        });

        mRadioSugar30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mSugar = 30;

            }
        });

        mRadioSugarFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mSugar = 0;

            }
        });

        RadioButton mRadioIce100 = itemView.findViewById(R.id.radio_ice_100);
        RadioButton mRadioIce70 = itemView.findViewById(R.id.radio_ice_70);
        RadioButton mRadioIce50 = itemView.findViewById(R.id.radio_ice_50);
        RadioButton mRadioIce30 = itemView.findViewById(R.id.radio_ice_30);
        RadioButton mRadioIceFree = itemView.findViewById(R.id.radio_ice_free);

        mRadioIce100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mIce = 100;

            }
        });

        mRadioIce70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mIce = 70;

            }
        });

        mRadioIce50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mIce = 50;

            }
        });

        mRadioIce30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mIce = 30;

            }
        });

        mRadioIceFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    Common.mIce = 0;

            }
        });

        RecyclerView mRecyclerTopping = itemView.findViewById(R.id.recycler_topping);
        mRecyclerTopping.setHasFixedSize(true);
        mRecyclerTopping.setLayoutManager(new LinearLayoutManager(mContext));

        Log.d("TOPPING", Common.mListTopping.toString());
        ToppingAdapter toppingAdapter = new ToppingAdapter(mContext, Common.mListTopping);
        mRecyclerTopping.setAdapter(toppingAdapter);

        Picasso.get()
                .load(mListDrink.get(position).getLink())
                .placeholder(R.drawable.banner_drink)
                .into(mImageProduct);
        mTxtProductName.setText(mListDrink.get(position).getName());

        dialogBuilder.setView(itemView);
        dialogBuilder.setNegativeButton("ADD TO CART", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (Common.mSizeOfCup == -1) {
                    Toast.makeText(mContext, "Please choose size of cup", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Common.mSugar == -1) {
                    Toast.makeText(mContext, "Please choose sugar", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Common.mIce == -1) {
                    Toast.makeText(mContext, "Please choose ice", Toast.LENGTH_SHORT).show();
                    return;
                }

                showConfirmDialog(position, quntityCount.getNumber());
                dialogInterface.dismiss();
            }
        });

        dialogBuilder.show();

    }

    private void showConfirmDialog(int position, String quntityCount) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View itemView = inflater.inflate(R.layout.layout_confirm_add_to_cart, null);

        ImageView mImageProduct = itemView.findViewById(R.id.img_product_confirm);
        TextView mTxtProductName = itemView.findViewById(R.id.txt_cart_product_name_confirm);
        TextView mTxtProductPrice = itemView.findViewById(R.id.txt_cart_product_price_confirm);
        TextView mTxtSugar = itemView.findViewById(R.id.txt_sugar_confirm);
        TextView mTxtIce = itemView.findViewById(R.id.txt_ice_confirm);
        TextView mTxtToppingExtras = itemView.findViewById(R.id.txt_topping_extras);

        // get Data
        Picasso.get()
                .load(mListDrink.get(position).getLink())
                .placeholder(R.drawable.banner_drink)
                .into(mImageProduct);

        mTxtProductName.setText(new StringBuilder(mListDrink.get(position).getName())
                .append(" x")
                .append(Common.mSizeOfCup == 0 ? " Size M" : " Size L")
                .append(quntityCount).toString());

        mTxtIce.setText(new StringBuilder("Ice: ").append(Common.mIce).append("%").toString());
        mTxtSugar.setText(new StringBuilder("Sugar: ").append(Common.mSugar).append("%").toString());

        double price = (Double.parseDouble(mListDrink.get(position).getPrice()) * Double.parseDouble(quntityCount) + Common.mToppingPrice);
        // Size L
        if (Common.mSizeOfCup == 1)
            price+=(3.0 * Double.parseDouble(quntityCount));

        StringBuilder builder = new StringBuilder("");
        for (String topExtras : Common.mListToppingAdded)
            builder.append(topExtras).append("\n");
        mTxtToppingExtras.setText(builder);

        double finalPrice = Math.round(price);
        mTxtProductPrice.setText(new StringBuilder("$").append(finalPrice));

        dialogBuilder.setNegativeButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Common.mListToppingAdded.clear();

                dialogInterface.dismiss();

                String topExtras = mTxtToppingExtras.getText().toString();

                try {
                    // Add to cart database
                    Cart cartItem = new Cart();
                    cartItem.setName(mListDrink.get(position).getName());
                    cartItem.setImage(mListDrink.get(position).getLink());
                    cartItem.setAmount(Integer.parseInt(quntityCount));
                    cartItem.setSugar(Common.mSugar);
                    cartItem.setIce(Common.mIce);
                    cartItem.setPrice(finalPrice);
                    cartItem.setSize(Common.mSizeOfCup);
                    cartItem.setTopExtras(topExtras);

                    Common.mCartRepository.addItemToCart(cartItem);
                    Log.d("CARTI_TEMS", new Gson().toJson(cartItem));
                    Toast.makeText(mContext, "Save item to cart", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e("CART_ERROR", e.getMessage());
                }
            }
        });

        dialogBuilder.setView(itemView);
        dialogBuilder.show();
    }

    @Override
    public int getItemCount() {
        return mListDrink.size();
    }

    static class DrinkHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_drink)
        ImageView mImgDrink;
        @BindView(R.id.txt_drink_name)
        TextView mTxtDrinkName;
        @BindView(R.id.txt_drink_price)
        TextView mTxtDrinkPrice;
        @BindView(R.id.img_add_to_cart)
        ImageView mImgAddToCart;
        @BindView(R.id.img_add_to_favorite)
        ImageView mImgAddToFavorite;


        IRecyclerItemClickListener IRecyclerItemClickListener;

        public DrinkHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void setIRecyclerItemClickListener(IRecyclerItemClickListener IRecyclerItemClickListener) {
            this.IRecyclerItemClickListener = IRecyclerItemClickListener;
        }

        @Override
        public void onClick(View view) {
            IRecyclerItemClickListener.onItemClick(view, getAdapterPosition());
        }


    }


}

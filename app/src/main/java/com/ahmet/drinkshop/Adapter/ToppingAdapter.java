package com.ahmet.drinkshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Model.Drink;
import com.ahmet.drinkshop.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ToppingAdapter extends RecyclerView.Adapter<ToppingAdapter.ToppingHolder> {

    private Context mContext;
    private List<Drink> mListTopping;
    private LayoutInflater inflater;

    public ToppingAdapter(Context mContext, List<Drink> mListTopping) {
        this.mContext = mContext;
        this.mListTopping = mListTopping;

        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ToppingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = inflater.inflate(R.layout.raw_topping, parent, false);
        return new ToppingHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ToppingHolder holder, int position) {

        holder.mCheckTopping.setText(mListTopping.get(position).getName());
        holder.mCheckTopping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked){

                    Common.mListToppingAdded.add(compoundButton.getText().toString());
                    Common.mToppingPrice += Double.parseDouble(mListTopping.get(position).getPrice());

                }else {

                    Common.mListToppingAdded.remove(compoundButton.getText().toString());
                    Common.mToppingPrice -= Double.parseDouble(mListTopping.get(position).getPrice());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mListTopping.size();
    }

    static class ToppingHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.check_topping)
        CheckBox mCheckTopping;

        public ToppingHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

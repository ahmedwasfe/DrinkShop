package com.ahmet.drinkshop.SubActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.LinearLayout;

import com.ahmet.drinkshop.Adapter.DrinkAdapter;
import com.ahmet.drinkshop.Common.Common;
import com.ahmet.drinkshop.Model.Drink;
import com.ahmet.drinkshop.R;
import com.ahmet.drinkshop.Retrofit.IDrinkShopAPI;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.search_bar_drink)
    MaterialSearchBar mSearchBarDrink;
    @BindView(R.id.recycler_search_drink)
    RecyclerView mRecyclerSeachDrink;

    private List<String> mListSuggestion;
    private List<Drink> mListDrink;

    private IDrinkShopAPI mService;
    private CompositeDisposable mDisposable;

    private DrinkAdapter mSearchAdapter, mDrinkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        init();

        loadAllDrinks();

        mSearchBarDrink.setCardViewElevation(10);
        mSearchBarDrink.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> mSuggestion = new ArrayList<>();
                for (String textSearch : mListSuggestion){
                    if (textSearch.toLowerCase().contains(mSearchBarDrink.getText().toLowerCase()))
                        mSuggestion.add(textSearch);
                }
                mSearchBarDrink.setLastSuggestions(mSuggestion);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchBarDrink.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // Restore full list of drink
                if (!enabled)
                    mRecyclerSeachDrink.setAdapter(mDrinkAdapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {

        List<Drink> drinkResult = new ArrayList<>();
        for (Drink drink : mListDrink)
            if (drink.getName().contains(text))
                drinkResult.add(drink);
        mSearchAdapter = new DrinkAdapter(this, drinkResult);
        mRecyclerSeachDrink.setAdapter(mSearchAdapter);
    }

    private void loadAllDrinks() {

        mDisposable.add(mService.searchingForDrink()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<List<Drink>>() {
                            @Override
                            public void accept(List<Drink> drinks) throws Exception {
                                displayAllDrinks(drinks);
                                buildSuggestion(drinks);
                            }
                        }));
    }

    private void buildSuggestion(List<Drink> drinks) {

        for (Drink drink : drinks)
            mListSuggestion.add(drink.getName());
        mSearchBarDrink.setLastSuggestions(mListSuggestion);
    }

    private void displayAllDrinks(List<Drink> drinks) {

        mListDrink = drinks;
        mDrinkAdapter = new DrinkAdapter(this, drinks);
        mRecyclerSeachDrink.setAdapter(mDrinkAdapter);
    }

    private void init() {

        mListSuggestion = new ArrayList<>();
        mService = Common.getAPI();
        mDisposable = new CompositeDisposable();

        mSearchBarDrink.setHint("Type a search...");

        mRecyclerSeachDrink.setHasFixedSize(true);
        mRecyclerSeachDrink.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));
    }

    @Override
    protected void onStop() {
        mDisposable.clear();
        super.onStop();
    }

}

package org.miage.placesearcher;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iammert.library.ui.multisearchviewlib.MultiSearchView;
import com.squareup.otto.Subscribe;

import org.miage.placesearcher.event.EventBusManager;
import org.miage.placesearcher.event.SearchResultEvent;
import org.miage.placesearcher.ui.PlaceAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private String textSelected = "";

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private PlaceAdapter mPlaceAdapter;

    @BindView(R.id.activity_main_loader)
    ProgressBar mProgressBar;

    //Bind de la vue multiSearchView
    @BindView(R.id.multiSearchView)
    MultiSearchView multiSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Binding ButterKnife annotations now that content view has been set
        ButterKnife.bind(this);

        // Instanciate a PlaceAdpater with empty content
        mPlaceAdapter = new PlaceAdapter(this, new ArrayList<>());
        mRecyclerView.setAdapter(mPlaceAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /**
         * Fonction permettant l'implémentation du framework MultiSearchView
         */
        multiSearchView.setSearchViewListener(new MultiSearchView.MultiSearchViewListener() {

            /**
             * Permet de définir ce qu'il se passe lorsque l'on clique sur l'une des recherches effectuées
             * @param index le numéro de la recherche
             * @param string le contenu de la recherche
             */
            @Override
            public void onItemSelected(int index, CharSequence string) {

                mProgressBar.setVisibility(View.VISIBLE);

                textSelected = string.toString();

                // Launch a search through the PlaceSearchService
                PlaceSearchService.INSTANCE.searchPlacesFromAddress(string.toString());
            }

            /**
             * Permet de changer le contenu d'une recherche existante
             * @param index le numéro de la recherche
             * @param string le contenu de la recherche
             */
            @Override
            public void onTextChanged(int index, CharSequence string) {
            }

            /**
             * Permet de définir ce qu'il se passe lors d'une recherche
             * @param index le numéro de la recherche
             * @param string le contenu de la recherche
             */
            @Override
            public void onSearchComplete(int index, CharSequence string) {

                mProgressBar.setVisibility(View.VISIBLE);

                textSelected = string.toString();

                // Launch a search through the PlaceSearchService
                PlaceSearchService.INSTANCE.searchPlacesFromAddress(string.toString());
            }

            /**
             * Permet de définir ce qu'il se passe lorsqu'un recherche est supprimée
             * @param index le numéro de la recherche
             */
            @Override
            public void onSearchItemRemoved(int index) {
                textSelected = "";
            }
        });
    }

    @Override
    protected void onResume() {
        // Do NOT forget to call super.onResume()
        super.onResume();

        // Register to Event bus : now each time an event is posted, the activity will receive it if it is @Subscribed to this event
        EventBusManager.BUS.register(this);

        // Refresh search
        PlaceSearchService.INSTANCE.searchPlacesFromAddress(textSelected);
    }

    @Override
    protected void onPause() {
        // Unregister from Event bus : if event are posted now, the activity will not receive it
        EventBusManager.BUS.unregister(this);

        super.onPause();
    }

    @Subscribe
    public void searchResult(final SearchResultEvent event) {
        // Here someone has posted a SearchResultEvent
        runOnUiThread (() -> {
            // Step 1: Update adapter's model
            mPlaceAdapter.setPlaces(event.getPlaces());
            mPlaceAdapter.notifyDataSetChanged();

            // Step 2: hide loader
            mProgressBar.setVisibility(View.GONE);
        });

    }

    @OnClick(R.id.activity_main_switch_button)
    public void clickedOnSwitchToMap() {
        Intent switchToMapIntent = new Intent(this, MapActivity.class);
        switchToMapIntent.putExtra("currentSearch", textSelected);
        startActivity(switchToMapIntent);
    }
}

package org.miage.placesearcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexmorel on 04/01/2018.
 */

public class PlaceDetailActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 42;

    @BindView(R.id.activity_detail_place_street)
    TextView mPlaceStreet;

    @BindView(R.id.activity_detail_place_pic)
    ImageView mPlacePic;

    private String mPlaceStreetValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        mPlaceStreetValue = getIntent().getStringExtra("placeStreet");
        mPlaceStreet.setText(mPlaceStreetValue);
    }

    @OnClick(R.id.activity_detail_place_street)
    public void clickedOnPlaceStreet() {
        finish();
    }

    @OnClick(R.id.activity_detail_button_search)
    public void clickedOnGoogleSearch() {
        // Open browser using an Intent
        Uri url = Uri.parse("https://www.google.fr/search?q=" + mPlaceStreetValue);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, url);
        startActivity(launchBrowser);
    }

    @OnClick(R.id.activity_detail_button_share)
    public void clickedOnShare() {
        // Open share picker using an Intent
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "J'ai découvert " + mPlaceStreetValue + " grâce à Place Searcher !");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @OnClick(R.id.activity_detail_button_galery)
    public void clickedOnPickFromGalery() {
        // Open galery picker using an Intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        // If we get a result from the SELECT_PHOTO query
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    // Get the selected image as bitmap
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);

                        // Set the bitmap to the picture
                        mPlacePic.setImageBitmap(selectedImageBitmap);
                    } catch (FileNotFoundException e) {
                        // Silent catch : image will not be displayed
                    }

                }
        }
    }
}

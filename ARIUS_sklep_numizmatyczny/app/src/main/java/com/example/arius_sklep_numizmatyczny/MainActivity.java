package com.example.arius_sklep_numizmatyczny;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

/**
 * Główna aktywność
 * Ma przyciski do logowania, oglądania kolekcji i sklepu
 */
public class MainActivity extends Activity {

    /**
     * Tag aktywności. Służy do logowania wiadomości
     */
    private static final String ACTIVITY_TAG = "ARIUS_projekt";

    public static int MY_PERMISSIONS_REQUEST_INTERNET = 1;
    private Resources res;
    private Button loginButton;
    private Button collectionButton;
    private Button storeButton;
    private boolean isLoggedIn = false;

    /**
     * Token JWT
     */
    private String accessToken = "";

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_COLLECTION = 2;
    private static final int REQUEST_CODE_STORE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(ACTIVITY_TAG, "onCreate invoked");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        requestPermission();

        res = getResources();

        loginButton      = findViewById(R.id.login_register_button);
        collectionButton = findViewById(R.id.collection_button);
        storeButton      = findViewById(R.id.store_button);

        refreshDisplay();
    }

    @Override
    protected void onStart(){
        Log.d(ACTIVITY_TAG, "onStart invoked");
        super.onStart();
    }

    @Override
    protected void onResume(){
        Log.d(ACTIVITY_TAG, "onResume invoked");

        refreshDisplay();

        super.onResume();
    }

    @Override
    protected void onRestart(){
        Log.d(ACTIVITY_TAG, "onRestart invoked");

        super.onRestart();
    }

    /**
     * Funkcja uzyskująca pozwolenie na dostęp do internetu
     */
    public void requestPermission(){
        Log.d(ACTIVITY_TAG, "requestPermission invoked");

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
        }
    }

    /**
     * Funkcja pomocnicza doświeżająca wygląd atkywności
     */
    public void refreshDisplay() {
        Log.d(ACTIVITY_TAG, "refreshDisplay invoked");

        // Jeśli użytkownik nie jest zalogowany
        if(!isLoggedIn){
            loginButton.setText("Zaloguj się");
            loginButton.setBackgroundColor(res.getColor(R.color.log_in_btn_bg));

            // Wyłącz przycik sklepu
            storeButton.setEnabled(false);
            storeButton.setBackgroundColor(res.getColor(R.color.store_btn_dis_bg));
        } else {
            // Jeśli użytkownik jest zalogowany
            loginButton.setText("Wyloguj się");
            loginButton.setBackgroundColor(res.getColor(R.color.log_out_btn_bg));

            // Włącz przycisk sklepu
            storeButton.setEnabled(true);
            storeButton.setBackgroundColor(res.getColor(R.color.store_btn_bg));
        }
    }

    /**
     * Przycisk do logowania
     * @param view
     */
    public void btnLogin(View view){
        Log.d(ACTIVITY_TAG, "btnLogin invoked");

        Toast.makeText(this, "Login button pressed!", Toast.LENGTH_SHORT).show();

        // Jeśli użytkownik jest zalogowany, wyloguj go
        if(isLoggedIn){
            isLoggedIn = false;
            accessToken = "";
            refreshDisplay();
            return;
        }

        Intent intent = new Intent(this, LoginActivity.class);

        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    /**
     * Przycisk do kolekcji
     * @param view
     */
    public void btnCollection(View view) {
        Log.d(ACTIVITY_TAG, "btnCollection invoked");
        Toast.makeText(this, "Collection button pressed!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, CollectionActivity.class);
        intent.putExtra("accessToken", accessToken);

        startActivityForResult(intent, REQUEST_CODE_COLLECTION);
    }

    /**
     * Przycisk do sklepu
     * @param view
     */
    public void btnStore(View view) {
        Log.d(ACTIVITY_TAG, "btnStore invoked");
        Toast.makeText(this, "Store button pressed!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("accessToken", accessToken);

        startActivityForResult(intent, REQUEST_CODE_STORE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(ACTIVITY_TAG, "onActivityResult invoked");

        // Jeśli kod zwrotny zgadza się oraz aktywność zakończyła się pomyślnie
        if(requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK){
            Toast.makeText(this, "Returned from login screen!", Toast.LENGTH_SHORT).show();
            // Jeśli dane zwrócone przez drugą aktywność zawierają odpowiednie pola
            if(data.hasExtra("isLoggedIn") && data.hasExtra("accessToken")){
                // Odczytaj dane zwrócone przez drugą aktywność
                isLoggedIn = data.getExtras().getBoolean("isLoggedIn");
                accessToken = data.getExtras().getString("accessToken");

                Log.d(ACTIVITY_TAG, "isLoggedIn: " + isLoggedIn);
                Log.d(ACTIVITY_TAG, "accessToken: " + accessToken);

                refreshDisplay();
            }
        }
    }
}
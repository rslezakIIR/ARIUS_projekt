package com.example.arius_sklep_numizmatyczny;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Aktywność przedstawiająca szczegóły wybranej monety
 * Pozwala na dodanie monety do koszyka jeśli aktywność została wywołana ze sklepu
 */
public class DetailsActivity extends Activity {

    /**
     * Tag aktywności. Służy do logowania wiadomości
     */
    private static final String ACTIVITY_TAG = "ARIUS_projekt_details";

    /**
     * Token JWT
     */
    protected String accessToken = "";

    /**
     * Identyfikator monety
     */
    protected int id;

    /**
     * Obiekt monety
     */
    protected Coin coin;

    private ImageView coinImage;
    private TextView nameText;
    private TextView yearText;
    private TextView descriptionText;
    private TextView priceText;
    private TextView countryText;
    private TextView quantityText;
    private TextView scoreText;
    private TextView scoreCountText;
    private Button addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(ACTIVITY_TAG, "onCreate invoked");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        // Otrzymaj dane z intencji
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        accessToken = intent.getStringExtra("accessToken");

        // Sprawdź, czy jest połączenie z internetem
        if(isOnline()){
            // Wywołaj żądanie o szczegóły monety i poczekaj, aż odpowiedź będzie otrzymana
            GetCoinDetails task = new GetCoinDetails();
            try {
                task.execute().get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Jeśli nie ma połączenia z internetem, wywołaj toast z informacją o tym
            Toast.makeText(this, "Nie ma połączenia z internetem.", Toast.LENGTH_SHORT).show();
            return;
        }

        coinImage = findViewById(R.id.coin_image);
        nameText = findViewById(R.id.name_text);
        yearText = findViewById(R.id.year_text);
        descriptionText = findViewById(R.id.description_text);
        priceText = findViewById(R.id.price_value_text);
        countryText = findViewById(R.id.country_value_text);
        quantityText = findViewById(R.id.quantity_value_text);
        scoreText = findViewById(R.id.score_value_text);
        scoreCountText = findViewById(R.id.score_count_value_text);
        addToCartButton = findViewById(R.id.add_to_cart_button);

        // Ustaw odpowiedni obrazek na podstawie nazwy pliku
        switch(coin.imageURL){
            case "us.jpg":
                coinImage.setImageResource(R.drawable.us);
                break;
            case "british_queen.jpg":
                coinImage.setImageResource(R.drawable.british_queen);
                break;
            case "australian_kangaroo.jpg":
                coinImage.setImageResource(R.drawable.australian_kangaroo);
                break;
            case "canadian_maple_leaf.jpg":
                coinImage.setImageResource(R.drawable.canadian_maple_leaf);
                break;
            case "chinese_panda.jpg":
                coinImage.setImageResource(R.drawable.chinese_panda);
                break;
            case "mexican_libertad.jpg":
                coinImage.setImageResource(R.drawable.mexican_libertad);
                break;
            default:
                coinImage.setImageResource(R.drawable.missing_texture);
                break;
        }
        nameText.setText(coin.name);
        yearText.setText(String.valueOf(coin.yearIssued));
        descriptionText.setText(coin.description);
        priceText.setText(String.format("%.2f", coin.price) + " zł");
        countryText.setText(coin.country);
        quantityText.setText(String.valueOf(coin.quantityAvailable));
        scoreText.setText(String.format("%.1f", coin.averageRating));
        scoreCountText.setText(String.valueOf(coin.reviewCount));

        // Jeśli aktywność została wywołana z ekranu kolekcji, to ustaw przycisk dodania do koszyka na niewidzialny
        if(accessToken == null){
            addToCartButton.setVisibility(View.INVISIBLE);
        }
    }

    public void finish() {
        Log.d(ACTIVITY_TAG, "finish invoked");

        Intent intent = new Intent();

        setResult(RESULT_OK, intent);

        super.finish();
    }

    /**
     * Funkcja pomocincza zwracająca informację o podłączeniu do internetu
     * @return Czy urządzenie jest podłączone do internetu
     */
    public boolean isOnline(){
        Log.d(ACTIVITY_TAG, "isOnline invoked");

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Przycisk dodania monety do koszyka
     * @param view Widok
     */
    public void btnAddToCart(View view) {
        Log.d(ACTIVITY_TAG, "btnAddToCart invoked");

        // Sprawdź, czy jest połączenie z internetem
        if(isOnline()){
            // Wywołaj żądanie o dodanie monety do koszyka
            AddToCart task = new AddToCart();
            task.execute();
        } else {
            // Jeśli nie ma połączenia z internetem, wywołaj toast z informacją o tym
            Toast.makeText(this, "Nie ma połączenia z internetem.", Toast.LENGTH_SHORT).show();
            return;
        }

        //finish();
    }

    /**
     * Zadanie asynchroniczne wykonujące żądanie do backendu o szczegóły monety
     */
    public class GetCoinDetails extends AsyncTask<String, String, Long> {
        /**
         * Adres IP backendu
         */
        private static final String ipAddr   = "192.168.0.164";

        /**
         * Port backendu
         */
        private static final String port     = "5000";

        /**
         * Endpoint backendu
         */
        private final String endpoint = "api/coins/" + DetailsActivity.this.id;

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected void onPostExecute(Long result){
            if(result == 0){
                Log.d(ACTIVITY_TAG, "GetCoinDetails: success");
                // StoreActivity.this.getCoinsSuccess = true;
            } else {
                Log.d(ACTIVITY_TAG, "GetCoinDetails: failure");
                // StoreActivity.this.getCoinsSuccess = false;
            }
        }

        @Override
        protected Long doInBackground(String... params){
            HttpURLConnection connection = null;
            try {
                URL dataUrl = new URL("http://" + ipAddr + ":" + port + "/" + endpoint);
                connection = (HttpURLConnection) dataUrl.openConnection();
                // Użyj metody GET
                connection.setRequestMethod("GET");

                connection.connect();

                int status = connection.getResponseCode();
                // Jeśli żądanie zwróciło oczekiwane dane
                if(status == 200){
                    // Odczytaj zwrócone dane
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    String responseString;
                    StringBuilder sb = new StringBuilder();

                    while((responseString = reader.readLine()) != null){
                        sb = sb.append(responseString);
                    }

                    String returnStr = sb.toString();
                    // Zinterpretuj zwrócone dane jako JSON
                    JSONObject data = new JSONObject(returnStr);

                    // Odczytaj dane ze zwróconego JSONa
                    int id = data.getInt("id");
                    String name = data.getString("name");
                    String desc = data.getString("description");
                    float price = (float) data.getDouble("price");
                    int year = data.getInt("year_issued");
                    String country = data.getString("country");
                    int quantity = data.getInt("quantity_available");
                    String imgUrl = data.getString("image_url").replace("/assets/", "");
                    float avgRating = (float) data.getDouble("average_rating");
                    int reviewCount = data.getInt("reviews_count");

                    // Stwórz obiekt monety i przypisz go do zmiennej w aktywności
                    DetailsActivity.this.coin = new Coin(id, name, desc, price, year, country,
                            quantity, imgUrl, avgRating, reviewCount);

                    Log.d(ACTIVITY_TAG, returnStr);

                    return (0l);
                } else {
                    // W razie błędu printuj do logów odpowiedź
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    String responseString;
                    StringBuilder sb = new StringBuilder();

                    while((responseString = reader.readLine()) != null){
                        sb = sb.append(responseString);
                    }

                    String returnStr = sb.toString();
                    JSONObject data = new JSONObject(returnStr);

                    Log.d(ACTIVITY_TAG, returnStr);
                    return (1l);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } finally {
                connection.disconnect();
            }
        }
    }

    /**
     * Przycisk powrotu
     * @param view Widok
     */
    public void btnReturn(View view) {
        Log.d(ACTIVITY_TAG, "btnReturn invoked");

        finish();
    }

    /**
     * Zadanie asynchroniczne wykonujące żądanie do backendu o dodanie monety do koszyka
     */
    public class AddToCart extends AsyncTask<String, String, Long> {
        /**
         * Adres IP backendu
         */
        private static final String ipAddr   = "192.168.0.164";

        /**
         * Port backendu
         */
        private static final String port     = "5000";

        /**
         * Endpoint backendu
         */
        private static final String endpoint = "api/orders/cart/add";

        @Override
        protected void onPreExecute(){

        }

        /**
         * Po zakończeniu zadania pokaż toast o sukcesie wykonania żądania
         * @param result Wynik wykonania zadania
         */
        @Override
        protected void onPostExecute(Long result){
            if(result == 0){
                Toast.makeText(DetailsActivity.this, "Dodano produkt do koszyka!", Toast.LENGTH_SHORT).show();
                DetailsActivity.this.finish();
            } else {
                Toast.makeText(DetailsActivity.this, "Wystąpił błąd", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Long doInBackground(String... params){
            HttpURLConnection connection = null;
            try {
                URL dataUrl = new URL("http://" + ipAddr + ":" + port + "/" + endpoint);
                Log.d(ACTIVITY_TAG, "http://" + ipAddr + ":" + port + "/" + endpoint);
                connection = (HttpURLConnection) dataUrl.openConnection();
                // Użyj metody POST
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Dane przekazane w żadaniu
                byte[] out = ("{\"coin_id\":\""
                            + DetailsActivity.this.id
                        + "\",\"quantity\":\""
                            + "1"
                        + "\"}").getBytes();
                int length = out.length;
                connection.setFixedLengthStreamingMode(length);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                // Nagłówek autoryzacji z tokenem JWT
                connection.setRequestProperty("Authorization", "Bearer " + DetailsActivity.this.accessToken);

                connection.connect();
                try(OutputStream os = connection.getOutputStream()) {
                    os.write(out);
                }

                int status = connection.getResponseCode();
                Log.d(ACTIVITY_TAG, String.valueOf(status));
                // Jeśli żądanie zwróciło oczekiwane dane
                if(status == 200){

                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    String responseString;
                    StringBuilder sb = new StringBuilder();

                    while((responseString = reader.readLine()) != null){
                        sb = sb.append(responseString);
                    }

                    String returnStr = sb.toString();

                    Log.d(ACTIVITY_TAG, returnStr);

                    return (0l);
                } else {
                    // W razie błędu printuj do logów odpowiedź
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    String responseString;
                    StringBuilder sb = new StringBuilder();

                    while((responseString = reader.readLine()) != null){
                        sb = sb.append(responseString);
                    }

                    String returnStr = sb.toString();

                    Log.d(ACTIVITY_TAG, returnStr);
                    return (1l);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                connection.disconnect();
            }
        }
    }
}
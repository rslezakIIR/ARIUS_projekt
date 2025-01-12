package com.example.arius_sklep_numizmatyczny;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Aktywność przedstawiająca kolekcję monet
 * Dotknięcie monety wywołuje aktywność pokazującą szczegóły monety
 */
public class CollectionActivity extends Activity implements AdapterView.OnItemClickListener {

    /**
     * Tag aktywności. Służy do logowania wiadomości
     */
    private static final String ACTIVITY_TAG = "ARIUS_projekt_collection";

    /**
     * ListView w layoucie aktywności
     */
    private ListView coinList;

    /**
     * Lista nazw monet
     */
    protected String[] coinNames;

    /**
     * Lista obiektów monet
     */
    protected Coin[] coins;

    /**
     * Zmienna przechowująca informację o sukcesie otrzymania informacji o monetach
     */
    protected boolean getCoinsSuccess;

    /**
     * Kod zwrotny przekazywany do drugiej aktywności
     */
    private static final int REQUEST_CODE_DETAILS = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(ACTIVITY_TAG, "onCreate invoked");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collection);

        coinList = findViewById(R.id.coin_list);

        // Sprawdź, czy jest połączenie z internetem
        if(isOnline()){
            // Wywołaj żądanie o listę monet i poczekaj, aż odpowiedź będzie otrzymana
            GetCoins task = new GetCoins();
            try {
                task.execute().get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Jeśli nie ma połączenia z internetem, wywołaj toast z informacją o tym
            getCoinsSuccess = false;
            Toast.makeText(this, "Nie ma połączenia z internetem.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Stwórz nowy adapter zadań i przypisz go do listy w layoucie
        CoinAdapter adapter = new CoinAdapter(this, coins, coinNames);
        coinList.setAdapter(adapter);

        // Nasłuchuj wydarzeń wciśnięcia elementów listy
        coinList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(ACTIVITY_TAG, "onItemClick invoked");

        Intent intent = new Intent(this, DetailsActivity.class);
        // Przekaż identyfikator monety do drugiej aktywności
        intent.putExtra("id", coins[i].id);

        startActivityForResult(intent, REQUEST_CODE_DETAILS);
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
     * Przycisk powrotu
     * @param view Widok
     */
    public void btnReturn(View view) {
        Log.d(ACTIVITY_TAG, "btnReturn invoked");

        finish();
    }

    /**
     * Zadanie asynchroniczne wykonujące żądanie do backendu o listę monet
     */
    public class GetCoins extends AsyncTask<String, String, Long> {
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
        private static final String endpoint = "api/coins";

        @Override
        protected void onPreExecute(){

        }

        /**
         * Po zakończeniu zadania przekaż informację o sukcesie
         * @param result Wynik wykonania zadania
         */
        @Override
        protected void onPostExecute(Long result){
            if(result == 0){
                Log.d(ACTIVITY_TAG, "GetCoins: success");
                CollectionActivity.this.getCoinsSuccess = true;
            } else {
                Log.d(ACTIVITY_TAG, "GetCoins: failure");
                CollectionActivity.this.getCoinsSuccess = false;
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

                    // Wyciągnij listę monet ze zwróconego JSONa
                    JSONArray coinArray = data.optJSONArray("coins");

                    // Ustaw długości tablic przechowujących informacje o monetach
                    coins = new Coin[coinArray.length()];
                    coinNames = new String[coinArray.length()];

                    // Dodaj informacje o monetach do tablic
                    for(int i = 0, size = coinArray.length(); i < size; ++i){
                        JSONObject coin = coinArray.getJSONObject(i);

                        int id = coin.getInt("id");
                        String name = coin.getString("name");
                        String desc = coin.getString("description");
                        float price = (float) coin.getDouble("price");
                        int year = coin.getInt("year_issued");
                        String country = coin.getString("country");
                        int quantity = coin.getInt("quantity_available");
                        String imgUrl = coin.getString("image_url").replace("/assets/", "");

                        coins[i] = new Coin(id, name, desc, price, year, country, quantity, imgUrl);
                    }

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
}
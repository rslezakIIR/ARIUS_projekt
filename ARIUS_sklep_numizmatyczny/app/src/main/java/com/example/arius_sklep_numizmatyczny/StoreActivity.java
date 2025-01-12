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

public class StoreActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String ACTIVITY_TAG = "ARIUS_projekt_store";
    protected static final int PER_PAGE = 100;

    private String accessToken;

    private ListView coinList;
    protected String[] coinNames;
    protected Coin[] coins;
    protected boolean getCoinsSuccess;

    private static final int REQUEST_CODE_DETAILS = 31;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(ACTIVITY_TAG, "onCreate invoked");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_store);

        Intent intent = getIntent();
        accessToken = intent.getStringExtra("accessToken");

        coinList = findViewById(R.id.coin_list);

        if(isOnline()){
            GetCoins task = new GetCoins();
            try {
                task.execute().get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            getCoinsSuccess = false;
            Toast.makeText(this, "Nie ma połączenia z internetem.", Toast.LENGTH_SHORT).show();
            return;
        }

        CoinAdapter adapter = new CoinAdapter(this, coins, coinNames);
        coinList.setAdapter(adapter);

        coinList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(ACTIVITY_TAG, "onItemClick invoked");

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("id", coins[i].id);
        intent.putExtra("accessToken", accessToken);

        startActivityForResult(intent, REQUEST_CODE_DETAILS);
    }

    public boolean isOnline(){
        Log.d(ACTIVITY_TAG, "isOnline invoked");

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void btnCheckout(View view) {
        Log.d(ACTIVITY_TAG, "btnCheckout invoked");

        finish();
    }
    public void btnReturn(View view) {
        Log.d(ACTIVITY_TAG, "btnReturn invoked");

        Toast.makeText(this, "Return button pressed!", Toast.LENGTH_SHORT).show();

        finish();
    }

    public class GetCoins extends AsyncTask<String, String, Long> {
        private static final String ipAddr   = "192.168.0.164";
        private static final String port     = "5000";
        private static final String endpoint = "api/coins";

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected void onPostExecute(Long result){
            if(result == 0){
                Log.d(ACTIVITY_TAG, "GetCoins: success");
                StoreActivity.this.getCoinsSuccess = true;
            } else {
                Log.d(ACTIVITY_TAG, "GetCoins: failure");
                StoreActivity.this.getCoinsSuccess = false;
            }
        }

        @Override
        protected Long doInBackground(String... params){
            HttpURLConnection connection = null;
            try {
                URL dataUrl = new URL("http://" + ipAddr + ":" + port + "/" + endpoint);
                connection = (HttpURLConnection) dataUrl.openConnection();
                connection.setRequestMethod("GET");

                connection.connect();

                int status = connection.getResponseCode();
                if(status == 200){

                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    String responseString;
                    StringBuilder sb = new StringBuilder();

                    while((responseString = reader.readLine()) != null){
                        sb = sb.append(responseString);
                    }

                    String returnStr = sb.toString();
                    JSONObject data = new JSONObject(returnStr);

                    JSONArray coinArray = data.optJSONArray("coins");

                    coins = new Coin[coinArray.length()];
                    coinNames = new String[coinArray.length()];

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
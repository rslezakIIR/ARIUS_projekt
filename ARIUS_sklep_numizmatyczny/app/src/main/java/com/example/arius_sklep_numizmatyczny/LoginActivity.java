package com.example.arius_sklep_numizmatyczny;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

/**
 * Aktywność pozwalająca na zalogowanie się
 * Ma przycisk wywołujący aktywność do rejestracji użytkownika
 */
public class LoginActivity extends Activity {

    /**
     * Tag aktywności. Służy do logowania wiadomości
     */
    private static final String ACTIVITY_TAG = "ARIUS_projekt_login";

    protected TextView loginTextfield;
    protected TextView passwordTextfield;

    /**
     * Token JWT, który będzie zwrócony do głównej aktywności
     */
    protected String accessToken = "";
    protected int userID;
    protected String username;
    private boolean isLoggedIn = false;

    /**
     * Kod zwrotny przekazywany do drugiej aktywności
     */
    private static final int REQUEST_CODE_REGISTER = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(ACTIVITY_TAG, "onCreate invoked");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        loginTextfield    = findViewById(R.id.editText_login);
        passwordTextfield = findViewById(R.id.editText_password);
    }

    @Override
    protected void onStart(){
        Log.d(ACTIVITY_TAG, "onStart invoked");
        super.onStart();
    }

    @Override
    protected void onResume(){
        Log.d(ACTIVITY_TAG, "onResume invoked");
        super.onResume();
    }

    @Override
    protected void onRestart(){
        Log.d(ACTIVITY_TAG, "onRestart invoked");
        super.onRestart();
    }

    public void finish() {
        Log.d(ACTIVITY_TAG, "finish invoked");

        Intent intent = new Intent();

        // Przekaż głównej aktywności stan zalogowania i token
        intent.putExtra("isLoggedIn", isLoggedIn);
        intent.putExtra("accessToken", accessToken);

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
     * Przycisk do logowania
     * @param view Widok
     */
    public void btnLogin(View view) {
        Log.d(ACTIVITY_TAG, "btnLogin invoked");

        // Sprawdź, czy jest połączenie z internetem
        if(isOnline()){
            // Wywołaj żądanie o zalogowanie się
            LogIn task = new LogIn();
            task.execute();
        } else {
            // Jeśli nie ma połączenia z internetem, wywołaj toast z informacją o tym
            Toast.makeText(this, "Nie ma połączenia z internetem.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * Przycisk do rejestracji
     * @param view
     */
    public void btnRegister(View view) {
        Log.d(ACTIVITY_TAG, "btnRegister invoked");

        Intent intent = new Intent(this, RegisterActivity.class);

        startActivityForResult(intent, REQUEST_CODE_REGISTER);
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
     * Zadanie asynchroniczne wykonujące żądanie do backendu o zalogowanie się
     */
    public class LogIn extends AsyncTask<String, String, Long> {
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
        private static final String endpoint = "api/auth/login";

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected void onPostExecute(Long result){
            if(result == 0){
                Log.d(ACTIVITY_TAG, "User ID: " + userID);
                Log.d(ACTIVITY_TAG, "Username: " + username);
                Log.d(ACTIVITY_TAG, "Token: " + accessToken);
                LoginActivity.this.finish();
            } else {
                Log.d(ACTIVITY_TAG, "Failure to authenticate");
            }
        }

        @Override
        protected Long doInBackground(String... params){
            HttpURLConnection connection = null;
            try {
                URL dataUrl = new URL("http://" + ipAddr + ":" + port + "/" + endpoint);
                connection = (HttpURLConnection) dataUrl.openConnection();
                // Użyj metody POST
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Dane przekazane w żądaniu
                byte[] out = ("{\"email\":\""
                        + LoginActivity.this.loginTextfield.getText().toString()
                        + "\",\"password\":\""
                        + LoginActivity.this.passwordTextfield.getText().toString()
                        + "\"}").getBytes();
                int length = out.length;
                connection.setFixedLengthStreamingMode(length);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                connection.connect();
                try(OutputStream os = connection.getOutputStream()) {
                    os.write(out);
                }

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

                    Log.d(ACTIVITY_TAG, returnStr);

                    // Odczytaj zwrócone informacje
                    LoginActivity.this.accessToken = data.optString("access_token");
                    LoginActivity.this.userID = data.optInt("user_id", -1);
                    LoginActivity.this.username = data.optString("username");
                    LoginActivity.this.isLoggedIn = true;

                    return (0l);
                } else {
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

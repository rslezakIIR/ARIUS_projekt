package com.example.arius_sklep_numizmatyczny;

import android.app.Activity;
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

public class RegisterActivity extends Activity {

    private static final String ACTIVITY_TAG = "ARIUS_projekt_register";

    protected TextView emailTextfield;
    protected TextView loginTextfield;
    protected TextView passwordTextfield;
    protected TextView repeatPasswordTextfield;

    protected boolean emailTaken = false;
    protected boolean usernameTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(ACTIVITY_TAG, "onCreate invoked");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        emailTextfield          = findViewById(R.id.editText_email);
        loginTextfield          = findViewById(R.id.editText_login);
        passwordTextfield       = findViewById(R.id.editText_password);
        repeatPasswordTextfield = findViewById(R.id.editText_password_repeat);
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

        setResult(RESULT_OK);

        super.finish();
    }

    public boolean isOnline(){
        Log.d(ACTIVITY_TAG, "isOnline invoked");

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    public void btnRegister(View view) {
        Log.d(ACTIVITY_TAG, "btnRegister invoked");

        if(isOnline()){
            Log.d(ACTIVITY_TAG, passwordTextfield.getText().toString());
            Log.d(ACTIVITY_TAG, repeatPasswordTextfield.getText().toString());
            if(!passwordTextfield.getText().toString().equals(repeatPasswordTextfield.getText().toString())){
                Toast.makeText(this, "Hasła się nie zgadzają", Toast.LENGTH_SHORT).show();
                return;
            }

            Register task = new Register();
            task.execute();
        } else {
            Toast.makeText(this, "Nie ma połączenia z internetem.", Toast.LENGTH_SHORT).show();
            return;
        }

        // finish();
    }

    public void btnReturn(View view) {
        Log.d(ACTIVITY_TAG, "btnReturn invoked");

        Toast.makeText(this, "Return button pressed!", Toast.LENGTH_SHORT).show();

        finish();
    }

    public class Register extends AsyncTask<String, String, Long> {
        private static final String ipAddr   = "192.168.0.164";
        private static final String port     = "5000";
        private static final String endpoint = "api/auth/register";

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected void onPostExecute(Long result){
            if(result == 0){
                Toast.makeText(RegisterActivity.this, "Pomyślnie zarejestrowano użytkownika!", Toast.LENGTH_SHORT).show();
                RegisterActivity.this.finish();
            } else {
                String reason = "";
                if(emailTaken){
                    reason = "Email already registered";
                } else if (usernameTaken){
                    reason = "Username already taken";
                }
                Toast.makeText(RegisterActivity.this, reason, Toast.LENGTH_SHORT).show();
                Log.d(ACTIVITY_TAG, "Could not create account");
            }
        }

        @Override
        protected Long doInBackground(String... params){
            HttpURLConnection connection = null;
            try {
                URL dataUrl = new URL("http://" + ipAddr + ":" + port + "/" + endpoint);
                connection = (HttpURLConnection) dataUrl.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                byte[] out = ("{\"email\":\""
                        + RegisterActivity.this.emailTextfield.getText().toString()
                        + "\",\"name\":\""
                        + RegisterActivity.this.loginTextfield.getText().toString()
                        + "\",\"password\":\""
                        + RegisterActivity.this.passwordTextfield.getText().toString()
                        + "\"}").getBytes();
                int length = out.length;
                connection.setFixedLengthStreamingMode(length);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //connection.setRequestProperty("Authorization", "token");

                connection.connect();
                try(OutputStream os = connection.getOutputStream()) {
                    os.write(out);
                }

                RegisterActivity.this.emailTaken = false;
                RegisterActivity.this.usernameTaken = false;

                int status = connection.getResponseCode();
                if(status == 201){

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

                    if(data.optString("error").equals("Email already registered")){
                        RegisterActivity.this.emailTaken = true;
                    }

                    if(data.optString("error").equals("Username already taken")){
                        RegisterActivity.this.usernameTaken = true;
                    }

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

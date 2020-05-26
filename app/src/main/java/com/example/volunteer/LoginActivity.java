package com.example.volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.volunteer.cryptography.Cryptography;
import com.example.volunteer.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Cryptography cryptography = new Cryptography();
        loginButton = findViewById(R.id.loginButton);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = null; // Add in try catch block if you get error.
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom()); // Add in try catch block if you get error.
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        URL url_obj = null;
        try {
            url_obj = new URL("https://10.0.2.2:5008");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection con = null;
        try {
            con = url_obj.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Reader reader = null;
        try {
            reader = new InputStreamReader(con.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            int ch = 0;
            try {
                ch = reader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (ch==-1) {
                break;
            }
            System.out.print((char)ch);
        }



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String login_url = "https://10.0.2.2:5008/api/User/login";
                final EditText emailAddressTextInput = findViewById(R.id.emailAddress);
                final EditText passwordTextInput = findViewById(R.id.password);
                JSONObject jsonObject = new JSONObject();

                assert emailAddressTextInput != null;
                assert passwordTextInput != null;

                try {
                    jsonObject.put("email", emailAddressTextInput.getText().toString());
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email address (1)", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                try {
                    jsonObject.put("password", passwordTextInput.getText().toString());
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid password (2)", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, login_url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            User currUser = null;
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            try {
                                currUser = new User(response.getString("email"),
                                        response.getString("firstName"),
                                        response.getString("lastName"),
                                        response.getInt("volunteerHours"));
                                Cryptography.storeEncryptedSecret(getApplicationContext(),
                                        Cryptography.decrypt(response.getString("secret").split("-")));
                            } catch (JSONException e) {
                                Toast.makeText(LoginActivity.this, "Server response couldn't be parsed. Please contact app developers. (3)", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, "Unable to store secret. Please contact app developers. (4)", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            intent.putExtra("user", currUser);
                            startActivity(intent);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(LoginActivity.this, "Invalid Email and Password Combination (5)" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonObjectRequest);
            }
        });
    }





}

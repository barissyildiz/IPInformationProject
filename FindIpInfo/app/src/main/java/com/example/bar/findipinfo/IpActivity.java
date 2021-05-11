package com.example.bar.findipinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IpActivity extends AppCompatActivity {

    Context context;
    TextView iptext,whatisipaddress;
    TextInputLayout textInputLayout;
    TextInputEditText ipedittext;
    CardView cardViewip;
    private InterstitialAd mInterstitialAd;
    String lat,lon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);

        identifiers();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4977622113046142/4527210519");

        mInterstitialAd.setAdListener(new AdListener(){

            @Override
            public void onAdLoaded() {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }

                super.onAdLoaded();
            }
        });
    }

    public void identifiers() {

        context = this;
        iptext = findViewById(R.id.ip_info);
        whatisipaddress  = findViewById(R.id.whatisippaddress);
        textInputLayout = findViewById(R.id.text_input_layout_ip);
        ipedittext = findViewById(R.id.text_input_edit_text_ip);
        cardViewip = findViewById(R.id.ip_cardview);

        getSupportActionBar().setTitle("");
    }

    public void ipquery(View v) {

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        boolean use = validate(ipedittext.getText().toString());

        if(use) {

            if(textInputLayout.isErrorEnabled()) {

                textInputLayout.setErrorEnabled(false);
            }

            networkjob(ipedittext.getText().toString());

            hideKeyboard();

        }
        else {

            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(getResources().getString(R.string.ip_error_text));
        }
    }

    public void hideKeyboard() {

        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean validate(final String ip) {

        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(PATTERN);
    }

    @SuppressLint("StaticFieldLeak")
    public void networkjob(final String ipaddress) {

        new AsyncTask<Void, Void, String[]>() {
            @Override
            protected String[] doInBackground(Void... voids) {

                URL url = null;
                URL url2 = null;
                String value = "";
                String value2 = "";

                try {

                    url = new URL("http://ip-api.com/json/"+ipaddress);
                    url2 = new URL("http://ip-api.com/json/"+ipaddress+"?fields=currency");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setDoInput(true);
                    if(httpURLConnection.getResponseCode() == 200) {

                        InputStream inputstream = httpURLConnection.getInputStream();
                        InputStreamReader streamreader = new InputStreamReader(inputstream);
                        BufferedReader reader = new BufferedReader(streamreader);

                        StringBuilder builder = new StringBuilder();

                        while((value = reader.readLine()) != null) {


                            builder.append(value);
                        }
                        inputstream.close();
                        streamreader.close();
                        reader.close();

                        value = builder.toString();
                    }

                    try {

                        HttpURLConnection httpURLConnection2 = (HttpURLConnection) url2.openConnection();
                        httpURLConnection2.setRequestMethod("GET");
                        httpURLConnection2.setDoInput(true);
                        if(httpURLConnection2.getResponseCode() == 200) {

                            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection2.getInputStream()));
                            value2 = reader.readLine();
                            reader.close();
                        }

                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }catch (Exception e) {

                    e.printStackTrace();
                }

                String[] array = new String[2];
                array[0] = value;
                array[1] = value2;

                return array;
            }

            @Override
            protected void onPostExecute(String[] aVoid) {

                try {

                    JSONObject jsonObject = new JSONObject(aVoid[0]);
                    lat = jsonObject.getString("lat");
                    lon = jsonObject.getString("lon");
                    String countryname = jsonObject.getString("country");
                    String countrycode = jsonObject.getString("countryCode");
                    JSONObject jsonObject2 = new JSONObject(aVoid[1]);
                    String countrymoneyname = jsonObject2.getString("currency");
                    String result = countryname +" "+countrycode + "-" +countrymoneyname;
                    iptext.setText(result);
                    cardViewip.setVisibility(View.VISIBLE);

                }catch (Exception e) {
                    e.printStackTrace();
                }

                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    public void find_location(View view) {

        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        String label = "ABC Label";
        String uriBegin = "geo:" + latitude + "," + longitude;
        String query = latitude + "," + longitude + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);

        }


    public void bringinfo(View view) {

        Intent intent = new Intent();
        intent.setClass(IpActivity.this,InfoActivity.class);
        intent.putExtra("ipaddress",ipedittext.getText().toString());
        startActivity(intent);
    }

    public void whatisipaddress(View view) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(R.string.what_is_ip_address);
        alert.setIcon(R.mipmap.info);
        alert.show();

        //Toast.makeText(context,"BİLGİ",Toast.LENGTH_SHORT).show();
    }
}

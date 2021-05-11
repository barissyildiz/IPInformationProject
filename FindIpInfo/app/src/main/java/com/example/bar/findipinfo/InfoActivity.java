package com.example.bar.findipinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class InfoActivity extends AppCompatActivity {

    Context context;
    TextView textView_countryname,textView_countrycode,textView_region,textView_regionname,textView_city,textView_zip,textView_lat,textView_lon,textView_timezone,textView_org;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        context = this;
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        textView_countryname = findViewById(R.id.textview_countryname);
        textView_countrycode = findViewById(R.id.textview_countrycode);
        textView_region = findViewById(R.id.textview_region);
        textView_regionname = findViewById(R.id.textview_regionname);
        textView_city = findViewById(R.id.textview_city);
        textView_zip = findViewById(R.id.textview_zip);
        textView_lat = findViewById(R.id.textview_lat);
        textView_lon = findViewById(R.id.textview_lon);
        textView_timezone = findViewById(R.id.textview_timezone);
        textView_org = findViewById(R.id.textview_org);

    }

    @Override
    protected void onStart() {
        super.onStart();

        String ipaddress = getIntent().getStringExtra("ipaddress");
        networkjob(ipaddress);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4977622113046142/4527210519");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent intent = new Intent();
                intent.setClass(InfoActivity.this, IpActivity.class);
                startActivity(intent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void backbutton(View view) {

        Intent intent = new Intent();
        intent.setClass(InfoActivity.this, IpActivity.class);
        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    public void networkjob(final String ipaddress) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                URL url = null;
                URL url2 = null;
                String value = "";
                String value2 = "";

                try {

                    url = new URL("http://ip-api.com/json/" + ipaddress);
                    url2 = new URL("http://ip-api.com/json/" + ipaddress + "?fields=currency");

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setDoInput(true);
                    if (httpURLConnection.getResponseCode() == 200) {

                        InputStream inputstream = httpURLConnection.getInputStream();
                        InputStreamReader streamreader = new InputStreamReader(inputstream);
                        BufferedReader reader = new BufferedReader(streamreader);

                        StringBuilder builder = new StringBuilder();

                        while ((value = reader.readLine()) != null) {


                            builder.append(value);
                        }
                        inputstream.close();
                        streamreader.close();
                        reader.close();

                        value = builder.toString();
                    }
                }catch (Exception e) {

                    e.printStackTrace();
                }
                return value;
            }
            @Override
            protected void onPostExecute(String aVoid) {

                try {

                    JSONObject jsonObject = new JSONObject(aVoid);
                    String lat = jsonObject.getString("lat");
                    String lon = jsonObject.getString("lon");
                    String countryname = jsonObject.getString("country");
                    String countrycode = jsonObject.getString("countryCode");
                    String region = jsonObject.getString("region");
                    String regionname = jsonObject.getString("regionName");
                    String city = jsonObject.getString("city");
                    String zip = jsonObject.getString("zip");
                    String timezone = jsonObject.getString("timezone");
                    String org = jsonObject.getString("org");

                    textView_countryname.setText(countryname);
                    textView_countrycode.setText(countrycode);
                    textView_region.setText(region);
                    textView_regionname.setText(regionname);
                    textView_city.setText(city);
                    textView_zip.setText(zip);
                    textView_lat.setText(lat);
                    textView_lon.setText(lon);
                    textView_timezone.setText(timezone);
                    textView_org.setText(org);

                }catch (Exception e) {
                    e.printStackTrace();
                }

                super.onPostExecute(aVoid);
            }
        }.execute();
        }
    }

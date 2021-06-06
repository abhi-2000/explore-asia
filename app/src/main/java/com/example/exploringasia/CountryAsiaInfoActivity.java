package com.example.exploringasia;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.exploringasia.AdaptersCollect.AdapterCountryAsia;
import com.example.exploringasia.ModelClasses.ModelAsiaCountry;
import com.example.exploringasia.Room.CountryAsiaDao;
import com.example.exploringasia.Room.DatabaseClient;
import com.example.exploringasia.Room.Information;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CountryAsiaInfoActivity extends AppCompatActivity {


    TextView txtAsiaCountry;
    RecyclerView AsiaCountryDetailsRecyclerView;
    Button delete;
    AdapterCountryAsia adapterCountryAsia;
    ProgressDialog progressDialog;
    ArrayList<ModelAsiaCountry> modelAsiaCountryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_asia_info);

        AsiaCountryDetailsRecyclerView = findViewById(R.id.AsiaCountryDetailsRecyclerView);
        txtAsiaCountry = findViewById(R.id.txtAsiaCountry);
        delete = findViewById(R.id.btn_id);
        progressDialog=new ProgressDialog(this);
        modelAsiaCountryArrayList = new ArrayList<>();
        modelAsiaCountryArrayList.clear();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            fetchAsiaCountryInformation();
        } else {
            fetchFromRoomTheData();
        }

    }

    private void fetchFromRoomTheData() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {


                List<Information> infoListNew = DatabaseClient.getInstance(com.example.exploringasia.CountryAsiaInfoActivity.this).getAppDatabase().countryAsiaDao().getAllNew();
                modelAsiaCountryArrayList.clear();
                for (Information info : infoListNew) {
                    ModelAsiaCountry repo = new ModelAsiaCountry(
                            "" + info.getName(),
                            "" + info.getCapital(),
                            "" + info.getRegion(),
                            "" + info.getSubregion(),
                            "" + info.getPopulation(),
                            "" + info.getFlag(),
                            "" + info.getBorder(),
                            "" + info.getLanguages()
                    );
                    modelAsiaCountryArrayList.add(repo);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterCountryAsia = new AdapterCountryAsia(com.example.exploringasia.CountryAsiaInfoActivity.this, modelAsiaCountryArrayList);
                        AsiaCountryDetailsRecyclerView.setAdapter(adapterCountryAsia);
                    }
                });
            }
        });
        thread.start();

    }


    private void fetchAsiaCountryInformation() {

        String url = "https://restcountries.eu/rest/v2/region/asia";

        StringRequest stringRequestNews = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String onetxt = jsonObject.getString("name");
                            String twotxt = jsonObject.getString("capital");
                            String threetxt = jsonObject.getString("region");
                            String fourtxt = jsonObject.getString("subregion");
                            String fivetxt = jsonObject.getString("population");
                            String sixtxt = jsonObject.getString("flag");
                            String jsonArray1 = jsonObject.getString("borders");
                            String border = "";
                            for (int p = 0; p < jsonArray1.length(); p++)
                                border = border + jsonArray1.charAt(p);

                            String Languages = "";
                            JSONArray jsonArray2 = jsonObject.getJSONArray("languages");
                            for (int u = 0; u < jsonArray2.length(); u++) {
                                JSONObject jsonObject1 = jsonArray2.getJSONObject(u);
                                if (u == 0) {
                                    Languages = Languages + "" + jsonObject1.getString("name");
                                } else {
                                    Languages = Languages + "," + jsonObject1.getString("name");
                                }
                            }
                            ModelAsiaCountry modelAsiaCountry = new ModelAsiaCountry(
                                    "" + onetxt,
                                    "" + twotxt,
                                    "" + threetxt,
                                    "" + fourtxt,
                                    "" + fivetxt,
                                    "" + sixtxt,
                                    "" + border,
                                    "" + Languages
                            );
                            modelAsiaCountryArrayList.add(modelAsiaCountry);

                        } catch (Exception e) {
                            Toast.makeText(com.example.exploringasia.CountryAsiaInfoActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                    adapterCountryAsia = new AdapterCountryAsia(com.example.exploringasia.CountryAsiaInfoActivity.this, modelAsiaCountryArrayList);
                    AsiaCountryDetailsRecyclerView.setAdapter(adapterCountryAsia);
                    adapterCountryAsia.notifyDataSetChanged();
                    saveTask();
                } catch (Exception e) {
                    Toast.makeText(com.example.exploringasia.CountryAsiaInfoActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(com.example.exploringasia.CountryAsiaInfoActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(com.example.exploringasia.CountryAsiaInfoActivity.this);
        requestQueue.add(stringRequestNews);


    }


        public void delete(View view) {
//        deleteAllWordsAsyncTask del= new deleteAllWordsAsyncTask();
//        del.execute();
            progressDialog.setMessage("Deleting");
            progressDialog.show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
            progressDialog.dismiss();
            ((ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();

                    //do something
                }
            }, 3000 );
//            Thread timer = new Thread() {
//                public void run(){
//                    try {
//                        sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            };
//            timer.start();
    }
//
//
//    class deleteall extends AsyncTask<Void,Void,Void>{
//
//         @Override
//         protected Void doInBackground(Void... voids) {
//             DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().countryAsiaDao().deleteAllNotes();
//             Toast.makeText(getApplicationContext(), " Information Deleted Successfully", Toast.LENGTH_LONG).show();
//             return null;
//         }
//     }

//     class deleteAllWordsAsyncTask extends AsyncTask<Void, Void, Void> {
//        CountryAsiaDao mAsyncTaskDao;
//
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            mAsyncTaskDao.deleteAllNotes();
//            return null;
//        }
//    }
    private void saveTask() {

        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task

                for (int i = 0; i < modelAsiaCountryArrayList.size(); i++) {
                    Information recipe = new Information();
                    recipe.setName(modelAsiaCountryArrayList.get(i).getName());
                    recipe.setCapital(modelAsiaCountryArrayList.get(i).getCapital());
                    recipe.setSubregion(modelAsiaCountryArrayList.get(i).getSubregion());
                    recipe.setRegion(modelAsiaCountryArrayList.get(i).getRegion());
                    recipe.setPopulation(modelAsiaCountryArrayList.get(i).getPopulation());
                    recipe.setFlag(modelAsiaCountryArrayList.get(i).getFlag());
                    recipe.setLanguages(modelAsiaCountryArrayList.get(i).getLanguages());
                    recipe.setBorder(modelAsiaCountryArrayList.get(i).getBorder());
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().countryAsiaDao().insert(recipe);
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), " Information Saved Successfully To Room Database", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();


    }


}

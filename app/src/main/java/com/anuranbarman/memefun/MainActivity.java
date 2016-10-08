package com.anuranbarman.memefun;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    public final String url="https://memegen.link/api/templates/";
    HashMap map=new HashMap();
    List<String> keyArray=new ArrayList<String>();
    List<String> linkArray=new ArrayList<String>();
    List<String> MemeNameArray=new ArrayList<String>();
    String[] meme;
    Button btn;
    TextView appName;
    public boolean hasInternet;
    CheckInternet ci;
    //GridView gridView;
    //public int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        appName=(TextView)findViewById(R.id.appName);
        Typeface type=Typeface.createFromAsset(getAssets(),"font.ttf");
        appName.setTypeface(type);
        btn=(Button)findViewById(R.id.btnGo);
        btn.setTypeface(type);
        ci=new CheckInternet();
        ci.execute();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putStringArray("meme_array",meme);
                Intent i = new Intent(MainActivity.this,Memes.class);
                i.putExtras(b);
                startActivity(i);
                finish();
            }
        });




    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("CHECK", "Error checking internet connection", e);
            }
        } else {
            Log.d("CHECK", "No network available!");
        }
        return false;
    }

    class CheckInternet extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            hasInternet=hasActiveInternetConnection();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(hasInternet==false){
                AlertDialog.Builder alerDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alerDialogBuilder.setMessage("The app does not work without Internet.Please connect to Internet.");
                alerDialogBuilder.setCancelable(false);
                alerDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog dialog = alerDialogBuilder.create();
                dialog.show();
            } else {
                final AlertDialog pDialog= new SpotsDialog(MainActivity.this,R.style.Custom);
                pDialog.show();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator iterator=response.keys();
                        while(iterator.hasNext()){
                            keyArray.add((String)iterator.next());
                        }
                        for(int i=0;i<response.length();i++){
                            try {
                                map.put(keyArray.get(i),response.getString(keyArray.get(i)));
                                linkArray.add(response.getString(keyArray.get(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        for(int i=0;i<linkArray.size();i++){
                            String temp=linkArray.get(i).substring(linkArray.get(i).lastIndexOf("/")+1,linkArray.get(i).length());
                            MemeNameArray.add(temp);
                        }
                        meme=new String[MemeNameArray.size()];
                        MemeNameArray.toArray(meme);
                        pDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

                MySingleton.getmInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);
            }

        }
    }
}

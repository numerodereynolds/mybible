package com.example.duart.mybible;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Gift extends AppCompatActivity {

    private SQLiteOpenHelper dataBase;
    private SQLiteDatabase sqLiteDatabase;
    private ListView listViewGift;
    private RequestQueue mRequestQueue;
    private static final String TAG = Gift.class.getName();
    private static final String REQUESTTAG = "string get new item";
    public String IpAddress = "http://192.168.1.9/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);

        listViewGift = (ListView) findViewById(R.id.list_view_gift);
        dataBase = new mybibleDataBase(this);

        printGiftList();
        clickableListView();
    }

    //necessary to show buttons on action bar menu
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_gift, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_add_new_item:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void printGiftList(){
        ArrayList<String> arrayListGift = new ArrayList<>();
        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListGift);

        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor giftData = sqLiteDatabase.rawQuery("SELECT gift.id, item.name, wallet.value FROM gift LEFT JOIN item ON gift.id_item=item.id LEFT JOIN wallet ON gift.id_wallet=wallet.id WHERE gift.status!=3;", null);

        while (giftData.moveToNext()){
            if (giftData.getString(1)==null) {
                arrayListGift.add("#" + giftData.getString(0) + " " + giftData.getString(2));
            }else if(giftData.getString(2)==null){
                arrayListGift.add("#" + giftData.getString(0) + " " + giftData.getString(1));
            }
        }
        listViewGift.setAdapter(listAdapter);
    }

    public void clickableListView(){
        listViewGift.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String giftSelected = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(Gift.this, ViewGift.class);
                intent.putExtra("giftSelected", giftSelected);
                startActivity( intent );
            }
        });
    }

}
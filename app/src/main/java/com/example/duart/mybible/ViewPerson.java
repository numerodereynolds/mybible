package com.example.duart.mybible;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewPerson extends AppCompatActivity {

    private String personId;
    private SQLiteOpenHelper dataBase;
    private SQLiteDatabase sqLiteDatabase;
    private TextView textViewName;
    private TextView textViewCompleteName;
    private TextView textViewBirthday;
    private ImageView imageViewBirthday;
    private TextView textViewMobile;
    private ImageView imageViewMobile;
    private TextView textViewHome;
    private ImageView imageViewHome;
    private TextView textViewWork;
    private ImageView imageViewWork;
    private TextView textViewEmail;
    private ImageView imageViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_person);

        imageViewBirthday = (ImageView) findViewById(R.id.image_view_birthday);
        imageViewMobile = (ImageView) findViewById(R.id.image_view_mobile);
        imageViewHome = (ImageView) findViewById(R.id.image_view_fix);
        imageViewWork = (ImageView) findViewById(R.id.image_view_work);
        imageViewEmail = (ImageView) findViewById(R.id.image_view_email);
        textViewName = (TextView) findViewById(R.id.text_view_name);
        textViewCompleteName = (TextView) findViewById(R.id.text_view_complete_name);
        textViewBirthday = (TextView) findViewById(R.id.text_view_birthday);
        textViewMobile = (TextView) findViewById(R.id.text_view_mobile);
        textViewHome = (TextView) findViewById(R.id.text_view_fix);
        textViewWork = (TextView) findViewById(R.id.text_view_work);
        textViewEmail = (TextView) findViewById(R.id.text_view_email);
        dataBase = new mybibleDataBase(this);

        personId = getIntentAndTransform();
        printName(personId);
        printBirthday(personId);
        printMobileNumber(personId);
        printHomeNumber(personId);
        printWorkNumber(personId);
        printEmailNumber(personId);

    }

    //necessary to show buttons on action bar menu
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_view_item, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.action_edit:

                    Intent intent = new Intent( ViewPerson.this, EditPerson.class );
                    intent.putExtra("personId", personId);
                    startActivity( intent );
                    return true;

                default:
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    return super.onOptionsItemSelected(item);
            }
        }

    public String getIntentAndTransform(){
        //this gets the selected string from SeeAllWallet
            Intent intent = getIntent();
            String personSelected = intent.getExtras().getString("personSelected");

        //this gets the id from the string
        personSelected = personSelected.split("#")[1];
        personId = personSelected.split(" ")[0];

        return personId;
    }

    public void printName(String id){
        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor personNameData = sqLiteDatabase.rawQuery("SELECT name, complete_name FROM person WHERE id=" + id + ";", null);
        while (personNameData.moveToNext()){
            textViewName.setText(personNameData.getString(0));
            textViewCompleteName.setText(personNameData.getString(1));
        }
    }

    public void printBirthday(String id){
        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor personBirthdayData = sqLiteDatabase.rawQuery("SELECT date FROM birthday WHERE id_person=" + id + ";", null);

        if (personBirthdayData.getCount()!=0){
            while (personBirthdayData.moveToNext()){
                if (!personBirthdayData.getString(0).equals("")){
                    textViewBirthday.setText(personBirthdayData.getString(0));
                }else {
                    textViewBirthday.setVisibility(View.GONE);
                    imageViewBirthday.setVisibility(View.GONE);
                }
            }
        }else {
            textViewBirthday.setVisibility(View.GONE);
            imageViewBirthday.setVisibility(View.GONE);
        }

    }

    public void printMobileNumber(String id){
        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor personMobileNumberData = sqLiteDatabase.rawQuery("SELECT number FROM phone WHERE category='telemóvel' AND id_person=" + id + " AND status!=3;", null);

        if (personMobileNumberData.getCount()!=0){
            while (personMobileNumberData.moveToNext()){
                textViewMobile.setText(personMobileNumberData.getString(0));
            }
        }else {
            textViewMobile.setVisibility(View.GONE);
            imageViewMobile.setVisibility(View.GONE);
        }

    }

    public void printHomeNumber(String id){
        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor personHomeNumberData = sqLiteDatabase.rawQuery("SELECT number FROM phone WHERE category='telefone' AND id_person=" + id + " AND status!=3;", null);

        Log.i("TAG", "SIZE: " + personHomeNumberData.getCount());

        if (personHomeNumberData.getCount()!=0){
            while (personHomeNumberData.moveToNext()){
                textViewHome.setText(personHomeNumberData.getString(0));
            }
        }else {
            textViewHome.setVisibility(View.GONE);
            imageViewHome.setVisibility(View.GONE);
        }

    }

    public void printWorkNumber(String id){
        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor personWorkNumberData = sqLiteDatabase.rawQuery("SELECT number FROM phone WHERE category='trabalho' AND id_person=" + id + " AND status!=3;", null);

        if (personWorkNumberData.getCount()!=0){
            while (personWorkNumberData.moveToNext()){
                textViewWork.setText(personWorkNumberData.getString(0));
            }
        }else {
            textViewWork.setVisibility(View.GONE);
            imageViewWork.setVisibility(View.GONE);
        }

    }

    public void printEmailNumber(String id){
        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor personEmailData = sqLiteDatabase.rawQuery("SELECT email FROM email WHERE id_person=" + id + " AND status!=3;", null);

        if (personEmailData.getCount()!=0){
            while (personEmailData.moveToNext()){
                textViewEmail.setText(personEmailData.getString(0));
            }
        }else {
            textViewEmail.setVisibility(View.GONE);
            imageViewEmail.setVisibility(View.GONE);
        }

    }

}

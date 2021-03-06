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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class NewItemBox extends AppCompatActivity {

    private SQLiteOpenHelper dataBase;
    private SQLiteDatabase sqLiteDatabase;
    private EditText editTextName;
    private AutoCompleteTextView editTextCategory;
    private AutoCompleteTextView editTextLocation;
    private AutoCompleteTextView editTextBox;
    private AutoCompleteTextView editTextConsumable;
    private EditText editTextCost;
    private Spinner spinnerGift;
    private AutoCompleteTextView editTextPersonGift;
    private AutoCompleteTextView editTextCategoryGift;
    private AutoCompleteTextView editTextDescriptionGift;
    private String personGiftId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item_box);

        editTextName = (EditText) findViewById(R.id.edit_text_item_name);
        editTextCategory = (AutoCompleteTextView) findViewById(R.id.edit_text_category);
        editTextLocation = (AutoCompleteTextView) findViewById(R.id.edit_text_item_location);
        editTextBox = (AutoCompleteTextView) findViewById(R.id.edit_text_item_box);
        editTextConsumable = (AutoCompleteTextView) findViewById(R.id.edit_text_consumable);
        editTextCost = (EditText) findViewById(R.id.edit_text_cost);
        spinnerGift = (Spinner) findViewById(R.id.spinner_gift);
        editTextPersonGift = (AutoCompleteTextView) findViewById(R.id.edit_text_person_gift);
        editTextCategoryGift = (AutoCompleteTextView) findViewById(R.id.edit_text_category_gift);
        editTextDescriptionGift = (AutoCompleteTextView) findViewById(R.id.edit_text_description_gift);
        dataBase = new mybibleDataBase(this);

        //this line removes the arrow from the action bar menu in this activity
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        getCategoryAutoComplete();
        getLocationAutoComplete();
        getBoxAutoComplete();
        getConsumableAutoComplete();
        getGiftSpinner();
        getPersonAutoComplete();
        getCategoryGiftAutoComplete();
        getDescriptionGiftAutoComplete();

    }

    //necessary to show buttons on action bar menu
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_new, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.action_add_new:
                    personGiftId = getPersonGiftId();
                    insertNewItemBox(personGiftId);
                    String stringQuery = "SELECT * FROM item";
                    Intent intent = new Intent(NewItemBox.this, ItemBox.class);
                    intent.putExtra("stringQuery", stringQuery);
                    startActivity( intent );
                    return true;

                default:
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    return super.onOptionsItemSelected(item);
            }
        }

    public void insertNewItemBox(String personGiftId){
        //inputs new item into item table
            sqLiteDatabase = dataBase.getWritableDatabase();
            sqLiteDatabase.execSQL("INSERT INTO item (name, category, status) VALUES ('" + editTextName.getText().toString() + "', '" + editTextCategory.getText().toString() + "', 0);");

        //inputs new item into location table
            sqLiteDatabase = dataBase.getReadableDatabase();
            Cursor itemData = sqLiteDatabase.rawQuery("SELECT id FROM item WHERE name='" + editTextName.getText() + "' AND category='" + editTextCategory.getText() + "';", null);
            itemData.moveToFirst();
            String itemId = itemData.getString(0);

            String subdivisionId;
            if (editTextLocation.getText().toString().equals("")){
                //if user didn't input any subdivision location
                subdivisionId = "0";
            }else {
                //if user inputted a subdivision location
                sqLiteDatabase = dataBase.getReadableDatabase();
                Cursor subdivisionData = sqLiteDatabase.rawQuery("SELECT id FROM subdivision WHERE subdivision='" + editTextLocation.getText() + "';", null);
                subdivisionData.moveToFirst();
                subdivisionId = subdivisionData.getString(0);
            }

            String boxId;
            if (editTextBox.getText().toString().equals("")){
                //if user didn't input any box location
                boxId = "0";
            }else {
                //if user inputted a box location
                sqLiteDatabase = dataBase.getReadableDatabase();
                Cursor boxData = sqLiteDatabase.rawQuery("SELECT id FROM box WHERE box='" + editTextBox.getText() + "';", null);
                boxData.moveToFirst();
                boxId = boxData.getString(0);
            }

            sqLiteDatabase = dataBase.getWritableDatabase();
            sqLiteDatabase.execSQL("INSERT INTO location (id_item, id_subdivision, id_box, status) VALUES (" + itemId + ", " + subdivisionId + ", '" + boxId +"', 0);");

        //inputs into consumables or no consumables table
            if( editTextConsumable.getText().toString().equals("sim") ){
                sqLiteDatabase = dataBase.getWritableDatabase();
                sqLiteDatabase.execSQL("INSERT INTO consumables (id_item, change_stock, status) VALUES (" + itemId + ", 1, 0);");
            }else if ( editTextConsumable.getText().toString().equals("não")){
                sqLiteDatabase = dataBase.getWritableDatabase();
                sqLiteDatabase.execSQL("INSERT INTO noconsumables (id_item, state, status) VALUES (" + itemId + ", 1, 0);");
            }

        //inputs into cost table
            sqLiteDatabase = dataBase.getWritableDatabase();
            sqLiteDatabase.execSQL("INSERT INTO cost (id_item, cost, status) VALUES (" + itemId + ",'" + editTextCost.getText().toString() + "', 0);");


        //if the new item is a gift, inputs into gift table
            if (spinnerGift.getSelectedItem().toString().equals("sim")){
                sqLiteDatabase = dataBase.getWritableDatabase();
                Cursor dateGiftData = sqLiteDatabase.rawQuery("SELECT date FROM cost WHERE id_item=" + itemId + ";", null);
                dateGiftData.moveToFirst();
                String date_gift = dateGiftData.getString(0).split(" ")[0];

                sqLiteDatabase = dataBase.getWritableDatabase();
                sqLiteDatabase.execSQL("INSERT INTO gift (id_item, id_wallet, id_person, date, description, category, status) VALUES ("
                        + itemId
                        + ", 0, "
                        + personGiftId
                        + ", '"
                        + date_gift
                        + "', '"
                        + editTextDescriptionGift.getText().toString()
                        + "', '"
                        + editTextCategoryGift.getText().toString()
                        + "', 0);");
            }

    }

    public void getCategoryAutoComplete(){
        ArrayList<String> arrayListCategory = new ArrayList<>();
        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayListCategory);

        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor data = sqLiteDatabase.rawQuery("SELECT DISTINCT category FROM item;", null);
        while (data.moveToNext()){
            arrayListCategory.add(data.getString(0));
        }
        editTextCategory.setAdapter(adapter);
    }

    public void getLocationAutoComplete(){
        ArrayList<String> arrayListLocation = new ArrayList<>();
        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayListLocation);

        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor data = sqLiteDatabase.rawQuery("SELECT DISTINCT subdivision FROM subdivision;", null);
        while (data.moveToNext()){
            arrayListLocation.add(data.getString(0));
        }
        editTextLocation.setAdapter(adapter);
    }

    public void getBoxAutoComplete(){
        final ArrayList<String> arrayListBox = new ArrayList<>();
        final ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayListBox);

        editTextLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    sqLiteDatabase = dataBase.getReadableDatabase();
                    Cursor data = sqLiteDatabase.rawQuery("SELECT box, subdivision FROM box LEFT JOIN subdivision ON box.id_subdivision=subdivision.id WHERE subdivision='" + editTextLocation.getText().toString() + "';", null);
                    Log.i("TAG", "SIZE: " + data.getCount());
                    while (data.moveToNext()){
                        arrayListBox.add(data.getString(0));
                    }
                    editTextBox.setAdapter(adapter);
                }
            }
        });
    }

    public void getConsumableAutoComplete(){
        ArrayList<String> arrayListConsumable = new ArrayList<>();
        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayListConsumable);

        arrayListConsumable.add("sim");
        arrayListConsumable.add("não");

        editTextConsumable.setAdapter(adapter);
    }

    public void getGiftSpinner(){
        ArrayList<String> arrayList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList);

        arrayList.add("sim");
        arrayList.add("não");
        spinnerGift.setAdapter(adapter);
    }

    public void getPersonAutoComplete(){
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayList);

        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor data = sqLiteDatabase.rawQuery("SELECT name FROM person;", null);
        while (data.moveToNext()){
            arrayList.add(data.getString(0));
        }
        editTextPersonGift.setAdapter(adapter);
    }

    public void getCategoryGiftAutoComplete(){
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayList);

        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor data = sqLiteDatabase.rawQuery("SELECT DISTINCT category FROM gift;", null);
        while (data.moveToNext()){
            arrayList.add(data.getString(0));
        }
        editTextCategoryGift.setAdapter(adapter);
    }

    public void getDescriptionGiftAutoComplete(){
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayList);

        sqLiteDatabase = dataBase.getReadableDatabase();
        Cursor data = sqLiteDatabase.rawQuery("SELECT DISTINCT description FROM gift;", null);
        while (data.moveToNext()){
            arrayList.add(data.getString(0));
        }
        editTextDescriptionGift.setAdapter(adapter);
    }

    private String getPersonGiftId() {
        //checks if new entry is a gift or not
        if (spinnerGift.getSelectedItem().toString().equals("sim")){
            //if it is...
            sqLiteDatabase = dataBase.getReadableDatabase();
            String insertNewEntryQuery = "SELECT id FROM person WHERE name='" + editTextPersonGift.getText() + "';";
            Cursor data = sqLiteDatabase.rawQuery(insertNewEntryQuery, null);
            data.moveToFirst();
            personGiftId = data.getString(0);
        }
        else{
            //if it is not...
            personGiftId = "0";
        }
        return personGiftId;
    }

}

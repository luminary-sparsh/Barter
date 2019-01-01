package com.hfad.barter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.os.Build.ID;

public class TransactionDetailActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private RecyclerViewAdapter adapter;
    private ArrayList<Transactions> list= new ArrayList<>();
    public String ID = "id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Transaction Detail");
        //getting value of id
        String id = getIntent().getStringExtra("ID").toString();
        Log.d(TAG, "onCreate: value of Id from Intent" + id);
        //setting layout manager on recycler View
        RecyclerView transactionRecycler = (RecyclerView) findViewById(R.id.transaction_detail_recycler);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        transactionRecycler.setLayoutManager(linearLayoutManager);

        //getting the information from database and storing it in Transactions class
        BarterDatabaseHelper barterDatabaseHelper = new BarterDatabaseHelper(this);
        db = barterDatabaseHelper.getReadableDatabase();
        Log.d(TAG, "onCreate: value of id" + id);
        Cursor cursor = barterDatabaseHelper.getInformation(db,id);
        if (cursor!=null && cursor.moveToFirst()){
            do {
                Transactions transactions = new Transactions(cursor.getString(0),cursor.getString(1),cursor.getString(2),
                        cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
                list.add(transactions);
            }while(cursor!=null && cursor.moveToNext());
        }
        //setting adapter
        adapter = new RecyclerViewAdapter(list);
        transactionRecycler.setAdapter(adapter);

    }
}

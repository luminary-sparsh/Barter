package com.hfad.barter;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import static android.content.ContentValues.TAG;

public class FavoriteFragment extends Fragment {

    private SQLiteDatabase db;
    RecyclerView.Adapter adapter;
    ArrayList<Transactions> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the recycler layout and set linear layout to it.
        View theView = inflater.inflate(R.layout.fragment_favorite, null);
        RecyclerView recyclerView = (RecyclerView) theView.findViewById(R.id.top_recycler);
        /*RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_lent,container,false);
        LinearLayoutManager llm= new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);*/


        //get database and information from it and store it in array list
        BarterDatabaseHelper barterDatabaseHelper = new BarterDatabaseHelper(getActivity());
        db = barterDatabaseHelper.getWritableDatabase();
        Cursor cursor = barterDatabaseHelper.getInformation(db,0);
        if(cursor !=null && cursor.moveToFirst())
            do{
                Transactions transactions = new Transactions(cursor.getString(0),cursor.getString(1),cursor.getString(2),
                        cursor.getString(3),cursor.getString(4), cursor.getString(5),cursor.getString(6));
                list.add(transactions);
            }while (cursor !=null && cursor.moveToNext());

        //set the recycler view adapter
        adapter= new RecyclerViewAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return theView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
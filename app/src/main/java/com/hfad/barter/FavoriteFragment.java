package com.hfad.barter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;

public class FavoriteFragment extends Fragment {

    private SQLiteDatabase db;
    private RecyclerView.Adapter adapter;
    private ArrayList<Transactions> list = new ArrayList<>();
    private TextView tv_noItem;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the recycler layout and set linear layout to it.
        View theView = inflater.inflate(R.layout.fragment_favorite, null);
        recyclerView = (RecyclerView) theView.findViewById(R.id.fav_recycler);
        tv_noItem = (TextView) theView.findViewById(R.id.tv_no_fav_items);

        //get database and information from it and store it in array list
        updateList();

        //reversing list for adding new item on top
        Collections.reverse(list);
        adapter= new RecyclerViewAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (!list.isEmpty()){
            setNonEmptyList();
            recyclerView.setAdapter(adapter);
        }else {
            setEmptyList();
        }
        return theView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        list.clear();
        updateList();
        if (!list.isEmpty()){
            setNonEmptyList();
            Collections.reverse(list);
            adapter.notifyDataSetChanged();
        }
        else {
            setEmptyList();
        }
    }

    private void updateList() {
        BarterDatabaseHelper barterDatabaseHelper = new BarterDatabaseHelper(getActivity());
        db = barterDatabaseHelper.getWritableDatabase();
        Cursor cursor = barterDatabaseHelper.getInformation(db, 0);
        if (cursor != null && cursor.moveToFirst())
            do {
                Transactions transactions = new Transactions(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                list.add(transactions);
            } while (cursor != null && cursor.moveToNext());
    }

    public void setEmptyList(){
        recyclerView.setVisibility(View.GONE);
        tv_noItem.setVisibility(View.VISIBLE);
    }

    public void setNonEmptyList(){
        recyclerView.setVisibility(View.VISIBLE);
        tv_noItem.setVisibility(View.GONE);
    }
}
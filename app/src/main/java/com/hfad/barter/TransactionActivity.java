package com.hfad.barter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.Calendar;

import static android.R.attr.description;
import static android.R.attr.id;
import static android.R.attr.name;
import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;


public class TransactionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private EditText lentBorrow;
    private TextView date_time;
    private String lb[];
    public boolean b;
    private java.util.Calendar c = java.util.Calendar.getInstance();
    private int m_day,m_month,m_year,m_hour,m_min;
    private String dateTime;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        getSupportActionBar().setTitle("New Transaction");     //setting initial title of the actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   ////setting back(home) button on the top left corner
        spinner = (Spinner) findViewById(R.id.lb_spinner);
        lentBorrow = (EditText) findViewById(R.id.lb_name);
        lb = getResources().getStringArray(R.array.lb_spinner);

        //setting up array adapter for spinner as it is required for setting up listener in order to
        //capture its value
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lb_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        setInitialDateTime();        //setting up initial date
    }

    private void setInitialDateTime(){
        //setting up initial date
        c = java.util.Calendar.getInstance();
        date_time = (TextView) findViewById(R.id.date_time_picker);
        m_year = c.get(java.util.Calendar.YEAR);
        m_month = c.get(java.util.Calendar.MONTH) + 1;
        m_day = c.get(java.util.Calendar.DATE);
        m_hour = c.get(java.util.Calendar.HOUR_OF_DAY);
        m_min = c.get(java.util.Calendar.MINUTE);
        String min =  m_min + "";
        if(min.length()==1){
            min = "0"+min;
        }
        date_time.setText(m_day+"-"+m_month+"-"+m_year+"   "+m_hour+":"+    min);
    }

    public void submit(View v){
        // on clicking submit button
        try {
            //creating the object of the database
            BarterDatabaseHelper barterDatabaseHelper = new BarterDatabaseHelper(this);
            db = barterDatabaseHelper.getWritableDatabase();

            //fetching the values from ui elements
            EditText itemName = (EditText)findViewById(R.id.item_name);
            EditText LBname = (EditText) findViewById(R.id.lb_name);
            EditText description = (EditText) findViewById(R.id.item_description);
            TextView dateTime =(TextView) findViewById(R.id.date_time_picker);
            Spinner lendBorrow = (Spinner) findViewById(R.id.lb_spinner);
            String LBvalue,favorite;
            if(lendBorrow.getSelectedItem().toString().equals("Lend")){  LBvalue = "0";   }
            else LBvalue = "1";

            //// TODO: 16-11-2017 for now favorite=0 change favorite value according to favorite button
            favorite ="0";

            //sending the fetched values into he db and printing it in logs
            barterDatabaseHelper.insertTransaction(db,LBvalue,itemName.getText().toString(),
                    LBname.getText().toString(),description.getText().toString(),
                    dateTime.getText().toString(),favorite);

        }
        catch(SQLiteException e) {
            Log.d(TAG, "submit: "+e.getMessage().toString());
            Toast toast = Toast.makeText(this, "Database unavailable"+e.getMessage().toString(),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void dateTimePicker(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateTime = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        Log.d(TAG, "onDateSet: "+ dateTime);
                        timePicker();
                    }
                }, m_year, m_month, m_day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        ConstraintLayout constraintLayout=(ConstraintLayout) View.inflate(this,R.layout.picker_dialog_title_layout,null);
        TextView title = (TextView)constraintLayout.findViewById(R.id.picker_dialog_header);
        title.setText("Transaction Date");
        datePickerDialog.setCustomTitle(constraintLayout);
        datePickerDialog.show();
    }

    public void timePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        m_hour = hourOfDay;
                        Log.d(TAG, "onTimeSet: " + hourOfDay);
                        m_min = minute;
                        String min = m_min+"";
                        if(min.length() == 1) min = "0"+min;
                        date_time.setText("  "+dateTime + "   " + hourOfDay + ":" + minute);
                    }
                }, m_hour, m_min, false);
        ConstraintLayout constraintLayout=(ConstraintLayout) View.inflate(this,R.layout.picker_dialog_title_layout,null);
        TextView title = (TextView)constraintLayout.findViewById(R.id.picker_dialog_header);
        title.setText("Transaction Time");
        timePickerDialog.setCustomTitle(constraintLayout);
        timePickerDialog.show();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (parent.getItemAtPosition(pos).toString().equals("Borrow")){
            lentBorrow.setHint("Enter lender's name");
        }
        else{
            lentBorrow.setHint("Enter Borrower's name");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    // TODO: 21-11-2017 only for testing purpose remove button and showDB() code before final upload
    public void showDB(View v){
        BarterDatabaseHelper BDH = new BarterDatabaseHelper(this);
        db = BDH.getReadableDatabase();
        BDH.printDB(db);
    }
}

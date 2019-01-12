package com.hfad.barter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import static android.content.ContentValues.TAG;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<Transactions> list= new ArrayList<>();
    private int currentPosition;
    Context context;
    private int m_day,m_month,m_year,m_hour,m_min;
    private ImageButton favorite;
    private TextView carditemname;
    private TextView cardlbname;
    private TextView carddescription;
    private TextView carddatetime;
    private TextView id;
    private TextView cardlorb;
    private ImageButton delete;
    private ImageButton remind;
    private Boolean isFav;
    private String TAG = "RecyclerViewAdpater";
    Transactions transactions;


    public RecyclerViewAdapter(ArrayList<Transactions> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v){
            super(v);
            cardView=v;
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        context = parent.getContext();
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);
        try{
            final Activity activity = (Activity) parent.getContext();
            FragmentManager fragmentManager=activity.getFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag("visible_fragment");

            if (fragment instanceof TopFragment){
                currentPosition =  0;
            }
            if (fragment instanceof LentFragment){
                currentPosition = 1;
            }
            if (fragment instanceof BorrowFragment){
                currentPosition = 2;
            }
            if (fragment instanceof FavoriteFragment){
                currentPosition = 3;
            }
        } catch (ClassCastException e) {
            Log.d(TAG, "Can't get the fragment manager with this");
        }
        return new ViewHolder(cv);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        final int pos = position;
        transactions = list.get(position);
        isFav = transactions.getFavorite().trim().equals("1");
        favorite = (ImageButton) holder.cardView.findViewById(R.id.card_favorite_button);
        carditemname = (TextView) holder.cardView.findViewById(R.id.card_item_name);
        cardlbname = (TextView) holder.cardView.findViewById(R.id.card_lb_name);
        carddescription = (TextView) holder.cardView.findViewById(R.id.card_description);
        carddatetime = (TextView) holder.cardView.findViewById(R.id.card_date_time);
        id=(TextView) holder.cardView.findViewById(R.id.card_id);
        cardlorb = (TextView) holder.cardView.findViewById(R.id.lorb);
        delete = (ImageButton) holder.cardView.findViewById(R.id.card_delete_button);
        remind = (ImageButton) holder.cardView.findViewById(R.id.card_reminder_button);

        carditemname.setText(transactions.getItemname());
        cardlbname.setText(transactions.getLBname());
        carddescription.setText(transactions.getDescription());
        carddatetime.setText(transactions.getDatetime().trim());
        id.setText(transactions.getID());

        //if favorite then filled heart else bordered heart
        if (isFav){
            favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_favorite_filled_24px));
        }
        if (!isFav){
            favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24px));
        }

        //lender / borrower textview setting on the card
        if (transactions.getLend().trim().equals("1")){
            cardlorb.setText("Lender");
        }else {
            cardlorb.setText("Borrower");
        }

        /*if(currentPosition==1){            cardlorb.setText("Borrower");
        }else cardlorb.setText("Lender:");*/


        /*holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,TransactionDetailActivity.class);
                intent.putExtra("ID",id.toString());
                context.startActivity(intent);
            }
        });*/


        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(context,R.style.AlertDialogTheme);
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(context.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                TextView id=(TextView) holder.cardView.findViewById(R.id.card_id);
                                String _id=id.getText().toString();
                                BarterDatabaseHelper barterDatabaseHelper = new BarterDatabaseHelper(context);
                                SQLiteDatabase db = barterDatabaseHelper.getWritableDatabase();
                                db.delete("TRANSACTIONS","_id = "+_id,null);
                                try {
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    //this line below gives you the animation and also updates the
                                    //list items after the deleted item
                                    notifyItemRangeChanged(position, getItemCount());
                                } catch (Exception e) {      e.printStackTrace();  }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // dismiss dialog
                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.ic_baseline_delete_18px)
                        .show();
            }
        });


        favorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView id=(TextView) holder.cardView.findViewById(R.id.card_id);
                String _id=id.getText().toString();
                BarterDatabaseHelper barterDatabaseHelper = new BarterDatabaseHelper(context);
                SQLiteDatabase db = barterDatabaseHelper.getWritableDatabase();
                if (isFav){
                    String strSQL = "UPDATE TRANSACTIONS SET FAVORITE = 0 WHERE _id = "+ _id;
                    db.execSQL(strSQL);
                    Log.d(TAG, "what position are we in "+position);
                    ImageButton fav = (ImageButton)v;
                    fav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24px ));
                    list.get(position).setFavorite("0");
                    isFav=false;

                }
                else {
                    String strSQL = "UPDATE TRANSACTIONS SET FAVORITE = 1 WHERE _id = "+ _id;
                    db.execSQL(strSQL);
                    ImageButton fav = (ImageButton)v;
                    fav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_favorite_filled_24px ));
                    transactions.setFavorite("1");
                    list.get(position).setFavorite("1");
                    isFav=true;
                }
            }
        });


        remind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String id = ((TextView) holder.cardView.findViewById(R.id.card_id)).getText().toString();
                String nameofitem = ((TextView) holder.cardView.findViewById(R.id.card_item_name)).getText().toString();
                String itemDescription =  ((TextView) holder.cardView.findViewById(R.id.card_description)).getText().toString();
                getDateTime(id,nameofitem,itemDescription);
            }
        });
    }

    private void getDateTime(final String id, final String nameofitem, final String itemDescription){
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        m_day = dayOfMonth;
                        m_month=(monthOfYear);
                        m_year=year;
                        Log.d(TAG, "onDateSet: "+ m_day+m_month+m_year);
                        timePicker(id, nameofitem, itemDescription);
                    }
                }, m_year, m_month, m_day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        ConstraintLayout constraintLayout=(ConstraintLayout) View.inflate(context,R.layout.picker_dialog_title_layout,null);
        TextView title = (TextView)constraintLayout.findViewById(R.id.picker_dialog_header);
        title.setText("Reminder");
        datePickerDialog.setCustomTitle(constraintLayout);
        datePickerDialog.show();

    }

    private void timePicker(final String id, final String nameofitem,final String itemdescription){
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d(TAG, "onTimeSet: " + hourOfDay+minute);
                        Calendar c= Calendar.getInstance();
                        c.set(m_year,m_month,m_day,hourOfDay,minute);
                        int dateComp =c.compareTo(Calendar.getInstance());
                        if(dateComp==0){}
                        else if (dateComp>0){
                            scheduleNotification(getNotification(nameofitem,itemdescription,id),c,id);
                        }
                        else {
                            String s= "Chosen date is in the past. Kindly choose correct a date in future to continue.";
                            Toast.makeText(context,s,Toast.LENGTH_LONG).show();
                        }
                    }

                }, m_hour, m_min, false);
        /*timePickerDialog.setTitle("Reminder time");*/
        ConstraintLayout constraintLayout=(ConstraintLayout) View.inflate(context,R.layout.picker_dialog_title_layout,null);
        TextView title = (TextView)constraintLayout.findViewById(R.id.picker_dialog_header);
        title.setText("Reminder");
        timePickerDialog.setCustomTitle(constraintLayout);
        timePickerDialog.show();
    }

    private void scheduleNotification(Notification notification,Calendar c,String id){
        // intent creation
        Intent notificationIntent = new Intent(context,NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID,id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION,notification);
        //Pending Intent that will stay with Android System till the time Specified in AlarmManager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,192837,notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //alarmmanger to send Broadcast to Android system at a specific time
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        c.set(Calendar.SECOND,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
    }

    private Notification getNotification(String title, String description, String id ){
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(description);
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        //intent for the activity that will be called on notification click
        Intent in = new Intent(context, TransactionDetailActivity.class);
        in.putExtra("ID",id.toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, in, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        return builder.build();
    }

    @Override
    public int getItemCount(){
        return list.size();
    }
}

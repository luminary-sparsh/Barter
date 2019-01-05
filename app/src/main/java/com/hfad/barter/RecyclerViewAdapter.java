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
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
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

            if (fragment instanceof LentFragment){
                currentPosition = 1;
            }
            if (fragment instanceof BorrowFragment){
                currentPosition = 2;
            }
        } catch (ClassCastException e) {
            Log.d(TAG, "Can't get the fragment manager with this");
        }
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        final int pos = position;
        final Transactions transactions = list.get(position);

        TextView carditemname = (TextView) holder.cardView.findViewById(R.id.card_item_name);
        carditemname.setText(transactions.getItemname());

        TextView cardlbname = (TextView) holder.cardView.findViewById(R.id.card_lb_name);
        cardlbname.setText(transactions.getLBname());

        TextView carddescription = (TextView) holder.cardView.findViewById(R.id.card_description);
        carddescription.setText(transactions.getDescription());

        TextView carddatetime = (TextView) holder.cardView.findViewById(R.id.card_date_time);
        carddatetime.setText(transactions.getDatetime().trim());

        final TextView id=(TextView) holder.cardView.findViewById(R.id.card_id);
        id.setText(transactions.getID());

        TextView cardlorb = (TextView) holder.cardView.findViewById(R.id.lorb);
        if(currentPosition==1){            cardlorb.setText("Borrower");
        }else cardlorb.setText("Lender:");

        /*holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,TransactionDetailActivity.class);
                intent.putExtra("ID",id.toString());
                context.startActivity(intent);
            }
        });*/

        ImageButton delete = (ImageButton) holder.cardView.findViewById(R.id.card_delete_button);
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
        });

        ImageButton favorite = (ImageButton) holder.cardView.findViewById(R.id.card_favorite_button);
        favorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView id=(TextView) holder.cardView.findViewById(R.id.card_id);
                String _id=id.getText().toString();
                BarterDatabaseHelper barterDatabaseHelper = new BarterDatabaseHelper(context);
                SQLiteDatabase db = barterDatabaseHelper.getWritableDatabase();
                String strSQL = "UPDATE TRANSACTIONS SET FAVORITE = 1 WHERE _id = "+ _id;
                db.execSQL(strSQL);
            }
        });

        ImageButton remind = (ImageButton) holder.cardView.findViewById(R.id.card_reminder_button);
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

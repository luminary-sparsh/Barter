package com.hfad.barter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DrawerListAdapter extends ArrayAdapter{

    private String[] titles = null;
    private int selectedPosition = 0;
    private TextView tvTitle;

    public DrawerListAdapter(@NonNull Context context, int resource, String[] titles, int selectedPosition) {
        super(context, resource);
        this.titles = titles;
        this.selectedPosition = selectedPosition;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = View.inflate(getContext(), R.layout.drawer_listview, null);
        tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
        tvTitle.setText(titles[position]);
        if (convertView != null){
            if (position == selectedPosition) {
                convertView.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
            } else {
                //convertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.white));
            }
        }
        return convertView;
        /*return super.getView(position, convertView, parent);*/
    }

    public void setSelectedPosition(int selectedPosition){
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }
}

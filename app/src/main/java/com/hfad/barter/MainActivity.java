package com.hfad.barter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends AppCompatActivity {

    ShareActionProvider shareActionProvider;
    private String titles[];
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    public int currentPosition = 0;
    private ActionBarDrawerToggle drawerToggle;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titles = getResources().getStringArray(R.array.titles);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final DrawerListAdapter drawerListAdapter = new DrawerListAdapter(this, android.R.layout.simple_list_item_1, titles,currentPosition);
        drawerList.setAdapter(drawerListAdapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());


        //create actionbartoggle
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,
                R.string.open_drawer,R.string.closed_drawer){
            //called when the drawer is completely closed
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            //called when the drawer is completely opened
            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //Display the correct fragment
        if(savedInstanceState!=null){
            currentPosition = savedInstanceState.getInt("position");
            setActionBarTitle(currentPosition);
            drawerList.setItemChecked(currentPosition,true);
            drawerListAdapter.setSelectedPosition(currentPosition);
        }else{
            selectItem(0);
        }

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        FragmentManager fragMan = getFragmentManager();
                        Fragment fragment = fragMan.findFragmentByTag("visible_fragment");
                        if (fragment instanceof TopFragment){
                            currentPosition = 0;
                        }
                        if (fragment instanceof LentFragment){
                            currentPosition = 1;
                        }
                        if (fragment instanceof BorrowFragment){
                            currentPosition = 2;
                        }
                        setActionBarTitle(currentPosition);
                        drawerList.setItemChecked(currentPosition,true);
                        drawerListAdapter.setSelectedPosition(currentPosition);
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchitem = menu.findItem(R.id.search_icon);
        final MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setIntent("This is an example text");

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_icon).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //Hiding magIcon
        int magId = getResources().getIdentifier("android:id/search_mag_icon",null,null);
        ImageView magimage = (ImageView) searchView.findViewById(magId);
        Log.d(TAG, "value of magId"+ magId);
        if(magimage==null){
            Log.d(TAG, "magimage is null");
        }
        magimage.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        // to hide all the menu items when searchview is expanded
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemsVisibility(menu, searchitem, false);
            }
        });


        //call the queryListener directly on the searchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.setIconified(true);
                searchView.clearFocus();
                (menu.findItem(R.id.search_icon)).collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        //for calling listener and method which is called when search item is closed.

        /*searchitem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Log.d(TAG, "onMenuItemActionExpand: ");
                searchView.setIconified(false);
                searchView.
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                setItemsVisibility(menu,searchitem,true);
                return false;
            }
        });*/

        // Do not iconify the widget;expand it by default
        searchView.setIconifiedByDefault(false);
        return super.onCreateOptionsMenu(menu);
    }



    //changing Visibilty of menuItems on searchView open and close
    private void setItemsVisibility(Menu menu,MenuItem exception, boolean visible){
        for(int i=0; i<menu.size();i++){
            MenuItem item = menu.getItem(i);
            if(item!=exception){item.setVisible(visible);}
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (drawerToggle.onOptionsItemSelected(menuItem)){
            return true;
        }
        switch (menuItem.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.add_new_Transaction:
                Intent intent = new Intent(this, TransactionActivity.class);
                startActivity(intent);
                return true;
            case R.id.search_icon:
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        //sync the state of actionBar Toggle
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    };

    private void selectItem(int position) {
        currentPosition = position;
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = new LentFragment();
                break;
            case 2:
                fragment = new BorrowFragment();
                break;
            default:
                fragment = new TopFragment();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment,"visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        setActionBarTitle(position);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("position",currentPosition);
    }

    private void setActionBarTitle(int position) {
        String title;
        if (position == 0) {
            title = getResources().getString(R.string.app_name);
        } else {
            title = titles[position];
        }
        getSupportActionBar().setTitle(title);
    }

    //called whenever invalidateOptionsMenu() is called
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        //if the drawer is open, hide action items related to content View
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    //todo: remove after testing
    int getThemeId() {
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //todo: remove after testing
    public String getThemeName()
    {
        PackageInfo packageInfo;
        try
        {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            int themeResId = packageInfo.applicationInfo.theme;
            return getResources().getResourceEntryName(themeResId);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return null;
        }
    }
}

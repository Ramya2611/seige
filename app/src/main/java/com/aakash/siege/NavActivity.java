package com.aakash.siege;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class NavActivity extends saveSP
        implements FragShelterList.OnFragmentInteractionListener,NavigationView.OnNavigationItemSelectedListener,FragmyShelter.OnFragmentInteractionListener,FragaddShelter.OnFragmentInteractionListener,FragallocateShelter.OnFragmentInteractionListener {

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menuNav = navigationView.getMenu();
        MenuItem nav_item = menuNav.findItem(R.id.nav_logout);
        MenuItem nav_item1 = menuNav.findItem(R.id.nav_addshelter);
        MenuItem nav_item2 = menuNav.findItem(R.id.nav_myshelter);
        String nid = saveSP.getUserName(this);
        if(nid.length() == 0) {
            nav_item.setEnabled(false);
            nav_item1.setEnabled(false);
            nav_item2.setEnabled(false);
        }
        navigationView.setNavigationItemSelectedListener(this);
        if(isOnline()){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.navframe, new FragShelterList());
        ft.commit();}
        else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.navframe, new FragallocateShelter());
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myshelter) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.replace(R.id.navframe,new FragmyShelter());
            ft.commit();
        } else if (id == R.id.nav_getshelter) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(new FragShelterList());
            ft.replace(R.id.navframe,new FragallocateShelter());
            ft.commit();
        } else if (id == R.id.nav_shelters) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.navframe,new FragShelterList());
            ft.commit();

        } else if (id == R.id.nav_logout) {
            saveSP.setUserName(this,"");
            Intent intent = new Intent(this,MainActivity.class);
            finish();
            startActivity(intent);

        }else if(id == R.id.nav_addshelter) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(new FragShelterList());
            ft.replace(R.id.navframe,new FragaddShelter());
            ft.commit();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

package com.innovativeproposals.inventorypokus2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.innovativeproposals.inventorypokus2.Budova.ListBudova;
import com.innovativeproposals.inventorypokus2.Info.InfoActivity;
import com.innovativeproposals.inventorypokus2.Komunikacia.ExportDatabase;
import com.innovativeproposals.inventorypokus2.Komunikacia.ImportDatabase;
import com.innovativeproposals.inventorypokus2.Models.DbUtils;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //private MajetokAdapter adapter = null;
    Intent intent;
    TextView diviziaId;
    TextView diviziaET;

    DbUtils dm = new DbUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // zakazanie zmeny orientacie
        // https://stackoverflow.com/questions/20021803/disable-and-enable-orientation-changes-in-an-activity-in-android-programatically
        // napln zoznamy - zodpovedne osoby, atd

        if(isDatabaseFile()) {
            naplnListy(); //xx
        } else {
           // Intent intent = new Intent(this, ImportDatabase.class);
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
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

        if (id == R.id.nav_inventory) {

            if(isDatabaseFile()) {
                Intent intent = new Intent(this, ListBudova.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, ImportDatabase.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_info) {
            if(isDatabaseFile()) {
                Intent intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
            } else {
               Intent intent = new Intent(this, ImportDatabase.class);
                startActivity(intent);
        }

        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            // import
            Intent intent = new Intent(this, ImportDatabase.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {
            // export
            if(isDatabaseFile()) {
                Intent intent = new Intent(this, ExportDatabase.class);
                startActivity(intent);
            } else {
               Intent intent = new Intent(this, ImportDatabase.class);
               startActivity(intent);
        }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/PieChartActivity.java#L272
    private void ImplementChart() {

/*        PackageManager m = getPackageManager();
        String saveDir = getPackageName();
        PackageInfo p = null;
        try {
            p = m.getPackageInfo(saveDir,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String sourceFileUri = p.applicationInfo.dataDir + "/databases/" + Constants.FILE_DATABASE; */

      //  int celkomInventarov = 0;
      //  int spracovane = 0;

        PieChart chart = (PieChart) findViewById(R.id.chart);
        chart.setDescription(null);


        if(isDatabaseFile()) {

            ArrayList<PieEntry> entries = new ArrayList();

            int celkomInventarov = dm.dajCelkovyPocetInventara("");
            int spracovane = dm.dajPocetSpracovanehoInventara();

            entries.add(new PieEntry(spracovane, getString(R.string.chart_processed))); // hodnoty dorob dynamicke
            entries.add(new PieEntry(celkomInventarov, getString(R.string.chart_opened)));

            PieDataSet dataSet = new PieDataSet(entries, getString(R.string.chart_items));
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            PieData data = new PieData(dataSet);
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);
            chart.setData(data);
            chart.animateY(1000);
            chart.invalidate();
        } else {

            chart.setVisibility(View.INVISIBLE); // VISIBLE

            Intent intent = new Intent(this, ImportDatabase.class);
            startActivity(intent);
        }
    }

    private boolean isDatabaseFile() {

        //   IO_Utilities util = new IO_Utilities(getApplicationContext());
        // boolean isFile = false;

        boolean jeTam = false;
        PackageManager m = getPackageManager();
        String saveDir = getPackageName();
        PackageInfo p = null;
        try {
            p = m.getPackageInfo(saveDir,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String sourceFileUri = p.applicationInfo.dataDir + "/databases/" + Constants.FILE_DATABASE;

        /*   if(util.fileExist(sourceFileUri)==true)
            isFile=true; */

        File file = new File(sourceFileUri);
        if(file.exists())
            jeTam = true;

        else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(R.string.database_file_didnt_find);
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

  /*          builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }); */

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }


        return jeTam;
    }

    void naplnListy() {

        Constants.spinnerZodpovedneOsoby = dm.dajZodpovedneOsoby();
        Constants.spinnerListTypyMajetku = dm.dajTypyMajetku();

    }

}

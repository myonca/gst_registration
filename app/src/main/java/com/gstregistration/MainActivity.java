package com.gstregistration;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.navigation.NavigationView;
import com.gstregistration.ui.home.HomeFragment;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private AdView adView,adView1;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("GST Registration App");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        adView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();


   //     adView.loadAd(adRequest);
        adView1 = findViewById(R.id.adView1);
        adView1.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                adView1.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }
        });
        adView1.loadAd(new AdRequest.Builder().build());


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                String urlToOpen = "";

                switch (id) {
                    case R.id.nav_dashboard:
                        Fragment fragment = new HomeFragment();
                        FragmentManager manager = getSupportFragmentManager();
                        manager.beginTransaction()
                                .add(R.id.nav_host_fragment, fragment, fragment.getTag())
                                .addToBackStack(null)
                                .commit();
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.gstRegistration:
                        urlToOpen = "https://app.myonlineca.org/gstapp-registration/";
                        break;
                    case R.id.gstRegistrationCharges:
                        urlToOpen = "https://app.myonlineca.org/gstapp-ammendment/";
                        break;
                    case R.id.gstReturnFiling:
                        urlToOpen = "https://app.myonlineca.org/gstapp-return/";
                        break;
                    case R.id.support:
                        urlToOpen = "https://app.myonlineca.org/gstapp-support-2/";
                        break;
                    case R.id.gstRateFinder:
                        urlToOpen = "https://cbic-gst.gov.in/gst-goods-services-rates.html";
                        break;
                    case R.id.gstVerification:
                        urlToOpen = "https://services.gst.gov.in/services/searchtp";
                        break;
                    case R.id.gstSearch:
                        urlToOpen = "https://services.gst.gov.in/services/quicklinks/searchtxp";
                        break;
                    case R.id.gstCalculator:
                        urlToOpen = "https://app.myonlineca.org/gstapp-calculator/";
                        break;
                    case R.id.otherServices:
                        urlToOpen = "https://app.myonlineca.org/myoncaapp-services/";

                        break;




                }

                if (!urlToOpen.equals("")) {
                    Fragment fragment = new webViewFragment(urlToOpen);
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction()
                            .add(R.id.nav_host_fragment, fragment, fragment.getTag())
                            .addToBackStack(null)
                            .commit();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("Are you sure you want to back?")
                    .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }

                    }).setNeutralButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }

            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}

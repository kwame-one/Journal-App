package journal.kwame.com.journal.Activity;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import journal.kwame.com.journal.Database.AppExecutors;
import journal.kwame.com.journal.Database.JournalDatabase;
import journal.kwame.com.journal.Fragment.BinFragment;
import journal.kwame.com.journal.Fragment.HomeFragment;
import journal.kwame.com.journal.Fragment.SettingsFragment;
import journal.kwame.com.journal.R;
import journal.kwame.com.journal.Utils.UserPreference;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private int navId;
    private UserPreference userPreference;
    private JournalDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userPreference = new UserPreference(this);
        if(!userPreference.isUserLoggedIn()) {
            userPreference.redirectUserToLoginActivity();
            finish();
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = JournalDatabase.getInstance(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navId = R.id.nav_home;
        displayView(navId);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        navId = item.getItemId();
        displayView(navId);


        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else if (navId == R.id.nav_home) {
//            super.onBackPressed();
//        } else {
//            displayView(R.id.nav_home);
//            navigationView.getMenu().getItem(0).setChecked(true);
//        }
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    database.journalDao().clearTableOnSignOut();
                }
            });
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        userPreference.clearUserDetails();
        finish();
    }

    private void displayView(int id) {
        Fragment fragment = null;

        switch (id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_bin:
                fragment = new BinFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;

        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main, fragment).commit();
        }
    }


}

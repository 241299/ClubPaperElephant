package ru.bibliowiki.litclubbs;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import ru.bibliowiki.litclubbs.util.DownloadTask;
import ru.bibliowiki.litclubbs.util.RoboErrorReporter;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String currentUrlLoaded;
    private int currentTypeLoaded;
    private String currentSeparatorLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RoboErrorReporter.bindReporter(this);
        currentUrlLoaded = getString(R.string.siteUrlHome);
        currentTypeLoaded = DownloadTask.TYPE_DEFAULT;
        currentSeparatorLoaded = DownloadTask.SEPARATOR_DEFAULT;


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            final Context context = this;
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    (new DownloadTask(context, currentUrlLoaded, currentTypeLoaded, currentSeparatorLoaded)).execute();
//                    Snackbar.make(view, getString(R.string.refreshStarted), Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    fab.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_center));
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Ссылки при нажатии на элементы navigation view
        if (id == R.id.nav_home) {
            (new DownloadTask(this, getResources().getString(R.string.siteUrlHome), DownloadTask.TYPE_DEFAULT, DownloadTask.SEPARATOR_DEFAULT)).execute();
        } else if (id == R.id.nav_publications) {
            (new DownloadTask(this, getResources().getString(R.string.siteUrlPublications), DownloadTask.TYPE_PUBLICATIONS, DownloadTask.SEPARATOR_PUBLICATIONS)).execute();
        } else if (id == R.id.nav_blog) {
            (new DownloadTask(this, getResources().getString(R.string.siteUrlBlog), DownloadTask.TYPE_DEFAULT, DownloadTask.SEPARATOR_DEFAULT)).execute();
        } else if (id == R.id.nav_writers) {
            (new DownloadTask(this, getResources().getString(R.string.siteUrlWriters), DownloadTask.TYPE_DEFAULT, DownloadTask.SEPARATOR_WRITERS)).execute();
        } else if (id == R.id.nav_journal) {
            (new DownloadTask(this, getResources().getString(R.string.siteUrlJournal), DownloadTask.TYPE_DEFAULT, DownloadTask.SEPARATOR_DEFAULT)).execute();
        } else if (id == R.id.nav_artists) {
            (new DownloadTask(this, getResources().getString(R.string.siteUrlArtists), DownloadTask.TYPE_DEFAULT, DownloadTask.SEPARATOR_DEFAULT)).execute();
        } else if (id == R.id.nav_photo) {
            (new DownloadTask(this, getResources().getString(R.string.siteUrlPhoto), DownloadTask.TYPE_DEFAULT, DownloadTask.SEPARATOR_DEFAULT)).execute();
        } else if (id == R.id.nav_help) {
            (new DownloadTask(this, getResources().getString(R.string.siteUrlHelp), DownloadTask.TYPE_DEFAULT, DownloadTask.SEPARATOR_DEFAULT)).execute();
        } else if (id == R.id.nav_add) {
            (new DownloadTask(this, getResources().getString(R.string.siteUrlAddPublication), DownloadTask.TYPE_DEFAULT, DownloadTask.SEPARATOR_DEFAULT)).execute();
        } else if (id == R.id.nav_settings) {

        }

        if ((toolbar != null) && !(id == R.id.nav_settings || id == R.id.nav_share || id == R.id.nav_send_report)) {
            toolbar.setTitle(item.getTitle());
            Toast.makeText(this, getText(R.string.refreshStarted), Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void setCurrentPageLoaded(String url, int typeOfPage, String separator){
        currentUrlLoaded = url;
        currentTypeLoaded = typeOfPage;
        currentSeparatorLoaded = separator;
    }
}

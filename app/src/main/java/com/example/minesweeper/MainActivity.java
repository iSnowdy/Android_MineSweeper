package com.example.minesweeper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private Fragment baseFragment; // Home fragment
    private Fragment selectedFragment; // Fragment to be displayed


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Base Fragment Config
        this.baseFragment = new HomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, this.baseFragment, this.baseFragment.getClass().getSimpleName())
                .addToBackStack(this.baseFragment.getClass().getSimpleName())
                .commit();

        // Toolbar Config
        createToolbar();

        // Bottom Navigation Bar Config
        createBottomNavigationBar(savedInstanceState);





    }

    // Toolbar configuration section
    private void createToolbar() {
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        setToolbarTitle(getCurrentFragmentName());
        setToolbarSubTitle();
        setToolbarNavigationIcon();
        setToolbarNavigationIconOnClickListener();
    }

    private void setToolbarTitle(final String title) {
        this.toolbar.setTitle(title);
        this.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
    }
    // Extracts the current fragment name so it can be displayed in the Toolbar
    private String getCurrentFragmentName() {
        Fragment tempFragment = null;
        // Retrieves all fragments that are currently in the FragmentManager
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        if (fragments != null && !fragments.isEmpty()) {
            for (int i = fragments.size() - 1; i >= 0; i--) {
                tempFragment = fragments.get(i);
                if (tempFragment != null && tempFragment.isVisible()) {
                    break;
                }
            }
        }
        return tempFragment  != null ? tempFragment.getTag() : null;
    }
    // TODO: Consider removing this method
    private void setToolbarSubTitle() {
        this.toolbar.setSubtitle("Minesweeper");
        this.toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void setToolbarNavigationIcon() {
        Drawable originalDrawable = ContextCompat.getDrawable(this, R.drawable.og_bomb_svg);
        this.toolbar.setNavigationIcon(originalDrawable);
        // Accessibility for navigation icon
        this.toolbar.setNavigationContentDescription("Bomb Navigation Icon in the Toolbar");
    }

    // When Home Icon is clicked (the toolbar bomb logo), redirects to Home Fragment
    private void setToolbarNavigationIconOnClickListener() {
        this.toolbar.setNavigationOnClickListener(v -> {
            this.selectedFragment = new HomeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, this.selectedFragment)
                    .commit();
        });
    }
    // Inflate the Toolbar menu and give functionality to the items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.searchButton) {
            Toast.makeText(this, "Search Button Clicked", Toast.LENGTH_SHORT).show();
            // Search Button Logic
        } else if (id == R.id.favouriteButton) {
            Toast.makeText(this, "Favourite Button Clicked", Toast.LENGTH_SHORT).show();
            // Favourite Button Logic
        }
        return super.onOptionsItemSelected(item);
    }


    // Bottom Navigation Bar Configuration
    private void createBottomNavigationBar(Bundle savedInstanceState) {
        this.bottomNavigationView = findViewById(R.id.bottomNavigationBar);

        setHomeFragmentToBottomNavigationBar(savedInstanceState);
        setBottomNavigationBarListener();
    }

    private void setHomeFragmentToBottomNavigationBar(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    private void setBottomNavigationBarListener() {
        this.bottomNavigationView.setOnItemSelectedListener(item -> {
            this.selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.game_nav_item) {
                this.selectedFragment = new GameFragment();
            } else if (id == R.id.settings_nav_item) {
                this.selectedFragment = new SettingsFragment();
            } else if (id == R.id.stats_nav_item) {
                this.selectedFragment = new StatsFragment();
            }
            // Load the selected fragment
            // and passes the name to the toolbar
            if (this.selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, this.selectedFragment, this.selectedFragment.getClass().getSimpleName())
                        .addToBackStack(this.selectedFragment.getClass().getSimpleName()) // Set tag for backstack
                        .commit();
                setToolbarTitle(getCurrentFragmentName());
            }
            return true;
        });
    }



}
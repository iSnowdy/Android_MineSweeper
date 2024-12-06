package com.example.minesweeper;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.minesweeper.Fragments.GameFragment;
import com.example.minesweeper.Fragments.HelpFragment;
import com.example.minesweeper.Fragments.HomeFragment;
import com.example.minesweeper.Fragments.SettingsFragment;
import com.example.minesweeper.Fragments.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String username;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    // Fragments
    private HomeFragment homeFragment;
    private GameFragment gameFragment;
    private SettingsFragment settingsFragment;
    private StatsFragment statsFragment;
    private HelpFragment helpFragment;
    private Fragment selectedFragment; // Fragment to be displayed

    private int toolbarHeight, navbarHeight, statusBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.username = getIntent().getStringExtra(SharedPreferences_Keys.USERNAME.toString());

        createFragments();

        // Base Fragment Config
        if (savedInstanceState == null) loadFragment(this.homeFragment);

        // Toolbar Config
        createToolbar();

        // Bottom Navigation Bar Config
        createBottomNavigationBar(savedInstanceState);

        this.toolbarHeight = this.toolbar.getMinimumHeight();
        this.navbarHeight = this.bottomNavigationView.getMinimumHeight();

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        this.statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        System.out.println("Resource id from the Status Bar is: " + resourceId);
        System.out.println("Status Bar Height is: " + statusBarHeight);

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    private void createFragments() {
        this.homeFragment = new HomeFragment();
        this.gameFragment = new GameFragment();
        this.settingsFragment = new SettingsFragment();
        this.statsFragment = new StatsFragment();
        this.helpFragment = new HelpFragment();
    }

    // Toolbar configuration section
    private void createToolbar() {
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);

        setToolbarTitle(getCurrentFragmentName());
        setToolbarNavigationIcon();

        this.toolbar.setNavigationOnClickListener(v -> navigateHomeFragment());
    }

    private void setToolbarTitle(final String title) {
        this.toolbar.setTitle(title);
        this.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
    }
    // Extracts the current fragment name so it can be displayed in the Toolbar
    private String getCurrentFragmentName() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment != null && fragment.isVisible()) {
                return fragment.getClass().getSimpleName();
            }
        }
        return "Minesweeper";
    }

    private void setToolbarNavigationIcon() {
        Drawable originalDrawable = ContextCompat.getDrawable(this, R.drawable.og_bomb_svg);
        this.toolbar.setNavigationIcon(originalDrawable);
        // Accessibility for navigation icon
        this.toolbar.setNavigationContentDescription("Bomb Navigation Icon in the Toolbar");
    }

    // When Home Icon is clicked (the toolbar bomb logo), redirects to Home Fragment
    private void navigateHomeFragment() {
        loadFragment(new HomeFragment());
        setToolbarTitle(getCurrentFragmentName());
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

        if (id == R.id.helpFragment) {
            Toast.makeText(this, "Help Button Clicked", Toast.LENGTH_SHORT).show();
            showHelpDialog();
            // TODO: Help Fragment Redirection
        } else if (id == R.id.loginButton) {
            Toast.makeText(this, "Logout Button Clicked", Toast.LENGTH_SHORT).show();
            // TODO: Logout Logic
            // SharedPreferenes and redirect to login class
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        // TODO: Implement a redirection to the help page? Like how to play Minesweeper
        if (this.selectedFragment != null) {
            loadFragment(new HelpFragment());
            setToolbarTitle(getCurrentFragmentName());
        }
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
            int id = item.getItemId();
            // TODO: Remove New Fragment Logic from the NavBar Listener
            if (id == R.id.game_nav_item) {
                this.selectedFragment = this.gameFragment;
                // TODO: Extract to a method. Consider making it dynamic
                Bundle gameFragmentBundle = new Bundle();
                putHeightInformationIntoBundle(gameFragmentBundle);
                this.selectedFragment.setArguments(gameFragmentBundle);
            } else if (id == R.id.settings_nav_item) {
                this.selectedFragment = this.settingsFragment;
            } else if (id == R.id.stats_nav_item) {
                this.selectedFragment = this.statsFragment;
            }
            // Load the selected fragment
            // and passes the name to the toolbar
            if (this.selectedFragment != null) {
                loadFragment(selectedFragment);
                setToolbarTitle(getCurrentFragmentName());
            }
            return true;
        });
    }

    private void putHeightInformationIntoBundle(Bundle bundle) {
        bundle.putInt("toolbarHeight", toolbarHeight);
        bundle.putInt("navbarHeight", navbarHeight);
        bundle.putInt("statusBarHeight", statusBarHeight);
    }
}
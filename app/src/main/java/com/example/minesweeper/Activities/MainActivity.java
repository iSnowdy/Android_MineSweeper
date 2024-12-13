package com.example.minesweeper.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.minesweeper.Fragments.GameFragment;
import com.example.minesweeper.Fragments.HelpFragment;
import com.example.minesweeper.Fragments.HomeFragment;
import com.example.minesweeper.Fragments.SettingsFragment;
import com.example.minesweeper.Fragments.StatsFragment;
import com.example.minesweeper.JavaClasses.Login;
import com.example.minesweeper.R;
import com.example.minesweeper.JavaClasses.SharedPreferences_Keys;
import com.example.minesweeper.JavaClasses.Utils.ToastUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String username;
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

        username = getIntent().getStringExtra(SharedPreferences_Keys.USERNAME.toString());
        System.out.println("USERNAME IN MAIN ACTIVITY (CONSTRUCTOR): " + username);




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
    }

    // If the fragment has already been initialized, then instead of creating it anew (and
    // deleting progress in the Game), just show it
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (getSupportFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName()) == null) {
            transaction.add(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        } else { // If the fragment is already on the stack, just show it
            transaction.show(fragment);
        }
        // Hide the other fragments
        for (Fragment frag : getSupportFragmentManager().getFragments()) {
            if (frag != null && frag != fragment) {
                transaction.hide(frag);
            }
        }
        transaction.commit();
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

    @Override
    public void onResume() {
        super.onResume();
        updateToolBarWithFragmentName();
    }

    public void updateToolBarWithFragmentName() { // Public to access it from fragments
        String currentFragmentName = getCurrentFragmentName();
        this.toolbar.setTitle(currentFragmentName);
    }


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
        Drawable originalDrawable = ContextCompat.getDrawable(this, R.drawable.bomb_svg);
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
            ToastUtil.createToast(this, "Help Button Clicked");
            showHelpDialog();
        } else if (id == R.id.logout) {
            ToastUtil.createToast(this, "Logout Button Clicked");
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            // TODO: Logout functionality?
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        if (this.selectedFragment != null) {
            this.selectedFragment = helpFragment;
            loadFragment(selectedFragment);
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

            if (id == R.id.game_nav_item) {
                this.selectedFragment = this.gameFragment;
                Bundle gameFragmentBundle = new Bundle();
                putRequiredGameFragmentInformationIntoBundle(gameFragmentBundle);
                this.selectedFragment.setArguments(gameFragmentBundle);
            } else if (id == R.id.settings_nav_item) {
                this.selectedFragment = this.settingsFragment;
            } else if (id == R.id.stats_nav_item) {
                // Only for Stats Fragment create it completely new each time
                // it is called
                System.out.println("Loading stats fragment");
                this.selectedFragment = new StatsFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, this.selectedFragment)
                        .commit();
                return true;
            }
            // Load the selected fragment
            // and passes the name to the toolbar
            if (this.selectedFragment != null) {
                System.out.println("Loading fragment...");
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void putRequiredGameFragmentInformationIntoBundle(Bundle bundle) {
        bundle.putInt("toolbarHeight", toolbarHeight);
        bundle.putInt("navbarHeight", navbarHeight);
        bundle.putInt("statusBarHeight", statusBarHeight);
        bundle.putString("username", username);
    }
}
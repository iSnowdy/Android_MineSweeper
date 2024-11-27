package com.example.minesweeper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class SettingsFragment extends Fragment {
    private Spinner difficultySpinner;
    private Spinner themeSpinner;
    private RadioGroup radioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toast.makeText(getActivity(), "Settings Fragment", Toast.LENGTH_SHORT).show();

        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Spinners Configuration
        createDifficultySpinner(settingsView);
        setDifficultySpinnerOnClick();
        createThemeSpinner(settingsView);
        setThemeSpinnerOnClick();

        this.radioGroup = settingsView.findViewById(R.id.themeRadioGroup);

        return settingsView;
    }


    // Difficulty Spinner / Configuration
    private void createDifficultySpinner(View view) {
        this.difficultySpinner = view.findViewById(R.id.difficultySpinner);

        var adapter = getDifficultySpinnerAdapter();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        difficultySpinner.setAdapter(adapter);
    }

    private ArrayAdapter<CharSequence> getDifficultySpinnerAdapter() {
        return ArrayAdapter.createFromResource(getContext(), R.array.difficultySpinner, android.R.layout.simple_spinner_item);
    }

    private void setDifficultySpinnerOnClick() {
        this.difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "You have selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(getContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Theme Spinner Creation / Configuration
    private void createThemeSpinner(View view) {
        this.themeSpinner = view.findViewById(R.id.themeSpinner);
        var adapter = getThemeSpinnerAdapter();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        themeSpinner.setAdapter(adapter);
    }

    private ArrayAdapter<CharSequence> getThemeSpinnerAdapter() {
        return ArrayAdapter.createFromResource(getContext(), R.array.themeSpinner, android.R.layout.simple_spinner_item);
    }

    private void setThemeSpinnerOnClick() {
        this.themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "You have selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(getContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // RadioGroup Listener for Theme Background
    private void radioGroupListener() {

    }




}
package com.example.minesweeper.Fragments;

/*
    Soundtracks:
    - Default: https://www.youtube.com/watch?v=NmCCQxVBfyM
    - Epic: https://www.youtube.com/watch?v=yQ2LGEqyvdk
    - Chill: https://www.youtube.com/watch?v=y_ezfsJ4-Lw


 */

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;


import androidx.fragment.app.Fragment;

import com.example.minesweeper.JavaClasses.Difficulty;
import com.example.minesweeper.Activities.MainActivity;
import com.example.minesweeper.R;
import com.example.minesweeper.JavaClasses.SharedPreferences_Keys;
import com.example.minesweeper.JavaClasses.Utils.SharedPreferencesUtil;
import com.example.minesweeper.JavaClasses.Utils.ToastUtil;
import com.google.android.material.switchmaterial.SwitchMaterial;


public class SettingsFragment extends Fragment {
    private Spinner difficultySpinner;
    private SwitchMaterial musicSwitch; // Kind of upgraded version of Switch
    private RadioGroup soundtrackRadioGroup;
    private Difficulty chosenDifficulty;
    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ToastUtil.createToast(getActivity(), "Settings Fragment");

        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);
        loadSettingsViews(settingsView);

        // Difficulty Spinner Configuration
        createDifficultySpinner();
        setDifficultySpinnerOnClick();

        // Switch Music Configuration
        setSwitchMusicOnClick();

        // RadioGroup Soundtrack Configuration
        setSoundtrackRadioGroup();

        return settingsView;
    }

    private void loadSettingsViews(View settingsView) {
        this.difficultySpinner = settingsView.findViewById(R.id.difficultySpinner);
        this.musicSwitch = settingsView.findViewById(R.id.musicSwitch);
        this.soundtrackRadioGroup = settingsView.findViewById(R.id.soundtrackRadioGroup);
    }


    // Difficulty Spinner / Configuration
    private void createDifficultySpinner() {
        var adapter = getDifficultySpinnerAdapter();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.difficultySpinner.setAdapter(adapter);
        setPreviousDifficultyToSpinner();
    }

    private void setPreviousDifficultyToSpinner() {
        String difficultyFromSharedPreferences = SharedPreferencesUtil.getSetting(getContext(), SharedPreferences_Keys.DIFFICULTY, Difficulty.EASY.toString());
        this.chosenDifficulty = Difficulty.valueOf(difficultyFromSharedPreferences);
        this.difficultySpinner.setSelection(this.chosenDifficulty.ordinal());
    }

    private ArrayAdapter<CharSequence> getDifficultySpinnerAdapter() {
        return ArrayAdapter.createFromResource(getContext(), R.array.difficultySpinner, android.R.layout.simple_spinner_item);
    }

    private void setDifficultySpinnerOnClick() {
        this.difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String difficultyFromSpinner = parentView.getItemAtPosition(position).toString();

                ToastUtil.createToast(getActivity(), "You have selected: " + difficultyFromSpinner + " difficulty");

                withdrawDifficultySettingFromSpinner(difficultyFromSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                ToastUtil.createToast(getActivity(), "No difficulty selected");
            }
        });
    }

    private void withdrawDifficultySettingFromSpinner(String chosenDifficulty) {
        switch (chosenDifficulty) {
            case "Easy":
                this.chosenDifficulty = Difficulty.EASY;
                break;
            case "Medium":
                this.chosenDifficulty = Difficulty.MEDIUM;
                break;
            case "Hard":
                this.chosenDifficulty = Difficulty.HARD;
                break;
            default:
                this.chosenDifficulty = Difficulty.EASY;
                break;
        }
        saveDifficultySettingToSharedPreferences();
    }

    private void saveDifficultySettingToSharedPreferences() {
        SharedPreferencesUtil.saveSetting(getActivity(), SharedPreferences_Keys.DIFFICULTY, this.chosenDifficulty.toString());
    }

    private void setSwitchMusicOnClick() {
        // Default to turned off
        String musicSwitchState = SharedPreferencesUtil.getSetting(getActivity(), SharedPreferences_Keys.BACKGROUND_MUSIC_STATE, "Off");
        // If it equals to On, returns true. Therefore, sets it to On
        this.musicSwitch.setChecked(musicSwitchState.equals("On"));

        this.musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveMusicStateToSharedPreferences(isChecked);

            if (isChecked) {
                playSelectedSoundtrack();
            } else {
                stopSoundtrack();
            }
        });
    }

    private void saveMusicStateToSharedPreferences(boolean musicState) {
        String musicStateString = musicState ? "On" : "Off";
        SharedPreferencesUtil.saveSetting(getActivity(), SharedPreferences_Keys.BACKGROUND_MUSIC_STATE, musicStateString);
    }

    // RadioGroup Listener for Soundtrack
    private void setSoundtrackRadioGroup() {
        soundtrackRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedSoundtrack = getSoundtrackFromRadioGroup(checkedId);
            saveSelectedSoundtrack(selectedSoundtrack);

            if (musicSwitch.isChecked()) {
                playSelectedSoundtrack();
            }
        });
        // Programmatically check the selected soundtrack from the SharedPreferences
        int savedSoundtrackId = getRadioButtonIdFromSavedSoundtrack();
        soundtrackRadioGroup.check(savedSoundtrackId);
    }

    private String getSoundtrackFromRadioGroup(int checkedId) {
        // Cries in switch
        if (checkedId == R.id.firstOption) return "Default";
        if (checkedId == R.id.secondOption) return "Epic";
        if (checkedId == R.id.thirdOption) return "Chill";
        return "Default";
    }

    private void saveSelectedSoundtrack(String soundtrack) {
        SharedPreferencesUtil.saveSetting(getActivity(), SharedPreferences_Keys.CHOSEN_SOUNDTRACK, soundtrack);
    }

    private int getRadioButtonIdFromSavedSoundtrack() {
        String savedSoundtrack = SharedPreferencesUtil.getSetting(getActivity(), SharedPreferences_Keys.CHOSEN_SOUNDTRACK, "Default");

        switch (savedSoundtrack) {
            case "Epic": return R.id.secondOption;
            case "Chill": return R.id.thirdOption;
            default: return R.id.firstOption;
        }
    }

    private void playSelectedSoundtrack() {
        stopSoundtrack();

        String soundtrack = getChosenSoundtrackFromSharedPreferences();
        int soundtrackResId = getSoundtrackResourceId(soundtrack);

        mediaPlayer = MediaPlayer.create(getContext(), soundtrackResId);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private String getChosenSoundtrackFromSharedPreferences() {
        // Retrieves the soundtrack from the settings SharedPreferences. By default play the default soundtrack
        return SharedPreferencesUtil.getSetting(getActivity(), SharedPreferences_Keys.CHOSEN_SOUNDTRACK, "Default");
    }

    private int getSoundtrackResourceId(String soundtrack) {
        switch (soundtrack) {
            case "Epic": return R.raw.epic_music;
            case "Chill": return R.raw.chill_music;
            default: return R.raw.default_music;
        }
    }

    private void stopSoundtrack() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).updateToolBarWithFragmentName();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        boolean playMusic = this.musicSwitch.isChecked();

        if (playMusic) playSelectedSoundtrack();
        else stopSoundtrack();
    }
}
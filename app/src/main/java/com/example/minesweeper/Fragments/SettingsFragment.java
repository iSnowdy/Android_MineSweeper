package com.example.minesweeper.Fragments;

/*
    Soundtracks:
    - Default: https://www.youtube.com/watch?v=NmCCQxVBfyM
    - Epic: https://www.youtube.com/watch?v=yQ2LGEqyvdk
    - Chill: https://www.youtube.com/watch?v=y_ezfsJ4-Lw


 */

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.minesweeper.Difficulty;
import com.example.minesweeper.MainActivity;
import com.example.minesweeper.R;
import com.example.minesweeper.SharedPreferences_Keys;
import com.example.minesweeper.Utils.ToastUtil;


public class SettingsFragment extends Fragment {
    private Spinner difficultySpinner;
    private Switch musicSwitch;
    private RadioGroup soundtrackRadioGroup;
    private Difficulty chosenDifficulty;
    private SharedPreferences settingsSharedPreferences;
    private SharedPreferences.Editor settingsSharedPreferencesEditor;
    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ToastUtil.createToast(getActivity(), "Settings Fragment");

        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        loadSettingsViews(settingsView);

        createSettingsSharedPreferences();
        createSettingsSharedPreferencesEditor();

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
        this.settingsSharedPreferencesEditor.putString(SharedPreferences_Keys.DIFFICULTY.toString(), this.chosenDifficulty.toString());
        this.settingsSharedPreferencesEditor.apply();
    }

    private void setSwitchMusicOnClick() {
        // Default to turned off
        this.musicSwitch.setChecked(settingsSharedPreferences.getBoolean(SharedPreferences_Keys.BACKGROUND_MUSIC_STATE.toString(), false));

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
        this.settingsSharedPreferencesEditor.putBoolean(SharedPreferences_Keys.BACKGROUND_MUSIC_STATE.toString(), musicState);
        this.settingsSharedPreferencesEditor.apply();
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
        settingsSharedPreferencesEditor.putString(SharedPreferences_Keys.CHOSEN_SOUNDTRACK.toString(), soundtrack);
        settingsSharedPreferencesEditor.apply();
    }

    private int getRadioButtonIdFromSavedSoundtrack() {
        String savedSoundtrack = settingsSharedPreferences.getString(SharedPreferences_Keys.CHOSEN_SOUNDTRACK.toString(), "Default");
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
        return this.settingsSharedPreferences.getString(SharedPreferences_Keys.CHOSEN_SOUNDTRACK.toString(), "Default");
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

    private void createSettingsSharedPreferences() {
        this.settingsSharedPreferences = getActivity().getSharedPreferences(SharedPreferences_Keys.SETTINGS_INFORMATION_SP.toString(), MODE_PRIVATE);
    }

    private void createSettingsSharedPreferencesEditor() {
        this.settingsSharedPreferencesEditor = this.settingsSharedPreferences.edit();
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
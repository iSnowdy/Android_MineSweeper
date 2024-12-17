package com.example.minesweeper.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.minesweeper.Activities.MainActivity;
import com.example.minesweeper.JavaClasses.SharedPreferences_Keys;
import com.example.minesweeper.JavaClasses.Utils.SharedPreferencesUtil;
import com.example.minesweeper.R;
import com.example.minesweeper.JavaClasses.Utils.ToastUtil;

import java.sql.SQLOutput;

public class StatsFragment extends Fragment {
    private TextView
            averageTimeTextView,
            winPercentageTextView, losePercentageTextView,
            winsTextView, lossesTextView, gamesPlayedTextView,
            longestWinningStreakTextView, longestLosingStreakTextView, currentStreakTextView;

    private Button resetStatsButton;
    private int
            wins, loses, score, gamesPlayed, time, longestWinningStreak, longestLosingStreak, currentStreak;
    private double
            averageTime, winPercentage, losePercentage;
    //private String username; // TODO: Consider making the freaking username STATIC FINAL
    private String gamesResults;
    // I use this boolean to keep track of the order of operations just in case
    // Only update the "complex" stats if the "basic" stats have been updated. I put this as a
    // requirement because the complex stats require the basic stats to be updated in order to
    // maintain data consistency
    public static boolean isBasicStatsUpdated = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ToastUtil.createToast(getActivity(), "Stats Fragment");

        View statsFragmentView = inflater.inflate(R.layout.fragment_stats, container, false);

        loadStatsViews(statsFragmentView);


        retrieveBasicInformationFromSharedPreferences();
        updateComplexStatsFromSharedPreferences();

        updateStreaks();
        updateStats();

        return statsFragmentView;
    }

    private void loadStatsViews(View statsFragmentView) {
        this.averageTimeTextView = statsFragmentView.findViewById(R.id.averageTimeText);
        this.winPercentageTextView = statsFragmentView.findViewById(R.id.winPercentageText);
        this.losePercentageTextView = statsFragmentView.findViewById(R.id.losePercentageText);
        this.winsTextView = statsFragmentView.findViewById(R.id.winsText);
        this.lossesTextView = statsFragmentView.findViewById(R.id.lossesText);
        this.gamesPlayedTextView = statsFragmentView.findViewById(R.id.gamesPlayedText);
        this.longestWinningStreakTextView = statsFragmentView.findViewById(R.id.longestWinningStreakText);
        this.longestLosingStreakTextView = statsFragmentView.findViewById(R.id.longestLosingStreakText);
        this.currentStreakTextView = statsFragmentView.findViewById(R.id.currentStreakText);

        this.resetStatsButton = statsFragmentView.findViewById(R.id.clearStatsButton);
        this.resetStatsButton.setOnClickListener(v -> resetStats());
    }

    private void resetStats() {
        SharedPreferencesUtil.resetStats(getContext(), MainActivity.username);
        retrieveBasicInformationFromSharedPreferences();
        updateComplexStatsFromSharedPreferences();

        updateStreaks();
        updateStats();
    }

    private void retrieveBasicInformationFromSharedPreferences() {
        System.out.println("HELLO FROM BASIC INFORMATION METHOD");
        this.gamesPlayed = SharedPreferencesUtil.getStat(getContext(), MainActivity.username, SharedPreferences_Keys.GAMES_PLAYED, 0);
        this.wins = SharedPreferencesUtil.getStat(getContext(), MainActivity.username, SharedPreferences_Keys.WINS, 0);
        this.loses = SharedPreferencesUtil.getStat(getContext(), MainActivity.username, SharedPreferences_Keys.LOSES, 0);
        this.score = SharedPreferencesUtil.getStat(getContext(), MainActivity.username, SharedPreferences_Keys.SCORE, 0);
        this.time = SharedPreferencesUtil.getStat(getContext(), MainActivity.username, SharedPreferences_Keys.TIME, 0);

        this.gamesResults = SharedPreferencesUtil.getStat(getContext(), MainActivity.username, SharedPreferences_Keys.GAMES_RESULTS, "");
    }

    private void updateComplexStatsFromSharedPreferences() {
        // TODO

        updateTimeAverageFromSharedPreferences();
        updatePercentagesFromSharedPreferences();
        isBasicStatsUpdated = false;
    }

    private void updateTimeAverageFromSharedPreferences() {
        SharedPreferencesUtil.saveAverageTimeStat(
                getContext(),
                MainActivity.username, SharedPreferences_Keys.AVERAGE_TIME,
                this.gamesPlayed, this.time);
    }

    private void updatePercentagesFromSharedPreferences() {
        this.winPercentage = ((float) this.wins / (float) this.gamesPlayed) * 100;
        this.losePercentage = ((float) this.loses / (float) this.gamesPlayed) * 100;

        System.out.println("Win percentage: " + this.winPercentage);
        System.out.println("Lose percentage: " + this.losePercentage);

        SharedPreferencesUtil.saveStat(
                getContext(),
                MainActivity.username, SharedPreferences_Keys.WIN_PERCENTAGE,
                (float) this.winPercentage);

        SharedPreferencesUtil.saveStat(
                getContext(),
                MainActivity.username, SharedPreferences_Keys.LOSE_PERCENTAGE,
                (float) this.losePercentage);
    }


    private void updateStreaks() {
        retrieveStreaksFromSharedPreferences();

        for (int i = this.gamesResults.length() - 2; i >= 0; i--) { // Start from second-to-last element
            char lastGameResult = this.gamesResults.charAt(i + 1);
            char currentGameResult = this.gamesResults.charAt(i);

            if (lastGameResult == currentGameResult) {
                // If current and last results are the same, increment streak
                this.currentStreak++;
                // Math.max returns the max number between the two arguments
                if (currentGameResult == 'W') {
                    this.longestWinningStreak = Math.max(this.longestWinningStreak, this.currentStreak);
                } else {
                    this.longestLosingStreak = Math.max(this.longestLosingStreak, this.currentStreak);
                }
            } else {
                // If results differ, reset current streak to 1
                this.currentStreak = 1;
            }
        }

        // Check if the last game is part of a streak
        if (this.gamesResults.length() > 0) {
            char lastGameResult = this.gamesResults.charAt(this.gamesResults.length() - 1);
            if (lastGameResult == 'W') {
                this.longestWinningStreak = Math.max(this.longestWinningStreak, this.currentStreak);
            } else {
                this.longestLosingStreak = Math.max(this.longestLosingStreak, this.currentStreak);
            }
        }

        saveStreaksIntoSharedPreferences();
    }

    private void retrieveStreaksFromSharedPreferences() {
        this.currentStreak = SharedPreferencesUtil.getStat(getContext(), MainActivity.username, SharedPreferences_Keys.CURRENT_STREAK, 0);
        this.longestWinningStreak = SharedPreferencesUtil.getStat(getContext(), MainActivity.username, SharedPreferences_Keys.LONGEST_WINNING_STREAK, 0);
        this.longestLosingStreak = SharedPreferencesUtil.getStat(getContext(), MainActivity.username, SharedPreferences_Keys.LONGEST_LOSING_STREAK, 0);
    }

    private void saveStreaksIntoSharedPreferences() {
        SharedPreferencesUtil.saveStat(getContext(), MainActivity.username, SharedPreferences_Keys.CURRENT_STREAK, this.currentStreak);
        SharedPreferencesUtil.saveStat(getContext(), MainActivity.username, SharedPreferences_Keys.LONGEST_WINNING_STREAK, this.longestWinningStreak);
        SharedPreferencesUtil.saveStat(getContext(), MainActivity.username, SharedPreferences_Keys.LONGEST_LOSING_STREAK, this.longestLosingStreak);
    }

    private void updateStats() {
        this.averageTimeTextView.setText(getString(R.string.avgTime, this.averageTime));
        this.winPercentageTextView.setText(getString(R.string.winPercentage, this.winPercentage));
        this.losePercentageTextView.setText(getString(R.string.losePercentage, this.losePercentage));
        this.winsTextView.setText(getString(R.string.wins, this.wins));
        this.lossesTextView.setText(getString(R.string.losses, this.loses));
        this.gamesPlayedTextView.setText(getString(R.string.gamesPlayed, this.gamesPlayed));
        this.longestWinningStreakTextView.setText(getString(R.string.longestWinningStreak, this.longestWinningStreak));
        this.longestLosingStreakTextView.setText(getString(R.string.longestLosingStreak, this.longestLosingStreak));
        this.currentStreakTextView.setText(getString(R.string.currentStreak, this.currentStreak));

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).updateToolBarWithFragmentName();
    }

}
package com.example.minesweeper.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.minesweeper.R;

public class StatsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toast.makeText(getActivity(), "Stats Fragment", Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

}
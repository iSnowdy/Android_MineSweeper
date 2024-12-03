package com.example.minesweeper.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.minesweeper.R;

public class HelpFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toast.makeText(getActivity(), "Help Fragment", Toast.LENGTH_SHORT).show();

        View helpView = inflater.inflate(R.layout.fragment_help, container, false);
        return helpView;
    }
}

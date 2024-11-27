package com.example.minesweeper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class GameFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toast.makeText(getActivity(), "Game Fragment", Toast.LENGTH_SHORT).show();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game_menu_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}

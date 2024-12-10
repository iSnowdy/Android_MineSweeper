package com.example.minesweeper.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.minesweeper.MainActivity;
import com.example.minesweeper.R;
import com.example.minesweeper.Utils.ToastUtil;

public class StatsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ToastUtil.createToast(getActivity(), "Stats Fragment");
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).updateToolBarWithFragmentName();
    }

}
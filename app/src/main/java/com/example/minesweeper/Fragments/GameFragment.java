package com.example.minesweeper.Fragments;

// Assets credit to: https://github.com/projojoboy/MineSweeper/blob/master/Assets/Art/mine.png

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Dimension;
import androidx.fragment.app.Fragment;

import com.example.minesweeper.Board.GridGameBoard;
import com.example.minesweeper.Difficulty;
import com.example.minesweeper.GameLogic.Game;
import com.example.minesweeper.MainActivity;
import com.example.minesweeper.R;
import com.example.minesweeper.Tile.GameTile;

public class GameFragment extends Fragment {
    private Toolbar toolbar;
    private GridGameBoard board;
    private int rows;
    private int columns;
    private GridLayout gridLayout;
    private Game game;
    private boolean isFirstClick;
    private int height;
    private int width;

    private int toolbarHeight, navbarHeight, statusBarHeight, totalHeight;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Enable options menu
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.toolbar.getMenu().clear();
        inflater.inflate(R.menu.game_menu_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View gameFragmentView = inflater.inflate(R.layout.fragment_game, container, false);

        this.toolbar = getActivity().findViewById(R.id.toolbar);
        this.gridLayout = gameFragmentView.findViewById(R.id.gridLayout);
        // Somehow change the difficulty here depending on user's chosen difficulty
        this.board = new GridGameBoard(Difficulty.EASY);
        this.game = new Game(this.board);

        this.isFirstClick = false;

        // Retrieves height values from toolbar, navbar and status bar from Main (where they
        // are created) and sets the height / width of the GameFragment according to those values
        retrieveHeightFromMainActivity();
        setHeight();

        // Game Logic here
        this.game.startNewGame();
        setSize();
        setGridLayout();
        initializeGrid();

        return gameFragmentView;
    }


    private void retrieveHeightFromMainActivity() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.toolbarHeight = bundle.getInt("toolbarHeight");
            this.navbarHeight = bundle.getInt("navbarHeight");
            this.statusBarHeight = bundle.getInt("statusBarHeight");
        }

        this.totalHeight = toolbarHeight + navbarHeight + statusBarHeight;
    }

    private void setHeight() {
        this.height = this.getHeight(this.getActivity()) - totalHeight;
        this.width = this.getWidth(this.getActivity());
    }

    private void restartGame() {
        game.resetGame();
        initializeGrid();
    }

    private void setSize() {
        this.rows = board.getRows();
        this.columns = board.getColumns();
    }

    private void setGridLayout() {
        if (gridLayout != null) {
            this.gridLayout.removeAllViews();
            this.gridLayout.setRowCount(this.rows);
            this.gridLayout.setColumnCount(this.columns);
        }
    }

    private void initializeGrid() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                // Creation of ImageButton for every tile
                ImageView tileButton = new ImageView(getActivity());

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(r, 1f);
                params.columnSpec = GridLayout.spec(c, 1f);
                params.width = GridLayout.LayoutParams.WRAP_CONTENT;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                tileButton.setLayoutParams(params);

                tileButton.setTag(this.board.getTile(r, c));

                tileButton.setImageResource(R.drawable.resource_default);
                // Sets the size of each tile according to the height / width
                // previously calculated
                tileButton.setMinimumHeight(this.width / this.rows);
                tileButton.setMinimumWidth(this.width / this.columns);

                tileButton.setScaleType(ImageView.ScaleType.FIT_XY);

                // Add the listener for every tile button
                tileButton.setOnClickListener(v -> onTileClick((GameTile) v.getTag()));
                // And a long click for putting the flags
                tileButton.setOnLongClickListener(v -> {
                    onTileLongClick((GameTile) v.getTag());
                    return true;
                });
                gridLayout.addView(tileButton);
            }
        }
    }

    private void onTileClick(GameTile tile) {
        if (!tile.isRevealed() && !tile.isFlagged()) {
            tile.revealTile();
            updateTileImage(tile); // Every click, update the UI
            checkGameStatus(); // Every click, check the game status
        }
    }

    private void onTileLongClick(GameTile tile) {
        if (!tile.isRevealed()) {
            if (!tile.isFlagged()) {
                onFlagClick(tile);
                updateTileImage(tile);
            } else {
                onUnflagClick(tile);
                updateTileImage(tile);
            }
        }
    }

    private void onFlagClick(GameTile tile) {
        if (!tile.isRevealed()) {
            tile.flag(); // Flag the tile
            updateTileImage(tile);
        }
    }

    private void onUnflagClick(GameTile tile) {
        if (tile.isFlagged()) {
            tile.removeFlag(); // Unflagging the tile
            updateTileImage(tile);
        }
    }

    private void updateTileImage(GameTile tile) {
        ImageView tileButton = gridLayout.findViewWithTag(tile);

        if (tile.isRevealed()) {
            Log.d("Tile", "isMine? " + tile.isMine());
            if (tile.isMine()) {
                tileButton.setImageResource(R.drawable.mine);
            } else {
                int adjacentMines = tile.countAdjacentMines(board);
                if (adjacentMines > 0) {
                    tileButton.setImageResource(getNumberImage(adjacentMines));
                } else {
                    tileButton.setImageResource(R.drawable.empty0);
                }
            }

        } else if (tile.isFlagged()) {
            tileButton.setImageResource(R.drawable.flag);

        } else {
            tileButton.setImageResource(R.drawable.resource_default);
        }
    }

    private int getNumberImage(int number) {
        return getResources().getIdentifier("empty" + number, "drawable", getActivity().getPackageName());
    }

    private void checkGameStatus() {
        System.out.println("Checking Game Status");
        this.game.checkGameStatus();
        // Alert
        // Alert Dialog
        if (game.isGameOver() && game.isGameLost()) {
            System.out.println("Game lost and over!");
        } else if (game.isGameOver() && !game.isGameLost()) {
            System.out.println("Game won and over!");
        }
    }

    // Get screen height
    private int getHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    // Get screen width
    private int getWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
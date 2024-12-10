package com.example.minesweeper.Fragments;

// Assets credit to: https://github.com/projojoboy/MineSweeper/blob/master/Assets/Art/mine.png

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.minesweeper.Board.GridGameBoard;
import com.example.minesweeper.Utils.ChronometerHelper;
import com.example.minesweeper.Difficulty;
import com.example.minesweeper.GameLogic.Game;
import com.example.minesweeper.MainActivity;
import com.example.minesweeper.R;
import com.example.minesweeper.SharedPreferences_Keys;
import com.example.minesweeper.Tile.GameTile;

public class GameFragment extends Fragment {
    private Toolbar toolbar;
    private Menu menu;
    private GridLayout gridLayout;
    private GridGameBoard board;
    private Game game;
    private Chronometer chronometer;
    private ChronometerHelper chronometerHelper;

    private SharedPreferences settingsSharedPreferences;

    private int
            gameBoardRows, gameBoardColumns,
            tileImageViewHeight, tileImageViewWidth;
    private boolean isFirstClick;

    private int toolbarHeight, navbarHeight, statusBarHeight, totalHeight;

    // Toolbar + Chronometer Configuration
    // Standard toolbar is replaced by a custom one for the Game Fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Enable options menu
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.toolbar.getMenu().clear();
        inflater.inflate(R.menu.game_menu_toolbar, menu);
        this.menu = menu;

        this.menu.findItem(R.id.timeRemaining).setActionView(chronometer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.resetButton) {
            restartGame();
            return true;
        } else if (id == R.id.minesLeft) {
            updateMinesLeft();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMinesLeft() {
        int remainingFlags = this.board.getRemainingFlagsToUse();
        this.menu.findItem(R.id.minesLeft).setTitle("Remaining Mines: " + remainingFlags);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View gameFragmentView = inflater.inflate(R.layout.fragment_game, container, false);

        this.toolbar = getActivity().findViewById(R.id.toolbar);
        this.gridLayout = gameFragmentView.findViewById(R.id.gridLayout);

        createSettingsSharedPreferences();
        // TODO: What happens if the user changes difficulty while playing one game?
        this.board = new GridGameBoard(loadDifficultyFromSettings());
        this.game = new Game(this.board);

        this.isFirstClick = true;

        // Retrieves height values from toolbar, navbar and status bar from Main (where they
        // are created) and sets the height / width of the GameFragment according to those values
        retrieveHeightFromMainActivity();
        setHeight();

        // Game Logic here
        this.game.startNewGame();

        this.chronometer = new Chronometer(getActivity());
        initializeChronometer();

        setGameBoardSize();
        setGameBoardGridLayout();
        loadDefaultViewsIntoGameGridLayout();

        return gameFragmentView;
    }

    private void createSettingsSharedPreferences() {
        this.settingsSharedPreferences = getActivity().getSharedPreferences(SharedPreferences_Keys.SETTINGS_INFORMATION_SP.toString(), MODE_PRIVATE);
    }

    private Difficulty loadDifficultyFromSettings() {
        // Default value is EASY
        return Difficulty.valueOf(this.settingsSharedPreferences.getString(SharedPreferences_Keys.DIFFICULTY.toString(), Difficulty.EASY.toString()));
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
        this.tileImageViewHeight = this.getHeight(this.getActivity()) - totalHeight;
        this.tileImageViewWidth = this.getWidth(this.getActivity());
    }

    // Chronometer Configuration
    private void initializeChronometer() {
        this.chronometerHelper = new ChronometerHelper();
        startChronometer();
        styleChronometer();
    }

    public void startChronometer() {
        if (this.chronometerHelper.getElapsedTime() == 0) {
            this.chronometer.setBase(SystemClock.elapsedRealtime());
        } else {
            // Start where we left off
            this.chronometer.setBase(SystemClock.elapsedRealtime() - this.chronometerHelper.getElapsedTime());
        }
        this.chronometer.start();
    }

    public void stopChronometer() {
        // Calculates and saves the elapsed time
        long timeElapsed = SystemClock.elapsedRealtime() - this.chronometer.getBase();
        this.chronometerHelper.setElapsedTime(timeElapsed);

        this.chronometer.stop();
    }

    private void styleChronometer() {
        chronometer.setFormat("Time Remaining: %s");
        chronometer.setTextColor(getResources().getColor(R.color.white));
        chronometer.setTextSize(20);
        chronometer.setPadding(10, 10, 10, 10);
        chronometer.setBackgroundColor(getResources().getColor(R.color.black));
    }

    private void resetChronometer() {
        this.chronometerHelper.reset();
        this.chronometer.stop();
        this.chronometer.setBase(SystemClock.elapsedRealtime());
        this.chronometer.start();
    }

    private void restartGame() {
        this.isFirstClick = true;
        this.game.startNewGame();
        resetChronometer();
        loadDefaultViewsIntoGameGridLayout();
    }

    private void setGameBoardSize() {
        this.gameBoardRows = board.getRows();
        this.gameBoardColumns = board.getColumns();
    }

    private void setGameBoardGridLayout() {
        if (gridLayout != null) {
            this.gridLayout.removeAllViews();
            this.gridLayout.setRowCount(this.gameBoardRows);
            this.gridLayout.setColumnCount(this.gameBoardColumns);
        }
    }

    private void loadDefaultViewsIntoGameGridLayout() {
        for (int r = 0; r < gameBoardRows; r++) {
            for (int c = 0; c < gameBoardColumns; c++) {
                // Creation of ImageViews for every tile
                ImageView tileImageView = new ImageView(getActivity());

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(r, 1f);
                params.columnSpec = GridLayout.spec(c, 1f);
                params.width = GridLayout.LayoutParams.WRAP_CONTENT;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                tileImageView.setLayoutParams(params);

                tileImageView.setTag(this.board.getTile(r, c));

                tileImageView.setImageResource(R.drawable.resource_default);
                // Sets the size of each tile according to the height / width
                // previously calculated
                tileImageView.setMinimumHeight(this.tileImageViewWidth / this.gameBoardRows);
                tileImageView.setMinimumWidth(this.tileImageViewWidth / this.gameBoardColumns);

                tileImageView.setScaleType(ImageView.ScaleType.FIT_XY);

                // Add the listener for every tile button
                tileImageView.setOnClickListener(v -> onTileClick((GameTile) v.getTag()));
                // And a long click for putting the flags
                tileImageView.setOnLongClickListener(v -> {
                    onTileLongClick((GameTile) v.getTag());
                    return true;
                });
                gridLayout.addView(tileImageView);
            }
        }
    }
    // TODO: Check if it is really working
    private void onTileClick(GameTile tile) {
        if (isFirstClick) {
            isFirstClick = false;
            this.board.placeMines(tile);
        }
        // After the first click, reveal the tile
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
            this.board.placeFlag(tile.getRow(), tile.getCol());
            updateTileImage(tile);
        }
    }

    private void onUnflagClick(GameTile tile) {
        if (tile.isFlagged()) {
            tile.removeFlag(); // Unflagging the tile
            this.board.removeFlag(tile.getRow(), tile.getCol());
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
                    revealAdjacentTiles(tile);
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

    private void revealAdjacentTiles(GameTile tile) {
        int row = tile.getRow();
        int col = tile.getCol();

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < gameBoardRows && c >= 0 && c < gameBoardColumns) {
                    GameTile adjacentTile = board.getTile(r, c);
                    // Only if the tile is not revealed nor marked as a flag, reveal it
                    if (!adjacentTile.isRevealed() && !adjacentTile.isFlagged()) {
                        adjacentTile.revealTile();
                        updateTileImage(adjacentTile);

                        // And only if the tile is considered as empty, reveal the adjacent tiles
                        // with a recursive call
                        if (adjacentTile.isEmpty(this.board)) {
                            revealAdjacentTiles(adjacentTile);
                        }
                    }
                }
            }
        }
    }

    private void checkGameStatus() {
        this.game.checkGameStatus();
        // Alert Dialog
        if (game.isGameOver() && game.isGameLost()) {
            System.out.println("Game lost and over!");
            int score = game.getScore();
            showGameResultDialog(false, score);
        } else if (game.isGameOver() && !game.isGameLost()) {
            System.out.println("Game won and over!");
            int score = game.getScore();
            showGameResultDialog(true, score);
        }
    }

    public void showGameResultDialog(boolean isWin, int finalScore) {
        String resultMessage;
        String buttonText;
        Drawable drawable;

        // Determina el mensaje de acuerdo al resultado
        if (isWin) {
            resultMessage = "Congratulations! You have won";
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.confetti_victory_svg, null);
            buttonText = "Play again";
        } else {
            resultMessage = "Booo! You have lost";
            drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.player_defeat_svg, null);
            buttonText = "(Lose) again";
        }

        String finalMessage = resultMessage + "\n" + "Current game stats have been saved";
        // TODO: Implement saving stats methods here

        new AlertDialog.Builder(this.getContext())
                .setTitle("Game Ended")
                .setMessage(finalMessage)
                .setCancelable(false) // Prevents the Alert Dialog from closing if the user didn't choose so
                .setIcon(drawable)
                .setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartGame();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .show();
    }

    // Get screen height
    private int getHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    // Get screen width
    private int getWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).updateToolBarWithFragmentName();

        startChronometer();
        System.out.println("Game Fragment Resumed");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            stopChronometer();
        } else {
            startChronometer();
        }
    }
}
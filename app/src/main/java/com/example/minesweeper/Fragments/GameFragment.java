package com.example.minesweeper.Fragments;

// Assets credit to: https://github.com/projojoboy/MineSweeper/blob/master/Assets/Art/mine.png

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.fragment.app.FragmentManager;

import com.example.minesweeper.JavaClasses.Board.GridGameBoard;
import com.example.minesweeper.JavaClasses.Utils.ChronometerHelper;
import com.example.minesweeper.JavaClasses.Difficulty;
import com.example.minesweeper.JavaClasses.GameLogic.Game;
import com.example.minesweeper.Activities.MainActivity;
import com.example.minesweeper.R;
import com.example.minesweeper.JavaClasses.SharedPreferences_Keys;
import com.example.minesweeper.JavaClasses.Tile.GameTile;
import com.example.minesweeper.JavaClasses.Utils.SharedPreferencesUtil;

public class GameFragment extends Fragment {
    private Toolbar toolbar;
    private Menu menu;
    private GridLayout gridLayout;
    private GridGameBoard board;
    private Difficulty difficulty;
    private Game game;
    private Chronometer chronometer;
    private ChronometerHelper chronometerHelper;

    private int
            gameBoardRows, gameBoardColumns,
            tileImageViewHeight, tileImageViewWidth;
    private boolean isFirstClick;

    private int toolbarHeight, navbarHeight, statusBarHeight, totalHeight;
    //private String username;

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

        loadDifficultyFromSettings();
        System.out.println("Loaded difficulty: " + this.difficulty.toString());
        this.board = new GridGameBoard(this.difficulty);
        this.game = new Game(this.board);

        this.isFirstClick = true;

        // Retrieves height values from toolbar, navbar and status bar from Main (where they
        // are created) and sets the height / width of the GameFragment according to those values
        retrieveInformationFromMainActivityBundle();
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

    private void loadDifficultyFromSettings() {
        // Default value is EASY
        String difficultyFromSharedPreferences = SharedPreferencesUtil.getSetting(getContext(), SharedPreferences_Keys.DIFFICULTY, Difficulty.EASY.toString());
        this.difficulty = Difficulty.valueOf(difficultyFromSharedPreferences);
    }

    private void retrieveInformationFromMainActivityBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.toolbarHeight = bundle.getInt("toolbarHeight");
            this.navbarHeight = bundle.getInt("navbarHeight");
            this.statusBarHeight = bundle.getInt("statusBarHeight");
            //this.username = bundle.getString("username");
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
    // Restart click condition, check again for a possible change in difficulty and
    // restart the game
    private void restartGame() {
        this.isFirstClick = true;

        loadDifficultyFromSettings();
        System.out.println("Restarted game");
        System.out.println("Loaded dififulty: " + this.difficulty.toString());
        this.board = new GridGameBoard(this.difficulty);
        this.game = new Game(this.board);
        this.game.startNewGame();

        resetChronometer();
        setGameBoardSize();
        setGameBoardGridLayout();
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
                //tileImageView.setMinimumHeight(this.tileImageViewWidth / this.gameBoardRows);
                tileImageView.setMinimumHeight(this.tileImageViewHeight / this.gameBoardRows);
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
        System.out.println("Long click detected");
        if (!tile.isRevealed()) {
            System.out.println("Unrevealed tile clicked");
            if (!tile.isFlagged()) {
                System.out.println("Flagging tile...");
                onFlagClick(tile);
                updateTileImage(tile);
            } else {
                System.out.println("Unflagging tile");
                onUnflagClick(tile);
                updateTileImage(tile);
            }
        }
    }

    private void onFlagClick(GameTile tile) {
        if (!tile.isRevealed()) {
            tile.flag(); // Flag the tile
            //this.board.placeFlag(tile.getRow(), tile.getCol()); not working
            updateTileImage(tile);
        }
    }

    private void onUnflagClick(GameTile tile) {
        if (tile.isFlagged()) {
            tile.removeFlag(); // Unflagging the tile
            //this.board.removeFlag(tile.getRow(), tile.getCol()); not working
            updateTileImage(tile);
        }
    }

    private void updateTileImage(GameTile tile) {
        ImageView tileImageView = gridLayout.findViewWithTag(tile);

        if (tile.isRevealed()) {
            Log.d("Tile", "isMine? " + tile.isMine());
            if (tile.isMine()) {
                // TODO: Put a different image for the selected mine tile
                tileImageView.setImageResource(R.drawable.mine);
                revealAllBombs();
            } else {
                int adjacentMines = tile.countAdjacentMines(board);
                if (adjacentMines > 0) {
                    tileImageView.setImageResource(getNumberImage(adjacentMines));
                } else {
                    tileImageView.setImageResource(R.drawable.empty0);
                    revealAdjacentTiles(tile);
                }
            }

        } else if (tile.isFlagged()) {
            System.out.println("Tile is flagged. Putting image");
            tileImageView.setImageResource(R.drawable.flag);

        } else {
            tileImageView.setImageResource(R.drawable.resource_default);
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
            this.chronometer.stop();
            showGameResultDialog(false, score);
            saveStatsToSharedPreferences(false);
        } else if (game.isGameOver() && !game.isGameLost()) {
            System.out.println("Game won and over!");
            int score = game.getScore();
            this.chronometer.stop();
            showGameResultDialog(true, score);
            saveStatsToSharedPreferences(true);
        }
    }
    // TODO: Ugly way of revealing the bombs. I don't like it at all
    private void revealAllBombs() {
        System.out.println("Inside REVEAL ALL BOMBS");
        for (int r = 0; r < gameBoardRows; r++) {
            for (int c = 0; c < gameBoardColumns; c++) {
                GameTile tile = board.getTile(r, c);
                if (tile.isMine()) {
                    ImageView tileImageView = gridLayout.findViewWithTag(tile);
                    tileImageView.setImageResource(R.drawable.mine);

                    tile.revealTile();
                }
            }
        }
    }

    public void showGameResultDialog(boolean isWin, int finalScore) {
        String resultMessage;
        String buttonText;
        Drawable drawable;

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

    private void saveStatsToSharedPreferences(boolean isWin) {
        // TODO: Implement saving stats methods here
        System.out.println("Saving stats to Shared Preferences");

        SharedPreferencesUtil.saveStat(getContext(), MainActivity.username, SharedPreferences_Keys.GAMES_PLAYED, 1);

        if (isWin) {
            SharedPreferencesUtil.saveStat(getContext(), MainActivity.username, SharedPreferences_Keys.WINS, 1);
            SharedPreferencesUtil.saveStat(getContext(), MainActivity.username, SharedPreferences_Keys.GAMES_RESULTS, "W");
        } else {
            SharedPreferencesUtil.saveStat(getContext(), MainActivity.username, SharedPreferences_Keys.LOSES, 1);
            SharedPreferencesUtil.saveStat(getContext(), MainActivity.username, SharedPreferences_Keys.GAMES_RESULTS, "L");
        }

        // I don't know if score works
        SharedPreferencesUtil.saveStat(getContext(), MainActivity.username, SharedPreferences_Keys.SCORE, this.game.getScore());
        // Consider int overflow situation
        int elapsedSeconds = (int) (this.chronometerHelper.getElapsedTime() / 1000);
        SharedPreferencesUtil.saveStat(getContext(), MainActivity.username, SharedPreferences_Keys.TIME, elapsedSeconds);
        // Think how to implement win/lose streaks. Iterate reversed String and count W or L 's
        StatsFragment.isBasicStatsUpdated = true;
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
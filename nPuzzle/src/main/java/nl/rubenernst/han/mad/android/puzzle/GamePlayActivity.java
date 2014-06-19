package nl.rubenernst.han.mad.android.puzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;
import nl.rubenernst.han.mad.android.puzzle.fragments.GamePlayFragment;
import nl.rubenernst.han.mad.android.puzzle.helpers.InsetsHelper;
import nl.rubenernst.han.mad.android.puzzle.helpers.SaveGameStateHelper;
import nl.rubenernst.han.mad.android.puzzle.helpers.TintHelper;
import nl.rubenernst.han.mad.android.puzzle.interfaces.GamePlayListener;
import nl.rubenernst.han.mad.android.puzzle.interfaces.GamePlayStatusViewAdapter;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;

/**
 * Created by rubenernst on 14-03-14.
 */
public class GamePlayActivity extends FragmentActivity implements GamePlayListener, GamePlayStatusViewAdapter {

    private Boolean mUnfinishedGame;
    private Difficulty mDifficulty;
    private Integer mPuzzleId;
    private String mPuzzleImageName;
    private Integer mPuzzleDrawableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_game_play);

            TintHelper.setupTransparentTints(this);
            InsetsHelper.setInsets(this, findViewById(R.id.container), true, false);

            Class res = R.drawable.class;

            Intent intent = getIntent();
            mUnfinishedGame = intent.getBooleanExtra("unfinished_game", false);
            mDifficulty = (Difficulty) intent.getSerializableExtra("difficulty");
            mPuzzleId = intent.getIntExtra("puzzle", 0);
            mPuzzleImageName = "puzzle_" + (mPuzzleId + 1);

            mPuzzleDrawableId = res.getField(mPuzzleImageName).getInt(null);

            GamePlayFragment gamePlayFragment = new GamePlayFragment();
            gamePlayFragment.setUnfinishedGame(mUnfinishedGame);
            gamePlayFragment.setDifficulty(mDifficulty);
            gamePlayFragment.setPuzzleDrawableId(mPuzzleDrawableId);
            gamePlayFragment.setGamePlayListener(this);
            gamePlayFragment.setGamePlayStatusViewAdapter(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
            }

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, gamePlayFragment)
                        .commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                showQuitDialog();
                break;
            case R.id.action_change_difficulty:
                showChangeDifficultyDialog();
                break;
            case R.id.action_restart:
                showRestartDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showQuitDialog();

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void showQuitDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_stop_game_title)
                .setMessage(R.string.dialog_stop_game_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        GamePlayActivity.this.finish();
                    }

                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void showRestartDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_restart_game_title))
                .setMessage(getString(R.string.dialog_restart_game_message))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restartGame(mDifficulty);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }


    private void showChangeDifficultyDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_change_difficulty_title))
                .setItems(R.array.difficulties, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        restartGame(Difficulty.fromString(getResources().obtainTypedArray(R.array.difficulties).getString(which)));
                    }
                })
                .show();
    }

    private void restartGame(Difficulty difficulty) {
        GamePlayFragment gamePlayFragment = new GamePlayFragment();
        gamePlayFragment.setDifficulty(difficulty);
        gamePlayFragment.setPuzzleDrawableId(mPuzzleDrawableId);
        gamePlayFragment.setGamePlayStatusViewAdapter(this);
        gamePlayFragment.setGamePlayListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, gamePlayFragment)
                .commit();
    }

    @Override
    public void onGameInitialisation() {

    }

    @Override
    public void onGameInitialised(Game game) {

    }

    @Override
    public void onGameStarting(Game game) {

    }

    @Override
    public void onGameStarted(Game game) {

    }

    @Override
    public void onGameUIUpdating(Game game) {

    }

    @Override
    public void onGameUIUpdated(Game game) {

    }

    @Override
    public void onGameFinished(Game game) {
        SaveGameStateHelper.removeSavedGameState(getApplicationContext());

        Intent intent = new Intent(this, GameFinishedActivity.class);
        intent.putExtra("number_of_turns", game.getScore());
        intent.putExtra("difficulty", Difficulty.fromGridSize(game.getGridSize()));

        startActivity(intent);
    }

    @Override
    public void onGamePaused(Game game) {
        SaveGameStateHelper.saveGameStateToFile(getApplicationContext(), game);
    }

    @Override
    public void onGameResumed(Game game) {

    }

    @Override
    public void handleStatusViewInitializing(GamePlayFragment fragment) {
        fragment.initializeGame();
    }

    @Override
    public void handleStatusViewPlaying(Game game, GamePlayFragment fragment) {
        fragment.playGame();
    }

    @Override
    public void handleStatusViewBeforePlaying(Game game, GamePlayFragment fragment) {
        fragment.startGame();
    }

    @Override
    public void handleStatusViewEnded(Game game, GamePlayFragment fragment) {
        fragment.finishGame();
    }
}

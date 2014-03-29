package nl.rubenernst.han.mad.android.puzzle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import nl.rubenernst.han.mad.android.puzzle.fragments.GamePlayFragment;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;

/**
 * Created by rubenernst on 14-03-14.
 */
public class GamePlayActivity extends ActionBarActivity {

    private Difficulty mDifficulty;
    private Integer mPuzzleId;
    private String mPuzzleImageName;
    private Integer mPuzzleDrawableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_game_play);

            Class res = R.drawable.class;

            Intent intent = getIntent();
            mDifficulty = (Difficulty) intent.getSerializableExtra("difficulty");
            mPuzzleId = intent.getIntExtra("puzzle", 0);
            mPuzzleImageName = "puzzle_" + (mPuzzleId + 1);

            mPuzzleDrawableId = res.getField(mPuzzleImageName).getInt(null);

            GamePlayFragment gamePlayFragment = new GamePlayFragment();
            gamePlayFragment.setDifficulty(mDifficulty);
            gamePlayFragment.setPuzzleDrawableId(mPuzzleDrawableId);

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
            case R.id.action_change_difficulty:
                showChangeDifficultyDialog();
                break;
            case R.id.action_restart:
                showRestartDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRestartDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.change_difficulty_dialog_title))
                .setTitle("Are you sure you want to restart the game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restartGame(mDifficulty);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }


    private void showChangeDifficultyDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.change_difficulty_dialog_title))
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

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, gamePlayFragment)
                .commit();
    }

}

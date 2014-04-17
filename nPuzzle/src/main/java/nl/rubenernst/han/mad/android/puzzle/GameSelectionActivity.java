package nl.rubenernst.han.mad.android.puzzle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import nl.rubenernst.han.mad.android.puzzle.fragments.GameSelectionFragment;
import nl.rubenernst.han.mad.android.puzzle.helpers.SaveGameStateHelper;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;

public class GameSelectionActivity extends ActionBarActivity {

    private Difficulty mDifficulty = Difficulty.MEDIUM;
    private GameSelectionFragment mGameSelectionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);

        if (SaveGameStateHelper.hasSavedGameState(getApplicationContext())) {
            showNotFinishedGameDialog();
        }

        mGameSelectionFragment = new GameSelectionFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mGameSelectionFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_puzzle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_change_difficulty:
                showChangeDifficultyDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showChangeDifficultyDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_change_difficulty_title))
                .setItems(R.array.difficulties, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Difficulty difficulty = Difficulty.fromString(getResources().obtainTypedArray(R.array.difficulties).getString(which));
                        setDifficulty(difficulty);
                    }
                })
                .show();
    }

    private void showNotFinishedGameDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Unfinished Game")
                .setMessage("You have an unfinished game. Do you want to finished it?")
                .setNegativeButton("No!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Yeah!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(GameSelectionActivity.this, GamePlayActivity.class);
                        intent.putExtra("unfinished_game", true);

                        startActivity(intent);
                    }
                })
                .show();
    }

    public void setDifficulty(Difficulty difficulty) {
        this.mDifficulty = difficulty;

        mGameSelectionFragment.setDifficulty(this.mDifficulty);
    }
}

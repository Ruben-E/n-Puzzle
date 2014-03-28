package nl.rubenernst.han.mad.android.puzzle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import nl.rubenernst.han.mad.android.puzzle.fragments.GamePlayFragment;
import nl.rubenernst.han.mad.android.puzzle.utils.Constants;

import java.lang.reflect.Field;

/**
 * Created by rubenernst on 14-03-14.
 */
public class GamePlayActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_game_play);

            Intent intent = getIntent();
            Constants.Difficulty difficulty = (Constants.Difficulty) intent.getSerializableExtra("difficulty");
            Integer puzzleId = intent.getIntExtra("puzzle", 0);

            String puzzleImageName = "puzzle_" + (puzzleId + 1);

            Class res = R.drawable.class;
            Field field = res.getField(puzzleImageName);

            Integer puzzleDrawableId = field.getInt(null);

            GamePlayFragment puzzleGameFragment = new GamePlayFragment();
            puzzleGameFragment.setDifficulty(difficulty);
            puzzleGameFragment.setPuzzleDrawableId(puzzleDrawableId);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, puzzleGameFragment)
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
        getMenuInflater().inflate(R.menu.start_puzzle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

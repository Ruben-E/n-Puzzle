package nl.rubenernst.han.mad.android.puzzle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import nl.rubenernst.han.mad.android.puzzle.helpers.BitmapGameHelper;
import nl.rubenernst.han.mad.android.puzzle.helpers.InsetsHelper;
import nl.rubenernst.han.mad.android.puzzle.helpers.SaveGameStateHelper;
import nl.rubenernst.han.mad.android.puzzle.helpers.TintHelper;
import nl.rubenernst.han.mad.android.puzzle.utils.Constants;
import com.google.example.games.basegameutils.BaseGameActivity;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;

import java.io.FileNotFoundException;

/**
 * Created by rubenernst on 28-03-14.
 */
public class GameFinishedActivity extends BaseGameActivity {

    private static final String TAG = "GameFinishedActivity";

    private int mScore;
    private Difficulty mDifficulty;

    @InjectView(R.id.puzzle_image)
    ImageView mPuzzleImage;

    @InjectView(R.id.number_of_turns_label)
    TextView mNumberOfTurnsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finished);

        TintHelper.setupTransparentTints(this);
        InsetsHelper.setInsets(this, findViewById(R.id.content_frame), true, false);

        ButterKnife.inject(this);

        SaveGameStateHelper.removeSavedGameState(getApplicationContext());
        beginUserInitiatedSignIn();


        Intent intent = getIntent();
        mScore = intent.getIntExtra("number_of_turns", 0);
        mDifficulty = (Difficulty) intent.getSerializableExtra("difficulty");

        mNumberOfTurnsLabel.setText("Turns: " + mScore);


        try {
            Bitmap puzzle = BitmapGameHelper.parseBitmapFromPrivateStorage(getApplicationContext().getDir(Constants.IMAGES_FOLDER, Context.MODE_PRIVATE).toString(), Constants.PUZZLE_IMAGE_NAME);
            mPuzzleImage.setImageBitmap(puzzle);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Could not read image from internal storage");
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, GameSelectionActivity.class);

            startActivity(intent);

            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {
        Games.Leaderboards.submitScore(getApiClient(), mDifficulty.getLeaderboardId(), mScore);

        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), mDifficulty.getLeaderboardId()), 123);
    }
}

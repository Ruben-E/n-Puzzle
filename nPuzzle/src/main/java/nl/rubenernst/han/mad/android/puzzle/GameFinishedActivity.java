package nl.rubenernst.han.mad.android.puzzle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.rubenernst.han.mad.android.puzzle.helpers.BitmapGameHelper;
import nl.rubenernst.han.mad.android.puzzle.helpers.SaveGameStateHelper;
import nl.rubenernst.han.mad.android.puzzle.utils.Constants;

import java.io.FileNotFoundException;

/**
 * Created by rubenernst on 28-03-14.
 */
public class GameFinishedActivity extends ActionBarActivity {

    private static final String TAG = "GameFinishedActivity";

    @InjectView(R.id.puzzle_image)
    ImageView mPuzzleImage;

    @InjectView(R.id.number_of_turns_label)
    TextView mNumberOfTurnsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finished);

        ButterKnife.inject(this);

        SaveGameStateHelper.removeSavedGameState(getApplicationContext());

        Intent intent = getIntent();

        mNumberOfTurnsLabel.setText("Turns: " + intent.getIntExtra("number_of_turns", 0));

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
}

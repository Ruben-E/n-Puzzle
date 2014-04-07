package nl.rubenernst.han.mad.android.puzzle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by rubenernst on 28-03-14.
 */
public class GameFinishedActivity extends ActionBarActivity {

    @InjectView(R.id.puzzle_image)
    ImageView mPuzzleImage;

    @InjectView(R.id.number_of_turns_label)
    TextView mNumberOfTurnsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finished);

        ButterKnife.inject(this);

        Intent intent = getIntent();

        mPuzzleImage.setImageDrawable(getResources().getDrawable(intent.getIntExtra("puzzle_drawable_id", 0)));
        mNumberOfTurnsLabel.setText("Turns: " + intent.getIntExtra("number_of_turns", 0));

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

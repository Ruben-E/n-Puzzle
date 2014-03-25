package nl.rubenernst.han.mad.android.puzzle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import nl.rubenernst.han.mad.android.puzzle.domain.CorrectPosition;
import nl.rubenernst.han.mad.android.puzzle.domain.CurrentPosition;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;

import java.lang.reflect.Field;

public class startPuzzleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_puzzle);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_start_puzzle, container, false);

            LinearLayout puzzleChoises = (LinearLayout) rootView.findViewById(R.id.puzzle_choices);
            for (int i = 0; i < Constants.PUZZLES.length; i++) {
                try {
                    String puzzle = Constants.PUZZLES[i];
                    String puzzleImageName = "ic_puzzle_" + (i + 1);

                    Class res = R.drawable.class;
                    Field field = res.getField(puzzleImageName);
                    int drawableId = field.getInt(null);

                    Bitmap image = BitmapFactory.decodeResource(getResources(), drawableId);
                    LinearLayout puzzleChoice = (LinearLayout) inflater.inflate(R.layout.puzzle_choice, null);

                    ImageView puzzleImage = (ImageView) puzzleChoice.findViewById(R.id.puzzle_image);
                    Button puzzleButton = (Button) puzzleChoice.findViewById(R.id.puzzle_button);

                    puzzleButton.setText(puzzle);
                    puzzleButton.setOnClickListener(this);
                    puzzleButton.setTag(i);


                    puzzleImage.setImageBitmap(image);

                    puzzleChoises.addView(puzzleChoice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return rootView;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), puzzleGameActivity.class);
            intent.putExtra("puzzle", (Integer) view.getTag());
            intent.putExtra("difficulty", getDifficulty());

            getActivity().startActivity(intent);
        }

        private Constants.Difficulty getDifficulty() {
            RadioGroup difficultyGroup = (RadioGroup) getView().findViewById(R.id.difficulty);

            int radioButtonID = difficultyGroup.getCheckedRadioButtonId();
            View radioButton = difficultyGroup.findViewById(radioButtonID);

            if (radioButton != null) {
                String tag = (String) radioButton.getTag();
                Constants.Difficulty difficulty = Constants.Difficulty.valueOf(tag.toUpperCase());

                if (difficulty != null) {
                    return difficulty;
                }
            }

            return Constants.Difficulty.NORMAL;
        }
    }

    public void testDomain() {
        String tag = "DomainTest";

        Game game = new Game();
        game.setGridSize(4);

        for (int i = 0; i < 15; i++) {
            CurrentPosition currentPosition = new CurrentPosition();
            currentPosition.setGame(game);
            currentPosition.setPosition(i);

            CorrectPosition correctPosition = new CorrectPosition();
            correctPosition.setPosition(i);

            currentPosition.setCorrectPosition(correctPosition);

            game.addCurrentPosition(currentPosition);
        }

        CurrentPosition currentPosition0 = game.getCurrentPositionAt(14);

        if (game.allPositionsCorrect()) {
            Log.d(tag, "All positions correct");
        } else {
            Log.d(tag, "Some positions not correct");
        }

        currentPosition0.move();

        if (game.allPositionsCorrect()) {
            Log.d(tag, "All positions correct");
        } else {
            Log.d(tag, "Some positions not correct");
        }

        for (CurrentPosition currentPosition : game.getCurrentPositions()) {
            Log.d(tag, "Position: " + currentPosition.getPosition());
        }


        currentPosition0 = game.getCurrentPositionAt(10);

        currentPosition0.move();

        if (game.allPositionsCorrect()) {
            Log.d(tag, "All positions correct");
        } else {
            Log.d(tag, "Some positions not correct");
        }


        for (CurrentPosition currentPosition : game.getCurrentPositions()) {
            Log.d(tag, "Position: " + currentPosition.getPosition());
        }
    }

}

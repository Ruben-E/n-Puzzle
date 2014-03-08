package nl.rubenernst.han.mad.android.puzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    public static class PlaceholderFragment extends Fragment {

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

                    Class res = R.drawable.class;
                    Field field = res.getField("ic_puzzle_" + (i + 1));
                    int drawableId = field.getInt(null);

                    Bitmap image = BitmapFactory.decodeResource(getResources(), drawableId);
                    LinearLayout puzzleChoice = (LinearLayout) inflater.inflate(R.layout.puzzle_choice, null);

                    ImageView puzzleImage = (ImageView) puzzleChoice.findViewById(R.id.puzzle_image);
                    Button puzzleButton = (Button) puzzleChoice.findViewById(R.id.puzzle_button);

                    puzzleButton.setText(puzzle);
                    puzzleImage.setImageBitmap(image);

                    puzzleChoises.addView(puzzleChoice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return rootView;
        }
    }

}

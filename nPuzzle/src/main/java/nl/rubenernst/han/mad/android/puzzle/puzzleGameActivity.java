package nl.rubenernst.han.mad.android.puzzle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import nl.rubenernst.han.mad.android.puzzle.domain.CorrectPosition;
import nl.rubenernst.han.mad.android.puzzle.domain.CurrentPosition;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;

import java.util.*;

/**
 * Created by rubenernst on 14-03-14.
 */
public class puzzleGameActivity extends ActionBarActivity {
    protected String test = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_game);

        Intent intent = getIntent();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new puzzleGameFragment())
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

    public static class puzzleGameFragment extends Fragment {

        private Game game;
        private Integer gridSize;

        public puzzleGameFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            gridSize = 4;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_puzzle_game, container, false);

            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            startGame();
            updateUI();
        }

        public void updateUI() {
            LinearLayout grid = (LinearLayout) getView().findViewById(R.id.grid);
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            Bitmap icon = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ic_puzzle_1);

            grid.removeAllViews();

            HashMap<Integer, CurrentPosition> currentGrid = game.getCurrentGrid();

            for (int i = 0; i < game.getGridSize(); i++) {
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                for (int j = (i * game.getGridSize()); j < ((i * game.getGridSize()) + game.getGridSize()); j++) {
                    final CurrentPosition currentPosition = currentGrid.get(j);
                    View puzzleGameTile = layoutInflater.inflate(R.layout.puzzle_game_tile, null, false);
                    Button button = (Button) puzzleGameTile.findViewById(R.id.tile_button);

                    if (currentPosition != null) {
                        button.setText(String.valueOf(currentPosition.getCorrectPosition().getPosition()) + " - " + String.valueOf(currentPosition.getPosition()));

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                currentPosition.move();
                                updateUI();
                            }
                        });
                    } else {
                        button.setText("");
                    }

                    layout.addView(puzzleGameTile);
                }

                grid.addView(layout);
            }

            for (int i = 0; i < game.numberOfTiles(); i++) {

            }
        }

        public void startGame() {
            game = new Game();
            game.setGridSize(gridSize);

            Integer tiles = (int) Math.pow(gridSize, 2);

            List<Integer> gridTilesRandomized = new ArrayList<Integer>();
            for (int j = 0; j < (tiles - 1); j++) {
                gridTilesRandomized.add(j);
            }

            // Randomize the grid tiles
            long seed = System.nanoTime();
            Collections.shuffle(gridTilesRandomized, new Random(seed));

            for (int i = 0; i < (tiles - 1); i++) {
                Integer randomPosition = gridTilesRandomized.get(i);

                CurrentPosition currentPosition = new CurrentPosition();
                currentPosition.setGame(game);
                currentPosition.setPosition(randomPosition);

                CorrectPosition correctPosition = new CorrectPosition();
                correctPosition.setPosition(i);

                currentPosition.setCorrectPosition(correctPosition);

                game.addCurrentPosition(currentPosition);
            }
        }
    }
}

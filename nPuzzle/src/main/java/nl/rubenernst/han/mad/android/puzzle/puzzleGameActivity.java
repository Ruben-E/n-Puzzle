package nl.rubenernst.han.mad.android.puzzle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
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
        private final static String TAG = "puzzleGame";
        private Game game;
        private Integer gridSize;
        private List<Bitmap> imageTiles;

        public puzzleGameFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            gridSize = 4;

            imageTiles = new ArrayList<Bitmap>();

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;

            Bitmap icon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.puzzle_2), screenWidth, screenWidth, false);

            int pieceHeight = (int) Math.floor(screenWidth / gridSize);
            int pieceWidth = (int) Math.floor(screenWidth / gridSize);

            for(int i = 0; i < gridSize; i++) {
                for(int j = 0; j < gridSize; j++) {
                    int x = (int) Math.floor(j * pieceWidth);
                    int y = (int) Math.floor(i * pieceHeight);

                    Bitmap tile = Bitmap.createBitmap(icon, x, y, pieceWidth, pieceHeight);
                    imageTiles.add(tile);
                }
            }
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

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;

            grid.removeAllViews();

            HashMap<Integer, CurrentPosition> currentGrid = game.getCurrentGrid();

            for (int i = 0; i < game.getGridSize(); i++) {
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                for (int j = (i * game.getGridSize()); j < ((i * game.getGridSize()) + game.getGridSize()); j++) {
                    final CurrentPosition currentPosition = currentGrid.get(j);
                    View puzzleGameTile = layoutInflater.inflate(R.layout.puzzle_game_tile, null, false);
                    ImageButton button = (ImageButton) puzzleGameTile.findViewById(R.id.tile_button);
                    button.setLayoutParams(new LinearLayout.LayoutParams(screenWidth / game.getGridSize(), screenWidth / game.getGridSize()));

                    if (currentPosition != null) {
                        //button.setText(String.valueOf(currentPosition.getCorrectPosition().getPosition()) + " - " + String.valueOf(currentPosition.getPosition()));
                        button.setImageBitmap(imageTiles.get(currentPosition.getCorrectPosition().getPosition()));

                        button.setOnTouchListener(new OnTileTouchListener(getActivity().getApplicationContext()) {
                            @Override
                            public void onPress() {
                                currentPosition.move();
                                updateUI();
                            }

                            @Override
                            public void onSwipeTop() {
                                Toast.makeText(getActivity().getApplicationContext(), "Swipe top on: " + currentPosition.getPosition(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSwipeBottom() {
                                Toast.makeText(getActivity().getApplicationContext(), "Swipe bottom on: " + currentPosition.getPosition(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSwipeLeft() {
                                Toast.makeText(getActivity().getApplicationContext(), "Swipe left on: " + currentPosition.getPosition(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSwipeRight() {
                                Toast.makeText(getActivity().getApplicationContext(), "Swipe Right on: " + currentPosition.getPosition(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        //button.setText("");
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

//            List<Integer> gridTilesRandomized = new ArrayList<Integer>();
//            for (int j = 0; j < (tiles - 1); j++) {
//                gridTilesRandomized.add(j);
//            }
//
//            // Randomize the grid tiles
//            long seed = System.nanoTime();
//            Collections.shuffle(gridTilesRandomized, new Random(seed));

            for (int i = 0; i < (tiles - 1); i++) {
                //Integer randomPosition = gridTilesRandomized.get(i);

                CurrentPosition currentPosition = new CurrentPosition();
                currentPosition.setGame(game);
                currentPosition.setPosition(i);

                CorrectPosition correctPosition = new CorrectPosition();
                correctPosition.setPosition(i);

                currentPosition.setCorrectPosition(correctPosition);

                game.addCurrentPosition(currentPosition);
            }

            game.randomize();
        }
    }
}

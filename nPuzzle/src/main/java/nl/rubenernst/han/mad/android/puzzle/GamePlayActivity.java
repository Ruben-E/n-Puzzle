package nl.rubenernst.han.mad.android.puzzle;

import android.content.Intent;
import android.graphics.*;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.rubenernst.han.mad.android.puzzle.domain.*;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskFinishedListener;
import nl.rubenernst.han.mad.android.puzzle.tasks.GameInitializationTask;
import nl.rubenernst.han.mad.android.puzzle.utils.Constants;
import nl.rubenernst.han.mad.android.puzzle.utils.OnTouchListener;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by rubenernst on 14-03-14.
 */
public class GamePlayActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_puzzle_game);

            Intent intent = getIntent();
            Constants.Difficulty difficulty = (Constants.Difficulty) intent.getSerializableExtra("difficulty");
            Integer puzzleId = intent.getIntExtra("puzzle", 0);

            String puzzleImageName = "puzzle_" + (puzzleId + 1);

            Class res = R.drawable.class;
            Field field = res.getField(puzzleImageName);

            Integer puzzleDrawableId = field.getInt(null);

            PuzzleGameFragment puzzleGameFragment = new PuzzleGameFragment();
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

    public static class PuzzleGameFragment extends Fragment {
        private final static String TAG = "puzzleGame";
        private static final String CORRECT_COLOR = "#659D32";

        private Game mGame;
        private Integer mGridSize;
        private Integer mPuzzleDrawableId;
        private List<Bitmap> mImageTiles;
        private Constants.Difficulty mDifficulty;
        private Boolean mIsPlayable = false;

        @InjectView(R.id.status_text)
        TextView statusText;
        @InjectView(R.id.grid)
        RelativeLayout grid;

        public PuzzleGameFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mGridSize = Constants.DIFFICULTY_GRIDSIZE.get(getDifficulty());
            mImageTiles = new ArrayList<Bitmap>();

            splicePuzzle();
        }

        private void splicePuzzle() {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;

            Bitmap icon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), mPuzzleDrawableId), screenWidth, screenWidth, false);

            int pieceHeight = (int) Math.floor(screenWidth / mGridSize);
            int pieceWidth = (int) Math.floor(screenWidth / mGridSize);

            for (int i = 0; i < mGridSize; i++) {
                for (int j = 0; j < mGridSize; j++) {
                    int x = (int) Math.floor(j * pieceWidth);
                    int y = (int) Math.floor(i * pieceHeight);

                    Bitmap tile = Bitmap.createBitmap(icon, x, y, pieceWidth, pieceHeight);
                    tile = addBlackBorder(tile, 1);
                    mImageTiles.add(tile);
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_puzzle_game, container, false);

            ButterKnife.inject(this, view);

            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            setupGame();
            updateUI();

            startGame();
        }

        private void startGame() {
            statusText.setText("Loading...");

            //TODO: Fix crash when user presses the back button
            final CountDownTimer countDownTimer = new CountDownTimer(4000, 1000) {
                public void onTick(long millisUntilFinished) {
                    statusText.setText("" + (int) Math.floor(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    if (statusText != null) {
                        statusText.setText("GO!");
                        mIsPlayable = true;

                        updateUI();
                    }
                }
            };

            GameInitializationTask gameInitializationTask = new GameInitializationTask();
            gameInitializationTask.setTaskFinishedListener(new TaskFinishedListener() {
                @Override
                public void onTaskFinished(Object result, String message) {
                    if (result instanceof Game) {
                        PuzzleGameFragment.this.mGame = (Game) result;
                        countDownTimer.start();
                    }
                }
            });

            gameInitializationTask.execute(mGame);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.reset(this);
        }

        public void updateUI() {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            HashMap<Integer, CurrentPosition> currentGrid = mGame.getCurrentGrid();

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            Integer screenWidth = size.x;

            Integer buttonWidth = screenWidth / mGame.getGridSize();

            grid.removeAllViews();

            for (int i = 0; i < mGame.getGridSize(); i++) {
                for (int j = 0; j < mGame.getGridSize(); j++) {
                    final Integer tileNumber = (i * mGame.getGridSize()) + j;
                    final CurrentPosition currentPosition = currentGrid.get(tileNumber);
                    final View puzzleGameTile = layoutInflater.inflate(R.layout.puzzle_game_tile, null, false);
                    final ImageButton tileButton = ButterKnife.findById(puzzleGameTile, R.id.tile_button);

                    Integer x = buttonWidth * j;
                    Integer y = buttonWidth * i;

                    RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(screenWidth / mGame.getGridSize(), screenWidth / mGame.getGridSize());
                    buttonParams.addRule(RelativeLayout.ALIGN_LEFT, 1);
                    buttonParams.addRule(RelativeLayout.ALIGN_TOP, 1);
                    buttonParams.setMargins(x, y, 0, 0);

                    tileButton.setLayoutParams(buttonParams);

                    if (currentPosition != null) {
                        tileButton.setImageBitmap(currentPosition.getImage().getBitmap());

                        tileButton.setOnTouchListener(new OnTouchListener(getActivity().getApplicationContext()) {
                            @Override
                            public void onPress() {
                                if (mIsPlayable) {
                                    currentPosition.move();
                                    updateUI();
                                }
                            }

                            @Override
                            public void onLongPress() {
                                if (mIsPlayable) {
                                    final ImageButton correctButton = (ImageButton) grid.getChildAt(currentPosition.getCorrectPosition().getPosition());
                                    final CurrentPosition correctPosition = mGame.getCurrentPositionAt(currentPosition.getCorrectPosition().getPosition());

                                    correctButton.setBackgroundColor(Color.parseColor(CORRECT_COLOR));
                                    correctButton.setImageBitmap(null);

                                    new CountDownTimer(1500, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                        }

                                        public void onFinish() {
                                            if (correctButton != null && correctPosition != null) {
                                                correctButton.setImageBitmap(correctPosition.getImage().getBitmap());
                                            } else if (correctPosition == null) {
                                                correctButton.getBackground().setAlpha(256);
                                            }
                                        }
                                    }.start();
                                }
                            }

                            @Override
                            public void onSwipeTop() {
                                if (mIsPlayable) {
                                    currentPosition.moveToDirection(Position.Directions.TOP);
                                    updateUI();
                                }
                            }

                            @Override
                            public void onSwipeBottom() {
                                if (mIsPlayable) {
                                    currentPosition.moveToDirection(Position.Directions.BOTTOM);
                                    updateUI();
                                }
                            }

                            @Override
                            public void onSwipeLeft() {
                                if (mIsPlayable) {
                                    currentPosition.moveToDirection(Position.Directions.LEFT);
                                    updateUI();
                                }
                            }

                            @Override
                            public void onSwipeRight() {
                                if (mIsPlayable) {
                                    currentPosition.moveToDirection(Position.Directions.RIGHT);
                                    updateUI();
                                }
                            }
                        });

                    } else {
                        tileButton.getBackground().setAlpha(256);
                    }

                    grid.addView(tileButton);
                }
            }
        }

        public void setupGame() {
            mGame = new Game();
            mGame.setGridSize(mGridSize);

            Integer tiles = (int) Math.pow(mGridSize, 2);

            for (int i = 0; i < (tiles - 1); i++) {
                CurrentPosition currentPosition = new CurrentPosition();
                currentPosition.setGame(mGame);
                currentPosition.setPosition(i);

                Image image = new Image();
                image.setBitmap(mImageTiles.get(i));

                currentPosition.setImage(image);

                CorrectPosition correctPosition = new CorrectPosition();
                correctPosition.setPosition(i);

                currentPosition.setCorrectPosition(correctPosition);

                mGame.addCurrentPosition(currentPosition);
            }

            mImageTiles = null;
        }

        public void setDifficulty(Constants.Difficulty difficulty) {
            this.mDifficulty = difficulty;
        }

        public Constants.Difficulty getDifficulty() {
            if (mDifficulty == null) {
                mDifficulty = Constants.Difficulty.NORMAL;
            }

            return mDifficulty;
        }

        public void setPuzzleDrawableId(Integer puzzleDrawableId) {
            this.mPuzzleDrawableId = puzzleDrawableId;
        }

        //TODO: Improve border. Now there is no border at the bottom and the right side
        private Bitmap addBlackBorder(Bitmap bmp, int borderSize) {
            Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
            Canvas canvas = new Canvas(bmpWithBorder);
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(bmp, borderSize, borderSize, null);
            return bmpWithBorder;
        }
    }
}

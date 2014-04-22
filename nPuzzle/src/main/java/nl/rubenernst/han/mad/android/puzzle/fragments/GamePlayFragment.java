package nl.rubenernst.han.mad.android.puzzle.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.*;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.rubenernst.han.mad.android.puzzle.GameFinishedActivity;
import nl.rubenernst.han.mad.android.puzzle.R;
import nl.rubenernst.han.mad.android.puzzle.domain.*;
import nl.rubenernst.han.mad.android.puzzle.helpers.BitmapGameHelper;
import nl.rubenernst.han.mad.android.puzzle.helpers.SaveGameStateHelper;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskFinishedListener;
import nl.rubenernst.han.mad.android.puzzle.tasks.GameInitializationTask;
import nl.rubenernst.han.mad.android.puzzle.utils.Constants;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;
import nl.rubenernst.han.mad.android.puzzle.utils.OnTouchListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rubenernst on 28-03-14.
 */
public class GamePlayFragment extends Fragment {
    private final static String TAG = "puzzleGame";
    private static final String CORRECT_COLOR = "#659D32";
    public static final int COUNTDOWN_TIMER_MILISECONDS = 3000;
    public static final int COUNTDOWN_INTERVAL = 500;
    public static final int BORDER_SIZE = 1;

    private LayoutInflater mLayoutInflater;

    private Game mGame;
    private Integer mGridSize;
    private Integer mPuzzleDrawableId;
    private List<Bitmap> mImageTiles;
    private Difficulty mDifficulty;
    private CountDownTimer mCountDownTimer;
    private Bitmap mEmptyTile;
    private Bitmap mCorrectTile;
    private boolean mUnfinishedGame;

    @InjectView(R.id.game_layout)
    RelativeLayout mGameLayout;

    @InjectView(R.id.status_bar)
    LinearLayout mStatusBar;

    @InjectView(R.id.grid)
    RelativeLayout mGrid;

    public GamePlayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayoutInflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (mUnfinishedGame) {
            onCreateUnfinishedGame();
        } else {
            onCreateNewGame();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        final View view = getView();

        ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                updateUI();
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        SaveGameStateHelper.saveGameState(getActivity().getApplicationContext(), mGame);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_play, container, false);

        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mUnfinishedGame) {
            onViewCreatedUnfinishedGame();
        } else {
            onViewCreatedNewGame();
        }
    }

    private void onViewCreatedUnfinishedGame() {
        setStatusBarContent(R.layout.fragment_game_play_statusbar_playing);

        updateUI();
    }

    private void onViewCreatedNewGame() {
        updateUI();

        startGame();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCountDownTimer != null && !mGame.isPlayable()) {
            mCountDownTimer.start();
        }
    }

    private void onCreateNewGame() {
        mGridSize = getDifficulty().getGridSize();

        splicePuzzle();
        setupGame();
    }

    private void onCreateUnfinishedGame() {
        mGame = SaveGameStateHelper.getSavedGameState(getActivity().getApplicationContext());

        if(mGame == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Could not load the game", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        mGridSize = mGame.getGridSize();
    }

    private void splicePuzzle() {
        int pieceWidth = getPieceWidth();
        int orientationWidth = getOrientationWidth();

        Bitmap puzzle = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), mPuzzleDrawableId), orientationWidth, orientationWidth, false);
        ArrayList<Bitmap> bitmaps = BitmapGameHelper.spliceBitmap(puzzle, mGridSize, pieceWidth);
        mImageTiles = BitmapGameHelper.addBorderAroundBitmaps(bitmaps, BORDER_SIZE);

        try {
            BitmapGameHelper.writeBitmapToPrivateStorage(getActivity().getApplicationContext(), puzzle, Constants.IMAGES_FOLDER, Constants.PUZZLE_IMAGE_NAME);
        } catch (IOException e) {
            Log.e(TAG, "Could not write puzzle bitmap to internal storage");
        }
    }

    private void startGame() {
        setStatusBarContent(R.layout.fragment_game_play_statusbar_initializing);
        final TextView statusText = ButterKnife.findById(mStatusBar, R.id.status_initializing);

        statusText.setText("Loading...");

        mCountDownTimer = new CountDownTimer(COUNTDOWN_TIMER_MILISECONDS, COUNTDOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                statusText.setText("" + (int) Math.ceil(millisUntilFinished / 1000d));
            }

            public void onFinish() {
                mCountDownTimer = null;
                if (mStatusBar != null) {
                    setStatusBarContent(R.layout.fragment_game_play_statusbar_playing);

                    TextView statusText = ButterKnife.findById(mStatusBar, R.id.status_playing);
                    statusText.setText("Turns: 0");

                    mGame.startGame();

                    updateUI();
                }
            }
        };

        GameInitializationTask gameInitializationTask = new GameInitializationTask();
        gameInitializationTask.setTaskFinishedListener(new TaskFinishedListener() {
            @Override
            public void onTaskFinished(Object result, String message) {
                if (result instanceof Game) {
                    GamePlayFragment.this.mGame = (Game) result;
                    mCountDownTimer.start();
                }
            }
        });

        gameInitializationTask.execute(mGame);
    }

    private int getOrientationWidth() {
        DisplayMetrics display = this.getResources().getDisplayMetrics();

        int screenWidth = display.widthPixels;
        int screenHeight = display.heightPixels;

        int aspect = screenWidth;
        if (screenHeight < screenWidth) {
            aspect = screenHeight;
        }

        return aspect;
    }

    private int getGridWidth() {
        DisplayMetrics display = this.getResources().getDisplayMetrics();

        int screenWidth = display.widthPixels;
        int screenHeight = display.heightPixels;

        int aspect = screenWidth;
        if (screenHeight < screenWidth) {
            View content = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);

            aspect = content.getHeight();
        }

        return aspect;
    }

    private int getPieceWidth() {
        return (int) Math.floor(getOrientationWidth() / mGridSize);
    }

    private void updateLayoutPositions() {
        int gridWidth = getGridWidth();

        RelativeLayout.LayoutParams statusBarParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics()));
        statusBarParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        statusBarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mStatusBar.setLayoutParams(statusBarParams);

        RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(gridWidth, gridWidth);
        gridParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        gridParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mGrid.setLayoutParams(gridParams);
    }

    public void updateUI() {
        updateLayoutPositions();

        if (mGame.isPlayable() && mGame.allPositionsCorrect()) {
            SaveGameStateHelper.removeSavedGameState(getActivity().getApplicationContext());

            setStatusBarContent(R.layout.fragment_game_play_statusbar_finished);

            TextView statusText = ButterKnife.findById(mStatusBar, R.id.status_finished);

            statusText.setText("You won!");

            Intent intent = new Intent(getActivity(), GameFinishedActivity.class);
            intent.putExtra("number_of_turns", mGame.getTurns().size());

            startActivity(intent);
        } else if (mGame.isPlayable()) {
            TextView statusText = ButterKnife.findById(mStatusBar, R.id.status_playing);
            statusText.setText("Turns: " + mGame.getTurns().size());
        }

        HashMap<Integer, CurrentPosition> currentGrid = mGame.getCurrentGrid();

        int aspect = getGridWidth();

        Integer buttonWidth = aspect / mGame.getGridSize();

        mGrid.removeAllViews();

        for (int i = 0; i < mGame.getGridSize(); i++) {
            for (int j = 0; j < mGame.getGridSize(); j++) {
                final Integer tileNumber = (i * mGame.getGridSize()) + j;
                final CurrentPosition currentPosition = currentGrid.get(tileNumber);
                final View puzzleGameTile = mLayoutInflater.inflate(R.layout.puzzle_game_tile, null, false);
                final ImageButton tileButton = ButterKnife.findById(puzzleGameTile, R.id.tile_button);

                Integer x = buttonWidth * j;
                Integer y = buttonWidth * i;

                RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(aspect / mGame.getGridSize(), aspect / mGame.getGridSize());
                buttonParams.addRule(RelativeLayout.ALIGN_LEFT, 1);
                buttonParams.addRule(RelativeLayout.ALIGN_TOP, 1);
                buttonParams.setMargins(x, y, 0, 0);

                tileButton.setLayoutParams(buttonParams);

                if (currentPosition != null) {
                    tileButton.setImageBitmap(currentPosition.getImage().getBitmap());

                    tileButton.setOnTouchListener(new OnTouchListener(getActivity().getApplicationContext()) {
                        @Override
                        public void onPress() {
                            if (mGame.isPlayable()) {
                                currentPosition.move();
                                updateUI();
                            }
                        }

                        @Override
                        public void onLongPress() {
                            if (mGame.isPlayable()) {
                                final ImageButton correctButton = (ImageButton) mGrid.getChildAt(currentPosition.getCorrectPosition().getPosition());
                                final CurrentPosition correctPosition = mGame.getCurrentPositionAt(currentPosition.getCorrectPosition().getPosition());

                                // correctButton.setBackgroundColor(Color.parseColor(CORRECT_COLOR));
                                correctButton.setImageBitmap(getCorrectTile());

                                new CountDownTimer(1500, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    public void onFinish() {
                                        if (correctButton != null && correctPosition != null) {
                                            correctButton.setImageBitmap(correctPosition.getImage().getBitmap());
                                        } else if (correctPosition == null) {
                                            correctButton.setImageBitmap(getEmptyTile());
                                        }
                                    }
                                }.start();
                            }
                        }

                        @Override
                        public void onSwipeTop() {
                            if (mGame.isPlayable()) {
                                currentPosition.moveToDirection(Position.Directions.TOP);
                                updateUI();
                            }
                        }

                        @Override
                        public void onSwipeBottom() {
                            if (mGame.isPlayable()) {
                                currentPosition.moveToDirection(Position.Directions.BOTTOM);
                                updateUI();
                            }
                        }

                        @Override
                        public void onSwipeLeft() {
                            if (mGame.isPlayable()) {
                                currentPosition.moveToDirection(Position.Directions.LEFT);
                                updateUI();
                            }
                        }

                        @Override
                        public void onSwipeRight() {
                            if (mGame.isPlayable()) {
                                currentPosition.moveToDirection(Position.Directions.RIGHT);
                                updateUI();
                            }
                        }
                    });

                } else {
                    //tileButton.getBackground().setAlpha(256);
                    tileButton.setImageBitmap(getEmptyTile());
                }

                mGrid.addView(tileButton);
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
    }

    public void setDifficulty(Difficulty difficulty) {
        this.mDifficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        if (mDifficulty == null) {
            mDifficulty = Difficulty.MEDIUM;
        }

        return mDifficulty;
    }

    public void setPuzzleDrawableId(Integer puzzleDrawableId) {
        this.mPuzzleDrawableId = puzzleDrawableId;
    }

    private Bitmap generateEmptyTile(int width, int height) {
        Bitmap emptyTile = BitmapGameHelper.createSolidBitmap(width, height, Color.WHITE);
        return BitmapGameHelper.addBorderAroundBitmap(emptyTile, BORDER_SIZE);
    }

    private Bitmap generateCorrectTile(int width, int height) {
        Bitmap correctTile = BitmapGameHelper.createSolidBitmap(width, height, Color.parseColor(CORRECT_COLOR));
        return BitmapGameHelper.addBorderAroundBitmap(correctTile, BORDER_SIZE);
    }

    private void setStatusBarContent(int layoutId) {
        View content = mLayoutInflater.inflate(layoutId, null, false);
        mStatusBar.removeAllViews();
        mStatusBar.addView(content);
    }

    public void setUnfinishedGame(Boolean unfinishedGame) {
        this.mUnfinishedGame = unfinishedGame;
    }

    public Bitmap getEmptyTile() {
        if(mEmptyTile == null) {
            int pieceWidth = getPieceWidth();

            mEmptyTile = generateEmptyTile(pieceWidth, pieceWidth);
        }

        return mEmptyTile;
    }

    public Bitmap getCorrectTile() {
        if (mCorrectTile == null) {
            int pieceWidth = getPieceWidth();

            mCorrectTile = generateCorrectTile(pieceWidth, pieceWidth);
        }

        return mCorrectTile;
    }
}

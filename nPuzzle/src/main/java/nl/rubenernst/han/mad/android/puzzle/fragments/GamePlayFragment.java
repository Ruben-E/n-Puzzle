package nl.rubenernst.han.mad.android.puzzle.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.rubenernst.han.mad.android.puzzle.GameFinishedActivity;
import nl.rubenernst.han.mad.android.puzzle.R;
import nl.rubenernst.han.mad.android.puzzle.domain.*;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskFinishedListener;
import nl.rubenernst.han.mad.android.puzzle.tasks.GameInitializationTask;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;
import nl.rubenernst.han.mad.android.puzzle.utils.OnTouchListener;

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
        mGridSize = getDifficulty().getGridSize();
        mImageTiles = new ArrayList<Bitmap>();

        splicePuzzle();
    }

    private void splicePuzzle() {
        DisplayMetrics display = this.getResources().getDisplayMetrics();

        int screenWidth = display.widthPixels;
        int screenHeight = display.heightPixels;

        Bitmap icon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), mPuzzleDrawableId), screenWidth, screenWidth, false);

        int pieceHeight = (int) Math.floor(screenWidth / mGridSize);
        int pieceWidth = (int) Math.floor(screenWidth / mGridSize);

        mEmptyTile = generateEmptyTile(pieceWidth, pieceHeight);

        for (int i = 0; i < mGridSize; i++) {
            for (int j = 0; j < mGridSize; j++) {
                int x = (int) Math.floor(j * pieceWidth);
                int y = (int) Math.floor(i * pieceHeight);

                Bitmap tile = Bitmap.createBitmap(icon, x, y, pieceWidth, pieceHeight);
                tile = addBorderToBitmap(tile, BORDER_SIZE);
                mImageTiles.add(tile);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_play, container, false);

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
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
    }

    public void updateUI() {
        if (mGame.isPlayable() && mGame.allPositionsCorrect()) {
            setStatusBarContent(R.layout.fragment_game_play_statusbar_finished);

            TextView statusText = ButterKnife.findById(mStatusBar, R.id.status_finished);

            statusText.setText("You won!");

            Intent intent = new Intent(getActivity(), GameFinishedActivity.class);
            intent.putExtra("puzzle_drawable_id", mPuzzleDrawableId);
            intent.putExtra("number_of_turns", mGame.getTurns().size());

            startActivity(intent);
        } else if (mGame.isPlayable()) {
            TextView statusText = ButterKnife.findById(mStatusBar, R.id.status_playing);
            statusText.setText("Turns: " + mGame.getTurns().size());
        }

        HashMap<Integer, CurrentPosition> currentGrid = mGame.getCurrentGrid();

        DisplayMetrics display = this.getResources().getDisplayMetrics();

        int screenWidth = display.widthPixels;
        int screenHeight = display.heightPixels;

        Integer buttonWidth = screenWidth / mGame.getGridSize();

        mGrid.removeAllViews();

        for (int i = 0; i < mGame.getGridSize(); i++) {
            for (int j = 0; j < mGame.getGridSize(); j++) {
                final Integer tileNumber = (i * mGame.getGridSize()) + j;
                final CurrentPosition currentPosition = currentGrid.get(tileNumber);
                final View puzzleGameTile = mLayoutInflater.inflate(R.layout.puzzle_game_tile, null, false);
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
                    tileButton.setImageBitmap(mEmptyTile);
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

    private Bitmap addBorderToBitmap(Bitmap bmp, int borderSize, int colorCode) {
        Canvas canvas = new Canvas(bmp);
        Paint p = new Paint();
        p.setStrokeWidth(borderSize);
        p.setColor(colorCode);

        canvas.drawLine(0, 0, bmp.getWidth(), 0, p);
        canvas.drawLine(bmp.getWidth() - borderSize, 0, bmp.getWidth() - borderSize, bmp.getHeight(), p);
        canvas.drawLine(bmp.getWidth(), bmp.getHeight() - borderSize, 0, bmp.getHeight() - borderSize, p);
        canvas.drawLine(0, bmp.getHeight(), 0, 0, p);

        return bmp;
    }

    private Bitmap addBorderToBitmap(Bitmap bmp, int borderSize) {
        return addBorderToBitmap(bmp, borderSize, Color.BLACK);
    }

    private Bitmap generateEmptyTile(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        return addBorderToBitmap(bitmap, BORDER_SIZE);
    }

    private void setStatusBarContent(int layoutId) {
        View content = mLayoutInflater.inflate(layoutId, null, false);
        mStatusBar.removeAllViews();
        mStatusBar.addView(content);
    }
}
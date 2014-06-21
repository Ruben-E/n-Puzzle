package nl.rubenernst.han.mad.android.puzzle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.afollestad.cardsui.*;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.android.gms.location.LocationClient;
import com.google.example.games.basegameutils.BaseGameActivity;
import nl.rubenernst.han.mad.android.puzzle.domain.Game;
import nl.rubenernst.han.mad.android.puzzle.fragments.GamePlayFragment;
import nl.rubenernst.han.mad.android.puzzle.helpers.LocationHelper;
import nl.rubenernst.han.mad.android.puzzle.helpers.MatchHelper;
import nl.rubenernst.han.mad.android.puzzle.helpers.SaveGameStateHelper;
import nl.rubenernst.han.mad.android.puzzle.interfaces.GamePlayListener;
import nl.rubenernst.han.mad.android.puzzle.interfaces.GamePlayStatusViewAdapter;
import nl.rubenernst.han.mad.android.puzzle.interfaces.LocationHelperListener;
import nl.rubenernst.han.mad.android.puzzle.interfaces.TaskFinishedListener;
import nl.rubenernst.han.mad.android.puzzle.tasks.*;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MultiplayerGamePlayActivity extends BaseGameActivity implements GamePlayListener, LocationHelperListener, GamePlayStatusViewAdapter {

    @InjectView(R.id.loading_container)
    RelativeLayout loadingContainer;

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @InjectView(R.id.loadingText)
    TextView loadingText;

    private static final String TAG = "Multiplayer";
    private static final String ORIGINAL_GAME_KEY = "original_game";

    protected TurnBasedMatch mMatch;
    protected String mCurrentPlayerParticipantId;
    protected HashMap<String, Game> mGames; // Hashmap with a game for all the players with participant id as key.
    protected LocationHelper mLocationHelper;
    protected LocationClient mLocationClient;
    protected int puzzleId;
    protected Difficulty difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game_play);

        mLocationHelper = new LocationHelper(this, this);

        ButterKnife.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mLocationHelper.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.multiplayer_game_play, menu);
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

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {

    }

    @Override
    protected void onStop() {
        saveGameState();

        mLocationHelper.disconnect();

        super.onStop();
    }

    public void launchMatch() {
        if (mMatch != null) {
            boolean playable = currentMatchIsPlayable();

            mCurrentPlayerParticipantId = getCurrentPlayerParticipantId();

            byte[] data = mMatch.getData();
            if (data != null) {
                mGames = getGamesFromMatchData();
            } else {
                mGames = initialiseGames();
            }

            if (allPlayersPlayed()) {
                showScoresScreen();
            } else if (playable) {
                showGameUI();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void showGameUI() {
        GamePlayFragment gamePlayFragment = new GamePlayFragment();
        if (difficulty != null) {
            gamePlayFragment.setDifficulty(difficulty);
        } else {
            gamePlayFragment.setDifficulty(Difficulty.DUMB);
        }

        if (puzzleId != 0) {
            gamePlayFragment.setPuzzleDrawableId(puzzleId);
        } else {
            gamePlayFragment.setPuzzleDrawableId(R.drawable.puzzle_1);
        }
        gamePlayFragment.setGamePlayListener(this);
        gamePlayFragment.setGamePlayStatusViewAdapter(this);
        if (getCurrentPlayersGame() != null) {
            gamePlayFragment.setUnfinishedGame2(getCurrentPlayersGame());
        } else if (getOriginalGame() != null) {
            gamePlayFragment.setUnfinishedGame2(getOriginalGame());
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, gamePlayFragment)
                .commit();
    }

    public String getNextParticipantId() {
        String myParticipantId = getCurrentPlayerParticipantId();

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    private String getCurrentPlayerId() {
        return Games.Players.getCurrentPlayerId(getApiClient());
    }

    private String getCurrentPlayerParticipantId() {
        String myPlayerId = getCurrentPlayerId();
        return mMatch.getParticipantId(myPlayerId);
    }

    private void saveGameState() {
        if (isSignedIn() && mMatch != null && currentMatchIsPlayable()) {
            GameStatesAsStringTask gameStatesAsStringTask = new GameStatesAsStringTask();
            gameStatesAsStringTask.setContext(getApplicationContext());
            gameStatesAsStringTask.setTaskFinishedListener(new TaskFinishedListener() {
                @Override
                public void onTaskFinished(Object result, String message) {
                    if (result != null) {
                        String gameState = (String) result;
                        byte[] gameStateByteArray = getGameStatesAsByteArray(gameState);

                        List<byte[]> bytes = new ArrayList<byte[]>();
                        bytes.add(gameStateByteArray);

                        BackgroundGameSaverTask backgroundGameSaverTask = new BackgroundGameSaverTask();
                        backgroundGameSaverTask.setGoogleApiClient(getApiClient());
                        backgroundGameSaverTask.setMatchId(mMatch.getMatchId());
                        backgroundGameSaverTask.setParticipantId(mCurrentPlayerParticipantId);
                        backgroundGameSaverTask.execute(bytes);
                    }
                }
            });
            gameStatesAsStringTask.execute(mGames);
        }
    }

    private String getGameStatesAsString() {
        return SaveGameStateHelper.saveGameStatesToString(getApplicationContext(), mGames);
    }

    private byte[] getGameStatesAsByteArray(String gameStatesString) {
        return gameStatesString.getBytes(Charset.forName("UTF-16"));
    }

    private byte[] getGameStatesAsByteArray() {
        return getGameStatesAsString().getBytes(Charset.forName("UTF-16"));
    }

    private HashMap<String, Game> initialiseGames() {
        HashMap<String, Game> gameStates = new HashMap<String, Game>();

        ArrayList<String> participantIds = mMatch.getParticipantIds();
        for (String participantId : participantIds) {
            gameStates.put(participantId, null);
        }

        gameStates.put(ORIGINAL_GAME_KEY, null);

        return gameStates;
    }

    private HashMap<String, Game> getGamesFromMatchData() {
        HashMap<String, Game> games = null;

        try {
            String JSON = new String(mMatch.getData(), "UTF-16");
            games = SaveGameStateHelper.getSavedGameStatesFromJson(getApplicationContext(), JSON);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return games;
    }

    private boolean allPlayersPlayed() {
        for (Participant participant : mMatch.getParticipants()) {
            String participantId = participant.getParticipantId();
            Game participantGame = mGames.get(participantId);

            if (participantGame != null) {
                if (participantGame.isPlayable() && !participantGame.allPositionsCorrect()) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    public void takeTurnWithNextParticipant(final String participantId, final ResultCallback<TurnBasedMultiplayer.UpdateMatchResult> resultCallback) {
        GameStatesAsStringTask gameStatesAsStringTask = new GameStatesAsStringTask();
        gameStatesAsStringTask.setContext(getApplicationContext());
        gameStatesAsStringTask.setTaskFinishedListener(new TaskFinishedListener() {
            @Override
            public void onTaskFinished(Object result, String message) {
                if (result != null) {
                    String gameState = (String) result;
                    byte[] gameStateByteArray = getGameStatesAsByteArray(gameState);

                    if (!getApiClient().isConnected()) {
                        getApiClient().blockingConnect(5000, TimeUnit.MILLISECONDS);
                    }

                    if (getApiClient().isConnected()) {
                        Games.TurnBasedMultiplayer.takeTurn(getApiClient(), mMatch.getMatchId(), gameStateByteArray, participantId)
                                .setResultCallback(resultCallback);
                    }


                }
            }
        });
        gameStatesAsStringTask.execute(mGames);

    }

    public void finishGame(final ResultCallback<TurnBasedMultiplayer.UpdateMatchResult> resultCallback) {
        GameStatesAsStringTask gameStatesAsStringTask = new GameStatesAsStringTask();
        gameStatesAsStringTask.setContext(getApplicationContext());
        gameStatesAsStringTask.setTaskFinishedListener(new TaskFinishedListener() {
            @Override
            public void onTaskFinished(Object result, String message) {
                if (result != null) {
                    String gameState = (String) result;
                    byte[] gameStateByteArray = getGameStatesAsByteArray(gameState);

                    if (getApiClient().isConnected()) {
                        Games.TurnBasedMultiplayer.finishMatch(getApiClient(), mMatch.getMatchId(), gameStateByteArray)
                                .setResultCallback(resultCallback);
                    }
                }
            }
        });
        gameStatesAsStringTask.execute(mGames);

    }

    private void takeNextTurn() {
        showLoadingIndicator("Saving the score...");

        if (allPlayersPlayed()) {
            finishGame(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                @Override
                public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                    processUpdateResult(result);

                    hideLoadingIndicator();

                    showScoresScreen();
                }
            });
        } else {
            String nextParticipantId = getNextParticipantId();
            Log.d(TAG, "Next participant ID: " + nextParticipantId);

            takeTurnWithNextParticipant(nextParticipantId, new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                @Override
                public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                    processUpdateResult(result);

                    hideLoadingIndicator();

                    showScoresScreen();
                }
            });
        }
    }

    private Game getCurrentPlayersGame() {
        return mGames.get(getCurrentPlayerParticipantId());
    }

    private Game getOriginalGame() {
        return mGames.get(ORIGINAL_GAME_KEY);
    }

    public void processCancelResult(TurnBasedMultiplayer.CancelMatchResult result) {
        hideLoadingIndicator();

        checkAndHandleStatusCode(null, result);

        showError("Match",
                "This match is canceled.  All other players will have their game ended.");
    }

    public void processInitiateResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        hideLoadingIndicator();

        TurnBasedMatch match = result.getMatch();
        boolean processed = checkAndHandleStatusCode(match, result);

        if (processed) {
            mMatch = result.getMatch();
            launchMatch();
        }
    }


    public void processLeaveResult(TurnBasedMultiplayer.LeaveMatchResult result) {
        hideLoadingIndicator();

        TurnBasedMatch match = result.getMatch();
        checkAndHandleStatusCode(match, result);

        showError("Left", "You've left this match.");
    }


    public void processUpdateResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        hideLoadingIndicator();

        TurnBasedMatch match = result.getMatch();
        checkAndHandleStatusCode(match, result);
    }

    public void showLoadingIndicator(String text) {
        loadingText.setText(text);
        loadingContainer.setVisibility(View.VISIBLE);
    }

    public void hideLoadingIndicator() {
        loadingText.setText("");
        loadingContainer.setVisibility(View.INVISIBLE);
    }

    public void showError(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                }
        );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void showErrorMessage(TurnBasedMatch match, int statusCode,
                                 int stringId) {

        showError("Error!", getResources().getString(stringId));
    }

    private boolean currentMatchIsPlayable() {
        int matchStatus = mMatch.getStatus();
        int turnStatus = mMatch.getTurnStatus();

        switch (matchStatus) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showError("Canceled!", "This game was canceled!");
                return false;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showError("Expired!", "This game is expired.  So sad!");
                return false;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showError("Waiting for auto-match...",
                        "We're still waiting for an automatch partner.");
                return false;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    showScoresScreen();
                    return false;
                }

                Games.TurnBasedMultiplayer.finishMatch(getApiClient(), mMatch.getMatchId())
                        .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                            @Override
                            public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                                processUpdateResult(result);
                            }
                        });

                showScoresScreen();
                return false;
        }

        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                return true;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                // Should return results.
                showError("Alas...", "It's not your turn.");
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                showError("Good inititative!",
                        "Still waiting for invitations.\n\nBe patient!");
        }

        return false;
    }

    private boolean checkAndHandleStatusCode(TurnBasedMatch match, Result result) {
        int resultStatusCode = result.getStatus().getStatusCode();

        switch (resultStatusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.

                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(match, resultStatusCode,
                        R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(match, resultStatusCode,
                        R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(match, resultStatusCode,
                        R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(match, resultStatusCode,
                        R.string.client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(match, resultStatusCode, R.string.internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(match, resultStatusCode,
                        R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(match, resultStatusCode,
                        R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(match, resultStatusCode, R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + resultStatusCode);
        }

        return false;
    }

    public void rematch() {
        showLoadingIndicator("Loading rematch...");

        Game previousGame = mGames.get(ORIGINAL_GAME_KEY);
        if (previousGame != null) {
            difficulty = previousGame.getDifficulty();
            puzzleId = previousGame.getPuzzleId();
        }

        Games.TurnBasedMultiplayer.rematch(getApiClient(), mMatch.getMatchId()).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                        processInitiateResult(result);
                    }
                }
        );
    }

    public void setLocationToGame() {
        Location lastLocation = mLocationClient.getLastLocation();

        if (lastLocation != null) {
            Log.d(TAG, "Location: " + lastLocation.toString());

            GeocoderTask geocoderTask = new GeocoderTask();
            geocoderTask.setTaskFinishedListener(new TaskFinishedListener() {
                @Override
                public void onTaskFinished(Object result, String message) {
                    if (result != null) {
                        List<Address> addresses = (List<Address>) result;
                        if (addresses.size() == 1) {
                            Address address = addresses.get(0);
                            if (address != null && MultiplayerGamePlayActivity.this.getApiClient().isConnected()) {
                                nl.rubenernst.han.mad.android.puzzle.domain.Location location = new nl.rubenernst.han.mad.android.puzzle.domain.Location();
                                location.setCounty(address.getCountryName());


                                //TODO: Soms lijkt het alsof de locatie voor ieder spel opgeslagen wordt. Dit bleek uit het feit dat de emulator geen locatie heeft maar toch in de scorelijst The Netherlands te zien was.
                                if (getCurrentPlayersGame() != null) {
                                    getCurrentPlayersGame().setLocation(location);
                                }
                            }
                        }
                    }
                }
            });
            geocoderTask.setContext(getApplicationContext());
            geocoderTask.execute(lastLocation);
        }
    }

    @Override
    public void onGameInitialisation() {
        Log.d(TAG, "Initialisation");
    }

    @Override
    public void onGameInitialised(Game game) {
        Log.d(TAG, "Initialised");
    }

    @Override
    public void onGameStarting(Game game) {
        Log.d(TAG, "Starting");
    }

    @Override
    public void onGameStarted(Game game) {
        Log.d(TAG, "Started");

        mGames.put(getCurrentPlayerParticipantId(), game);

        setLocationToGame();
    }

    @Override
    public void onGameUIUpdating(Game game) {
        Log.d(TAG, "Updating");
    }

    @Override
    public void onGameUIUpdated(Game game) {
        Log.d(TAG, "Updated");
    }

    @Override
    public void onGameFinished(Game game) {
        Log.d(TAG, "Finished");

        Games.Achievements.increment(getApiClient(), getResources().getString(R.string.achievement_play_5_multiplayer_games), 1);
        Games.Achievements.increment(getApiClient(), getResources().getString(R.string.achievement_play_10_multiplayer_games), 1);
        Games.Achievements.increment(getApiClient(), getResources().getString(R.string.achievement_play_20_multiplayer_games), 1);

        Games.Leaderboards.submitScore(getApiClient(), game.getDifficulty().getLeaderboardId(), game.getScore());

        takeNextTurn();
    }

    public void showScoresScreen() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MultiplayerGameFinishedFragment())
                .commit();
    }

    @Override
    public void onGamePaused(Game game) {

    }

    @Override
    public void onGameResumed(Game game) {

    }

    @Override
    public void onLocationClientConnected(Bundle bundle, LocationClient locationClient) {
        Log.d(TAG, "Locationhelper connected");
        mLocationClient = locationClient;
    }

    @Override
    public void onLocationClientDisconnected() {

    }

    @Override
    public void onLocationClientConnectionFailed() {

    }

    @Override
    public void handleStatusViewInitializing(GamePlayFragment fragment) {
        fragment.initializeGame();
    }

    @Override
    public void handleStatusViewPlaying(Game game, GamePlayFragment fragment) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View statusBarView = layoutInflater.inflate(R.layout.fragment_game_play_statusbar_multiplayer_playing, null, false);

        fragment.setStatusBarView(statusBarView);

        Participant opponent = MatchHelper.getOpponent(mMatch.getParticipants(), MatchHelper.getCurrentPlayerId(getApiClient()));

        TextView playerOneName = ButterKnife.findById(statusBarView, R.id.player_one_name);
        TextView playerTwoName = ButterKnife.findById(statusBarView, R.id.player_two_name);
        TextView playerOneScore = ButterKnife.findById(statusBarView, R.id.player_one_score);
        TextView playerTwoScore = ButterKnife.findById(statusBarView, R.id.player_two_score);

        playerOneName.setText("You");
        playerOneScore.setText("" + game.getScore());

        playerTwoName.setText("");
        playerTwoScore.setText("");

        if (opponent != null) {
            Game opponentGame = mGames.get(opponent.getParticipantId());
            if (opponentGame != null) {
                playerTwoName.setText(opponent.getDisplayName());
                playerTwoScore.setText("" + opponentGame.getScore());
            } else {
                playerTwoName.setText(opponent.getDisplayName());
                playerTwoScore.setText("-");
            }
        }

        fragment.playGame();
    }

    @Override
    public void handleStatusViewBeforePlaying(Game game, final GamePlayFragment fragment) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View statusBarView = layoutInflater.inflate(R.layout.fragment_game_play_statusbar_initializing, null, false);

        fragment.setStatusBarView(statusBarView);

        final TextView statusText = ButterKnife.findById(statusBarView, R.id.status_initializing);

        final CountDownTimer countDownTimer = new CountDownTimer(3000, 500) {
            public void onTick(long millisUntilFinished) {
                statusText.setText("" + (int) Math.ceil(millisUntilFinished / 1000d));
            }

            public void onFinish() {
                if (fragment.isAdded()) {
                    fragment.startGame();
                }
            }
        };

        if (mGames != null && mGames.get(ORIGINAL_GAME_KEY) == null) {
            showLoadingIndicator("Loading...");

            GameCloneTask gameCloneTask = new GameCloneTask();
            gameCloneTask.setContext(getApplicationContext());
            gameCloneTask.setTaskFinishedListener(new TaskFinishedListener() {
                @Override
                public void onTaskFinished(Object result, String message) {
                    if (result != null) {
                        Game originalGame = (Game) result;
                        if (mGames != null) {
                            mGames.put(ORIGINAL_GAME_KEY, originalGame);

                            hideLoadingIndicator();

                            countDownTimer.start();
                        }
                    }
                }
            });
            gameCloneTask.execute(game);
        } else {
            countDownTimer.start();
        }
    }

    @Override
    public void handleStatusViewEnded(Game game, GamePlayFragment fragment) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View statusBarView = layoutInflater.inflate(R.layout.fragment_game_play_statusbar_finished, null, false);

        fragment.setStatusBarView(statusBarView);

        TextView finished = ButterKnife.findById(statusBarView, R.id.status_finished);
        finished.setText("Solved!");

        fragment.finishGame();
    }

    public class MultiplayerGameFinishedFragment extends Fragment {

        private LayoutInflater layoutInflater;

        @InjectView(R.id.multiplayer_results)
        CardListView resultList;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_multiplayer_game_finished, container, false);

            ButterKnife.inject(this, view);

            this.layoutInflater = inflater;

            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            final CardAdapter adapter = new CardAdapter(MultiplayerGamePlayActivity.this, R.color.main_color);

            CardHeader header = new CardHeader("Results");

            if (mMatch.canRematch()) {
                header.setAction("Rematch", new CardHeader.ActionListener() {
                    @Override
                    public void onHeaderActionClick(CardHeader header) {
                        rematch();
                    }
                });
            } else {
                header.setAction(null);
            }

            adapter.add(header);

            ArrayList<Participant> participants = mMatch.getParticipants();
            for (Participant participant : participants) {
                final Card card = new Card(participant.getDisplayName(), "");

                String scoreText = "Did not play yet";
                Game game = mGames.get(participant.getParticipantId());
                if (game != null) {
                    scoreText = "Score: " + game.getScore();
                }

                String imageUrl = participant.getIconImageUrl();
                if (imageUrl != null) {
                    ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask();
                    imageDownloaderTask.setTaskFinishedListener(new TaskFinishedListener() {
                        @Override
                        public void onTaskFinished(Object result, String message) {
                            if (result != null) {
                                Bitmap bitmap = (Bitmap) result;

                                card.setThumbnail(MultiplayerGamePlayActivity.this, bitmap);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    imageDownloaderTask.execute(imageUrl);
                }

                card.setClickable(false);
                card.setContent(scoreText);

                adapter.add(card);
            }

            CardHeader locationsHeader = new CardHeader("Locations");
            adapter.add(locationsHeader);

            for (Participant participant : participants) {
                CardCompressed card = new CardCompressed(participant.getDisplayName(), "");

                String locationText = "N/A";

                Game game = mGames.get(participant.getParticipantId());
                if (game != null) {
                    if (game.getLocation() != null) {
                        locationText = game.getLocation().getCounty();
                    }
                }

                card.setClickable(false);
                card.setContent(locationText);

                adapter.add(card);
            }

            resultList.setAdapter(adapter);
        }
    }
}

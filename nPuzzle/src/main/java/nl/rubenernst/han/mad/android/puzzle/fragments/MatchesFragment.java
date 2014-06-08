package nl.rubenernst.han.mad.android.puzzle.fragments;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationBuffer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.turnbased.LoadMatchesResponse;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchBuffer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.GameHelper;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;
import nl.rubenernst.han.mad.android.puzzle.MultiplayerGamePlayActivity;
import nl.rubenernst.han.mad.android.puzzle.MultiplayerGamePlayIntentActivity;
import nl.rubenernst.han.mad.android.puzzle.R;

import java.util.*;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class MatchesFragment extends Fragment implements GameHelper.GameHelperListener {

    @InjectView(R.id.invited_matches)
    CardListView invitedMatchesList;

    @InjectView(R.id.my_turn_matches)
    CardListView myTurnMatchesList;

    @InjectView(R.id.their_turn_matches)
    CardListView theirTurnMatchesList;

    @InjectView(R.id.ended_matches)
    CardListView endedMatchesList;

    private GoogleApiClient apiClient;
    private Activity activity;

    public static MatchesFragment newInstance() {
        return new MatchesFragment();
    }
    public static MatchesFragment newInstance(Activity activity) {
        return new MatchesFragment(activity);
    }

    public MatchesFragment() {
        // Required empty public constructor
    }

    public MatchesFragment(Activity activity) {
        this.activity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_matches, container, false);

        ButterKnife.inject(this, rootView);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {
        int[] games = {TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE,
                TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN,
                TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN,
                TurnBasedMatch.MATCH_TURN_STATUS_INVITED};

        final ArrayList<Card> inviteCards = new ArrayList<Card>();
        final CardArrayAdapter invitedCardsAdapter = new CardArrayAdapter(activity, inviteCards);

        final ArrayList<Card> myTurnCards = new ArrayList<Card>();
        final CardArrayAdapter myTurnCardsAdapter = new CardArrayAdapter(activity, myTurnCards);

        final ArrayList<Card> theirTurnCards = new ArrayList<Card>();
        final CardArrayAdapter theirTurnCardsAdapter = new CardArrayAdapter(activity, theirTurnCards);

        final ArrayList<Card> endedCards = new ArrayList<Card>();
        final CardArrayAdapter endedCardsAdapter = new CardArrayAdapter(activity, endedCards);


        Games.TurnBasedMultiplayer.loadMatchesByStatus(apiClient, games)
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.LoadMatchesResult>() {
                    public void onResult(TurnBasedMultiplayer.LoadMatchesResult r) {


                        LoadMatchesResponse matches = r.getMatches();
                        InvitationBuffer invitedMatches =  matches.getInvitations();
                        TurnBasedMatchBuffer myTurnMatches =  matches.getMyTurnMatches();
                        TurnBasedMatchBuffer theirTurnMatches =  matches.getTheirTurnMatches();
                        TurnBasedMatchBuffer endedMatches =  matches.getCompletedMatches();

//                        for (int i = 0; i < invitedMatches.getCount(); i++) {
//                            Invitation invitation = invitedMatches.get(i);
//                            Log.d("MainMenu", match.getMatchId());
//
//                            invitation.
//                        }

                        for (int i = 0; i < myTurnMatches.getCount(); i++) {
                            TurnBasedMatch match = myTurnMatches.get(i);
                            Log.d("MainMenu", match.getMatchId());

                            Card card = getCardForMatch(match);
                            if (card != null) {
                                myTurnCards.add(card);
                            }
                        }

                        for (int i = 0; i < theirTurnMatches.getCount(); i++) {
                            TurnBasedMatch match = theirTurnMatches.get(i);
                            Log.d("MainMenu", match.getMatchId());

                            Card card = getCardForMatch(match);
                            if (card != null) {
                                theirTurnCards.add(card);
                            }
                        }

                        for (int i = 0; i < endedMatches.getCount(); i++) {
                            TurnBasedMatch match = endedMatches.get(i);
                            Log.d("MainMenu", match.getMatchId());

                            Card card = getCardForMatch(match);
                            if (card != null) {
                                endedCards.add(card);
                            }
                        }

                        invitedMatchesList.setAdapter(invitedCardsAdapter);
                        myTurnMatchesList.setAdapter(myTurnCardsAdapter);
                        theirTurnMatchesList.setAdapter(theirTurnCardsAdapter);
                        endedMatchesList.setAdapter(endedCardsAdapter);
                    }
                });
    }

    public Card getCardForMatch(final TurnBasedMatch match) {
        Participant opponent = getOpponent(match);
        Card card = null;

        if (opponent != null) {
            card = new Card(activity);
            card.setType(2);

            card.setTitle(getDateTime(match.getLastUpdatedTimestamp()));

            CardHeader header = new CardHeader(activity);
            header.setTitle(opponent.getDisplayName());

            CardThumbnail thumbnail = new CardThumbnail(getActivity());
            thumbnail.setUrlResource(opponent.getIconImageUrl());

            card.addCardThumbnail(thumbnail);
            card.addCardHeader(header);

            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Intent intent = new Intent(activity, MultiplayerGamePlayIntentActivity.class);
                    intent.putExtra("matchId", match.getMatchId());

                    startActivity(intent);
                }
            });
        }

        return card;
    }

    public Participant getOpponent(TurnBasedMatch match) {
        ArrayList<String> participantIds = match.getParticipantIds();

        for (String participantId : participantIds) {
            if (!participantId.equals(getParticipantIdForPlayerId(match, getCurrentPlayerId()))) {
                return  match.getParticipant(participantId);
            }
        }

        return null;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    private String getDateTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
        return date;
    }

    public void setApiClient(GoogleApiClient apiClient) {
        this.apiClient = apiClient;
    }

    private String getCurrentPlayerId() {
        return Games.Players.getCurrentPlayerId(apiClient);
    }

    private String getParticipantIdForPlayerId(TurnBasedMatch match, String playerId) {
        return match.getParticipantId(playerId);
    }
}

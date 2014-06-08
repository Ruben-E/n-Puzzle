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
import com.google.android.gms.games.GamesStatusCodes;
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

import java.lang.reflect.Array;
import java.util.*;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
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
        View rootView = inflater.inflate(R.layout.fragment_matches, container, false);

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

        Games.TurnBasedMultiplayer.loadMatchesByStatus(apiClient, games)
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.LoadMatchesResult>() {
                    public void onResult(TurnBasedMultiplayer.LoadMatchesResult r) {
                        LoadMatchesResponse matches = r.getMatches();

                        ArrayList<Card> inviteCards = getCardsForInvitations(matches.getInvitations());
                        ArrayList<Card> myTurnCards = getCardsForMatches(matches.getMyTurnMatches());
                        ArrayList<Card> theirTurnCards = getCardsForMatches(matches.getTheirTurnMatches());
                        ArrayList<Card> endedCards = getCardsForMatches(matches.getCompletedMatches());

                        CardArrayAdapter invitedCardsAdapter = new CardArrayAdapter(activity, inviteCards);
                        CardArrayAdapter myTurnCardsAdapter = new CardArrayAdapter(activity, myTurnCards);
                        CardArrayAdapter theirTurnCardsAdapter = new CardArrayAdapter(activity, theirTurnCards);
                        CardArrayAdapter endedCardsAdapter = new CardArrayAdapter(activity, endedCards);

                        invitedMatchesList.setAdapter(invitedCardsAdapter);
                        myTurnMatchesList.setAdapter(myTurnCardsAdapter);
                        theirTurnMatchesList.setAdapter(theirTurnCardsAdapter);
                        endedMatchesList.setAdapter(endedCardsAdapter);
                    }
                });
    }

    public ArrayList<Card> getCardsForInvitations(InvitationBuffer invitations) {
        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i = 0; i < invitations.getCount(); i++) {
            Invitation invitation = invitations.get(i);
            Log.d("MainMenu", invitation.getInvitationId());

            Card card = getCardForInvitation(invitation);
            if (card != null) {
                cards.add(card);
            }
        }

        return cards;
    }

    public ArrayList<Card> getCardsForMatches(TurnBasedMatchBuffer matches) {
        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i = 0; i < matches.getCount(); i++) {
            TurnBasedMatch match = matches.get(i);
            Log.d("MainMenu", match.getMatchId());

            Card card = getCardForMatch(match);
            if (card != null) {
                cards.add(card);
            }
        }

        return cards;
    }

    public Card getCardForMatch(final TurnBasedMatch match) {
        Participant opponent = getOpponent(match.getParticipants());
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

    public Card getCardForInvitation(final Invitation invitation) {
        Participant opponent = getOpponent(invitation.getParticipants());
        Card card = null;

        if (opponent != null) {
            card = new Card(activity);
            card.setType(2);

            card.setTitle(getDateTime(invitation.getCreationTimestamp()));

            CardHeader header = new CardHeader(activity);
            header.setTitle(opponent.getDisplayName());

            CardThumbnail thumbnail = new CardThumbnail(getActivity());
            thumbnail.setUrlResource(opponent.getIconImageUrl());

            card.addCardThumbnail(thumbnail);
            card.addCardHeader(header);

            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Games.TurnBasedMultiplayer.acceptInvitation(apiClient, invitation.getInvitationId())
                            .setResultCallback(new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                                @Override
                                public void onResult(TurnBasedMultiplayer.InitiateMatchResult initiateMatchResult) {
                                    if(initiateMatchResult.getStatus().getStatusCode() == GamesStatusCodes.STATUS_OK) {
                                        TurnBasedMatch match = initiateMatchResult.getMatch();

                                        Intent intent = new Intent(activity, MultiplayerGamePlayIntentActivity.class);
                                        intent.putExtra("matchId", match.getMatchId());

                                        startActivity(intent);
                                    }
                                }
                            });
                }
            });
        }

        return card;
    }

    public Participant getOpponent(ArrayList<Participant> participants) {
        for (Participant participant : participants) {
            if (!participant.getPlayer().getPlayerId().equals(getCurrentPlayerId())) {
                return participant;
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
}

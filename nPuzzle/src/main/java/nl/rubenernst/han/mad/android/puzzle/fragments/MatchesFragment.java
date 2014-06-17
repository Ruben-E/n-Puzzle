package nl.rubenernst.han.mad.android.puzzle.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.afollestad.cardsui.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationBuffer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.turnbased.*;
import com.google.example.games.basegameutils.GameHelper;
import nl.rubenernst.han.mad.android.puzzle.MultiplayerGamePlayActivity;
import nl.rubenernst.han.mad.android.puzzle.MultiplayerGamePlayIntentActivity;
import nl.rubenernst.han.mad.android.puzzle.R;

import java.lang.reflect.Array;
import java.util.*;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class MatchesFragment extends Fragment implements GameHelper.GameHelperListener {

    @InjectView(R.id.matches)
    CardListView matchesList;

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
        setListeners();
        refreshGames();
    }

    public void setListeners() {
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(apiClient, new OnTurnBasedMatchUpdateReceivedListener() {
            @Override
            public void onTurnBasedMatchReceived(TurnBasedMatch match) {
                refreshGames();
            }

            @Override
            public void onTurnBasedMatchRemoved(String s) {
                refreshGames();
            }
        });
    }

    public void refreshGames() {
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

                        CardHeader endedHeader = new CardHeader("Ended matches");
                        CardHeader invitationsHeader = new CardHeader("Invitations");
                        CardHeader theirTurnHeader = new CardHeader("Their turn");
                        CardHeader myTurnHeader = new CardHeader("My turn");

                        final CardAdapter adapter = new CardAdapter(activity, android.R.color.holo_blue_dark);
                        adapter.add(myTurnHeader);
                        if (myTurnCards.size() > 0) {
                            adapter.add(myTurnCards);
                        } else {
                            adapter.add(new CardCenteredHeader("No games"));
                        }

                        adapter.add(invitationsHeader);
                        if (inviteCards.size() > 0) {
                            adapter.add(inviteCards);
                        } else {
                            adapter.add(new CardCenteredHeader("No games"));
                        }

                        adapter.add(theirTurnHeader);
                        if (theirTurnCards.size() > 0) {
                            adapter.add(theirTurnCards);
                        } else {
                            adapter.add(new CardCenteredHeader("No games"));
                        }

                        adapter.add(endedHeader);
                        if (endedCards.size() > 0) {
                            adapter.add(endedCards);
                        } else {
                            adapter.add(new CardCenteredHeader("No games"));
                        }

                        matchesList.setOnCardClickListener(new CardListView.CardClickListener() {
                            @Override
                            public void onCardClick(int index, CardBase card, View view) {
                                Object tag = card.getTag();
                                if (tag instanceof TurnBasedMatch) {
                                    TurnBasedMatch match = (TurnBasedMatch) tag;
                                    Intent intent = new Intent(activity, MultiplayerGamePlayIntentActivity.class);
                                    intent.putExtra("matchId", match.getMatchId());

                                    startActivity(intent);
                                } else if (tag instanceof Invitation) {
                                    Invitation invitation = (Invitation) tag;

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
                            }
                        });

                        matchesList.setAdapter(adapter);
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
            card = new Card("Playing with " + opponent.getDisplayName(), DateUtils.getRelativeDateTimeString(activity, match.getLastUpdatedTimestamp(), 60000, DateUtils.MINUTE_IN_MILLIS, 0));
            card.setTag(match);
        }

        return card;
    }

    public Card getCardForInvitation(final Invitation invitation) {
        Participant opponent = getOpponent(invitation.getParticipants());
        Card card = null;

        if (opponent != null) {
            card = new Card("Invitation from " + opponent.getDisplayName(), getDateTime(invitation.getCreationTimestamp()));
            card.setTag(invitation);
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

package nl.rubenernst.han.mad.android.puzzle.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.afollestad.cardsui.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import nl.rubenernst.han.mad.android.puzzle.*;
import nl.rubenernst.han.mad.android.puzzle.R;

public class MainMenuFragment extends Fragment implements GameHelper.GameHelperListener {

    private static final int SINGLEPLAYER_TAG = 1;
    private static final int MULTIPLAYER_TAG = 2;
    private static final int ACHIEVEMENTS_TAG = 3;
    private static final int HIGHSCORES_TAG = 4;

    private MainMenuActivity activity;
    private GoogleApiClient apiClient;
    private CardAdapter adapter;
    private boolean isVisible = false;

    @InjectView(R.id.menu)
    CardListView menuList;

    public static MainMenuFragment newInstance(MainMenuActivity activity) {
        return new MainMenuFragment(activity);
    }

    public MainMenuFragment() {
    }

    public MainMenuFragment(MainMenuActivity activity) {
        this.activity = activity;
        this.adapter = new CardAdapter(activity, R.color.main_color);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        ButterKnife.inject(this, rootView);

        setMenuItems();
        showMenu();

        return rootView;
    }

    public void setMenuItems() {
        adapter.clear();
        adapter.notifyDataSetChanged();

        CardCompressed singleplayerCard = new CardCompressed("Play a singleplayer game", "");
        singleplayerCard.setTag(SINGLEPLAYER_TAG);

        CardCompressed multiplayerCard = new CardCompressed("Play a multiplayer game", "");
        multiplayerCard.setTag(MULTIPLAYER_TAG);

        CardCompressed achievementsCard = new CardCompressed("View achievements", "");
        achievementsCard.setTag(ACHIEVEMENTS_TAG);

        CardCompressed highscoresCard = new CardCompressed("View highscores", "");
        highscoresCard.setTag(HIGHSCORES_TAG);

        adapter.add(new CardHeader("What do you want to do?"));
        adapter.add(singleplayerCard);
        if (apiClient.isConnected()) {
            adapter.add(multiplayerCard);
            adapter.add(achievementsCard);
            adapter.add(highscoresCard);
        } else {
            adapter.add(new CardCenteredHeader("Multiplayer not available"));
        }
    }

    public void showMenu() {
        menuList.setAdapter(adapter);

        menuList.setOnCardClickListener(new CardListView.CardClickListener() {
            @Override
            public void onCardClick(int index, CardBase card, View view) {
                Object tag = card.getTag();
                if (tag != null) {
                    if (tag instanceof Integer) {
                        Integer type = (Integer)tag;

                        switch (type) {
                            case SINGLEPLAYER_TAG: {
                                Intent intent = new Intent(getActivity(), GameSelectionActivity.class);
                                startActivity(intent);
                                break;
                            }

                            case MULTIPLAYER_TAG: {
                                activity.pager.setCurrentItem(1, true);
                                break;
                            }

                            case ACHIEVEMENTS_TAG: {
                                Intent intent = Games.Achievements.getAchievementsIntent(apiClient);
                                startActivityForResult(intent, 0);
                                break;
                            }

                            case HIGHSCORES_TAG: {
                                Intent intent = Games.Leaderboards.getAllLeaderboardsIntent(apiClient);
                                startActivityForResult(intent, 1);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }

    @Override
    public void onSignInFailed() {
        setMenuItems();

        if (isVisible) {
            showMenu();
        }
    }

    @Override
    public void onSignInSucceeded() {
        setMenuItems();

        if (isVisible) {
            showMenu();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
    }

    public void setApiClient(GoogleApiClient apiClient) {
        this.apiClient = apiClient;
    }
}

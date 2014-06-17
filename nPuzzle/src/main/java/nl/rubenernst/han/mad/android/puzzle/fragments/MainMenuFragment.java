package nl.rubenernst.han.mad.android.puzzle.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.afollestad.cardsui.*;
import nl.rubenernst.han.mad.android.puzzle.GameSelectionActivity;
import nl.rubenernst.han.mad.android.puzzle.MultiplayerGamePlayInboxActivity;
import nl.rubenernst.han.mad.android.puzzle.MultiplayerGamePlayPlayerSelectionActivity;
import nl.rubenernst.han.mad.android.puzzle.R;

public class MainMenuFragment extends Fragment {

    @InjectView(R.id.menu)
    CardListView menuList;

//    @InjectView(R.id.multiplayer_new_game_button)
//    Button multiplayerNewGameButton;
//
//    @InjectView(R.id.multiplayer_existing_game_button)
//    Button multiplayerExistingGameButton;
//
//    @InjectView(R.id.awards_button)
//    Button awardsButton;
//
//    @InjectView(R.id.scores_button)
//    Button scoresButton;

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    public MainMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        ButterKnife.inject(this, rootView);

        final CardAdapter adapter = new CardAdapter(getActivity(), R.color.main_color);

        CardCompressed singleplayerCard = new CardCompressed("Singleplayer", "");
        singleplayerCard.setTag(1);

        CardCompressed multiplayerCard = new CardCompressed("Multiplayer", "");
        multiplayerCard.setTag(2);

        adapter.add(new CardHeader("What do you want to play?"));
        adapter.add(singleplayerCard);
        adapter.add(multiplayerCard);

        menuList.setAdapter(adapter);

        menuList.setOnCardClickListener(new CardListView.CardClickListener() {
            @Override
            public void onCardClick(int index, CardBase card, View view) {
                Object tag = card.getTag();
                if (tag != null) {
                    if (tag instanceof Integer) {
                        Integer type = (Integer)tag;

                        switch (type) {
                            case 1: {
                                Intent intent = new Intent(getActivity(), GameSelectionActivity.class);
                                startActivity(intent);
                                break;
                            }

                            case 2: {
                                Intent intent = new Intent(getActivity(), MultiplayerGamePlayPlayerSelectionActivity.class);
                                startActivity(intent);
                                break;
                            }
                        }
                    }
                }
            }
        });

//        singleplayerButton.setOnClickListener(this);
//        multiplayerNewGameButton.setOnClickListener(this);
//        multiplayerExistingGameButton.setOnClickListener(this);
//        awardsButton.setOnClickListener(this);
//        scoresButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }

//    @Override
//    public void onClick(View view) {
//        Intent intent;
//        switch (view.getId()) {
//            case R.id.singleplayer_button:
//                intent = new Intent(getActivity(), GameSelectionActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.multiplayer_new_game_button:
//                intent = new Intent(getActivity(), MultiplayerGamePlayPlayerSelectionActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.multiplayer_existing_game_button:
//                intent = new Intent(getActivity(), MultiplayerGamePlayInboxActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.awards_button:
////                if (isSignedIn()) {
////                    startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 0);
////                } else {
////                    showAlert("Achievements not available");
////                }
//                break;
//            case R.id.scores_button:
////                if (isSignedIn()) {
////                    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), 0);
////                } else {
////                    showAlert("Leaderboards not available");
////                }
//                break;
//        }
//    }
}

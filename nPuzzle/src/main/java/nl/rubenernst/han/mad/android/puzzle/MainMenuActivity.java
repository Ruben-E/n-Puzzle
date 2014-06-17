package nl.rubenernst.han.mad.android.puzzle;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.*;
import com.google.example.games.basegameutils.BaseGameActivity;
import nl.rubenernst.han.mad.android.puzzle.fragments.MainMenuFragment;
import nl.rubenernst.han.mad.android.puzzle.fragments.MatchesFragment;

import java.util.ArrayList;


public class MainMenuActivity extends BaseGameActivity implements View.OnClickListener {

//    @InjectView(R.id.singleplayer_button)
//    Button singleplayerButton;
//
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

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MainMenuPagerAdapter adapter;

    private MatchesFragment matchesFragment;

    public MainMenuActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ButterKnife.inject(this);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MainMenuPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setOffscreenPageLimit(10);

        tabs.setViewPager(pager);

        if (isSignedIn()) {
            getMatchesFragment().onSignInSucceeded();
        } else {
            beginUserInitiatedSignIn();
        }

//        singleplayerButton.setOnClickListener(this);
//        multiplayerNewGameButton.setOnClickListener(this);
//        multiplayerExistingGameButton.setOnClickListener(this);
//        awardsButton.setOnClickListener(this);
//        scoresButton.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
    public void onClick(View view) {
//        Intent intent;
//        switch (view.getId()) {
//            case R.id.singleplayer_button:
//                intent = new Intent(this, GameSelectionActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.multiplayer_new_game_button:
//                intent = new Intent(this, MultiplayerGamePlayPlayerSelectionActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.multiplayer_existing_game_button:
//                intent = new Intent(this, MultiplayerGamePlayInboxActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.awards_button:
//                if (isSignedIn()) {
//                    startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 0);
//                } else {
//                    showAlert("Achievements not available");
//                }
//                break;
//            case R.id.scores_button:
//                if (isSignedIn()) {
//                    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), 0);
//                } else {
//                    showAlert("Leaderboards not available");
//                }
//                break;
//        }
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {
        getMatchesFragment().onSignInSucceeded();
    }

    public class MainMenuPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Home", "Your matches", "High Scores", "Achievements"};

        public MainMenuPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1: {
                    return getMatchesFragment();
                }

                default: {
                    return MainMenuFragment.newInstance();
                }
            }
        }

    }

    public MatchesFragment getMatchesFragment() {
        if (matchesFragment == null) {
            matchesFragment = MatchesFragment.newInstance(this);
            matchesFragment.setApiClient(getApiClient());
        }

        return matchesFragment;
    }
}

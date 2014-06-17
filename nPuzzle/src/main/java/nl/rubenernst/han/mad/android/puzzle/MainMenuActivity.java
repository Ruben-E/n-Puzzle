package nl.rubenernst.han.mad.android.puzzle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;
import com.astuetz.PagerSlidingTabStrip;
import com.google.example.games.basegameutils.BaseGameActivity;
import nl.rubenernst.han.mad.android.puzzle.fragments.MainMenuFragment;
import nl.rubenernst.han.mad.android.puzzle.fragments.MatchesFragment;
import nl.rubenernst.han.mad.android.puzzle.helpers.InsetsHelper;
import nl.rubenernst.han.mad.android.puzzle.helpers.TintHelper;


public class MainMenuActivity extends BaseGameActivity {

    private PagerSlidingTabStrip tabs;
    public ViewPager pager;
    private MainMenuPagerAdapter adapter;

    private MatchesFragment matchesFragment;
    private MainMenuFragment mainMenuFragment;

    public MainMenuActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ButterKnife.inject(this);

        TintHelper.setupTransparentTints(this);
        InsetsHelper.setInsets(this, findViewById(R.id.content_frame), true, false);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MainMenuPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setOffscreenPageLimit(10);

        tabs.setViewPager(pager);
        tabs.setIndicatorColor(getResources().getColor(R.color.main_color));

        if (isSignedIn()) {
            getMainMenuFragment().onSignInSucceeded();
            getMatchesFragment().onSignInSucceeded();
        } else {
            beginUserInitiatedSignIn();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSignInFailed() {
        getMatchesFragment().onSignInFailed();
        getMainMenuFragment().onSignInFailed();
    }

    @Override
    public void onSignInSucceeded() {
        getMatchesFragment().onSignInSucceeded();
        getMainMenuFragment().onSignInSucceeded();
    }

    public class MainMenuPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Home", "Your matches"/*, "High Scores", "Achievements"*/};

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
                case 0: {
                    return getMainMenuFragment();
                }

                case 1: {
                    return getMatchesFragment();
                }

                default: {
                    return null;
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

    public MainMenuFragment getMainMenuFragment() {
        if (mainMenuFragment == null) {
            mainMenuFragment = MainMenuFragment.newInstance(this);
            mainMenuFragment.setApiClient(getApiClient());
        }

        return mainMenuFragment;
    }
}

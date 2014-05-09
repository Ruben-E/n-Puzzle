package nl.rubenernst.han.mad.android.puzzle;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameActivity;

import java.util.ArrayList;


public class MainMenuActivity extends ActionBarActivity implements View.OnClickListener {

    @InjectView(R.id.singleplayer_button)
    Button singleplayerButton;

    @InjectView(R.id.multiplayer_new_game_button)
    Button multiplayerNewGameButton;

    @InjectView(R.id.multiplayer_existing_game_button)
    Button multiplayerExistingGameButton;

    @InjectView(R.id.awards_button)
    Button awardsButton;

    @InjectView(R.id.scores_button)
    Button scoresButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ButterKnife.inject(this);

        singleplayerButton.setOnClickListener(this);
        multiplayerNewGameButton.setOnClickListener(this);
        multiplayerExistingGameButton.setOnClickListener(this);
        awardsButton.setOnClickListener(this);
        scoresButton.setOnClickListener(this);
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
        Intent intent;
        switch (view.getId()) {
            case R.id.singleplayer_button:
                intent = new Intent(this, GameSelectionActivity.class);
                startActivity(intent);
                break;
            case R.id.multiplayer_new_game_button:
                intent = new Intent(this, MultiplayerGamePlayPlayerSelectionActivity.class);
                startActivity(intent);
                break;
            case R.id.multiplayer_existing_game_button:
                intent = new Intent(this, MultiplayerGamePlayInboxActivity.class);
                startActivity(intent);
                break;
            case R.id.awards_button:

                break;
            case R.id.scores_button:

                break;
        }
    }
}

package nl.rubenernst.han.mad.android.puzzle.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import nl.rubenernst.han.mad.android.puzzle.MultiplayerGamePlayPlayerSelectionActivity;
import nl.rubenernst.han.mad.android.puzzle.R;
import nl.rubenernst.han.mad.android.puzzle.dialogs.MultiplayerImageSelectionDialog;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;

/**
 * Created by rubenernst on 18-06-14.
 */
public class MultiplayerHelper {

    public static void startMultiplayer(Activity activity) {
        MultiplayerHelper multiplayerHelper = new MultiplayerHelper(activity);
        multiplayerHelper.startMultiplayer();
    }

    private Activity activity;
    private FragmentManager fragmentManager;

    private int puzzleId;
    private Difficulty difficulty;

    public MultiplayerHelper(Activity activity) {
        this.activity = activity;
        this.fragmentManager = activity.getFragmentManager();
    }

    public void startMultiplayer() {
        showImageSelectionDialog();
    }

    public void showImageSelectionDialog() {
        MultiplayerImageSelectionDialog multiplayerImageSelectionDialog = new MultiplayerImageSelectionDialog();
        multiplayerImageSelectionDialog.setDialogListener(new MultiplayerImageSelectionDialog.DialogListener() {
            @Override
            public void onImageSelected(DialogFragment dialog, int drawableId) {
                puzzleId = drawableId;
                showDifficultySelectionDialog();
            }
        });
        //http://developer.android.com/guide/topics/ui/dialogs.html
        multiplayerImageSelectionDialog.show(fragmentManager, "dialog");
    }

    public void showDifficultySelectionDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_change_difficulty_title)
                .setItems(R.array.difficulties, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        difficulty = Difficulty.fromString(activity.getResources().obtainTypedArray(R.array.difficulties).getString(which));

                        launch();
                    }
                })
                .show();
    }

    private void launch() {
        Intent intent = new Intent(activity, MultiplayerGamePlayPlayerSelectionActivity.class);
        intent.putExtra("puzzleId", puzzleId);
        intent.putExtra("difficulty", difficulty.toString());

        activity.startActivity(intent);
    }
}

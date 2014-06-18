package nl.rubenernst.han.mad.android.puzzle.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import butterknife.ButterKnife;
import nl.rubenernst.han.mad.android.puzzle.R;

/**
 * Created by rubenernst on 18-06-14.
 */
public class MultiplayerImageSelectionDialog extends DialogFragment {
    public interface DialogListener {
        public void onImageSelected(DialogFragment dialog, int drawableId);
    }

    private DialogListener dialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.dialog_multiplayer_image_selection, container, false);

        ImageButton imageButton1 = ButterKnife.findById(rootView, R.id.tile_button_1);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogListener != null) {
                    dialogListener.onImageSelected(MultiplayerImageSelectionDialog.this, R.drawable.puzzle_1);
                    MultiplayerImageSelectionDialog.this.dismiss();
                }
            }
        });

        ImageButton imageButton2 = ButterKnife.findById(rootView, R.id.tile_button_2);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogListener != null) {
                    dialogListener.onImageSelected(MultiplayerImageSelectionDialog.this, R.drawable.puzzle_2);
                    MultiplayerImageSelectionDialog.this.dismiss();
                }
            }
        });

        return rootView;
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}

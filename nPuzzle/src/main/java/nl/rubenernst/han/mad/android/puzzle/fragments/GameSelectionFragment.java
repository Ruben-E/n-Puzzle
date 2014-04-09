package nl.rubenernst.han.mad.android.puzzle.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.rubenernst.han.mad.android.puzzle.GamePlayActivity;
import nl.rubenernst.han.mad.android.puzzle.R;
import nl.rubenernst.han.mad.android.puzzle.utils.Constants;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;

import java.lang.reflect.Field;

/**
 * A placeholder fragment containing a simple view.
 */
public class GameSelectionFragment extends Fragment implements View.OnClickListener {

    private Difficulty mDifficulty = Difficulty.MEDIUM;

    @InjectView(R.id.puzzle_choices)
    LinearLayout puzzleChoices;

    public GameSelectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game_selection, container, false);

        ButterKnife.inject(this, rootView);

        LinearLayout horizontalLayout = null;

        for (int i = 0; i < Constants.PUZZLES.length; i++) {
            try {
                if (horizontalLayout == null || i % 2 == 0) {
                    horizontalLayout = new LinearLayout(getActivity());
                    horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                    horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                    puzzleChoices.addView(horizontalLayout);
                }

                String puzzle = Constants.PUZZLES[i];
                String puzzleImageName = "puzzle_" + (i + 1);

                Class res = R.drawable.class;
                Field field = res.getField(puzzleImageName);
                int drawableId = field.getInt(null);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, .50f);
                layoutParams.setMargins(12, 8, 12, 8);

                ImageButton imageButton = new ImageButton(getActivity());
                imageButton.setLayoutParams(layoutParams);
                imageButton.setImageResource(drawableId);
                imageButton.setBackgroundResource(R.drawable.card);
                imageButton.setTag(i);
                imageButton.setOnClickListener(this);
                imageButton.setAdjustViewBounds(true);

                horizontalLayout.addView(imageButton);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), GamePlayActivity.class);
        intent.putExtra("puzzle", (Integer) view.getTag());
        intent.putExtra("difficulty", getDifficulty());

        getActivity().startActivity(intent);
    }

    //TODO: Crash bij het draaien van het scherm
    private Difficulty getDifficulty() {
        return this.mDifficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.mDifficulty = difficulty;
    }
}

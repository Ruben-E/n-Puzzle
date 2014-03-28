package nl.rubenernst.han.mad.android.puzzle.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import nl.rubenernst.han.mad.android.puzzle.GamePlayActivity;
import nl.rubenernst.han.mad.android.puzzle.R;
import nl.rubenernst.han.mad.android.puzzle.utils.Constants;

import java.lang.reflect.Field;

/**
 * A placeholder fragment containing a simple view.
 */
public class GameSelectionFragment extends Fragment implements View.OnClickListener {

    public GameSelectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game_selection, container, false);

        LinearLayout puzzleChoises = (LinearLayout) rootView.findViewById(R.id.puzzle_choices);
        for (int i = 0; i < Constants.PUZZLES.length; i++) {
            try {
                String puzzle = Constants.PUZZLES[i];
                String puzzleImageName = "ic_puzzle_" + (i + 1);

                Class res = R.drawable.class;
                Field field = res.getField(puzzleImageName);
                int drawableId = field.getInt(null);

                Bitmap image = BitmapFactory.decodeResource(getResources(), drawableId);
                LinearLayout puzzleChoice = (LinearLayout) inflater.inflate(R.layout.puzzle_choice, null);

                ImageView puzzleImage = (ImageView) puzzleChoice.findViewById(R.id.puzzle_image);
                Button puzzleButton = (Button) puzzleChoice.findViewById(R.id.puzzle_button);

                puzzleButton.setText(puzzle);
                puzzleButton.setOnClickListener(this);
                puzzleButton.setTag(i);


                puzzleImage.setImageBitmap(image);

                puzzleChoises.addView(puzzleChoice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), GamePlayActivity.class);
        intent.putExtra("puzzle", (Integer) view.getTag());
        intent.putExtra("difficulty", getDifficulty());

        getActivity().startActivity(intent);
    }

    private Constants.Difficulty getDifficulty() {
        RadioGroup difficultyGroup = (RadioGroup) getView().findViewById(R.id.difficulty);

        int radioButtonID = difficultyGroup.getCheckedRadioButtonId();
        View radioButton = difficultyGroup.findViewById(radioButtonID);

        if (radioButton != null) {
            String tag = (String) radioButton.getTag();
            Constants.Difficulty difficulty = Constants.Difficulty.valueOf(tag.toUpperCase());

            if (difficulty != null) {
                return difficulty;
            }
        }

        return Constants.Difficulty.NORMAL;
    }
}

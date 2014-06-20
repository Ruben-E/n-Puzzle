package nl.rubenernst.han.mad.android.puzzle.UI;

import android.content.Context;
import com.afollestad.cardsui.Card;
import nl.rubenernst.han.mad.android.puzzle.R;

/**
 * Created by rubenernst on 20-06-14.
 */
public class MultiLineCard extends Card {
    public MultiLineCard() {
    }

    public MultiLineCard(CharSequence title, CharSequence subtitle, boolean isHeader) {
        super(title, subtitle, isHeader);
    }

    public MultiLineCard(CharSequence title) {
        super(title);
    }

    public MultiLineCard(CharSequence title, CharSequence content) {
        super(title, content);
    }

    public MultiLineCard(Context context, int titleRes) {
        super(context, titleRes);
    }

    public MultiLineCard(Context context, String title, int contentRes) {
        super(context, title, contentRes);
    }

    public MultiLineCard(Context context, int titleRes, String content) {
        super(context, titleRes, content);
    }

    public MultiLineCard(Context context, int titleRes, int contentRes) {
        super(context, titleRes, contentRes);
    }

    @Override
    public int getLayout() {
        return R.layout.multi_line_card;
    }
}

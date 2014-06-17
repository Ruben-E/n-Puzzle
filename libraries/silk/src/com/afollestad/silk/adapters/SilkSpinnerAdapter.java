package com.afollestad.silk.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.afollestad.silk.R;

/**
 * @author Aidan Follestad (afollestad)
 */
public class SilkSpinnerAdapter extends ArrayAdapter<String> {

    public SilkSpinnerAdapter(Context context) {
        super(context, R.layout.spinner_item);
        super.setDropDownViewResource(R.layout.spinner_item_dropdown);
    }

    public SilkSpinnerAdapter(Context context, String[] items) {
        this(context);
        addAll(items);
    }

    public SilkSpinnerAdapter(Context context, int stringArrayRes) {
        this(context);
        addAll(context.getResources().getStringArray(stringArrayRes));
    }
}

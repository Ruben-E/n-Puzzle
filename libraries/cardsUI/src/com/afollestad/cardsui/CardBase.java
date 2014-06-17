package com.afollestad.cardsui;

import android.graphics.drawable.Drawable;
import com.afollestad.silk.SilkComparable;

/**
 * @author Aidan Follestad (afollestad)
 */
public interface CardBase<ItemType> extends SilkComparable<ItemType> {

    public abstract CharSequence getTitle();

    public abstract CharSequence getContent();

    public abstract boolean isHeader();

    public abstract boolean isClickable();

    public abstract int getPopupMenu();

    public CardHeader.ActionListener getActionCallback();

    public String getActionTitle();

    public abstract Card.CardMenuListener<ItemType> getPopupListener();

    public abstract Drawable getThumbnail();

    public abstract int getLayout();

    public abstract Object getTag();
}

package com.afollestad.silk.fragments.feed;

import android.content.ContentResolver;
import com.afollestad.silk.SilkComparable;
import com.afollestad.silk.SilkCursorItem;
import com.afollestad.silk.fragments.list.SilkCursorListFragment;

import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class SilkCursorFeedFragment<ItemType extends SilkCursorItem & SilkComparable> extends SilkCursorListFragment<ItemType> {

    protected void onPreLoad() {
        clearProvider();
    }

    protected void onPostLoad(List<ItemType> items) {
        setListShown(true);
        if (items == null || items.size() == 0) return;
        ContentResolver resolver = getActivity().getContentResolver();
        for (ItemType item : items) {
            if (item.getContentValues() == null) continue;
            resolver.insert(getLoaderUri(), item.getContentValues());
        }
        super.onInitialRefresh();
    }

    protected abstract List<ItemType> refresh() throws Exception;

    protected abstract void onError(Exception e);

    @Override
    protected void onCursorEmpty() {
        super.onCursorEmpty();
        performRefresh(true);
    }

    public void performRefresh(final boolean showProgress) {
        if (!isListShown()) return;
        if (showProgress) setListShown(false);
        onPreLoad();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<ItemType> items = refresh();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onPostLoad(items);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onError(e);
                            if (showProgress) setListShown(true);
                        }
                    });
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }
}
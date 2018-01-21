package com.example.t_hant.pagefold;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.example.t_hant.pagefold.utils.MLog;

import junit.framework.Assert;

/**
 * Created by t_hant on 1/20/2018.
 */

public class FlipViewController extends AdapterView<Adapter>{

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    public static interface ViewFlipListener {
        void onViewFlipped(View view, int position);
    }

    private static final int MAX_RELEASED_VIEW_SIZE = 1;
    static final int MSG_SURFACE_CREATED = 1;

    private Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg)
        {
            if(msg.what == MSG_SURFACE_CREATED)
            {
                contentWidth = 0;
                contentHeight = 0;
                requestLayout();
                return true;
            }else
            {
                MLog.w("Unknown msg.what: " + msg.what);
            }
            return false;
        }
    });

    private GLSurfaceView surfaceView;
    private FLipRenderer renderer;
    private FlipCard cards;

    @Override
    public Adapter getAdapter(){
        return adapter;
    }

    @Override
    public void setAdapter(Adapter adapter)
    {
        setAdapter(adapter, 0);
    }

    public void setAdapter(Adapter adapter, int initialPosition) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(adapterDataObserver);
        }

        Assert.assertNotNull("adapter should not be null", adapter);

        this.adapter = adapter;
        adapterDataCount = adapter.getCount();

        adapterDataObserver = new MyDataSetObserver();
        this.adapter.registerDataSetObserver(adapterDataObserver);
        if (adapterDataCount > 0) {
            setSelection(initialPosition);
        }
    }

    @Override
    public View getSelectedView()
    {
        return (bufferIndex < bufferedViews.size() && bufferIndex >= 0) ? bufferedViews.get(bufferIndex)
                : null;
    }

    @Override
    public void setSelection(int position)
    {
        if(adapter == null)
        {
            return;
        }

        Assert.assertTrue("Invalid selection position", position >= 0 && position < adapterDataCount);

        releaseViews();

        View selectedView = viewFromAdapter(position, true);
        bufferedViews.add(selectedView);

        for (int i = 1; i <= sideBufferSize; i++) {
            int previous = position - i;
            int next = position + i;

            if (previous >= 0) {
                bufferedViews.addFirst(viewFromAdapter(previous, false));
            }
            if (next < adapterDataCount) {
                bufferedViews.addLast(viewFromAdapter(next, true));
            }
        }

        bufferIndex = bufferedViews.indexOf(selectedView);
        adapterIndex = position;

        requestLayout();
        updateVisibleView(inFlipAnimation ? -1 : bufferIndex);

        cards.resetSelection(position, adapterDataCount);

    }
}

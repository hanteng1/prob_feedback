package com.example.t_hant.pagefold;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.t_hant.pagefold.utils.MLog;
import com.example.t_hant.pagefold.utils.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by t_hant on 1/21/2018.
 */

public class UIAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int repeatCount = 1;

    private List<UIData.Data> uiData;

    public UIAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
        uiData = new ArrayList<UIData.Data>(UIData.Cmd_List);
    }

    @Override
    public int getCount() {
        return uiData.size() * repeatCount;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //here it defines how the view looks like
        View layout = convertView;
        if(convertView == null)
        {
            layout = inflater.inflate(R.layout.basic, null);
            MLog.d("created new view from adapter: %d", position);
        }

        final UIData.Data data = uiData.get(position % uiData.size());
        UI.<TextView>findViewById(layout, R.id.title).setText(MLog.format("%d. %s", position, data.title));

        return layout;
    }

    public void removeData(int index)
    {
        if(uiData.size() > 1)
        {
            uiData.remove(index);
        }
    }

}

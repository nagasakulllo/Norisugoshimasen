package com.norisugosimasen.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.norisugosimasen.model.stationdata.DataInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nagai on 2016/10/09.
 */

class SpinnerAdapter<T extends DataInterface> extends BaseAdapter {
    Context mContext;
    List<T> mList;

    SpinnerAdapter(Context context) {
        super();

        mContext = context;
        mList = new ArrayList<>();
    }

    void setItem(List<T> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(android.R.layout.simple_spinner_item, null);
        }

        DataInterface data = mList.get(i);
        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText(data.getName());

        return view;
    }

    @Override
    public View getDropDownView(int position,
                                View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        }

        DataInterface data = mList.get(position);
        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        text.setText(data.getName());

        return convertView;
    }
}

package com.nexless.devicecollect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nexless.ccommble.util.CommLog;
import com.nexless.devicecollect.R;
import com.nexless.devicecollect.model.KeyboardBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @date: 2019/8/23
 * @author: su qinglin
 * @description:
 */
public class KeyboardAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private static String[] mKeys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#"};
    private List<KeyboardBean> mDatas = new ArrayList<>();

    public KeyboardAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        for (int i = 0; i < mKeys.length; i++) {
            KeyboardBean keyboardBean = new KeyboardBean(false, mKeys[i]);
            mDatas.add(keyboardBean);
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public KeyboardBean getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mLayoutInflater.inflate(R.layout.item_keyboard, null);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tvNumber = convertView.findViewById(R.id.tv_keyboard_item_num);
        KeyboardBean keyboard = getItem(position);
        holder.tvNumber.setText(keyboard.keyNum);
        if (keyboard.isSelect) {
            holder.tvNumber.setBackgroundResource(R.color.green);
        } else {
            holder.tvNumber.setBackgroundResource(R.color.gray);
        }
//        convertView.setBackgroundResource(keyboard.isSelect ? R.color.green : R.color.gray);
//        holder.tvNumber.setBackgroundResource(R.color.green);
        return convertView;
    }

    class Holder {
        TextView tvNumber;
    }

    public void reset() {
        for (int i = 0; i < mDatas.size(); i++) {
            mDatas.get(i).isSelect = false;
        }
        notifyDataSetChanged();
    }

    public void selectKey(int index) {
        CommLog.logE("selectKey:" + index);
        mDatas.get(index).isSelect = true;
        notifyDataSetChanged();
    }
}

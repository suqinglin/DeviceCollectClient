package com.nexless.devicecollect.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 通用的ViewHolder
 *
 * @author Calm
 * @data 2015.05.12
 */
public class PViewHolder
{

    private final SparseArray<View> mViews;
    private View mConvertView;

    private PViewHolder(Context context, ViewGroup parent, int layoutId, int position)
    {
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    /**
     * 拿到一个ViewHolder对象
     */
    public static PViewHolder get(Context context, View convertView,
                                  ViewGroup parent, int layoutId, int position)
    {
        if (convertView == null)
        {
            return new PViewHolder(context, parent, layoutId, position);
        }
        return (PViewHolder) convertView.getTag();
    }

    /**
     * 通过控件id获取对应的控件，如果没有则加入views
     */
    public <T extends View> T getView(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView()
    {
        return mConvertView;
    }
}

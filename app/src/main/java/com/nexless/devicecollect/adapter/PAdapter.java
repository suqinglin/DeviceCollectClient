package com.nexless.devicecollect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 通用的Adapter
 *
 * @author Calm
 * @data 2015.05.12
 */
public abstract class PAdapter<T> extends BaseAdapter
{

    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;

    /**
     * 适配器构造
     *
     * @param context
     * @param mDatas       数据
     * @param itemLayoutId 每项布局id
     */
    public PAdapter(Context context, List<T> mDatas, int itemLayoutId)
    {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount()
    {
        if (mDatas != null)
        {
            return mDatas.size();
        }
        return 0;
    }
//	public int  getItemNumder() {
//		return  this.position;
//	}

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public T getItem(int position)
    {
        if (mDatas != null)
        {
            return mDatas.get(position);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final PViewHolder viewHoler = getViewHolder(position, convertView, parent);
        convert(viewHoler, getItem(position), position);
        return viewHoler.getConvertView();
    }

    //2017-03-17修改，加入position参数,外部可获取到当前view的position
    public abstract void convert(PViewHolder helper, T item, int position);

    private PViewHolder getViewHolder(int position, View convertView,
                                      ViewGroup parent)
    {
        return PViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public void setData(List<T> data)
    {
        mDatas = data;
        notifyDataSetChanged();
    }

    /**
     * 追加数据
     *
     * @param data 数据
     */
    public void appendData(List<T> data)
    {
        if (mDatas == null)
        {
            mDatas = data;
        } else
        {
            mDatas.addAll(data);
        }
        notifyDataSetChanged();
    }
}

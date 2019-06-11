package com.nexless.devicecollect.view;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nexless.devicecollect.R;

/**
 * 普通样式的titleBar 包含中间文字  左边按钮及右边按钮
 * 左边按钮默认显示  右边按钮默认隐藏
 * 可在xml配置左边、右边按钮的显示隐藏，可配置title显示文字
 * 其余设置功能通过开放公共方法进行设置
 * @author 苏清林
 * @note 注意：右边是一个TextView 如果设置图片，需从drawable里取，否则会变形
 * 此自定义titlebar不支持跑马灯
 * <AppTitleBar
 *      android:layout_width="match_parent"
 *      android:layout_height="wrap_content"
 *      xmlns:cus="http://schemas.android.com/apk/res-auto"
 *      cus:title="我是AppTitleBar">
 * </AppTitleBar>
 */
public class AppTitleBar extends RelativeLayout
{
	private TextView mTitle;
//	private RelativeLayout mRlLeft;
	private ImageView mBtnLeft;
//	private TextView mTvRight;
	private ImageView mIBtnRight;
	private TextView mTvRight;
	private LinearLayout mLlRight;
//	private View mVDivider;
	public AppTitleBar(Context context)
	{
		super(context);
		initView(context);
	}
	public AppTitleBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
		setAttrs(context,attrs);
	}
	public AppTitleBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
		setAttrs(context,attrs);
	}
	private void initView(Context context)
	{
		View lay = LayoutInflater.from(context).inflate(R.layout.view_apptitlebar, this);
		mTitle = lay.findViewById(R.id.apptitlebar_tv_title);
//		mRlLeft = lay.findViewById(R.id.apptitlebar_rl_left);
		mBtnLeft = lay.findViewById(R.id.apptitlebar_btn_left);
//		mTvRight = lay.findViewById(R.id.apptitlebar_btn_right);
		mIBtnRight = lay.findViewById(R.id.apptitlebar_btn_right);
		mTvRight = lay.findViewById(R.id.apptitlebar_tv_right);
		mLlRight = lay.findViewById(R.id.apptitlebar_ll_right);
//		mVDivider = lay.findViewById(R.id.apptitlebar_view_divider);
		mBtnLeft.setOnClickListener(listener);
//		mRlLeft.setOnClickListener(listener);
	}
	private Activity scanForActivity(Context cont) {
		if (cont == null)
			return null;
		else if (cont instanceof Activity)
			return (Activity) cont;
		else if (cont instanceof ContextWrapper)
			return scanForActivity(((ContextWrapper) cont).getBaseContext());
		return null;
	}
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			Activity activity = scanForActivity(getContext());
			if(activity != null)
			{
				activity.finish();
			}
		}
	};
	private void setAttrs(Context context, AttributeSet attrs)
	{
		if(!this.isInEditMode())
		{
			TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.AppTitleBar);
			//左边按钮设置隐藏
			if(!arr.getBoolean(R.styleable.AppTitleBar_leftBtn, true))
			{
//				mRlLeft.setVisibility(View.GONE);
				mBtnLeft.setVisibility(View.GONE);
			}
			String title = arr.getString(R.styleable.AppTitleBar_title);
			if(title != null)
			{
				mTitle.setText(title);
			}
			arr.recycle();
		}
	}
	/**
	 * 设置左边按钮是否可见
	 * @param visibility
	 */
	public void setLeftBtnVisibility(int visibility)
	{
//		mRlLeft.setVisibility(visibility);
		mBtnLeft.setVisibility(visibility);
	}
	/**
	 * 设置左边按钮背景资源
	 * @param res
	 */
	public void setLeftImageResource(int res)
	{
		mBtnLeft.setImageResource(res);
	}

	/**
	 * 设置左边按钮背景资源
	 * @param color
	 */
	public void setLeftImageColor(int color)
	{
		mBtnLeft.setColorFilter(color);
	}

	/**
	 * 为左边按钮设置监听
	 * @param listener
	 */
	public void setLeftBtnListener(OnClickListener listener)
	{
//		mRlLeft.setOnClickListener(listener);
		mBtnLeft.setOnClickListener(listener);
	}

	/**
	 * 为右边按钮设置监听
	 * @param listener
	 */
	public void setRightListener(OnClickListener listener)
	{
		mIBtnRight.setVisibility(VISIBLE);
		mIBtnRight.setOnClickListener(listener);
	}

	/**
	 * 为右边按钮设置监听
	 * @param listener
	 */
	public void setRightListener(String rightText, OnClickListener listener)
	{
		mLlRight.setVisibility(VISIBLE);
		mTvRight.setText(rightText);
		mLlRight.setOnClickListener(listener);
	}

	/**
	 * 设置右边按钮背景资源
	 * @param res
	 */
	public void setRightImageResource(int res)
	{
		mIBtnRight.setVisibility(VISIBLE);
		mIBtnRight.setImageResource(res);
	}

	/**
	 * 设置title颜色
	 * @param color
	 */
	public void setTitleColor(int color)
	{
		mTitle.setTextColor(color);
	}
	/**
	 * 设置title字体大小
	 * @param unit
	 * @param size
	 */
	public void setTitleTextSize(int unit,int size)
	{
		mTitle.setTextSize(unit, size);
	}
	/**
	 * 设置title文字
	 * @param text
	 */
	public void setTitleText(String text)
	{
		mTitle.setText(text);
	}
	/**
	 * 为Title设置监听
	 * @param listener
	 */
	public void setTitleClickListener(OnClickListener listener)
	{
		mTitle.setOnClickListener(listener);
	}

	/**
	 * 获取Title View
	 * @return TextView
     */
	public TextView getTitleText()
	{
		return mTitle;
	}

	/**
	 * 返回左边按钮
	 * @return
     */
	public ImageView getLeftBtn()
	{
		return mBtnLeft;
	}

}

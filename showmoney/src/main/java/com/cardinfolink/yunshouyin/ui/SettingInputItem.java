package com.cardinfolink.yunshouyin.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cardinfolink.yunshouyin.R;


/**
 * Created by mamh on 15-11-1.
 * 自定义的组合控件,用来输入文本的和输入密码的多少不太一样
 */
public class SettingInputItem extends RelativeLayout {
    private TextView mTitle;
    private EditTextClear mText;
    private ImageView mImageView;

    public SettingInputItem(Context context) {
        super(context);
        initView(context);
    }

    public SettingInputItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
        String title = typeArray.getString(R.styleable.SettingItemView_title);
        String hint = typeArray.getString(R.styleable.SettingItemView_hint);
        typeArray.recycle();
        mText.setHint(hint);
        mTitle.setText(title);
    }

    public SettingInputItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.setting_input_item, this);
        mTitle = (TextView) this.findViewById(R.id.tv_title);
        mText = (EditTextClear) this.findViewById(R.id.et_input);
        mImageView = (ImageView) this.findViewById(R.id.iv_help);
    }


    public void setTitle(String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }

    public String getTitle() {
        return mTitle.getText().toString();
    }

    public void setText(String username) {
        if (mText != null) {
            mText.setText(username);
        }
    }

    public String getText() {
        return mText.getText().toString();
    }


    public void setImageViewOnClickListener(OnClickListener l) {
        mImageView.setOnClickListener(l);
    }

    public void setTitleOnClickListener(OnClickListener l) {
        mTitle.setOnClickListener(l);
    }

    public void setImageViewResource(int id) {
        mImageView.setImageResource(id);
    }

    public void setImageViewDrawable(Drawable d) {
        mImageView.setImageDrawable(d);
    }
}
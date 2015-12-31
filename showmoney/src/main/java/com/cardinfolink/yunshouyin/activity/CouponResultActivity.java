package com.cardinfolink.yunshouyin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cardinfolink.yunshouyin.R;
import com.cardinfolink.yunshouyin.constant.Msg;
import com.cardinfolink.yunshouyin.data.SessonData;

/**
 * Created by charles on 2015/12/29.
 */
public class CouponResultActivity extends Activity {

    private Context mContext;

    private TextView mCouponContent;
    private Button mPayByCash;//现金收款按钮
    private Button mPayByScanCode;//扫码付款按钮


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_result);
        mContext = this;

        mCouponContent = (TextView) findViewById(R.id.tv_coupon_message);
        mPayByCash = (Button) findViewById(R.id.bt_money);
        mPayByScanCode = (Button) findViewById(R.id.bt_scancode);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        boolean isSuccess = bundle.getBoolean("check_coupon_result_flag", false);//核销成功失败的标记
        if (isSuccess) {
            mCouponContent.setText(SessonData.loginUser.getResultData().cardId);
            mPayByScanCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.getHandler().sendEmptyMessage(Msg.MSG_FROM_SERVER_COUPON_SUCCESS);
                    finish();
                }
            });
        } else {
            mCouponContent.setText("您的卡券来自其他星球，我们无法识别哦");
            mPayByScanCode.setVisibility(View.GONE);
            mPayByCash.setText("返回");
        }

        //现金支付
        mPayByCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getHandler().sendEmptyMessage(Msg.MSG_FROM_SERVER_COUPON_SUCCESS);
                finish();
            }
        });

    }
}
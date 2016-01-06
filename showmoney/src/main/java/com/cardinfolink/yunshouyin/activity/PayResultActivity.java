package com.cardinfolink.yunshouyin.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cardinfolink.cashiersdk.listener.CashierListener;
import com.cardinfolink.cashiersdk.model.OrderData;
import com.cardinfolink.cashiersdk.model.ResultData;
import com.cardinfolink.cashiersdk.sdk.CashierSdk;
import com.cardinfolink.yunshouyin.R;
import com.cardinfolink.yunshouyin.constant.Msg;
import com.cardinfolink.yunshouyin.data.Coupon;
import com.cardinfolink.yunshouyin.data.SessonData;
import com.cardinfolink.yunshouyin.data.TradeBill;
import com.cardinfolink.yunshouyin.ui.ResultInfoItem;
import com.cardinfolink.yunshouyin.ui.SettingActionBarItem;
import com.cardinfolink.yunshouyin.view.HintDialog;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


/**
 * Created by charles on 2015/12/20.
 */
public class PayResultActivity extends Activity {
    private SettingActionBarItem mActionBar;

    private TextView mPayResult;
    private ImageView mPayResultPhoto;
    private TextView mResultExplain;
    private ResultInfoItem mPersonAccount;
    private ResultInfoItem mMakeDealTime;
    private ResultInfoItem mBillOrderNum;
    private TextView mReceiveMoney;
    private Button mConfirm;
    private ImageView mPayAccess;
    private TextView mReceiveMoneyStatus;
    private ResultInfoItem mCouponContent;
    private ResultInfoItem mActualTotalMoney;
    private ResultInfoItem mActualDiscount;
    private HintDialog mHintDialog;
    private TradeBill tradeBill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);

        mActionBar = (SettingActionBarItem) findViewById(R.id.action_bar);
        mHintDialog = new HintDialog(PayResultActivity.this, findViewById(R.id.hint_dialog));

        //获取交易参数
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("BillBundle");
        if (bundle != null) {
            tradeBill = (TradeBill) bundle.getSerializable("TradeBill");
        }


        mPayResult = (TextView) findViewById(R.id.tv_pay_result);
        mPayResultPhoto = (ImageView) findViewById(R.id.iv_pay_result);
        mResultExplain = (TextView) findViewById(R.id.tv_result_explain);

        mPersonAccount = (ResultInfoItem) findViewById(R.id.pay_account);
        mMakeDealTime = (ResultInfoItem) findViewById(R.id.pay_datetime);
        mBillOrderNum = (ResultInfoItem) findViewById(R.id.order_number);
        mCouponContent = (ResultInfoItem) findViewById(R.id.rif_coupon_content);
        mActualTotalMoney = (ResultInfoItem) findViewById(R.id.rif_coupon_total);
        mActualDiscount = (ResultInfoItem) findViewById(R.id.rif_coupon_discount);

        mReceiveMoney = (TextView) findViewById(R.id.total_money);
        mReceiveMoneyStatus = (TextView) findViewById(R.id.total_state);
        mConfirm = (Button) findViewById(R.id.btnconfirm);
        mPayAccess = (ImageView) findViewById(R.id.pay_chcd);

        //判断是否有卡券优惠
        boolean hasCouponDiscount = Coupon.getInstance().getSaleDiscount() != null && !"0".equals(Coupon.getInstance().getSaleDiscount());
        if (hasCouponDiscount) {
            mCouponContent.setVisibility(View.VISIBLE);
            mActualTotalMoney.setVisibility(View.VISIBLE);
            mActualDiscount.setVisibility(View.VISIBLE);

            mCouponContent.setRightText(Coupon.getInstance().getCardId());//卡券内容
            mActualTotalMoney.setRightText(tradeBill.originalTotal);//消费金额
            mActualDiscount.setRightText(String.valueOf(new BigDecimal(tradeBill.originalTotal).subtract(new BigDecimal(tradeBill.total)).doubleValue()));//优惠金额
        } else {
            mCouponContent.setVisibility(View.GONE);
            mActualTotalMoney.setVisibility(View.GONE);
            mActualDiscount.setVisibility(View.GONE);
        }

        //判断支付是否成功
        if ("success".equals(tradeBill.response)) {
            mPayResult.setText(R.string.pay_result_success);
            mPayResultPhoto.setImageResource(R.drawable.pay_result_succeed);
            mResultExplain.setText("");
            //标题收款键
            mActionBar.setLeftTextOnclickListner(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Coupon.getInstance().clear();
                    finish();
                }
            });
        } else if ("fail".equals(tradeBill.response)) {
            mPayResult.setText(R.string.pay_result_fail);
            mPayResult.setTextColor(Color.RED);
            mPayResultPhoto.setImageResource(R.drawable.pay_result_fail);
            mResultExplain.setText(tradeBill.errorDetail);
            mReceiveMoneyStatus.setTextColor(Color.RED);
            mReceiveMoney.setTextColor(Color.RED);
            mReceiveMoneyStatus.setText(R.string.pay_total_state_fail);
            if (hasCouponDiscount) {
                Boolean preferenceScancode = Coupon.getInstance().getVoucherType() != null && (Coupon.getInstance().getVoucherType().startsWith("4")
                        || Coupon.getInstance().getVoucherType().startsWith("5"));
                mActionBar.setLeftText(getString(R.string.coupon_getmoney));
                //指定扫码支付
                if (preferenceScancode) {
                    mActionBar.setLeftTextOnclickListner(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPayFailPref();
                        }
                    });
                }
                else{
                    //未指定扫码支付
                    mActionBar.setLeftTextOnclickListner(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPayFailUnPref();
                        }
                    });
                }
            } else {
                mActionBar.setLeftTextOnclickListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        }
        mPersonAccount.setRightText(SessonData.loginUser.getUsername());
        mMakeDealTime.setRightText(tradeBill.tandeDate);
        mBillOrderNum.setRightText(tradeBill.orderNum);
        mReceiveMoney.setText(tradeBill.total);

        if ("ALP".equals(tradeBill.chcd)) {
            mPayAccess.setImageResource(R.drawable.scan_alipay);
        } else if ("WXP".equals(tradeBill.chcd)) {
            mPayAccess.setImageResource(R.drawable.scan_wechat);
        } else {
            mPayAccess.setImageDrawable(null);
        }

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Coupon.getInstance().clear();
                finish();
            }
        });
    }

    private void showPayFailUnPref() {
        mHintDialog.setText(getString(R.string.coupon_abandom_verial_or_not), getString(R.string.coupon_pay_again), getString(R.string.coupon_payby_cash));
        mHintDialog.setCancelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Coupon.getInstance().clear();
                mHintDialog.hide();
                finish();
            }
        });
        mHintDialog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新支付
                MainActivity.getHandler().sendEmptyMessage(Msg.MSG_FROM_SERVER_COUPON_SUCCESS);
                mHintDialog.hide();
                finish();
            }

        });
        mHintDialog.show();

    }

    //弹出是否放弃本子核销对话框
    public void showPayFailPref() {
        mHintDialog.setText(getString(R.string.coupon_abandom_verial_or_not), getString(R.string.coupon_pay_again), getString(R.string.coupon_abandom));
        mHintDialog.setCancelOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //卡券冲正
                OrderData orderData = new OrderData();
                orderData.orderNum = geneOrderNumber();//订单号
                orderData.origOrderNum = Coupon.getInstance().getOrderNum();//设置原始订单号
                CashierSdk.startReversal(orderData, new CashierListener() {
                    @Override
                    public void onResult(ResultData resultData) {
                        if ("00".equals(resultData.respcd)) {
                            //冲正成功
                            Coupon.getInstance().clear();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PayResultActivity.this, getString(R.string.coupon_verial_success), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        } else {
                            //冲正失败
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PayResultActivity.this, getString(R.string.coupon_verial_fail), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int errorCode) {

                    }
                });
                mHintDialog.hide();
            }
        });
        mHintDialog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新支付
                MainActivity.getHandler().sendEmptyMessage(Msg.MSG_FROM_SERVER_COUPON_SUCCESS);
                mHintDialog.hide();
                finish();
            }

        });
        mHintDialog.show();
    }


    private String geneOrderNumber() {
        String mOrderNum;

        Date now = new Date();
        SimpleDateFormat spf = new SimpleDateFormat("yyMMddHHmmss");
        mOrderNum = spf.format(now);
        Random random = new Random();//订单号末尾随机的生成一个数
        for (int i = 0; i < 5; i++) {
            mOrderNum = mOrderNum + random.nextInt(10);
        }
        return mOrderNum;
    }
}
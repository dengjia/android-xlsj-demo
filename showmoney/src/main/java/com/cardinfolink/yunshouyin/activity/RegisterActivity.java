package com.cardinfolink.yunshouyin.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.cardinfolink.yunshouyin.R;
import com.cardinfolink.yunshouyin.api.QuickPayException;
import com.cardinfolink.yunshouyin.core.QuickPayCallbackListener;
import com.cardinfolink.yunshouyin.data.SaveData;
import com.cardinfolink.yunshouyin.data.SessonData;
import com.cardinfolink.yunshouyin.data.User;
import com.cardinfolink.yunshouyin.ui.SettingActionBarItem;
import com.cardinfolink.yunshouyin.ui.SettingPasswordItem;
import com.cardinfolink.yunshouyin.ui.SettingInputItem;
import com.cardinfolink.yunshouyin.util.ShowMoneyApp;
import com.cardinfolink.yunshouyin.view.ActivateDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity {

    private SettingActionBarItem mActionBar;//注册页面的标题栏
    private SettingInputItem mEmailEdit;
    private SettingPasswordItem mPasswordEdit;
    private SettingPasswordItem mQrPasswordEdit;

    private Button mRegisterNext;

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;
        initView();
    }

    private void initView() {
        mActionBar = (SettingActionBarItem) findViewById(R.id.action_bar);//注册页面标题栏
        mEmailEdit = (SettingInputItem) findViewById(R.id.register_email);

        mPasswordEdit = (SettingPasswordItem) findViewById(R.id.register_password);

        mQrPasswordEdit = (SettingPasswordItem) findViewById(R.id.register_qr_password);

        //注册页面标题栏添加返回事件监听
        mActionBar.setLeftTextOnclickListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRegisterNext = (Button) findViewById(R.id.btnregister);
        mRegisterNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegisterNextOnClick(v);
            }
        });
    }

    public void btnRegisterNextOnClick(View view) {
        final String username = mEmailEdit.getText(); //用户名
        final String password = mPasswordEdit.getPassword(); //密码，第一次输入的
        final String qrPassword = mQrPasswordEdit.getPassword(); //确认密码，第二次输入的

        if (!validate(username, password, qrPassword)) {
            return;
        }

        mLoadingDialog.startLoading();
        quickPayService.registerAsync(username, password, qrPassword, new QuickPayCallbackListener<Void>() {
            @Override
            public void onSuccess(Void data) {
                //没有返回值的,那边返回的是null.
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                SaveData.setUser(mContext, user);//保存到本地文件

                //这里保存到sessondata中，下面一个activity中会用到
                SessonData.loginUser.setUsername(username);//保存到sessondata里面
                SessonData.loginUser.setPassword(password);//保存到sessondata里面

                mLoadingDialog.endLoading();

                //走到这里是注册成功了 ，就是注册的第一步成功了，之后会进入完善信息的页面。
                //如果注册成功 点击 了 已激活 按钮 就跳转到 注册的第二个页面。
                View activateView = findViewById(R.id.activate_dialog);
                ActivateDialog activateDialog = new ActivateDialog(mContext, activateView, username);
                activateDialog.setBodyText(getResources().getString(R.string.activate_message) + username);
                activateDialog.setCancelText(getResources().getString(R.string.activate_after));
                activateDialog.setOkText(getResources().getString(R.string.activate_had_activated));
                //这里重新实现右边确定按钮的点击事件。默认是进入login界面，这里要进入RegisterActivateActivity界面
                activateDialog.setOkOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RegisterActivity.this, RegisterActivateActivity.class);
                        startActivity(intent);//跳转到RegisterActivateActivity页面
                        //在RegisterActivateActivity里面判断一下是否真的激活了。是的话就进入RegisterNext页面了。
                        finish();//结束当前的页面
                    }
                });

                activateDialog.show();//显示激活对话框，这里文本都是自定义的。
            }

            @Override
            public void onFailure(QuickPayException ex) {
                String errorMsg = ex.getErrorMsg();
                //更新UI
                mLoadingDialog.endLoading();
                Bitmap alertBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wrong);
                mAlertDialog.show(errorMsg, alertBitmap);
            }
        });
    }

    private boolean validate(String email, String password, String qrPassword) {
        String alertMsg = "";
        Bitmap alertBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.wrong);
        if (TextUtils.isEmpty(email)) {
            alertMsg = ShowMoneyApp.getResString(R.string.alert_error_email_cannot_empty);
            mAlertDialog.show(alertMsg, alertBitmap);
            return false;
        }
        if (!checkEmail(email)) {
            alertMsg = ShowMoneyApp.getResString(R.string.alert_error_email_format_error);
            mAlertDialog.show(alertMsg, alertBitmap);
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            alertMsg = ShowMoneyApp.getResString(R.string.alert_error_password_cannot_empty);
            mAlertDialog.show(alertMsg, alertBitmap);
            return false;
        }
        if (password.length() < 6) {
            alertMsg = ShowMoneyApp.getResString(R.string.alert_error_password_short_six);
            mAlertDialog.show(alertMsg, alertBitmap);
            return false;
        }
        if (!password.equals(qrPassword)) {
            alertMsg = ShowMoneyApp.getResString(R.string.alert_error_qrpassword_error);
            mAlertDialog.show(alertMsg, alertBitmap);
            return false;
        }

        return true;
    }

}

package com.cardinfolink.yunshouyin.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.cardinfolink.cashiersdk.model.InitData;
import com.cardinfolink.cashiersdk.sdk.CashierSdk;
import com.cardinfolink.yunshouyin.R;
import com.cardinfolink.yunshouyin.api.QuickPayException;
import com.cardinfolink.yunshouyin.constant.SystemConfig;
import com.cardinfolink.yunshouyin.core.QuickPayCallbackListener;
import com.cardinfolink.yunshouyin.data.SessonData;
import com.cardinfolink.yunshouyin.data.User;
import com.cardinfolink.yunshouyin.model.Bank;
import com.cardinfolink.yunshouyin.model.City;
import com.cardinfolink.yunshouyin.model.Province;
import com.cardinfolink.yunshouyin.model.SubBank;
import com.cardinfolink.yunshouyin.util.BankBaseUtil;
import com.cardinfolink.yunshouyin.util.CommunicationListener;
import com.cardinfolink.yunshouyin.util.HttpCommunicationUtil;
import com.cardinfolink.yunshouyin.util.JsonUtil;
import com.cardinfolink.yunshouyin.util.ParamsUtil;
import com.cardinfolink.yunshouyin.util.TelephonyManagerUtil;
import com.cardinfolink.yunshouyin.util.VerifyUtil;
import com.cardinfolink.yunshouyin.view.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegisterNextActivity extends BaseActivity {
    private static final String TAG = "RegisterNextActivity";

    private EditText mNameEdit;
    private EditText mBanknumEdit;
    private EditText mPhonenumEdit;

    private AutoCompleteTextView mProvinceEdit;
    private Spinner mProvinceSpinner;
    private List<String> mProvinceList;
    private ArrayAdapter mProvinceAdapter;
    private SearchAdapter mProvinceSearchAdapter;

    private AutoCompleteTextView mCityEdit;
    private Spinner mCitySpinner;
    private List<String> mCityList;
    private List<String> mBankIdList;
    private ArrayAdapter mCityAdapter;
    private SearchAdapter mCitySearchAdapter;

    private AutoCompleteTextView mOpenBankEdit;
    private Spinner mOpenBankSpinner;
    private List<String> mOpenBankList;
    private List<String> mCityCodeList;
    private ArrayAdapter mOpenBankAdapter;
    private SearchAdapter mOpenBankSearchAdapter;

    private AutoCompleteTextView mBranchBankEdit;
    private Spinner mBranchBankSpinner;
    private List<String> mBranchBankList;
    private List<String> mBankNoList;
    private ArrayAdapter mBranchBankAdapter;
    private SearchAdapter mBranchBankSearchAdapter;

    private String info_province;
    private String info_city;
    private String info_openbank;
    private String info_branch_bank;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_next_activity);
        initLayout();
        initListener();
        initData();
    }

    private void initLayout() {

        mNameEdit = (EditText) findViewById(R.id.info_name);
        mBanknumEdit = (EditText) findViewById(R.id.info_banknum);
        mPhonenumEdit = (EditText) findViewById(R.id.info_phonenum);
        VerifyUtil.bankCardNumAddSpace(mBanknumEdit);

        VerifyUtil.bankCardNumAddSpace(mBanknumEdit);

        mProvinceEdit = (AutoCompleteTextView) findViewById(R.id.edit_province);
        mProvinceSpinner = (Spinner) findViewById(R.id.spinner_province);
        // 适配器
        mProvinceList = new ArrayList<String>();
        mProvinceList.add("开户行所在省份");
        mProvinceAdapter = new ArrayAdapter<String>(mContext,
                R.layout.spinner_item, mProvinceList);

        // 设置样式
        mProvinceAdapter.setDropDownViewResource(R.layout.spinner_drop_item);
        // 加载适配器
        mProvinceSpinner.setAdapter(mProvinceAdapter);

        mProvinceSearchAdapter = new SearchAdapter(mContext, mProvinceList);

        mProvinceEdit.setAdapter(mProvinceSearchAdapter);
        mProvinceEdit.setThreshold(1);
        // mProvinceEdit.setf

        mCityEdit = (AutoCompleteTextView) findViewById(R.id.edit_city);
        mCitySpinner = (Spinner) findViewById(R.id.spinner_city);
        // 适配器
        mCityList = new ArrayList<String>();
        mCityCodeList = new ArrayList<String>();
        mCityList.add("开户行所在城市");
        mCityCodeList.add("");
        mCityAdapter = new ArrayAdapter<String>(mContext,
                R.layout.spinner_item, mCityList);
        // 设置样式
        mCityAdapter.setDropDownViewResource(R.layout.spinner_drop_item);
        // 加载适配器
        mCitySpinner.setAdapter(mCityAdapter);

        mCitySearchAdapter = new SearchAdapter(mContext, mCityList);

        mCityEdit.setAdapter(mCitySearchAdapter);
        mCityEdit.setThreshold(1);

        mOpenBankEdit = (AutoCompleteTextView) findViewById(R.id.edit_openbank);
        mOpenBankSpinner = (Spinner) findViewById(R.id.spinner_openbank);
        // 适配器
        mOpenBankList = new ArrayList<String>();
        mOpenBankList.add("请选择开户银行");
        mBankIdList = new ArrayList<String>();
        mBankIdList.add("");

        mOpenBankAdapter = new ArrayAdapter<String>(mContext,
                R.layout.spinner_item, mOpenBankList);
        // 设置样式
        mOpenBankAdapter.setDropDownViewResource(R.layout.spinner_drop_item);
        // 加载适配器
        mOpenBankSpinner.setAdapter(mOpenBankAdapter);

        mOpenBankSearchAdapter = new SearchAdapter(mContext, mOpenBankList);
        mOpenBankEdit.setAdapter(mOpenBankSearchAdapter);
        mOpenBankEdit.setThreshold(1);

        mBranchBankEdit = (AutoCompleteTextView) findViewById(R.id.edit_branchbank);
        mBranchBankSpinner = (Spinner) findViewById(R.id.spinner_branchbank);
        // 适配器
        mBranchBankList = new ArrayList<String>();
        mBranchBankList.add("请选择开户支行");
        mBankNoList = new ArrayList<String>();
        mBankNoList.add("行号");

        mBranchBankAdapter = new ArrayAdapter<String>(mContext,
                R.layout.spinner_item, mBranchBankList);
        // 设置样式
        mBranchBankAdapter.setDropDownViewResource(R.layout.spinner_drop_item);
        // 加载适配器
        mBranchBankSpinner.setAdapter(mBranchBankAdapter);

        mBranchBankSearchAdapter = new SearchAdapter(mContext, mBranchBankList);
        mBranchBankEdit.setAdapter(mBranchBankSearchAdapter);
        mBranchBankEdit.setThreshold(1);

    }


    public void initData() {
        bankDataService.getProvince(new ProvinceQuickPayCallbackListener());
        bankDataService.getBank(new BankQuickPayCallbackListener());
    }

    private void initListener() {
        mProvinceEdit.addTextChangedListener(new RegisterTextWatcher(mProvinceEdit));

        mCityEdit.addTextChangedListener(new RegisterTextWatcher(mCityEdit));

        mOpenBankEdit.addTextChangedListener(new RegisterTextWatcher(mOpenBankEdit));

        mProvinceSpinner.setOnItemSelectedListener(new RegisterOnItemSelectedListener());

        mCitySpinner.setOnItemSelectedListener(new RegisterOnItemSelectedListener());

        mOpenBankSpinner.setOnItemSelectedListener(new RegisterOnItemSelectedListener());

        mBranchBankSpinner.setOnItemSelectedListener(new RegisterOnItemSelectedListener());

        mCityEdit.setOnFocusChangeListener(new RegisterOnFocusChangeListener());

        mOpenBankEdit.setOnFocusChangeListener(new RegisterOnFocusChangeListener());

        mBranchBankEdit.setOnFocusChangeListener(new RegisterOnFocusChangeListener());

    }

    public void BtnRegisterFinishedOnClick(View view) {
        if (validate()) {
            mLoadingDialog.startLoading();
            User user = new User();
            user.setUsername(SessonData.loginUser.getUsername());
            user.setPassword(SessonData.loginUser.getPassword());
            // user.setBankOpen(mOpenBankEdit.getText().toString());
            user.setProvince(mProvinceEdit.getText().toString());
            user.setBankOpen(mOpenBankEdit.getText().toString());
            user.setCity(mCityEdit.getText().toString());
            user.setBranchBank(mBranchBankEdit.getText().toString());
            user.setBankNo(mBankNoList.get(mBranchBankList.indexOf(mBranchBankEdit.getText().toString())));
            user.setPayee(mNameEdit.getText().toString());
            user.setPayeeCard(mBanknumEdit.getText().toString()
                    .replace(" ", ""));
            user.setPhoneNum(mPhonenumEdit.getText().toString());


            HttpCommunicationUtil.sendDataToServer(
                    ParamsUtil.getImproveInfo(user),
                    new CommunicationListener() {

                        @Override
                        public void onResult(String result) {
                            String state = JsonUtil.getParam(result, "state");
                            if (state.equals("success")) {
                                String user_json = JsonUtil.getParam(result,
                                        "user");
                                SessonData.loginUser.setClientid(JsonUtil
                                        .getParam(user_json, "clientid"));
                                SessonData.loginUser.setObjectId(JsonUtil
                                        .getParam(user_json, "objectId"));
                                SessonData.loginUser.setLimit(JsonUtil
                                        .getParam(user_json, "limit"));
                                InitData data = new InitData();
                                data.mchntid = SessonData.loginUser
                                        .getClientid();// 商户号
                                data.inscd = JsonUtil.getParam(user_json,
                                        "inscd");// 机构号
                                data.signKey = JsonUtil.getParam(user_json,
                                        "signKey");// 秘钥
                                data.terminalid = TelephonyManagerUtil
                                        .getDeviceId(mContext);// 设备号
                                data.isProduce = SystemConfig.IS_PRODUCE;// 是否生产环境
                                CashierSdk.init(data);
                                Intent intent = new Intent(
                                        RegisterNextActivity.this,
                                        MainActivity.class);
                                RegisterNextActivity.this.startActivity(intent);
                                RegisterNextActivity.this.finish();

                            } else {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // 更新UI
                                        mLoadingDialog.endLoading();
                                        mAlertDialog.show(
                                                "提交失败!",
                                                BitmapFactory.decodeResource(
                                                        mContext.getResources(),
                                                        R.drawable.wrong));
                                    }

                                });
                            }
                        }

                        @Override
                        public void onError(final String error) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // 更新UI
                                    mLoadingDialog.endLoading();
                                    mAlertDialog.show(error, BitmapFactory
                                            .decodeResource(
                                                    mContext.getResources(),
                                                    R.drawable.wrong));
                                }

                            });
                        }
                    });

            // Intent intent = new
            // Intent(RegisterNextActivity.this,MainActivity.class);
            // RegisterNextActivity.this.startActivity(intent);
            // RegisterNextActivity.this.finish();
        }

        // Intent intent = new
        // Intent(RegisterNextActivity.this,MainActivity.class);
        // RegisterNextActivity.this.startActivity(intent);
        // RegisterNextActivity.this.finish();
    }

    @SuppressLint("NewApi")
    private boolean validate() {
        if (mBranchBankList.indexOf(mBranchBankEdit.getText().toString()) < 0) {
            mBranchBankEdit.setText("");
        }
        String openbank = "";
        String name = mNameEdit.getText().toString().replace(" ", "");
        String banknum = mBanknumEdit.getText().toString().replace(" ", "");
        String phonenum = mPhonenumEdit.getText().toString().replace(" ", "");

        if (mProvinceEdit.getText().toString().isEmpty()) {
            alertShow("开户行所在省份不能为空!", BitmapFactory
                    .decodeResource(this.getResources(), R.drawable.wrong));
            return false;
        }

        if (mCityEdit.getText().toString().isEmpty()) {
            alertShow("开户行所在城市不能为空!", BitmapFactory
                    .decodeResource(this.getResources(), R.drawable.wrong));
            return false;
        }

        if (mOpenBankEdit.getText().toString().isEmpty()) {
            alertShow("开户行不能为空!", BitmapFactory.decodeResource(
                    this.getResources(), R.drawable.wrong));
            return false;
        }

        if (mBranchBankEdit.getText().toString().isEmpty()) {
            alertShow("开户支行不能为空!", BitmapFactory.decodeResource(
                    this.getResources(), R.drawable.wrong));
            return false;
        }

        if (name.isEmpty()) {
            alertShow("姓名不能为空!", BitmapFactory.decodeResource(
                    this.getResources(), R.drawable.wrong));
            return false;
        }

        if (banknum.isEmpty()) {
            alertShow("银行卡号不能为空!", BitmapFactory.decodeResource(
                    this.getResources(), R.drawable.wrong));
            return false;
        }

        if (!VerifyUtil.checkBankCard(banknum)) {
            alertShow("请输入正确的银行卡号!", BitmapFactory
                    .decodeResource(this.getResources(), R.drawable.wrong));
            return false;
        }

        if (phonenum.isEmpty()) {
            alertShow("手机号不能为空!", BitmapFactory.decodeResource(
                    this.getResources(), R.drawable.wrong));
            return false;
        }

        if (!VerifyUtil.isMobileNO(phonenum)) {
            alertShow("请输入正确的手机号!", BitmapFactory.decodeResource(
                    this.getResources(), R.drawable.wrong));
            return false;
        }

        return true;
    }


    private void updateProvinceAdapter(List<Province> data) {
        //这里直接得到的就是一个省份的list，不需要再去用json去解析了。
        List<String> tempProvinceList = new ArrayList<>();
        tempProvinceList.add(0, "开户行所在省份");

        Iterator<Province> iterator = data.iterator();
        while (iterator.hasNext()) {
            Province p = iterator.next();
            tempProvinceList.add(p.getProvinceName());
        }
        mProvinceList.clear();
        mProvinceList.addAll(tempProvinceList);
        mProvinceAdapter.notifyDataSetChanged();
        mProvinceSearchAdapter.setData(mProvinceList);
        mProvinceSearchAdapter.notifyDataSetChanged();
    }

    private void updateBankAdapter(List<Bank> data) {
        List<String> tempOpenBankList = new ArrayList<String>();
        List<String> tempBankIdList = new ArrayList<String>();

        Iterator<Bank> it = data.iterator();
        while (it.hasNext()) {
            Bank b = it.next();
            tempOpenBankList.add(b.getBankName());
            tempBankIdList.add(b.getId());
        }
        tempOpenBankList.add(0, "请选择开户银行");
        tempBankIdList.add(0, "");//为了使index对应起来

        mOpenBankList.clear();
        mBankIdList.clear();
        mOpenBankList.addAll(tempOpenBankList);
        mBankIdList.addAll(tempBankIdList);
        mOpenBankSpinner.setSelection(0);
        mOpenBankAdapter.notifyDataSetChanged();
        mOpenBankSearchAdapter.notifyDataSetChanged();
    }

    private void updateCityAdapter(final List<City> data) {
        ArrayList<String> tempCityList = new ArrayList<String>();
        ArrayList<String> tempCityCodeList = new ArrayList<String>();
        tempCityList.add(0, "开户行所在城市");
        tempCityCodeList.add(0, "");
        Iterator<City> it = data.iterator();
        while (it.hasNext()) {
            City c = it.next();
            tempCityList.add(c.getCityName());//"city_name"这个要注意别弄成getCity（）了。
            tempCityCodeList.add(c.getCityCode());//"city_code"
        }
        mCityList.clear();
        mCityList.addAll(tempCityList);
        mCityCodeList.clear();
        mCityCodeList.addAll(tempCityCodeList);

        mCitySpinner.setSelection(0);
        mCityAdapter.notifyDataSetChanged();
        mCitySearchAdapter.setData(mCityList);
    }


    private void updateBranchBankAdapter(List<SubBank> data) {
        final List<String> tempBranchBankList = new ArrayList<String>();
        final List<String> tempBankNoList = new ArrayList<String>();
        Iterator<SubBank> it = data.iterator();

        while (it.hasNext()) {
            SubBank sb = it.next();
            tempBranchBankList.add(sb.getBankName());
            tempBankNoList.add(sb.getOneBankNo() + "|" + sb.getTwoBankNo());
        }
        tempBranchBankList.add(0, "请选择开户支行");
        tempBankNoList.add(0, "行号");

        mBranchBankList.clear();
        mBranchBankList.addAll(tempBranchBankList);
        mBankNoList.clear();
        mBankNoList.addAll(tempBankNoList);
        mBranchBankSpinner.setSelection(0);
        mBranchBankAdapter.notifyDataSetChanged();
        mBranchBankSearchAdapter.setData(mBranchBankList);
    }

    //内部类，实现QuickPayCallbackListener接口,用来获取bank信息
    private class BankQuickPayCallbackListener implements QuickPayCallbackListener<List<Bank>> {

        @Override
        public void onSuccess(List<Bank> data) {
            updateBankAdapter(data);
        }

        @Override
        public void onFailure(QuickPayException ex) {

        }
    }

    //内部类，实现QuickPayCallbackListener接口
    private class ProvinceQuickPayCallbackListener implements QuickPayCallbackListener<List<Province>> {

        @Override
        public void onSuccess(List<Province> data) {
            updateProvinceAdapter(data);
        }

        @Override
        public void onFailure(QuickPayException ex) {

        }
    }

    private class CityQuickPayCallbackListener implements QuickPayCallbackListener<List<City>> {

        @Override
        public void onSuccess(List<City> data) {
            updateCityAdapter(data);
        }

        @Override
        public void onFailure(QuickPayException ex) {

        }
    }

    private class BranchBankQuickPayCallbackListener implements QuickPayCallbackListener<List<SubBank>> {

        @Override
        public void onSuccess(List<SubBank> data) {
            updateBranchBankAdapter(data);
        }

        @Override
        public void onFailure(QuickPayException ex) {

        }
    }

    private class RegisterTextWatcher implements TextWatcher {
        private View view;

        public RegisterTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String province, city, openBank;

            switch (view.getId()) {
                case R.id.edit_province:
                    //province
                    mCityEdit.setText("");//先把city的清空
                    province = mProvinceEdit.getText().toString();
                    if (mProvinceList.indexOf(province) > 0) {
                        bankDataService.getCity(province, new CityQuickPayCallbackListener());
                    }//end if()
                    break;
                case R.id.edit_city:
                    //city
                    mOpenBankEdit.setText("");
                    city = mCityEdit.getText().toString();
                    if (mCityList.indexOf(city) > 0) {
                        bankDataService.getBank(new BankQuickPayCallbackListener());
                    }
                    break;
                case R.id.edit_openbank:
                    //bank
                    mBranchBankEdit.setText("");
                    openBank = mOpenBankEdit.getText().toString();
                    city = mCityEdit.getText().toString();
                    province = mProvinceEdit.getText().toString();
                    if (mOpenBankList.indexOf(openBank) > 0) {
                        int indexCity = mCityList.indexOf(city);
                        int indexBank = mOpenBankList.indexOf(openBank);
                        if (indexBank > 0 && indexCity > 0) {
                            String cityCode = mCityCodeList.get(indexCity);
                            String bankId = mBankIdList.get(indexBank);
                            bankDataService.getBranchBank(cityCode, bankId, new BranchBankQuickPayCallbackListener());
                        }
                    }
                    break;
                case R.id.edit_branchbank:
                    //branch bank
                    break;
                case R.id.info_name:
                    //name
                    break;
                case R.id.info_banknum:
                    //bank number
                    break;
                case R.id.info_phonenum:
                    //phone number
                    break;
            }
        }
    }

    private class RegisterOnItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                switch (parent.getId()) {
                    case R.id.spinner_province:
                        mProvinceEdit.setText(mProvinceList.get(position));
                        break;
                    case R.id.spinner_city:
                        mCityEdit.setText(mCityList.get(position));
                        break;
                    case R.id.spinner_openbank:
                        mOpenBankEdit.setText(mOpenBankList.get(position));
                        break;
                    case R.id.spinner_branchbank:
                        mBranchBankEdit.setText(mBranchBankList.get(position));
                        break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class RegisterOnFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                return;
            }
            switch (view.getId()) {
                case R.id.edit_city:
                    if (mProvinceList.indexOf(mProvinceEdit.getText().toString()) < 0) {
                        mProvinceEdit.setText("");
                    }
                    break;
                case R.id.edit_openbank:
                    if (mCityList.indexOf(mCityEdit.getText().toString()) < 0) {
                        mCityEdit.setText("");
                    }
                    break;
                case R.id.edit_branchbank:
                    if (mOpenBankList.indexOf(mOpenBankEdit.getText().toString()) < 0) {
                        mOpenBankEdit.setText("");
                    }
                    break;
                case R.id.edit_province:
                    break;
            }
        }
    }
}

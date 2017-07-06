package cn.appscomm.l38t.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.checkbox.MutlRadioGroup;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.config.AppConfig;
import cn.appscomm.l38t.config.GoalConfig;
import cn.appscomm.l38t.utils.DateUtil;
import cn.appscomm.l38t.utils.HeightWeightDialogHelper;
import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.l38t.utils.UnitHelper;
import cn.appscomm.netlib.bean.account.AccountEdit;
import cn.appscomm.netlib.bean.account.AccountInfo;
import cn.appscomm.netlib.bean.account.UserInfoBean;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;
import cn.appscomm.netlib.util.NetworkUtil;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/1 17:02
 */
public class RegisterUserInfoActivity extends BaseActivity {

    @BindView(R.id.et_reg_username)
    EditText etRegUsername;
    @BindView(R.id.mrp_sex)
    MutlRadioGroup mrpSex;
    @BindView(R.id.mrp_unit)
    MutlRadioGroup mrpUnit;
    @BindView(R.id.tv_height)
    TextView tvHeight;
    @BindView(R.id.iv_info_height)
    ImageView ivInfoHeight;
    @BindView(R.id.rl_height)
    RelativeLayout rlHeight;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.ic_info_weight)
    ImageView icInfoWeight;
    @BindView(R.id.rl_weight)
    RelativeLayout rlWeight;
    @BindView(R.id.tv_reg_birthday)
    TextView tvRegBirthday;
    @BindView(R.id.ll_birthday)
    LinearLayout llBirthday;
    @BindView(R.id.btn_reg)
    Button btnReg;
    @BindView(R.id.rbtn_male)
    RadioButton rbtnMale;
    @BindView(R.id.rbtn_female)
    RadioButton rbtnFemale;
    @BindView(R.id.rbtn_unit_us)
    RadioButton rbtnUnitUs;
    @BindView(R.id.rbtn_unit_metric)
    RadioButton rbtnUnitMetric;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;

    private int sex = UserInfoBean.SEX_MALE;
    private int unit = UnitHelper.UNIT_METRIC;
    private int userInfoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_info);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setTitle(getString(R.string.title_sign_up));
        setRadioGroupListenters();
        selectSex(UserInfoBean.SEX_MALE);
        selectUnit(UnitHelper.UNIT_METRIC);
        if (getIntent() != null && getIntent().getExtras() != null) {
            userInfoId = getIntent().getExtras().getInt("userInfoId");
        }
    }

    private void selectSex(int sexNow) {
        if (sexNow == UserInfoBean.SEX_MALE) {
            rbtnMale.setChecked(true);
            rbtnFemale.setChecked(false);
            sex = UserInfoBean.SEX_MALE;
        } else {
            rbtnMale.setChecked(false);
            rbtnFemale.setChecked(true);
            sex = UserInfoBean.SEX_FEMALE;
        }
    }

    private void selectUnit(int unitNow) {
        if (unitNow == UnitHelper.UNIT_US) {
            rbtnUnitUs.setChecked(true);
            rbtnUnitMetric.setChecked(false);
            unit = UnitHelper.UNIT_US;
        } else {
            rbtnUnitUs.setChecked(false);
            rbtnUnitMetric.setChecked(true);
            unit = UnitHelper.UNIT_METRIC;
        }
    }

    private void setRadioGroupListenters() {
        mrpSex.setOnCheckedChangeListener(new MutlRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MutlRadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_male) {
                    sex = UserInfoBean.SEX_MALE;
                } else {
                    sex = UserInfoBean.SEX_FEMALE;
                }
            }
        });
        mrpUnit.setOnCheckedChangeListener(new MutlRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MutlRadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtn_unit_us) {
                    if (unit == UnitHelper.UNIT_METRIC) {//转换数据
                        if (!TextUtils.isEmpty(tvHeight.getText().toString())) {
                            int[] ftIn = UnitHelper.getSwitchCmToFtIn(tvHeight);
                            tvHeight.setText(ftIn[0] + UnitHelper.UNIT_FT_SPLIT + ftIn[1] + UnitHelper.UNIT_IN_SPLIT + " " + UnitHelper.getArrHeightUnit()[UnitHelper.UNIT_US]);
                        }
                        if (!TextUtils.isEmpty(tvWeight.getText().toString())) {
                            float nowWeight = UnitHelper.getSwitchWeightKgToLbs(tvWeight);
                            tvWeight.setText(nowWeight + " " + UnitHelper.getArrWeightUnit()[UnitHelper.UNIT_US]);
                        }
                    }
                    unit = UnitHelper.UNIT_US;
                } else {
                    if (unit == UnitHelper.UNIT_US) {//转换数据
                        if (!TextUtils.isEmpty(tvHeight.getText().toString())) {
                            float height = UnitHelper.getSwitchHeightFtInToCm(tvHeight);
                            tvHeight.setText((int)height + " " + UnitHelper.getArrHeightUnit()[UnitHelper.UNIT_METRIC]);
                        }
                        if (!TextUtils.isEmpty(tvWeight.getText().toString())) {
                            float nowWeight = UnitHelper.getSwitchWeightLbsToKg(tvWeight);
                            tvWeight.setText(nowWeight + " " + UnitHelper.getArrWeightUnit()[UnitHelper.UNIT_METRIC]);
                        }
                    }
                    unit = UnitHelper.UNIT_METRIC;
                }
            }
        });
    }

    @OnClick({R.id.iv_info_height, R.id.rl_height, R.id.ic_info_weight, R.id.rl_weight, R.id.ll_birthday, R.id.btn_reg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_info_height:
                showNoTipDialog(getString(R.string.show_height_tips));
                break;
            case R.id.rl_height:
                HeightWeightDialogHelper.getInstance().showHeightChooseDialog(RegisterUserInfoActivity.this, unit, tvHeight, rlMain);
                break;
            case R.id.ic_info_weight:
                showNoTipDialog(getString(R.string.show_weight_tips));
                break;
            case R.id.rl_weight:
                HeightWeightDialogHelper.getInstance().showWeightChooseDialog(RegisterUserInfoActivity.this, unit, tvWeight, rlMain);
                break;
            case R.id.ll_birthday:
                showBirthdayChooseDialog();
                break;
            case R.id.btn_reg:
                saveDataToServer();
                break;
        }
    }

    private boolean checkPostData(String userName, float height, float weight, String birthday) {
        if (TextUtils.isEmpty(userName)) {
            showToast(getString(R.string.reg_name_null));
            return false;
        }
        if (height <= 0) {
            showToast(getString(R.string.reg_height_null));
            return false;
        }
        if (weight <= 0) {
            showToast(getString(R.string.reg_weight_null));
            return false;
        }
        if (TextUtils.isEmpty(birthday)) {
            showToast(getString(R.string.reg_birthday_null));
            return false;
        }
        return true;
    }


    private void saveDataToServer() {
        String userName = etRegUsername.getText().toString().trim();
        String birthday = tvRegBirthday.getText().toString().trim();
        float heightUP = 0, weightUP;
        if (unit == UnitHelper.UNIT_US) {//全部用公制来传输,英制转换为公制后传输
            heightUP = UnitHelper.getSwitchHeightFtInToCm(tvHeight);
            weightUP = UnitHelper.getSwitchWeightLbsToKg(tvWeight);
        } else {
            heightUP = UnitHelper.getNowCmHeightValue(tvHeight);
            weightUP = UnitHelper.getNowKgWeightValue(tvWeight);
        }
        if (checkPostData(userName, heightUP, weightUP, birthday)) {
            final AccountEdit accountEdit = new AccountEdit(userInfoId);
            accountEdit.setUserName(userName);
            accountEdit.setBirthday(birthday);
            accountEdit.setHeight(heightUP);
            accountEdit.setHeightUnit(UnitHelper.UNIT_METRIC);//全部用公制来传输
            accountEdit.setWeight(weightUP);
            accountEdit.setWeightUnit(UnitHelper.UNIT_METRIC);//全部用公制来传输
            accountEdit.setSex(sex);
            LogUtil.w(TAG, "userName=" + userName + ",birthday=" + birthday + ",sex=" + sex + ",unit=" + UnitHelper.UNIT_METRIC + ",heightUP=" + heightUP + ",weightUP=" + weightUP);
            sendToServer(accountEdit);
        }
    }

    private void sendToServer(final AccountEdit accountEdit) {
        if (!NetworkUtil.isNetworkConnected(this)) {
            showTipDialog(getString(R.string.http_network_failed));
            return;
        }
        showBigLoadingProgress(null);
        RequestManager.getInstance().accountEdit(accountEdit, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                dismissLoadingDialog();
                if (HttpCode.isSuccess(statusCode)) {
                    editSuccess(accountEdit);
                } else {
                    String msg = HttpCode.getInstance(RegisterUserInfoActivity.this).getCodeString(statusCode);
                    showTipDialog(msg);
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
                dismissLoadingDialog();
                String msg = HttpCode.getInstance(RegisterUserInfoActivity.this).getCodeString(statusCode);
                showTipDialog(msg);
            }
        });
    }

    private void editSuccess(AccountEdit accountEdit) {
        UserInfoBean localUser = AccountConfig.getUserInfoBean();
        if (localUser != null) {
            localUser.setUserName(accountEdit.getUserName());
            localUser.setBirthday(accountEdit.getBirthday());
            localUser.setHeight(accountEdit.getHeight());
            localUser.setHeightUnit(UnitHelper.UNIT_METRIC);//全部用公制来传输
            localUser.setWeight(accountEdit.getWeight());
            localUser.setWeightUnit(UnitHelper.UNIT_METRIC);//全部用公制来传输
            localUser.setSex(accountEdit.getSex());
            AccountConfig.setUserInfoBean(localUser);
        }
        AppConfig.setLocalUnit(unit);//根据用户选择
        GoalConfig.setLocalUserGoal(null);
        AccountConfig.setFriendsAccountInfo(null);
        startActivity(MainActivity.class);
        finish();
    }

    private void showBirthdayChooseDialog() {
        int year = 82 + UnitHelper.YEAR_START;
        int month = 0;
        int day = 0;
        new DatePickerDialog(RegisterUserInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
            // 这三个参数就是用户选择完成时的时间
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (!view.isShown())
                    return;

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                if (calendar.getTimeInMillis()<= System.currentTimeMillis()){
                    tvRegBirthday.setText(DateUtil.dateToStr(calendar.getTime(), AccountInfo.DIRTHDAY_FORMATER));
                }
            }
        }, year, month, day + 1).show();
    }

   
    @Override
    protected void onDestroy() {
        super.onDestroy();
        UnitHelper.getInstance().onDestory();
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent(this, WelcomeYQActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
        finish();
    }
}

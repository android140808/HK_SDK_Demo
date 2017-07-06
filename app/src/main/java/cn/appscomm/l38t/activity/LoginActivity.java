package cn.appscomm.l38t.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appscomm.bluetooth.config.BluetoothConfig;
import com.appscomm.bluetooth.manage.GlobalVarManager;

import org.litepal.crud.DataSupport;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.edit.AutoDelEditText;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.config.AppConfig;
import cn.appscomm.l38t.config.GoalConfig;
import cn.appscomm.l38t.constant.APPConstant;
import cn.appscomm.l38t.model.database.RemindDataModel;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.CommonUtils;
import cn.appscomm.l38t.utils.UnitHelper;
import cn.appscomm.netlib.bean.account.Login;
import cn.appscomm.netlib.bean.account.LoginObtain;
import cn.appscomm.netlib.bean.account.UserInfoBean;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.config.BaseLocalConfig;
import cn.appscomm.netlib.config.NetConfig;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;
import cn.appscomm.netlib.util.NetworkUtil;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/1 17:03
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_login_email)
    AutoDelEditText etLoginEmail;
    @BindView(R.id.et_login_password)
    AutoDelEditText etLoginPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_login_forgot_password)
    TextView tvLoginForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        setTitle(getString(R.string.title_login));
        String loginName = AccountConfig.getUserLoginName();
        String password = AccountConfig.getUserLoginPassword();
        if (!TextUtils.isEmpty(loginName)) {
            etLoginEmail.setText(loginName);
        }
        if (!TextUtils.isEmpty(password)) {
            etLoginPassword.setText(password);
        }
        etLoginEmail.setText("ceshi01@163.com");
        etLoginPassword.setText("123456");
    }

    @OnClick({R.id.btn_login, R.id.tv_login_forgot_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (checkPostData()) {
                    login();
                }
                break;
            case R.id.tv_login_forgot_password:
                break;
        }
    }


    private void login() {
        if (!NetworkUtil.isNetworkConnected(this)) {
            showTipDialog(getString(R.string.http_network_failed));
            return;
        }
        showBigLoadingProgress(null);
        String account = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();
        Login login = new Login(account, password);
        RequestManager.getInstance().login(login, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                dismissLoadingDialog();
                if (HttpCode.isSuccess(statusCode) && baseObtainBean != null) {
                    LoginObtain loginObtain = (LoginObtain) baseObtainBean;
                    String accessToken = loginObtain.getAccessToken();
                    if (TextUtils.isEmpty(accessToken)) {
                        loginFail(HttpCode.CODE_ERROR);
                    } else {
                        if (loginObtain.getResMap() != null) {
                            loginSuccess(loginObtain);
                        } else {
                            loginFail(HttpCode.CODE_ERROR);
                        }
                    }
                } else {
                    loginFail(statusCode);
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
                dismissLoadingDialog();
                loginFail(statusCode);
            }
        });
    }

    private void loginFail(int statusCode) {
        if (statusCode == 2018) {
            etLoginPassword.setAutoDelFlag(true);
        }
        String msg = HttpCode.getInstance(LoginActivity.this).getCodeString(statusCode);
        showTipDialog(msg);
    }

    private void loginSuccess(LoginObtain loginObtain) {
        String account = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();
        NetConfig.setAccessToken(loginObtain.getAccessToken());
        UserInfoBean loginUser = loginObtain.getResMap().getUserInfo();
        AccountConfig.setUserInfoBean(loginUser);
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        if (userBindDevice != null) {
            BluetoothConfig.setDefaultMac(GlobalApp.getAppContext(), userBindDevice.getDeviceAddress());
        }
        BaseLocalConfig.getInstance().saveBoolean(APPConstant.QUERY_SETTING, true);
        BaseLocalConfig.getInstance().saveString(APPConstant.QUERY_USER, etLoginEmail.getText().toString().trim());
        BaseLocalConfig.getInstance().saveLong(APPConstant.QUERY_TIME, System.currentTimeMillis());
        BaseLocalConfig.getInstance().saveLong(APPConstant.QUERY_TIME_BASE, System.currentTimeMillis());
        if (loginUser != null && !TextUtils.isEmpty(loginUser.getUserName())) {
            String localAccount = AccountConfig.getUserLoginName();
            if (!account.equals(localAccount)) {
                clearUserData();//不同用户，已经填写个人信息
            }
            AccountConfig.setUserLoginName(account);
            AccountConfig.setUserLoginPassword(password);
            startActivity(MainActivity.class);
            finish();
        } else {
            AccountConfig.setUserLoginName(account);
            AccountConfig.setUserLoginPassword(password);
            clearUserData();//新用户，尚未填写个人信息
            Bundle bundle = new Bundle();
            bundle.putInt("userInfoId", loginUser.getUserInfoId());
            startActivity(RegisterUserInfoActivity.class, bundle);
            finish();
        }
    }

    private void clearUserData() {
        AppConfig.setLocalUnit(UnitHelper.UNIT_METRIC);//使用公制
        GoalConfig.setLocalUserGoal(null);
        GlobalVarManager.getInstance().onDestory();
        DataSupport.deleteAll(RemindDataModel.class);
        AccountConfig.setFriendsAccountInfo(null);
    }

    private boolean checkPostData() {
        String account = etLoginEmail.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            showTipDialog(getString(R.string.account_null));
            return false;
        }
        if (account.indexOf("@") != -1) {
            if (account.length() > 30) {
                showTipDialog(getString(R.string.email_length_over));
                return false;
            }
            if (!CommonUtils.emailFormat(account)) {
                showTipDialog(getString(R.string.not_valid_email));
                return false;
            }
        } else {
            if (!checkPhone(etLoginEmail)) {
                return false;
            }
        }
        if (!checkPassword(etLoginPassword)) {
            etLoginPassword.setAutoDelFlag(true);
            return false;
        }
        return true;
    }

    private boolean checkPhone(EditText editText) {
        if (!CommonUtils.getPhonePattern().matcher(editText.getText().toString()).matches()) {
            showTipDialog(getString(R.string.not_valid_phone));
            return false;
        }
        return true;
    }

    private boolean checkPassword(EditText editText) {
        String text = editText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            showTipDialog(getString(R.string.password_null));
            return false;
        }
        if (text.length() < 6 || text.length() > 16) {
            showTipDialog(getString(R.string.password_length_over));
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent(this, WelcomeYQActivity.class);
        startActivity(mIntent);
        finish();
    }
}

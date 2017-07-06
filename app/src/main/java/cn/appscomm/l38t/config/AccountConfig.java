package cn.appscomm.l38t.config;

import android.text.TextUtils;

import cn.appscomm.netlib.bean.account.UserInfoBean;
import cn.appscomm.netlib.bean.leaderboard.FriendsAccount;
import cn.appscomm.netlib.config.BaseLocalConfig;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/2 11:54
 */
public class AccountConfig extends BaseLocalConfig {


    private static final String user_info_bean = "user_info_bean";
    private static final String user_login_name = "user_login_name";
    private static final String user_login_password = "user_login_password";
    private static final String user_id = "user_id";
    private static final String friends_account_info_bean = "friends_account_info_bean";


    public static FriendsAccount getFriendsAccountInfo() {
        String result = getInstance().getString(friends_account_info_bean, "");
        FriendsAccount account = null;
        if (!TextUtils.isEmpty(result)) {
            account = getInstance().getGson().fromJson(result, FriendsAccount.class);
        }
        return account;
    }

    public static void setFriendsAccountInfo(FriendsAccount friendsAccount) {
        if (friendsAccount == null) {
            getInstance().saveString(friends_account_info_bean, "");
            return;
        }
        String result = getInstance().getGson().toJson(friendsAccount);
        if (TextUtils.isEmpty(result)) {
            getInstance().saveString(friends_account_info_bean, "");
        } else {
            getInstance().saveString(friends_account_info_bean, result);
        }
    }


    public static UserInfoBean getUserInfoBean() {
        String result = getInstance().getString(user_info_bean, "");
        UserInfoBean userInfoBean = null;
        if (!TextUtils.isEmpty(result)) {
            userInfoBean = getInstance().getGson().fromJson(result, UserInfoBean.class);
        }
        return userInfoBean;
    }

    public static void setUserInfoBean(UserInfoBean userInfoBean) {
        if (userInfoBean == null) {
            getInstance().saveString(user_info_bean, "");
            return;
        }
        String result = getInstance().getGson().toJson(userInfoBean);
        if (TextUtils.isEmpty(result)) {
            getInstance().saveString(user_info_bean, "");
        } else {
            getInstance().saveString(user_info_bean, result);
        }
    }

    public static String getUserLoginName() {
        return getInstance().getString(user_login_name, "");
    }

    public static void setUserLoginName(String loginName) {
        getInstance().saveString(user_login_name, loginName);
    }

    public static String getUserLoginPassword() {
        return getInstance().getString(user_login_password, "");
    }

    public static void setUserLoginPassword(String password) {
        getInstance().saveString(user_login_password, password);
    }

    public static int getUserId() {
        UserInfoBean userInfo = getUserInfoBean();
        if (userInfo != null) {
            return userInfo.getRefUserId();
        }
        return -1;
    }

    public static int getUserInfoId() {
        UserInfoBean userInfo = getUserInfoBean();
        if (userInfo != null) {
            return userInfo.getUserInfoId();
        }
        return -1;
    }

    public static int getAccountDDID() {
        FriendsAccount account = getFriendsAccountInfo();
        if (account != null) {
            return account.getDdId();
        }
        return -1;
    }

    public static String getAccountId() {
        FriendsAccount account = getFriendsAccountInfo();
        if (account != null) {
            return account.getAccountId();
        }
        return "";
    }

    public static String getUserIconUrl() {
        UserInfoBean userInfo = getUserInfoBean();
        String iconUrl = "";
        if (userInfo != null) {
            iconUrl = userInfo.getIconUrl();
        }
        FriendsAccount account = getFriendsAccountInfo();
        if (account != null && TextUtils.isEmpty(iconUrl)) {
            iconUrl = account.getIconUrl();
        }
        return iconUrl;
    }


    public static void updateIconUrl(String iconUrl) {
        UserInfoBean userInfo = getUserInfoBean();
        if (userInfo != null) {
            userInfo.setIconUrl(iconUrl);
            setUserInfoBean(userInfo);
        }
        FriendsAccount account = getFriendsAccountInfo();
        if (account != null) {
            account.setIconUrl(iconUrl);
            setFriendsAccountInfo(account);
        }
    }
}

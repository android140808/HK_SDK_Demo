package cn.appscomm.netlib.bean.account;

/**
 * Created by Administrator on 2016/8/31.
 */
public class UserInfoBean extends AccountInfo{
    public static final int SEX_MALE = 0;
    public static final int SEX_FEMALE = 1;

    private int refUserId;
    private String iconUrl;

    public int getRefUserId() {
        return refUserId;
    }

    public void setRefUserId(int refUserId) {
        this.refUserId = refUserId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}

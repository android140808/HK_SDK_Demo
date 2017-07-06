package cn.appscomm.netlib.bean.friends;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/6 11:30
 */
public class PendingFriendAccount {
    private int memberId;
    private int ddId;
    private String userName;
    private String iconUrl;

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getDdId() {
        return ddId;
    }

    public void setDdId(int ddId) {
        this.ddId = ddId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}

package cn.appscomm.netlib.bean.account;

import android.text.TextUtils;
import android.text.format.DateUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.util.DateUtil;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/2 16:36
 */
public class AccountInfo extends BasePostBean {
    public static final String DIRTHDAY_FORMATER = "yyyy-MM-dd";
    /**
     * seq : 11111111
     * versionNo : 2.0
     * clientType : iphone
     * extendParams : {}
     * userInfoId : 9
     * userName : imti
     * nickname : qq
     * sex : 1
     * birthday : 1991-09-25
     * height : 150.0
     * heightUnit : 1
     * weight : 50.6
     * weightUnit : 1
     * country : china
     * province : guangdong
     * city : guangzhou
     * area : huangpu
     * isManage : 0
     */
    private int userInfoId;
    private String userName;
    private String nickname;
    private int sex;
    private String birthday;
    private float height;
    private int heightUnit;
    private float weight;
    private int weightUnit;
    private String country;
    private String province;
    private String city;
    private String area;
    private int isManage;


    public int getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(int userInfoId) {
        this.userInfoId = userInfoId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(int heightUnit) {
        this.heightUnit = heightUnit;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(int weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getIsManage() {
        return isManage;
    }

    public void setIsManage(int isManage) {
        this.isManage = isManage;
    }

    public float getBMI() {
        float h = height / 100.0f;
        float result = 0;
        if (h * h > 0) {
            result = weight / (h * h);
        }
        BigDecimal bd = new BigDecimal(result);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public int getAge() {
        if (!TextUtils.isEmpty(birthday)) {
            Date date = DateUtil.strToDate(birthday, DIRTHDAY_FORMATER);
            return getDateYearsDis(date);
        }
        return 0;
    }

    private int getDateYearsDis(Date birthday) {
        if (birthday == null) {
            return 0;
        }
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(birthday);
        c2.setTime(new Date());
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        return year2 - year1;
    }
}

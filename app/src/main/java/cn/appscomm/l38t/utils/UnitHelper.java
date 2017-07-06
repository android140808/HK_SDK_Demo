package cn.appscomm.l38t.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.widget.TextView;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.app.GlobalApp;

/**
 * Created by Administrator on 2016/8/17.
 */
public class UnitHelper {
    public static final int UNIT_US = 0;//英制
    public static final int UNIT_METRIC = 1;//公制

    public static final double UNIT_EN_S_NUM = 1.609344;//1英里=1.609344千米
    public static final double UNIT_S_EN_NUM = 0.6214;//1千米=0.6214英里
    public static final double UNIT_S_EN_WIGHT = 2.2065;//1KG=2.2065英镑
    public static final double UNIT_EN_S_WIGHT = 0.4536;//1英磅=0.4536KG
    public static final double UNIT_EN_S_HEIGHT = 2.54;//1英寸=2.54cm
    public static final double UNIT_S_EN_HEIGHT = 0.3937008;//1cm=0.3937英寸
    public static final double UNIT_IN_TO_FT_HEIGHT = 0.0833;//1英寸=0.0833英尺
    public static final int UNIT_FT_TO_IN_HEIGHT = 12;//1英尺=12英寸
    public static final String UNIT_FT_SPLIT = "'";//英尺分隔符
    public static final String UNIT_IN_SPLIT = "\"";//英寸分隔符
    public static final String UNIT_De_SPLIT = "\\.";//小数点分隔符


    public final static int YEAR_START = 1900; // 出生年的起点是
    public final static int HEIGHT_INT_START = 90; // 人身高的起点是90cm
    public final static int HEIGHT_INT_MAX = 300; //
    public final static int HEIGHT_FT_INT_START = 3; //人身高的起点是3英尺
    public final static int HEIGHT_FT_INT_MAX = 7; //
    public final static int WEIGHT_START = 30; // 人体重的起点是30 kg
    public final static int WEIGHT_MAX = 400; //
    public final static int WEIGHT_lb_START = 50; // 人体重的起点是50磅
    public final static int WEIGHT_lb_MAX = 999; //

    private String[] arrHeight_Int;   //高度整数值 cm 下拉列表
    private String[] arrHeightFt_Int;  //高度整数值  ft 下拉列表
    private String[] arrWeight_Int;   //体重整数值 cm  下拉列表
    private String[] arrWeightlbs_Int;  //体重整数值  lbs 下拉列表
    private String[] arrHeight_Dec = new String[]{".0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9"};  //10进制的小数
    private String[] arrHeightFt_Dec = new String[]{"0\"", "1\"", "2\"", "3\"", "4\"", "5\"", "6\"", "7\"", "8\"", "9\"", "10\"", "11\""}; //英寸单位 12进制
    private String[] arrWeight_Dec = new String[]{".0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9"};  //10进制的小数
    private String[] arrgender = new String[]{GlobalApp.getAppContext().getString(R.string.male), GlobalApp.getAppContext().getString(R.string.female)};

    private static UnitHelper unitHelper;

    private UnitHelper() {
        init();
    }

    public static UnitHelper getInstance() {
        if (null == unitHelper) {
            synchronized (UnitHelper.class) {
                if (null == unitHelper) {
                    unitHelper = new UnitHelper();
                }
            }
        }
        return unitHelper;
    }

    public void init() {
        arrgender = new String[]{GlobalApp.getAppContext().getString(R.string.male), GlobalApp.getAppContext().getString(R.string.female)};
        arrHeight_Int = new String[HEIGHT_INT_MAX - HEIGHT_INT_START + 1];
        for (int i = 0; i < arrHeight_Int.length; i++) {
            arrHeight_Int[i] = HEIGHT_INT_START + i + "";// + " cm";
        }

        arrHeightFt_Int = new String[HEIGHT_FT_INT_MAX - HEIGHT_FT_INT_START + 1];
        for (int i = 0; i < arrHeightFt_Int.length; i++) {
            arrHeightFt_Int[i] = HEIGHT_FT_INT_START + i + UNIT_FT_SPLIT;// + " ft";
        }


        arrWeightlbs_Int = new String[WEIGHT_lb_MAX - WEIGHT_lb_START + 1];// 14--313
        for (int i = 0; i < arrWeightlbs_Int.length; i++) {
            arrWeightlbs_Int[i] = WEIGHT_lb_START + i + "";// + " lbs";
        }

        arrWeight_Int = new String[WEIGHT_MAX - WEIGHT_START + 1];// 14--313
        for (int i = 0; i < arrWeight_Int.length; i++) {
            arrWeight_Int[i] = WEIGHT_START + i + "";// + " kg";
        }
    }

    public static String[] getArrHeightUnit() {
        return new String[]{GlobalApp.getAppContext().getString(R.string.unit_ft_in), GlobalApp.getAppContext().getString(R.string.unit_cm)};
    }

    public static String[] getArrDistanceUnit() {
        return new String[]{GlobalApp.getAppContext().getString(R.string.unit_distance_miles), GlobalApp.getAppContext().getString(R.string.unit_distance)};
    }

    public static String[] getArrWeightUnit() {
        return new String[]{GlobalApp.getAppContext().getString(R.string.unit_lbs), GlobalApp.getAppContext().getString(R.string.unit_kg)};
    }


    public static String getHeightString(String text) {
        String heightString = text.toString().trim();
        if (heightString != null) {
            heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_ft_in), "");
            heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_cm), "");
        }
        return heightString;
    }

    public static String getWeightString(String text) {
        String heightString = text.toString().trim();
        if (heightString != null) {
            heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_ft_in), "");
            heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_cm), "");
        }
        return heightString;
    }

    public static float getSwitchHeightFtInToCm(TextView textView) {
        try {
            String heightString = textView.getText().toString().trim();
            if (heightString != null) {
                heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_ft_in), "");
                heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_cm), "");
            }
            if (heightString == null || "".equals(heightString)) {
                return 0;
            }
            heightString = heightString.trim();
            int inTotal = 0;
            String[] fts = heightString.split(UNIT_FT_SPLIT);
            if (fts != null && fts.length == 2) {
                inTotal = Integer.parseInt(fts[0]) * UNIT_FT_TO_IN_HEIGHT;
                fts[1] = fts[1].replace(UNIT_IN_SPLIT, "");
                inTotal = inTotal + Integer.parseInt(fts[1]);
                return UnitTool.getHalfUpValue(inTotal * UNIT_EN_S_HEIGHT, 0).floatValue();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int[] getSwitchCmToFtIn(TextView textView) {
        return switchCmToFtIn(textView.getText().toString());
    }

    public static int[] switchCmToFtIn(String text) {
        try {
            if (text == null || "".equals(text)) {
                return new int[]{0, 0};
            }
            String heightString = text.toString().trim();
            if (heightString != null) {
                heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_ft_in), "");
                heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_cm), "");
            }
            if (heightString == null || "".equals(heightString)) {
                return new int[]{0, 0};
            }
            heightString = heightString.trim();
            int nowHeight = Integer.parseInt(UnitTool.getHalfUpValue(Float.valueOf(heightString) * UNIT_S_EN_HEIGHT, 0) + "");
            int ft = nowHeight / UnitHelper.UNIT_FT_TO_IN_HEIGHT;
            int in = nowHeight % UnitHelper.UNIT_FT_TO_IN_HEIGHT;
            return new int[]{ft, in};
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return new int[]{0, 0};
    }


    public static float getNowCmHeightValue(TextView textView) {
        String heightString = textView.getText().toString().trim();
        if (heightString != null) {
            heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_ft_in), "");
            heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_cm), "");
        }
        if ("".equals(heightString)) {
            heightString = "0";
        }
        heightString = heightString.trim();
        float heightUP = UnitTool.getHalfUpValue(heightString, 0).floatValue(); // mHeight;
        return heightUP;
    }

    public static float getNowWeightValue(String text) {
        if (text == null || "".equals(text)) {
            text = "0";
        }
        String weightString = text.toString().trim();
        if (weightString != null) {
            weightString = weightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_lbs), "");
            weightString = weightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_kg), "");
        }
        if ("".equals(weightString)) {
            weightString = "0";
        }
        weightString = weightString.trim();
        final float weightUP = Float.valueOf(weightString); // mHeight;
        return weightUP;
    }

    public static float getNowKgWeightValue(TextView textView) {
        return getNowWeightValue(textView.getText().toString());
    }

    public static float switchWeightLbsToKg(String text) {
        try {
            float weight = getNowWeightValue(text);
            double nowWeight = weight * UnitHelper.UNIT_EN_S_WIGHT;
            return UnitTool.getHalfUpValue(nowWeight, 1).floatValue();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static float switchWeightKgToLbs(String text) {
        try {
            float weight = getNowWeightValue(text);
            double nowWeight = weight * UnitHelper.UNIT_S_EN_WIGHT;
            return UnitTool.getHalfUpValue(nowWeight, 1).floatValue();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static float getSwitchWeightLbsToKg(TextView textView) {
        return switchWeightLbsToKg(textView.getText().toString());
    }

    public static float getSwitchWeightKgToLbs(TextView textView) {
        return switchWeightKgToLbs(textView.getText().toString());
    }

    public static int[] getCmHeightIndexs(TextView textView) {
        try {
            String heightString = textView.getText().toString().trim();
            if (heightString != null) {
                heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_ft_in), "");
                heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_cm), "");
            }
            if (heightString == null || "".equals(heightString)) {
                return new int[]{-1, -1};
            }
            heightString = heightString.trim();
            String[] values = heightString.split(UNIT_De_SPLIT);
            if (values != null && values.length >= 1) {
                return new int[]{Integer.parseInt(values[0]) - HEIGHT_INT_START, 0};
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return new int[]{-1, -1};
    }


    public static int[] getFtInHeightIndexs(TextView textView) {
        try {
            String heightString = textView.getText().toString().trim();
            if (heightString != null) {
                heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_ft_in), "");
                heightString = heightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_cm), "");
            }
            if (heightString == null || "".equals(heightString)) {
                return new int[]{-1, -1};
            }
            heightString = heightString.trim();
            int inTotal = 0;
            String[] fts = heightString.split(UNIT_FT_SPLIT);
            if (fts != null && fts.length == 2) {
                fts[1] = fts[1].replace(UNIT_IN_SPLIT, "");
                return new int[]{Integer.parseInt(fts[0]) - HEIGHT_FT_INT_START, Integer.parseInt(fts[1])};
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return new int[]{-1, -1};
    }

    public static int[] getKgWeightIndexs(TextView textView) {
        try {
            String weightString = textView.getText().toString().trim();
            if (weightString != null) {
                weightString = weightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_lbs), "");
                weightString = weightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_kg), "");
            }
            if (weightString == null || "".equals(weightString)) {
                return new int[]{-1, -1};
            }
            weightString = weightString.trim();
            String[] values = weightString.split(UNIT_De_SPLIT);
            if (values != null && values.length == 2) {
                return new int[]{Integer.parseInt(values[0]) - WEIGHT_START, Integer.parseInt(values[1])};
            } else if (values != null && values.length == 1) {
                return new int[]{Integer.parseInt(values[0]) - WEIGHT_START, 0};
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return new int[]{-1, -1};
    }

    public static int[] getLbsWeightIndexs(TextView textView) {
        try {
            String weightString = textView.getText().toString().trim();
            if (weightString != null) {
                weightString = weightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_lbs), "");
                weightString = weightString.replaceAll(GlobalApp.getAppContext().getString(R.string.unit_kg), "");
            }
            if (weightString == null || "".equals(weightString)) {
                return new int[]{-1, -1};
            }
            weightString = weightString.trim();
            String[] values = weightString.split(UNIT_De_SPLIT);
            if (values != null && values.length == 2) {
                return new int[]{Integer.parseInt(values[0]) - WEIGHT_lb_START, Integer.parseInt(values[1])};
            } else if (values != null && values.length == 1) {
                return new int[]{Integer.parseInt(values[0]) - WEIGHT_lb_START, 0};
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return new int[]{-1, -1};
    }

    public String[] getArrHeight_Int() {
        return arrHeight_Int;
    }

    public String[] getArrHeightFt_Int() {
        return arrHeightFt_Int;
    }

    public String[] getArrWeight_Int() {
        return arrWeight_Int;
    }

    public String[] getArrWeightlbs_Int() {
        return arrWeightlbs_Int;
    }

    public String[] getArrHeight_Dec() {
        return arrHeight_Dec;
    }

    public String[] getArrHeightFt_Dec() {
        return arrHeightFt_Dec;
    }


    public String[] getArrWeight_Dec() {
        return arrWeight_Dec;
    }

    public int getGenderIndex(TextView tvGender) {
        String gender = tvGender.getText().toString().trim();
        int index = 0;
        if (!TextUtils.isEmpty(gender)) {
            for (int i = 0; i < arrgender.length; i++) {
                if (arrgender[i].equals(gender)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public String[] getArrgender() {
        arrgender = new String[]{GlobalApp.getAppContext().getString(R.string.male), GlobalApp.getAppContext().getString(R.string.female)};
        return arrgender;
    }

    public void onDestory() {
        this.arrgender = null;
        this.arrHeight_Dec = null;
        this.arrHeight_Int = null;
        this.arrHeightFt_Int = null;
        this.arrHeightFt_Dec = null;
        this.arrWeight_Dec = null;
        this.arrWeight_Int = null;
        this.arrWeightlbs_Int = null;
        unitHelper = null;
    }

}

package cn.appscomm.l38t.utils;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/8/17.
 */
public class UnitTool {

    public static BigDecimal getHalfUpValue(float value, int num) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(num, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public static BigDecimal getHalfUpValue(double value, int num) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(num, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public static BigDecimal getHalfUpValue(String value, int num) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(num, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public static String getMinToHourShowString(int min) {
        return "" + getMinToHour(min);
    }

    public static String getMiToKmShowString(int mi) {
        return "" + getMiToKm(mi);
    }

    public static String getMiToKmShowString(double mi) {
        return "" + getMiToKm(mi);
    }

    public static String getKaToKKaShowString(int ka) {
        return "" + getKaToKKa(ka);
    }

    public static BigDecimal getMinToHour(int min) {
        BigDecimal bd = null;
        double value = min / 60.0;
        bd = new BigDecimal(value);
        if (min % 60 == 0) {
            bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        } else {
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return bd;
    }

    public static int[] getMinToHourMin(int min) {
        int hours = min / 60;
        int mins= min % 60;
        return new int[]{hours,mins};
    }

    public static BigDecimal getMiToKm(int mi) {
        BigDecimal bd = null;
        double value = mi / 1000.0;
        bd = new BigDecimal(value);
        if (mi % 1000 == 0) {
            bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        } else {
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return bd;
    }

    public static BigDecimal getMiToKm(double mi) {
        BigDecimal bd = null;
        double value = mi / 1000.0;
        bd = new BigDecimal(value);
        if (mi % 1000 == 0) {
            bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        } else {
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return bd;
    }

    public static BigDecimal getKaToKKa(int ka) {
        BigDecimal bd = null;
        double value = ka / 1000.0;
        bd = new BigDecimal(value);
        if (ka % 1000 == 0) {
            bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        } else {
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return bd;
    }

    public static BigDecimal getKaToKKa(double ka) {
        BigDecimal bd = null;
        double value = ka / 1000.0;
        bd = new BigDecimal(value);
        if (ka % 1000 == 0) {
            bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        } else {
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return bd;
    }

}

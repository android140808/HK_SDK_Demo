package cn.appscomm.l38t.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.popwin.NewHeightPop;
import cn.appscomm.l38t.UI.popwin.NewWeightPop;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/2 16:43
 */
public class HeightWeightDialogHelper {

    private static HeightWeightDialogHelper helper;

    private NewHeightPop wheelWindowHeight;
    private NewWeightPop wheelWindowWeight;

    public static HeightWeightDialogHelper getInstance() {
        if (null == helper) {
            synchronized (HeightWeightDialogHelper.class) {
                if (null == helper) {
                    helper = new HeightWeightDialogHelper();
                }
            }
        }
        return helper;
    }

    public void showHeightChooseDialog(Context context, final int unit, final TextView tvHeight, final View viewParent) {
        if (wheelWindowHeight != null) {
            wheelWindowHeight.dismiss();
            wheelWindowHeight = null;
        }
        if (wheelWindowHeight == null) {
            int[] indexs = new int[]{-1, -1};
            if (unit == UnitHelper.UNIT_US) {
                indexs = UnitHelper.getFtInHeightIndexs(tvHeight);
            } else {
                indexs = UnitHelper.getCmHeightIndexs(tvHeight);
            }
            wheelWindowHeight = new NewHeightPop(context, indexs[0], indexs[1], unit, 8,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            switch (arg0.getId()) {
                                case R.id.HeightWheelSave:
                                    if (unit == UnitHelper.UNIT_US) {
                                        tvHeight.setText(UnitHelper.getInstance().getArrHeightFt_Int()[wheelWindowHeight.getCurrentItemInt()] + "" + UnitHelper.getInstance().getArrHeightFt_Dec()[wheelWindowHeight.getCurrentItemDes()] + " " + UnitHelper.getArrHeightUnit()[unit]);
                                    } else {
                                        //tvHeight.setText(UnitHelper.getInstance().getArrHeight_Int()[wheelWindowHeight.getCurrentItemInt()] + "" + UnitHelper.getInstance().getArrHeight_Dec()[wheelWindowHeight.getCurrentItemDes()] + " " + UnitHelper.getArrHeightUnit()[unit]);
                                        tvHeight.setText(UnitHelper.getInstance().getArrHeight_Int()[wheelWindowHeight.getCurrentItemInt()] + " " + UnitHelper.getArrHeightUnit()[unit]);
                                    }
                                    wheelWindowHeight.dismiss();
                                    break;
                                case R.id.HeightWheelCancel:
                                default:
                                    wheelWindowHeight.dismiss();
                                    break;
                            }
                        }
                    });
        }
        // 显示窗口 //设置layout在PopupWindow中显示的位置
        wheelWindowHeight.showAtLocation(viewParent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    public void showWeightChooseDialog(Context context, final int unit, final TextView tvWeight, final View viewParent) {
        if (wheelWindowWeight != null) {
            wheelWindowWeight.dismiss();
            wheelWindowWeight = null;
        }
        if (wheelWindowWeight == null) {
            int[] indexs = new int[]{-1, -1};
            if (unit == UnitHelper.UNIT_US) {
                indexs = UnitHelper.getLbsWeightIndexs(tvWeight);
            } else {
                indexs = UnitHelper.getKgWeightIndexs(tvWeight);
            }
            wheelWindowWeight = new NewWeightPop(context, indexs[0], indexs[1], unit, 8,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            switch (arg0.getId()) {
                                case R.id.WeightWheelSave:
                                    if (unit == UnitHelper.UNIT_US) {
                                        tvWeight.setText(UnitHelper.getInstance().getArrWeightlbs_Int()[wheelWindowWeight.getCurrentItemInt()] + "" + UnitHelper.getInstance().getArrWeight_Dec()[wheelWindowWeight.getCurrentItemDes()] + " " + UnitHelper.getArrWeightUnit()[unit]);
                                    } else {
                                        tvWeight.setText(UnitHelper.getInstance().getArrWeight_Int()[wheelWindowWeight.getCurrentItemInt()] + "" + UnitHelper.getInstance().getArrWeight_Dec()[wheelWindowWeight.getCurrentItemDes()] + " " + UnitHelper.getArrWeightUnit()[unit]);
                                    }
                                    wheelWindowWeight.dismiss();
                                    break;
                                case R.id.WeightWheelCancel:
                                default:
                                    wheelWindowWeight.dismiss();
                                    break;

                            }
                            wheelWindowWeight.dismiss();
                        }
                    });
        }
        // 显示窗口 //设置layout在PopupWindow中显示的位置
        wheelWindowWeight.showAtLocation(viewParent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
}

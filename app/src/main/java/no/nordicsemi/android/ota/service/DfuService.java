package no.nordicsemi.android.ota.service;

import android.app.Activity;

import no.nordicsemi.android.dfu.DfuBaseService;
import no.nordicsemi.android.ota.activity.DfuNotificationActivity;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/8 17:56
 */
public class DfuService extends DfuBaseService {
    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return DfuNotificationActivity.class;
    }
}

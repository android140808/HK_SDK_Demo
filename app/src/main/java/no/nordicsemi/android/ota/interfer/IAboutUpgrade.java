package no.nordicsemi.android.ota.interfer;

/**
 * Created by Administrator on 2016/7/11.
 */
public interface IAboutUpgrade {
    /**
     * 升级进度
     *
     * @param curPackage 目前已经发送包数
     */
    void curUpgradeProgress(int curPackage);

    /**
     * 升级总包数
     *
     * @param totalPackage 总包数
     */
    void curUpgradeMax(int totalPackage);

    /**
     * 升级结果
     *
     * @param result true:成功 false:失败
     */
    void upgradeResult(boolean result);
}

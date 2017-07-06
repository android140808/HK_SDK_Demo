package no.nordicsemi.android.ota.model;

/**
 * Created by Administrator on 2016/10/10.
 */
public class FirmwareServerInfo {

    /** 固件名称 */
    public String filename;
    /** 版本 */
    public String version;
    /** 是否强制升级 */
    public boolean isForceUpdate;
    /** 升级命令码 */
    public byte orderCode;

    public FirmwareServerInfo(String filename, String version, boolean isForceUpdate, byte orderCode) {
        this.filename = filename;
        this.version = version;
        this.isForceUpdate = isForceUpdate;
        this.orderCode = orderCode;
    }
}

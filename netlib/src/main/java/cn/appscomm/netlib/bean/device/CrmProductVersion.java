package cn.appscomm.netlib.bean.device;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 14:44
 */
public class CrmProductVersion {
    int versionId;
    int productId;
    String deviceVersion;
    String updateUrl;
    String updateMessage;
    int fileCrcSize;
    int invalidStatus;
    String insertTime;

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public int getFileCrcSize() {
        return fileCrcSize;
    }

    public void setFileCrcSize(int fileCrcSize) {
        this.fileCrcSize = fileCrcSize;
    }

    public int getInvalidStatus() {
        return invalidStatus;
    }

    public void setInvalidStatus(int invalidStatus) {
        this.invalidStatus = invalidStatus;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }
}

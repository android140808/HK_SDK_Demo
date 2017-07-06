package no.nordicsemi.android.ota.model;

public class UpgradeInfo {
	/** Nordic Bin文件总长度 */
	public byte[] binTotalLength;
	/** Nordic Bin文件长度 */
	public byte[] binLength;
	/** nordic CRC校验 */
	public byte[] crcCheck;
	/** nordic Bin内容 */
	public byte[] binContents;
	/** 命令码 */
	public byte orderCode;

	public UpgradeInfo(byte[] binLength, byte[] crcCheck, byte[] binContents) {
		this.binLength = binLength;
		this.crcCheck = crcCheck;
		this.binContents = binContents;
	}

	public UpgradeInfo(byte[] binTotalLength, byte[] binLength, byte[] crcCheck, byte[] binContents, byte orderCode) {
		this.binTotalLength = binTotalLength;
		this.binLength = binLength;
		this.crcCheck = crcCheck;
		this.binContents = binContents;
		this.orderCode = orderCode;
	}
}

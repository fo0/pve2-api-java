package net.elbandi.pve2api.data;

import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 8/19/13
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BlockDevice {
	public String storage;
	/*  e.g 111/vm-111-disk-2.qcow2 */
	public  String url;
	/* size, in bytes */
	public long size;

	/* system interface. virtio, unused, scsi, sata or ide */
	public String bus;

	public int getDevice() {
		return device;
	}

	public void setDevice(int device) {
		this.device = device;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getBus() {
		return bus;
	}

	public void setBus(String bus) {
		this.bus = bus;
	}

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public int device;

	public static String readableFileSize(long size) {
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "K", "M", "G", "T" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + units[digitGroups];
	}
}

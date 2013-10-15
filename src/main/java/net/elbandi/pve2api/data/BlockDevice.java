package net.elbandi.pve2api.data;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 8/19/13
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BlockDevice {
	public String storage;
	/* media type, disk or cdrom */
	public String media;
	/* system interface. virtio, unused, scsi, sata or ide */
	public String bus;
	public Volume volume;
	public int getDevice() {
		return device;
	}

	public void setDevice(int device) {
		this.device = device;
	}

	public String getBus() {
		return bus;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public void setBus(String bus) {
		this.bus = bus;
	}

	public String getStorage() {
		return storage;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
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
	public String getCreateString() throws VmQemu.MissingFieldException{
		StringBuilder stringBuilder = new StringBuilder();
		if(volume == null) throw new VmQemu.MissingFieldException("Field volume is not set");
		stringBuilder.append("volume=" + volume.getVolid());
		stringBuilder.append(",media=" + media);
		stringBuilder.append(",size=" + volume.getSize());
		return stringBuilder.toString();

	}
	public static String parseMedia(String blockDeviceData){
		String mediaPattern = "media=(cdrom|disk)";
		Pattern r = Pattern.compile(mediaPattern);
		Matcher m = r.matcher(blockDeviceData);
		if(m.find()){
			return m.group(1);
		} else {
			return null;
		}
	}
	public static long parseSize(String blockDeviceData){
		String sizePattern = "size=([0-9.KMGT]+)";
		Pattern r = Pattern.compile(sizePattern);
		Matcher m = r.matcher(blockDeviceData);
		long bytes = 0;
		if(m.find()){
			if(m.group(1).endsWith("G")){
				bytes = Long.parseLong(m.group(1).replace("G", "")) * 1024l * 1024l * 1024l;
			} else if(m.group(1).endsWith("T")){
				bytes = Long.parseLong(m.group(1).replace("T", "")) * 1024l * 1024l * 1024l * 1024l;
			} else if(m.group(1).endsWith("M")){
				bytes = Long.parseLong(m.group(1).replace("M", "")) * 1024l * 1024l;
			} else if(m.group(1).endsWith("K")){
				bytes = Long.parseLong(m.group(1).replace("M", "")) * 1024l;
			} else {
				bytes = Long.parseLong(m.group(1));
			}

		}
		return bytes;
	}
	public static String parseStorage(String blockDeviceData){
		String storagePattern = "^[a-z0-9_\\-.]+";
		Pattern r = Pattern.compile(storagePattern);
		Matcher m = r.matcher(blockDeviceData);
		if(m.find()){
			return m.group(0);
		} else {
			return null;
		}
	}
	public static String parseUrl(String blockDeviceData){
		String urlPattern = ":([a-z0-9_\\-/.]+),";
		Pattern r = Pattern.compile(urlPattern);
		Matcher m = r.matcher(blockDeviceData);
		if(m.find()){
			return m.group(1);
		} else {
			return null;
		}

	}
}

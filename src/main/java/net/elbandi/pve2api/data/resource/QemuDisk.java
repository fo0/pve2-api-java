package net.elbandi.pve2api.data.resource;

import net.elbandi.pve2api.data.BlockDevice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 8/19/13
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class QemuDisk extends BlockDevice {
	/* throttle access speed */
	int mbps_rd;
	int mbps_wr;
	int iops_rd;
	int iops_wr;

	public QemuDisk(String bus, int device){
		this.bus = bus;
		this.device = device;
	}

	public int getMbps_rd() {
		return mbps_rd;
	}

	public void setMbps_rd(int mbps_rd) {
		this.mbps_rd = mbps_rd;
	}

	public int getMbps_wr() {
		return mbps_wr;
	}

	public void setMbps_wr(int mbps_wr) {
		this.mbps_wr = mbps_wr;
	}

	public int getIops_rd() {
		return iops_rd;
	}

	public void setIops_rd(int iops_rd) {
		this.iops_rd = iops_rd;
	}

	public int getIops_wr() {
		return iops_wr;
	}

	public void setIops_wr(int iops_wr) {
		this.iops_wr = iops_wr;
	}

	@Override
	public String toString(){
		String string = this.storage + ":" + this.url;
		if(mbps_rd > 0) string += ",mbps_rd=" + mbps_rd;
		if (mbps_wr > 0) string += ",mbps_wr=" + mbps_wr;
		if (iops_rd > 0) string += ",iops_rd=" + iops_rd;

		if (iops_wr > 0) string += ",iops_wr=" + iops_wr;
		string += readableFileSize(this.size);
		return string;
	}
	public String getCreateString(){
		String string = this.storage + ":" + size / (1024l * 1024l * 1024l);
		if(mbps_rd > 0) string += ",mbps_rd=" + mbps_rd;
		if (mbps_wr > 0) string += ",mbps_wr=" + mbps_wr;
		if (iops_rd > 0) string += ",iops_rd=" + iops_rd;
		if (iops_wr > 0) string += ",iops_wr=" + iops_wr;
		return string;

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
	public static String parseIops_rd(String blockDeviceData){
		String iopsPattern = "iops_rd=([0-9]+)";
		Pattern r = Pattern.compile(iopsPattern);
		Matcher m = r.matcher(blockDeviceData);
		if(m.find()){
			return m.group(1);
		} else {
			return null;
		}

	}
	public static String parseIops_wr(String blockDeviceData){
		String iopsPattern = "iops_wr=([0-9]+)";
		Pattern r = Pattern.compile(iopsPattern);
		Matcher m = r.matcher(blockDeviceData);
		if(m.find()){
			return m.group(1);
		} else {
			return null;
		}

	}
	public static String parseMbps_rd(String blockDeviceData){
		String mbpsPattern = "mbps_rd=([0-9.]+)";
		Pattern r = Pattern.compile(mbpsPattern);
		Matcher m = r.matcher(blockDeviceData);
		if(m.find()){
			return m.group(1);
		} else {
			return null;
		}

	}
	public static String parseMbps_wr(String blockDeviceData){
		String mbpsPattern = "mbps_wr=([0-9.]+)";
		Pattern r = Pattern.compile(mbpsPattern);
		Matcher m = r.matcher(blockDeviceData);
		if(m.find()){
			return m.group(1);
		} else {
			return null;
		}

	}
	public static String parseSize(String blockDeviceData){
		String sizePattern = "size=([0-9.BMGT]+)";
		Pattern r = Pattern.compile(sizePattern);
		Matcher m = r.matcher(blockDeviceData);
		if(m.find()){
			return m.group(1);
		} else {
			return null;
		}

	}

}

package net.elbandi.pve2api.data.resource;

import net.elbandi.pve2api.data.BlockDevice;
import net.elbandi.pve2api.data.VmQemu;
import net.elbandi.pve2api.data.Volume;

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
	double mbps_rd;
	double mbps_wr;
	int iops_rd;
	int iops_wr;
	public QemuDisk(String bus, int device){
		this.bus = bus;
		this.device = device;
	}

	public double getMbps_rd() {
		return mbps_rd;
	}

	public void setMbps_rd(double mbps_rd) {
		this.mbps_rd = mbps_rd;
	}

	public double getMbps_wr() {
		return mbps_wr;
	}

	public void setMbps_wr(double mbps_wr) {
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

	/*@Override
	public String toString(){
		String string = this.storage + ":" + this.url;
		if(mbps_rd > 0) string += ",mbps_rd=" + mbps_rd;
		if (mbps_wr > 0) string += ",mbps_wr=" + mbps_wr;
		if (iops_rd > 0) string += ",iops_rd=" + iops_rd;

		if (iops_wr > 0) string += ",iops_wr=" + iops_wr;
		string += readableFileSize(this.size);
		return string;
	}*/
	@Override
	public String getCreateString() throws VmQemu.MissingFieldException{
		StringBuilder stringBuilder = new StringBuilder();
		if(volume == null) throw new VmQemu.MissingFieldException("Field volume is not set");
		stringBuilder.append("volume=" + volume.getVolid());
		stringBuilder.append(",media=disk");
		if(mbps_rd > 0) stringBuilder.append(",mbps_rd=" + mbps_rd);
		if (mbps_wr > 0) stringBuilder.append(",mbps_wr=" + mbps_wr);
		if (iops_rd > 0) stringBuilder.append(",iops_rd=" + iops_rd);
		if (iops_wr > 0) stringBuilder.append(",iops_wr=" + iops_wr);
		stringBuilder.append(",size=" + volume.getSize());
		return stringBuilder.toString();

	}

	public static int parseIops_rd(String blockDeviceData){
		String iopsPattern = "iops_rd=([0-9]+)";
		Pattern r = Pattern.compile(iopsPattern);
		Matcher m = r.matcher(blockDeviceData);
		int result= 0;
		if(m.find()){
			result = Integer.parseInt(m.group(1));
		}
		return result;

	}
	public static int parseIops_wr(String blockDeviceData){
		String iopsPattern = "iops_wr=([0-9]+)";
		Pattern r = Pattern.compile(iopsPattern);
		Matcher m = r.matcher(blockDeviceData);
		int result = 0;
		if(m.find()){
			result = Integer.parseInt(m.group(1));
		}
		return result;
	}
	public static double parseMbps_rd(String blockDeviceData){
		String mbpsPattern = "mbps_rd=([0-9.]+)";
		Pattern r = Pattern.compile(mbpsPattern);
		Matcher m = r.matcher(blockDeviceData);
		double result = 0;
		if(m.find()){
			result =  Double.parseDouble(m.group(1));
		}
		return result;

	}
	public static double parseMbps_wr(String blockDeviceData){
		String mbpsPattern = "mbps_wr=([0-9.]+)";
		Pattern r = Pattern.compile(mbpsPattern);
		Matcher m = r.matcher(blockDeviceData);
		double result = 0;
		if(m.find()){
			result = Double.parseDouble(m.group(1));
		}
		return result;

	}


}

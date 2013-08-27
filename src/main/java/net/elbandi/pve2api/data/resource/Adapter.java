package net.elbandi.pve2api.data.resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 8/21/13
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Adapter {
	/* system interface. virtio, unused, scsi, sata or ide */
	public String bus;

	public int device;

	public String mac;

	public String bridge;

	//Device model, e.g e1000
	public String model;

	/* vlan tag */
	public int tag;

	/* NIC max. rate in Mbytes per second */
	public float rate;

	public String getCreateString(){
		StringBuffer buffer = new StringBuffer();
		if(model != null) buffer.append(model);
		if (mac != null) buffer.append("=" + mac);
		if (bridge != null) buffer.append(",bridge=" + bridge);
		if (rate > 0) buffer.append(",rate=" + rate);
		if (tag > 0) buffer.append(",tag=" + tag);
		return buffer.toString();
	}
	public static String parseModel(String networkDeviceString){
		String storagePattern = "^[a-z0-9_\\-.]+";
		Pattern r = Pattern.compile(storagePattern);
		Matcher m = r.matcher(networkDeviceString);
		if(m.find()){
			return m.group(0);
		} else {
			return null;
		}
	}
	public static String parseMac(String networkDeviceString){
		String storagePattern = "^[a-z0-9_\\-.]+=([a-zA-Z0-9:]+)";
		Pattern r = Pattern.compile(storagePattern);
		Matcher m = r.matcher(networkDeviceString);
		if(m.find()){
			return m.group(1);
		} else {
			return null;
		}
	}
	public static String parseBridge(String networkDeviceString){
		String storagePattern = "bridge=([a-zA-Z0-9]+)";
		Pattern r = Pattern.compile(storagePattern);
		Matcher m = r.matcher(networkDeviceString);
		if(m.find()){
			return m.group(1);
		} else {
			return null;
		}
	}
	public static float parseRate(String networkDeviceString){
		String storagePattern = "rate=([0-9.]+)";
		Pattern r = Pattern.compile(storagePattern);
		Matcher m = r.matcher(networkDeviceString);
		if(m.find()){
			return Float.parseFloat(m.group(1));
		} else {
			return 0;
		}
	}
	public static int parseTag(String networkDeviceString){
		String storagePattern = "tag=([0-9]+)";
		Pattern r = Pattern.compile(storagePattern);
		Matcher m = r.matcher(networkDeviceString);
		if(m.find()){
			return Integer.parseInt(m.group(1));
		} else {
			return 0;
		}
	}
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public String getBus() {
		return bus;
	}

	public void setBus(String bus) {
		this.bus = bus;
	}

	public int getDevice() {
		return device;
	}

	public void setDevice(int device) {
		this.device = device;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getBridge() {
		return bridge;
	}

	public void setBridge(String bridge) {
		this.bridge = bridge;
	}

	public Adapter(String bus, int device){
		this.bus = bus;
		this.device = device;

	}
}

package net.elbandi.pve2api.data;

import java.util.HashMap;
import java.util.Map;

import net.elbandi.pve2api.Pve2Api;
import net.elbandi.pve2api.data.resource.Adapter;
import net.elbandi.pve2api.data.resource.Cdrom;
import net.elbandi.pve2api.data.resource.QemuDisk;
import net.elbandi.pve2api.data.resource.Node;
import org.json.JSONException;
import org.json.JSONObject;

public class VmQemu {
	private int vmid;

	private String name;
	private Status vmStatus;
	private net.elbandi.pve2api.data.resource.Node node;
	/* enable/disable acpi */
	private boolean acpi;
	/* boot order [acdn]{1,4} */
	private String boot = "cdn";
	/* e.g ide0 */
	private String bootdisk;
	private int cores;
	/* Emulated CPU type */
	private String cpu;
	private int cpuunits = 1000;
	private String desc;
	private String digest;
	private boolean freeze;
	/* Enable/disable kvm virtualization */
	private boolean kvm;

	/* in mbytes */
	private int memory;

	private boolean onboot;
	private String ostype;
	private int sockets;

	private Map<String, BlockDevice> blockDeviceMap = new HashMap<String, BlockDevice>();

	private Map<String, Adapter> adapterMap = new HashMap<String, Adapter>();



	public void addBlockDevice(BlockDevice device){
		blockDeviceMap.put(device.getBus() + device.getDevice(), device);
	}
	public void addAdapter(Adapter adapter){
		adapterMap.put(adapter.getBus() + adapter.getDevice(), adapter);
	}
	public Status getVmStatus() {
		return vmStatus;
	}

	public void setVmStatus(Status vmStatus) {
		this.vmStatus = vmStatus;
	}


	public Map<String, String> toMap() throws DeviceException, MissingFieldException{
		Map<String, String> map = new HashMap<String, String>();
		if(vmid == 0) throw new MissingFieldException("Field 'vmid' is missing");
		map.put("vmid", Integer.toString(vmid));
		/*if(node == null) throw new MissingFieldException("Field 'node' is missing");
		map.put("node", node.getNode());*/
		map.put("name", name);
		/*map.put("acpi", Boolean.toString(acpi));*/
		map.put("boot", boot);
		if(bootdisk != null) map.put("bootdisk", bootdisk);
		if(cores > 0) map.put("cores", Integer.toString(cores));
		if(cpu != null) map.put("cpu", cpu);
		if (cpuunits > 0) map.put("cpuunits", Integer.toString(cpuunits));
		if(desc != null) map.put("description", desc);
		if(digest != null) map.put("digest", digest);
		if(freeze){ map.put("freeze",  "1"); } else { map.put("freeze",  "0"); }

		if(memory > 0) map.put("memory", Integer.toString(memory));
		/*map.put("kvm", Boolean.toString(kvm));*/
		if(onboot) { map.put("onboot", "1"); } else { map.put("onboot", "0"); }
		/*map.put("onboot", Boolean.toString(onboot));*/
		if(ostype != null) map.put("ostype", ostype);

		for(String device : blockDeviceMap.keySet()){
			String blockDevice;
			if(blockDeviceMap.get(device) instanceof QemuDisk){
				blockDevice =  ((QemuDisk)blockDeviceMap.get(device)).getCreateString();
			} else if(blockDeviceMap.get(device) instanceof Cdrom){
				blockDevice = ((Cdrom)blockDeviceMap.get(device)).toString();
			} else {
				throw new DeviceException("Unknown type of block device: " + blockDeviceMap.get(device).getClass());
			}
			map.put(device, blockDevice);
		}
		for(String adapter : adapterMap.keySet()){
			map.put(adapter, adapterMap.get(adapter).getCreateString());
		}
		return  map;
	}
	public VmQemu(int vmid){
		this.vmid = vmid;
	}
	public VmQemu(int vmid, String name){
		this.vmid = vmid;
		this.name = name;
	}
	public VmQemu(JSONObject data) throws JSONException {
		name = data.getString("name");
		acpi = data.optInt("acpi", 1) == 1;
		cpu = data.getString("cpu");
		cores = data.getInt("cores");
		cpuunits = data.optInt("cpuunits", 1000);
		desc = data.optString("description");
		bootdisk = data.optString("bootdisk");
		boot = data.optString("boot");
		digest = data.getString("digest");
		freeze = data.optInt("freeze", 0) == 1;
		kvm = data.optInt("kvm", 1) == 1;
		memory = data.getInt("memory");
		onboot = data.optInt("onboot") == 1;
		sockets = data.optInt("sockets", 1);
		ostype = data.getString("ostype");
		for (String k : JSONObject.getNames(data)) {
			if (k.startsWith("ide") ||k.startsWith("scsi") || k.startsWith("virtio")){

				String blockDeviceString = data.optString(k);
				if(BlockDevice.parseMedia(blockDeviceString) != null && BlockDevice.parseMedia(blockDeviceString).equals("cdrom")){
					Cdrom cdrom = new Cdrom(k.replaceAll("[0-9]+", ""), Integer.parseInt(k.substring(k.length() - 1)));
					cdrom.setMedia(BlockDevice.parseMedia(blockDeviceString));
					cdrom.setSize(BlockDevice.parseSize(blockDeviceString));
				 	cdrom.setStorage(BlockDevice.parseStorage(blockDeviceString));
					cdrom.setUrl(BlockDevice.parseUrl(blockDeviceString));
					blockDeviceMap.put(cdrom.getBus() + cdrom.getDevice(), cdrom);
				} else {
					QemuDisk qemuDisk = new QemuDisk(k.replaceAll("[0-9]+", ""), Integer.parseInt(k.substring(k.length() - 1)));
					qemuDisk.setStorage(QemuDisk.parseStorage(blockDeviceString));
					qemuDisk.setSize(QemuDisk.parseSize(blockDeviceString));
					qemuDisk.setUrl(QemuDisk.parseUrl(blockDeviceString));
					qemuDisk.setIops_rd(QemuDisk.parseIops_rd(blockDeviceString));
					qemuDisk.setIops_wr(QemuDisk.parseIops_wr(blockDeviceString));
					qemuDisk.setMbps_rd(QemuDisk.parseMbps_rd(blockDeviceString));
					qemuDisk.setMbps_wr(QemuDisk.parseMbps_wr(blockDeviceString));
					qemuDisk.setMedia(BlockDevice.parseMedia(blockDeviceString));
					///System.out.println("Putting " + qemuDisk.getBus() + qemuDisk.getDevice());
					blockDeviceMap.put(qemuDisk.getBus() + qemuDisk.getDevice(), qemuDisk);
				}

			}else if (k.startsWith("net")){
				String netDeviceString = data.getString(k);
				Adapter adapter = new Adapter("net", Integer.parseInt(k.substring(3)));
				adapter.setModel(Adapter.parseModel(netDeviceString));
				adapter.setMac(Adapter.parseMac(netDeviceString));
				adapter.setBridge(Adapter.parseBridge(netDeviceString));
				adapter.setRate(Adapter.parseRate(netDeviceString));
				adapter.setTag(Adapter.parseTag(netDeviceString));
				adapterMap.put(adapter.getBus() + adapter.getDevice(), adapter);
			}
		}

	}
	/*
	 * "boot" : "dnc", cdn c disk d, cdrom a, floppy n, network "bootdisk" :
	 * "virtio0",
	 */
	/*public static String getOsType(String name) {
		if (name.equals("wxp"))
			return "Microsoft Windows XP/2003";
		else if (name.equals("w2k"))
			return "Microsoft Windows 2000";
		else if (name.equals("w2k8"))
			return "Microsoft Windows Vista/2008";
		else if (name.equals("win7"))
			return "Microsoft Windows 7/2008r2";
		else if (name.equals("l24"))
			return "Linux 2.4 Kernel";
		else if (name.equals("l26"))
			return "Linux 3.X/2.6 Kernel";
		else
			return "Other OS types";
	}*/
	//Now moved to Status nested class
	/*public Map<String, String> getCreateConfig(){
		Map<String, String> params = new HashMap<String, String>();
		if(this.cpu > 0) params.put("cpu", Float.toString(this.cpu));
		if (this.cpus > 0) params.put("cpus", Integer.toString(this.cpus));
		if(this.disk > 0) params.put("disk", Float.toString(this.disk));
		if(this.diskread > 0) params.put("diskread", Long.toString(this.diskread));
		if(this.diskwrite > 0) params.put("diskwrite", Long.toString(this.diskwrite));
		params.put("ha", Boolean.toString(this.ha));
		if(this.maxdisk > 0) params.put("maxdisk", )
	}*/
	public class DeviceException extends Exception {
		public DeviceException() {
			super();
		}
		public DeviceException(String message) { super(message); }
		public DeviceException(String message, Throwable cause) { super(message, cause); }
		public DeviceException(Throwable cause) { super(cause); }
	}
	public class MissingFieldException extends Exception{
		public MissingFieldException(){
			super();
		}
		public MissingFieldException(String message) { super(message);}
		public MissingFieldException(String message, Throwable cause){ super(message, cause);}
		public MissingFieldException(Throwable cause){ super(cause);}
	}
	class Status {
		/* cpu - current cpu usage, %. 1 - 100 usage	 */
		private float cpu;
		/* amount of cpus */
		private int cpus;
		/* unknown parameter */
		private float disk;
		/* amount of disk read requests issued */
		private long diskread;
		/* amount of disk write requests issued */
		private long diskwrite;
		/* unknown parameter */
		private boolean ha;
		/* disk size, bytes */
		private long maxdisk;
		/* amount of memory assigned to vm */
		private long maxmem;
		/* used memory */
		private long mem;
/*		private String name;*/ //Move to main class?
		private long netin;
		private long netout;
		private int pid;
		/* current VM status. running, stopped */
		private String status;
		private int uptime;
		Status(JSONObject data) throws JSONException {
			this.cpu = (float)data.getDouble("cpu");
			this.cpus = data.getInt("cpus");
			this.disk = (float)data.getDouble("disk");
			this.diskread = data.getLong("diskread");
			this.diskwrite = data.getLong("diskwrite");
			this.ha = data.getBoolean("ha");
			this.maxdisk = data.getLong("maxdisk");
			this.maxmem = data.getLong("maxmem");
			this.mem = data.getLong("mem");
			this.netin = data.getLong("netin");
			this.netout = data.getLong("netout");
			this.pid = data.getInt("pid");
			this.status = data.getString("status");
			this.uptime = data.getInt("uptime");
		}
		public float getCpu() {
			return cpu;
		}

		public int getCpus() {
			return cpus;
		}

		public float getDisk() {
			return disk;
		}

		public long getDiskread() {
			return diskread;
		}

		public long getDiskwrite() {
			return diskwrite;
		}

		public boolean isHa() {
			return ha;
		}

		public long getMaxdisk() {
			return maxdisk;
		}

		public long getMaxmem() {
			return maxmem;
		}

		public long getMem() {
			return mem;
		}



		public long getNetin() {
			return netin;
		}

		public long getNetout() {
			return netout;
		}

		public int getPid() {
			return pid;
		}

		public String getStatus() {
			return status;
		}

		public int getUptime() {
			return uptime;
		}

	}


	public boolean isAcpi() {
		return acpi;
	}

	public String getBoot() {
		return boot;
	}

	public String getBootdisk() {
		return bootdisk;
	}

	public int getCores() {
		return cores;
	}

	public int getCpuunits() {
		return cpuunits;
	}

	public String getDesc() {
		return desc;
	}

	public String getDigest() {
		return digest;
	}

	public boolean isFreeze() {
		return freeze;
	}
	public boolean isKvm() {
		return kvm;
	}

	public int getMemory() {
		return memory;
	}

	public boolean isOnboot() {
		return onboot;
	}

	public String getOstype() {
		return ostype;
	}

	public int getSockets() {
		return sockets;
	}
	public String getName() {
		return name;
	}
	public int getVmid() {
		return vmid;
	}

	public void setVmid(int vmid) {
		this.vmid = vmid;
	}

	public Map<String, BlockDevice> getBlockDeviceMap() {
		return blockDeviceMap;
	}
	public Map<String, Adapter> getAdapterMap() {
		return adapterMap;
	}

	public String getCpu() {
		return cpu;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAcpi(boolean acpi) {
		this.acpi = acpi;
	}

	public void setBoot(String boot) {
		this.boot = boot;
	}

	public void setBootdisk(String bootdisk) {
		this.bootdisk = bootdisk;
	}

	public void setCores(int cores) {
		this.cores = cores;
	}

	public void setCpuunits(int cpuunits) {
		this.cpuunits = cpuunits;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public void setFreeze(boolean freeze) {
		this.freeze = freeze;
	}

	public void setKvm(boolean kvm) {
		this.kvm = kvm;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public void setOnboot(boolean onboot) {
		this.onboot = onboot;
	}

	public void setOstype(String ostype) {
		this.ostype = ostype;
	}

	public void setSockets(int sockets) {
		this.sockets = sockets;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
}

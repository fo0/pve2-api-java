package net.elbandi.pve2api.data;

import java.util.HashMap;
import java.util.Map;

import net.elbandi.pve2api.data.resource.Adapter;
import net.elbandi.pve2api.data.resource.Cdrom;
import net.elbandi.pve2api.data.resource.QemuDisk;
import org.json.JSONException;
import org.json.JSONObject;

public class VmQemu {
	private int vmid;

	private String name;
	private Status vmStatus;
	/* enable/disable acpi */
	private boolean acpi;
	/* boot order [acdn]{1,4} */
	private String boot;
	/* e.g ide0 */
	private String bootdisk;
	private int cores;
	private int cpuunits;
	private String desc;
	private String digest;
	private boolean freeze;

	private boolean kvm;
	private int memory;

	private boolean onboot;
	private String ostype;
	private int sockets;


	private Map<String, BlockDevice> blockDeviceMap = new HashMap<String, BlockDevice>();



	private Map<String, Adapter> adapterMap = new HashMap<String, Adapter>();


	public Status getVmStatus() {
		return vmStatus;
	}

	public void setVmStatus(Status vmStatus) {
		this.vmStatus = vmStatus;
	}

	private Map<Integer, String> virtio = new HashMap<Integer, String>();

	public Map<String, String> toMap() throws DeviceException{
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		map.put("acpi", Boolean.toString(acpi));
		map.put("boot", boot);
		map.put("bootdisk", bootdisk);
		if(cores > 0) map.put("cores", Integer.toString(cores));
		if (cpuunits > 0) map.put("cpuunits", Integer.toString(cpuunits));
		if(desc != null) map.put("desc", desc);
		if(digest != null) map.put("digest", digest);
		map.put("freeze", Boolean.toString(freeze));
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
	public VmQemu(JSONObject data) throws JSONException {
		acpi = data.optInt("acpi", 1) == 1;
		cores = data.getInt("cores");
		cpuunits = data.optInt("cpuunits", 1000);
		desc = data.getString("description");
		bootdisk = data.optString("bootdisk");
		boot = data.optString("boot");
		digest = data.getString("digest");
		freeze = data.optInt("freeze", 0) == 1;
		kvm = data.optInt("kvm", 1) == 1;
		memory = data.getInt("memory");
		onboot = data.getInt("onboot") == 1;
		sockets = data.getInt("sockets");
		ostype = data.getString("ostype");
		for (String k : JSONObject.getNames(data)) {
			if (k.startsWith("ide") ||k.startsWith("scsi") || k.startsWith("virtio")){
				String blockDeviceString = data.optString(k);
				QemuDisk qemuDisk = new QemuDisk("ide", Integer.parseInt(k.substring(3)));
				qemuDisk.setStorage(QemuDisk.parseStorage(blockDeviceString));
				qemuDisk.setSize(Long.parseLong(QemuDisk.parseSize(blockDeviceString)));
				qemuDisk.setUrl(QemuDisk.parseUrl(blockDeviceString));
				qemuDisk.setIops_rd(Integer.parseInt(QemuDisk.parseIops_rd(blockDeviceString)));
				qemuDisk.setIops_wr(Integer.parseInt(QemuDisk.parseIops_wr(blockDeviceString)));
				qemuDisk.setMbps_rd(Integer.parseInt(QemuDisk.parseMbps_rd(blockDeviceString)));
				qemuDisk.setMbps_wr(Integer.parseInt(QemuDisk.parseIops_wr(blockDeviceString)));
				blockDeviceMap.put(qemuDisk.getBus() + qemuDisk.getDevice(), qemuDisk);
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

	public Map<Integer, String> getVirtio() {
		return virtio;
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
}

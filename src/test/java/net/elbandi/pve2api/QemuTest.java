package net.elbandi.pve2api;

import static org.junit.Assert.*;

import net.elbandi.pve2api.data.VmQemu;
import net.elbandi.pve2api.data.Volume;
import net.elbandi.pve2api.data.resource.Adapter;
import net.elbandi.pve2api.data.resource.Cdrom;
import net.elbandi.pve2api.data.resource.QemuDisk;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 8/22/13
 * Time: 9:41 AM
 * To change this template use File | Settings | File Templates.
 */


public class QemuTest {
	Pve2Api pve2Api = new Pve2Api(getConfig().getProperty("host"), getConfig().getProperty("username"), "pam", getConfig().getProperty("password"));
	@Test
	public void simpleTest(){
		VmQemu vm = new VmQemu(100, "test");
		assertEquals(vm.getVmid(), 100);
		assertEquals(vm.getName(), "test");

	}
	@Test
	public void testJsonParsing(){
		JSONObject jsonObject = new JSONObject();
		VmQemu vm = new VmQemu(100);
		try {
			jsonObject.put("name", "test");
			jsonObject.put("sockets", 1);
			jsonObject.put("ostype", "l26");
			jsonObject.put("cpu", "host");
			jsonObject.put("cores", 1);
			jsonObject.put("memory", 1024);
			jsonObject.put("boot", "nc");
			jsonObject.put("digest", "ef92da73dc78c0cba38dbb04becd616132dff920");
			jsonObject.put("bootdisk", "ide0");

			jsonObject.put("net1", "e1000=1A:B1:0E:95:1A:2A,bridge=vmbr1");
			jsonObject.put("virtio1", "local:111/vm-111-disk-2.qcow2,iops_rd=10,iops_wr=10,mbps_wr=1.1,size=1G");
			jsonObject.put("ide0", "local:111/vm-111-disk-1.qcow2,mbps_rd=10,size=1G");
			jsonObject.put("ide1","local:iso/grml64-small_2013.02.iso,media=cdrom,size=166M");
			vm = new VmQemu(jsonObject);
		}catch (JSONException e){
			fail("Json exception: " + e.getMessage());
		}
		assertEquals(vm.getName(), "test");
		//assertEquals(vm.getVmid(), 100);
		assertEquals(vm.getSockets(), 1);
		assertEquals(vm.getOstype(), "l26");
		assertEquals(vm.getCpu(), "host");
		assertEquals(vm.getCores(), 1);
		assertEquals(vm.getMemory(), 1024);
		assertEquals(vm.getBoot(), "nc");
		assertEquals(vm.getDigest(), "ef92da73dc78c0cba38dbb04becd616132dff920");
		assertEquals(vm.getBootdisk(), "ide0");

		//testing network devices parsing
		assertNotNull(vm.getAdapterMap().get("net1"));
		assertEquals(vm.getAdapterMap().get("net1").getModel(), "e1000");
		assertEquals(vm.getAdapterMap().get("net1").getMac(), "1A:B1:0E:95:1A:2A");

		//testing block device parsing
		assertNotNull(vm.getBlockDeviceMap().get("virtio1"));
		assertEquals(vm.getBlockDeviceMap().get("virtio1").getStorage(), "local");
		assertEquals(((QemuDisk)vm.getBlockDeviceMap().get("virtio1")).getIops_rd(), 10);
		assertEquals(((QemuDisk)vm.getBlockDeviceMap().get("virtio1")).getIops_wr(), 10);
		assertEquals(((QemuDisk)vm.getBlockDeviceMap().get("virtio1")).getMbps_wr(), 1.1, 0.01d);
		//testing cdrom
		assertEquals(vm.getBlockDeviceMap().get("ide1").getMedia(), "cdrom");
		assert vm.getBlockDeviceMap().get("ide1") instanceof Cdrom;
	}
	@Test
	public void getQemuVmTest(){

		VmQemu vmQemu = null;
		try {
			vmQemu = pve2Api.getQemuVM("nod2", 111);
		}catch (Exception e){
			fail(e.getMessage());
		}
		//System.out.println(vmQemu.getName());
	}
	@Test
	public void testNextId(){
		try {
			assert pve2Api.nextId() instanceof Integer;
		}catch (Exception e){
			fail(e.getMessage());
		}
	}
	/*@Test
	public void testAssignDisk() {
		Volume testVolume = null;
		try {
			testVolume = pve2Api.getVolumeById("nod2", "local", "local:111/testvolume.qcow2");
		} catch (Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		QemuDisk qemuDisk = new QemuDisk("sata", 0);
		qemuDisk.setStorage("local");
		qemuDisk.setMbps_rd(10.1);
		qemuDisk.setVolume(testVolume);
		try {
			pve2Api.assignDiskToQemu(111, "nod2", qemuDisk);
		}catch (Exception e){
			fail(e.getMessage());
		}
	}*/
	/*@Test
	public void testCreateVolume(){
		try {
			pve2Api.createVolume("nod2", "local", "testvolume.qcow2", "10G", 111, "qcow2");
		}catch (Exception e){
			fail(e.getMessage());
		}

	}*/
	/*@Test
	public void testCreateQemuVm() {
		//creating a disk volume
		Volume volume = new Volume("qcow2")

		//creating a disk image, QemuDisk
		QemuDisk qemuDisk = new QemuDisk("ide", 0);
		qemuDisk.setSize(1024l * 1024l * 1024l);
		qemuDisk.setStorage("local");
		qemuDisk.setMbps_rd(10d);
		//creating a new interface, Adapter
		Adapter adapter = new Adapter("net", 0);
		adapter.setModel("e1000");
		VmQemu vmQemu = null;
		try {
			vmQemu = new VmQemu(pve2Api.nextId(), "pve2test");
			vmQemu.setName("pve2test");
			vmQemu.setCpu("host");
			vmQemu.setOstype("l26");
			*//*vmQemu.setBootdisk("ide0");*//*
			vmQemu.setMemory(1024);
			vmQemu.setCores(1);
			vmQemu.setOnboot(true);
			vmQemu.setSockets(1);
			*//*vmQemu.addBlockDevice(qemuDisk);*//*
			vmQemu.addAdapter(adapter);
			vmQemu.setNode(pve2Api.getNodeList().get(0));
		}catch (LoginException loginE){
			fail(loginE.getMessage());
		}catch (JSONException jsonE){
			fail(jsonE.getMessage());
		}catch (IOException ioException){
			fail(ioException.getMessage());
		}
		try {
			Map<String, String> map = vmQemu.toMap();
			for(String key : map.keySet()){
				System.out.println("Key: " + key + " Value: " + map.get(key));
			}
			pve2Api.createQemu(vmQemu);
		} catch (VmQemu.MissingFieldException missingFieldEx){
			fail(missingFieldEx.getMessage());
		} catch (VmQemu.DeviceException deviceException){
			fail(deviceException.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}

	}*/
	public Properties getConfig(){
		Properties props = new Properties();
		try {
			props.load(new FileReader(new File("/home/artemz/Documents/pve2.properties")));
		}catch (Exception e){}
		return props;
	}
}

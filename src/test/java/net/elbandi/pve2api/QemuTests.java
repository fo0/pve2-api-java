package net.elbandi.pve2api;

import static org.junit.Assert.*;

import net.elbandi.pve2api.data.VmQemu;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 8/22/13
 * Time: 9:41 AM
 * To change this template use File | Settings | File Templates.
 */


public class QemuTests {
	@Test
	public void simpleTest(){
		VmQemu vm = new VmQemu(100, "test");
		assertEquals(vm.getVmid(), 100);
		assertEquals(vm.getName(), "test");

	}
}

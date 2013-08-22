package net.elbandi.pve2api.data.resource;

import net.elbandi.pve2api.data.BlockDevice;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 8/19/13
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Cdrom extends BlockDevice {
	@Override
	public String toString(){
		return this.storage + ":" + this.url + ",media=cdrom,size=" + readableFileSize(this.size);

	}
}

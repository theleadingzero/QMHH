package net.qmat.qmhh.controllers;

import java.util.concurrent.LinkedBlockingQueue;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.models.Models;
import net.qmat.qmhh.utils.Settings;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

public class OscController extends Thread {
	
	OscP5 oscP5;
	LinkedBlockingQueue<QueueMessage> queue;

	NetAddress soundMachine;
	NetAddress sequencerMachine;
	
	public OscController() {
		oscP5 = new OscP5(this, Settings.getInteger(Settings.OSC_LOCAL_PORT));
		queue = new LinkedBlockingQueue<QueueMessage>();
		soundMachine = new NetAddress(Settings.getString(Settings.OSC_SOUND_REMOTE_IP),
									  Settings.getInteger(Settings.OSC_SOUND_REMOTE_PORT));
	}
	
	public void queueSoundEvent(String endPoint, Object... objects) {
		queueEvent(soundMachine, endPoint, objects);
	}
	
	private void queueEvent(NetAddress address, String endPoint, Object[] objects) {
		try {
			queue.put(new QueueMessage(address, endPoint, objects));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void sendEvent(QueueMessage qm) {
		OscMessage m = new OscMessage(qm.endPoint);
		m.add(Main.table);
		for(Object obj : qm.objects) {
			if(obj instanceof Integer)
				m.add((Integer)obj);
			if(obj instanceof Long)
				m.add((Long)obj);
			if(obj instanceof Float)
				m.add((Float)obj);
			if(obj instanceof Double)
				m.add((Double)obj);
			if(obj instanceof String)
				m.add((String)obj);
		}
		oscP5.send(m, qm.address);
	}
	
	public void sendBurstingMessage() {
		queueSoundEvent("/bursting");
	}

	@Override
	public void run() {
		while(true) {
			try {
				QueueMessage qm = (QueueMessage)queue.take();
				sendEvent(qm);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class QueueMessage {
		NetAddress address;
		String endPoint;
		Object[] objects;
		
		public QueueMessage(NetAddress address, String endPoint, Object[] objects) {
			this.address = address;
			this.endPoint = endPoint;
			this.objects = objects;
		}
	}
	
	void oscEvent(OscMessage theOscMessage) {
		if(theOscMessage.checkAddrPattern("/explode")==true) {
			if(theOscMessage.checkTypetag("")) {
				Models.getExplosionModel().startExplosion();
			}  
		}
	}
}

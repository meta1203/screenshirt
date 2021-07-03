package com.meta1203.screenshirt.bluetooth;

import java.io.IOException;
import java.util.StringJoiner;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.meta1203.screenshirt.ShirtManager;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class BluetoothListener {
	@Autowired 
	private SimpMessagingTemplate websocket;
	@Autowired
	private BluetoothManager bm;
	
	public void readMessage(BluetoothMessage incoming) {
		if (incoming == null || incoming.getMessage() == null || incoming.getMessage().equals("")) return;
		log.info("received message: " + incoming.getMessage());
		
		if (incoming.getMessage().startsWith("setshirt|")) {
			String toSend = incoming.getMessage().replace("setshirt|", "");
			log.info("Setting shirt to " + toSend);
			websocket.convertAndSend("/response/select", toSend);
			return;
		}
		
		if (incoming.getMessage().startsWith("disconnect|")) {
			log.info("Received disconnect command.");
			try {
				synchronized (incoming) {
					incoming.getConnection().close();
				}
			} catch (IOException e) {
				log.error("Failed to close connection!", e);
			}
			return;
		}
		
		if (incoming.getMessage().startsWith("getshirts|")) {
			StringJoiner sj = new StringJoiner("|");
			sj.add("shirtlist");
			for (String s : ShirtManager.getShirts()) {
				sj.add(s);
			}
			incoming.getConnection().write(sj.toString());
			return;
		}
	}
	
	@PostConstruct
	public void init() {
		bm.setMessageListener(this::readMessage);
	}
}

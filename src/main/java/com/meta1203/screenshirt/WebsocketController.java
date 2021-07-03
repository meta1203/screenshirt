package com.meta1203.screenshirt;

import java.awt.Color;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.meta1203.screenshirt.models.SystemStatus;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class WebsocketController {
	@Autowired 
	private SimpMessagingTemplate websocket;
	
	@MessageMapping("/status")
	public void command() {
		log.info("responding to status request");
		websocket.convertAndSend("/response/status", SystemStatus.builder().now(LocalDateTime.now()).shirtColor(Color.BLACK).build());
	}
	
	@MessageMapping("/shirts")
	public void getShirts() {
		log.info("returning shirts");
		websocket.convertAndSend("/response/shirts", ShirtManager.getShirts());
	}
}

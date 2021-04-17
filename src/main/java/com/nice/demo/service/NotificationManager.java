package com.nice.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

@Component
public class NotificationManager {

	@Value("${notification.telegram.bot.token}") 
	private String token; 
	
	@Value("${notification.telegram.chat.id}") 
	private String chatId;
	
	
	public void send(String content) {
		//텔레그램 통보
		String url = "https://api.telegram.org/bot" + token + "/sendMessage"; 
		try { 
			TelegramMessage telegramMessage = new TelegramMessage(chatId, content); 
			
			String param = new Gson().toJson(telegramMessage); 
			RestTemplate restTemplate = new RestTemplate(); 
			HttpHeaders headers = new HttpHeaders(); 
			headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE); // send the post request 
			HttpEntity<String> entity2 = new HttpEntity<>(param, headers); 
			restTemplate.postForEntity(url, entity2, String.class); 
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
	}
	


}
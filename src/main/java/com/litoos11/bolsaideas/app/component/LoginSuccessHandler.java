package com.litoos11.bolsaideas.app.component;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

@Component("loginSuccessComponent")
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
		
		FlashMap flashMap = new FlashMap();
		flashMap.put("success", "Hola "+ authentication.getName() +", haz iniciado sesión con éxito!");
		
		if(authentication != null) {
			logger.info("El usuario "+ authentication.getName() +" ha inicado sesión con éxito!");
		}
		
		flashMapManager.saveOutputFlashMap(flashMap, request, response);
		super.onAuthenticationSuccess(request, response, authentication);
	}

}

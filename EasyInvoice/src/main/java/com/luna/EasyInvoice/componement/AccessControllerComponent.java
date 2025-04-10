package com.luna.EasyInvoice.componement;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

public class AccessControllerComponent implements AccessDeniedHandler {
	private static Logger logger = LoggerFactory.getLogger(AccessControllerComponent.class);
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			logger.debug("User '" + auth.getName() + "' attempted to access the protected URL: " + request.getRequestURI());
		}
		response.sendRedirect(request.getContextPath() + "/403");
		
	} 

}

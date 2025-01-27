package com.kdk.app.main.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 1. 27. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
@Controller
public class MainController {

	@GetMapping("/main")
	public ModelAndView main(Authentication authentication) {
		ModelAndView mav = new ModelAndView();

		String sUsernam = authentication.getName();

		StringBuilder sb = new StringBuilder();

		@SuppressWarnings("unchecked")
		List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();

		for ( GrantedAuthority authority : authorities ) {
			sb.append(authority.getAuthority()).append(" ");
		}

		mav.addObject("username", sUsernam);
		mav.addObject("roles", sb.toString().trim());

		mav.setViewName("main/main");
		return mav;
	}

}

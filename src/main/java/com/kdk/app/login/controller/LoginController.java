package com.kdk.app.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
public class LoginController {

	@GetMapping("/login")
	public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
		ModelAndView mav = new ModelAndView();

		if ( error != null ) {
			mav.addObject("errorMessage", "아이디 또는 비밀번호를 정확히 입력해 주세요.");
		}

		mav.setViewName("login/login");
		return mav;
	}

}

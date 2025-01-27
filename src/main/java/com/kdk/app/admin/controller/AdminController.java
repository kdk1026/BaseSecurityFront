package com.kdk.app.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("main")
	public ModelAndView main() {
		ModelAndView mav = new ModelAndView();

		mav.setViewName("admin/main");
		return mav;
	}

}

package com.kdk.app.test.controller;

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
@RequestMapping("/test")
public class TestController {

	@GetMapping("/layoutBase")
	public ModelAndView tilesBase() {
		ModelAndView mav = new ModelAndView();

		mav.setViewName("/test/layoutBase");
		return mav;
	}

	@GetMapping("/layoutBase2")
	public ModelAndView tilesBase2() {
		ModelAndView mav = new ModelAndView();

		mav.setViewName("/test/layoutBase2");
		return mav;
	}

	@GetMapping("/i18n")
	public ModelAndView i18n() {
		ModelAndView mav = new ModelAndView();

		mav.setViewName("test/i18n");
		return mav;
	}

}

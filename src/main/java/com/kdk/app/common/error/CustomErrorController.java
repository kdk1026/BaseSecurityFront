package com.kdk.app.common.error;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;

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
public class CustomErrorController implements ErrorController {

	@Autowired
	private ErrorAttributes errorAttributes;

    @GetMapping("/error")
    public String handlError(WebRequest webRequest, Model model) {
    	Map<String, Object> errors = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
    	model.addAttribute("errors", errors);

    	Integer statusCode = (Integer) errors.get("status");

    	if ( statusCode == 404 ) {
    		return "error/404";
    	} else if ( statusCode == 403 ) {
    		return "error/403";
    	} else {
    		return "error/default";
    	}
    }

}

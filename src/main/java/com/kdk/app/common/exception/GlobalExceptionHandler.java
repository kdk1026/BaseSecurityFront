package com.kdk.app.common.exception;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 9. 10. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ArithmeticException.class)
	public ModelAndView handleArithmeticException(ArithmeticException e) {
		log.error("", e);

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", e.getMessage());
		mav.setViewName("error/exception");
		return mav;
	}

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgument(IllegalArgumentException e) {
    	log.error("", e);

    	String sResponseMessage = "Illegal Argument";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleIAccessDenied(AccessDeniedException e) {
    	log.error("", e);

    	String sResponseMessage = "File Access Denied";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ModelAndView handleFileNotFound(FileNotFoundException e) {
    	log.error("", e);

    	String sResponseMessage = "File Not Found";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

    @ExceptionHandler(NullPointerException.class)
    public ModelAndView handleNullPointerException(NullPointerException e) {
        log.error("", e);

        String sResponseMessage = "A null pointer exception occurred. Please check your data and try again.";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

    @ExceptionHandler(IOException.class)
    public ModelAndView handleIOException(IOException e) {
        log.error("", e);

        String sResponseMessage = "An I/O error occurred. Please try again later.";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

    @ExceptionHandler(TimeoutException.class)
    public ModelAndView handleTimeoutException(TimeoutException e) {
        log.error("", e);

        String sResponseMessage = "A timeout error occurred. Please try again later.";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

    /*
    @ExceptionHandler(UncategorizedSQLException.class)
    public ModelAndView handleUncategorizedSQLException(UncategorizedSQLException e) {
    	log.error("", e);

    	String sResponseMessage = "An unexpected database error occurred. Please try again later.";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ModelAndView handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error("", e);

        String sResponseMessage = "A database error occurred due to incorrect SQL syntax. Please contact support.";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    	log.error("", e);

    	String sResponseMessage = "Data integrity violation occurred. Please check your data and try again.";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ModelAndView handleDuplicateKeyException(DuplicateKeyException e) {
    	log.error("", e);

    	String sResponseMessage = "Duplicate key error occurred. Please ensure unique values for key fields.";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

    @ExceptionHandler(CannotAcquireLockException.class)
    public ModelAndView handleCannotAcquireLockException(CannotAcquireLockException e) {
    	log.error("", e);

    	String sResponseMessage = "Database lock acquisition failed. Please try again later.";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

    @ExceptionHandler(DeadlockLoserDataAccessException.class)
    public ModelAndView handleDeadlockLoserDataAccessException(DeadlockLoserDataAccessException e) {
    	log.error("", e);

    	String sResponseMessage = "A database deadlock occurred. Please try again later.";

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }
    */

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
    	log.error("", e);

    	String sResponseMessage = e.getMessage();
    	String sExceptionMessage = e.getMessage();

    	if ( sExceptionMessage.indexOf("Could not open JDBC Connection") > -1 ) {
    		sResponseMessage = "Could not Database Connection";
    	}

    	if ( sExceptionMessage.indexOf("MailConnectException") > -1 ) {
    		sResponseMessage = "Could not Mail Connection";
    	}

    	if ( sExceptionMessage.indexOf("Unresolved compilation problem") > -1 ) {
    		sResponseMessage = "Unreachable code";
    	}

		ModelAndView mav = new ModelAndView();
		mav.addObject("message", sResponseMessage);
		mav.setViewName("error/exception");
		return mav;
    }

}

package com.db.awmd.challenge.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {
	private static Logger LOG = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);


	@ExceptionHandler({MethodArgumentNotValidException.class})
	@ResponseBody
	public ErrorTo handleMethodArgumentNotValidException(HttpServletResponse response, MethodArgumentNotValidException e) {
		LOG.warn(e.getMessage(), e);
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return new ErrorTo(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler({AccountWouldBeInDeficitException.class})
	@ResponseBody
	public ErrorTo handleAccountWouldBeInDeficitException(HttpServletResponse response, AccountWouldBeInDeficitException e) {
		LOG.warn(e.getMessage(), e);
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return new ErrorTo(HttpServletResponse.SC_BAD_REQUEST,  e.getMessage());
	}

	@ExceptionHandler({RestValidationException.class})
	@ResponseBody
	public ErrorTo handleValidationException(HttpServletResponse response, RestValidationException e) {
		LOG.warn(e.getMessage(), e);
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return new ErrorTo(HttpServletResponse.SC_BAD_REQUEST,  e.getMessage());
	}

	@ExceptionHandler(DuplicateAccountIdException.class)
	@ResponseBody
	public ErrorTo handleDuplicateAccountIdException(HttpServletResponse response, DuplicateAccountIdException e) {
		LOG.error("DuplicateAccountIdException: " + e.getMessage(), e);
		response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
		return new ErrorTo(HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
	}

	@ExceptionHandler(MultipartException.class)
	@ResponseBody
	public ErrorTo handleMultipartException(HttpServletResponse response, MultipartException e) {
		LOG.error("Internal Server Error: " + e.getMessage(), e);
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return new ErrorTo(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	public ErrorTo handleHttpMessageNotReadable(HttpServletResponse response, HttpMessageNotReadableException e) {
		LOG.warn("Invalid input: " + e.getMessage(), e);
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return new ErrorTo(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ErrorTo handleRemainingExceptions(HttpServletResponse response, Exception e) {
		LOG.error("Internal Server Error: " + e.getMessage(), e);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return new ErrorTo(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseBody
	public ErrorTo handleMethodArgumentMismatch(HttpServletResponse response, MethodArgumentTypeMismatchException e) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return new ErrorTo(HttpServletResponse.SC_BAD_REQUEST, "invalid value for :" + e.getParameter().getParameterName());
	}
}

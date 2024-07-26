package com.zhengtianbao.jsonexporter.exception.custom;

public class JsonPathExtractionException extends RuntimeException {

	public JsonPathExtractionException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonPathExtractionException(String message) {
		super(message);
	}

}

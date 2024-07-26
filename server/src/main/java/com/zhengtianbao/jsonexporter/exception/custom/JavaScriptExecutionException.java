package com.zhengtianbao.jsonexporter.exception.custom;

public class JavaScriptExecutionException extends RuntimeException {

	private final String scriptContent;

	public JavaScriptExecutionException(String message, Throwable cause, String scriptContent) {
		super(message, cause);

		this.scriptContent = scriptContent;
	}

	public String getScriptContent() {
		return scriptContent;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + "\nScript Content:\n" + scriptContent;
	}
}

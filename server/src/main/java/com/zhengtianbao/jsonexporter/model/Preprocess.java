package com.zhengtianbao.jsonexporter.model;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhengtianbao.jsonexporter.exception.custom.JavaScriptExecutionException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "preprocess")
public class Preprocess {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "script")
	private String script;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "server_id", nullable = false)
	@JsonIgnore
	private Server server;

	protected Preprocess() {
	}

	public Preprocess(String name, String script) {
		this.name = name;
		this.script = script;
	}

	@Override
	public String toString() {
		return String.format("Preprocess [id=%s, name=%s, script=%s]", id, name, script);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public String apply(String origin) {
		Context context = Context.enter();
		try {
			Scriptable scope = context.initStandardObjects();
			context.evaluateString(scope, script, "preprocess", 1, null);
			Object result = context.evaluateString(scope, "modify('" + escapeJavaScriptString(origin) + "')",
					"preprocessCall", 1, null);
			return Context.toString(result);
		} catch (EcmaError e) {
			throw new JavaScriptExecutionException("Error executing JavaScript function 'modify'",
					e,
					script + "\n\n" + "modify('" + escapeJavaScriptString(origin) + "')");
		} catch (RhinoException e) {
			throw new JavaScriptExecutionException("Unexpected error executing JavaScript",
					e,
					script + "\n\n" + "modify('" + escapeJavaScriptString(origin) + "')");
		} finally {
			Context.exit();
		}
	}

	private String escapeJavaScriptString(String str) {
		if (str == null) {
			return "";
		}
		return str.replace("\\", "\\\\")
				.replace("'", "\\'")
				.replace("\"", "\\\"")
				.replace("\r", "\\r")
				.replace("\n", "\\n")
				.replace("\t", "\\t");
	}
}

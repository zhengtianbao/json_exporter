package com.zhengtianbao.jsonexporter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
}

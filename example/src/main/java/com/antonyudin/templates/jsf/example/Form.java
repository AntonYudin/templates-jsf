
package com.antonyudin.templates.jsf.example;


import java.io.Serializable;

import java.util.logging.Logger;

import jakarta.faces.application.FacesMessage;

import jakarta.inject.Named;

import jakarta.enterprise.context.RequestScoped;

import jakarta.faces.context.FacesContext;


@RequestScoped
@Named
public class Form implements Serializable {

	private final static Logger logger = Logger.getLogger(Form.class.getName());


	private String string;

	public String getString() {
		return string;
	}

	public void setString(final String value) {
		string = value;
	}


	private int number;

	public int getNumber() {
		return number;
	}

	public void setNumber(final int value) {
		number = value;
	}


	private boolean checkbox;

	public boolean isCheckbox() {
		return checkbox;
	}

	public void setCheckbox(final boolean value) {
		checkbox = value;
	}


	protected void addMessage(final FacesMessage.Severity severity, final String summary, final String detail) {
		FacesContext.getCurrentInstance().addMessage(
			null,
			new FacesMessage(severity, summary, detail)
		);
	}

	public void update() {
		logger.info("update()");
		addMessage(FacesMessage.SEVERITY_INFO, "Form updated", null);
	}


	public void cancel() {
		logger.info("cancel");
		addMessage(FacesMessage.SEVERITY_INFO, "Form canceled", null);
	}

}


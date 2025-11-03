
package com.antonyudin.templates.jsf.example;


import java.io.Serializable;

import java.util.logging.Logger;

import java.util.List;
import java.util.ArrayList;

import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Inject;


@RequestScoped
@Named
public class Search implements Serializable {

	private final static Logger logger = Logger.getLogger(Search.class.getName());

	private final List<String> results = new ArrayList<>();

	public List<String> getResults() {
		return results;
	}

	private String query = null;

	public String getQuery() {
		return query;
	}

	public void setQuery(final String value) {
		query = value;
	}

	public void search() {
		logger.info("search(), " + getQuery());
		results.clear();
		for (var i = 0; i < (int) (Math.random() * 10.0); i++) {
			results.add("result " + Math.random());
		}
		FacesContext.getCurrentInstance().addMessage(
			null,
			new FacesMessage(
				FacesMessage.SEVERITY_INFO,
				"Search results",
				"Found " + results.size() + " items."
			)
		);
	}

}

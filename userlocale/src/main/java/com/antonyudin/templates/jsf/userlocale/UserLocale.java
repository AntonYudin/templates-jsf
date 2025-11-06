
package com.antonyudin.templates.jsf.userlocale;


import java.io.Serializable;

import java.util.logging.Logger;

import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import jakarta.inject.Named;

import jakarta.enterprise.context.RequestScoped;

import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;

import jakarta.servlet.http.HttpServletRequest;


@RequestScoped
@Named
public class UserLocale implements Serializable {

	private final static Logger logger = Logger.getLogger(UserLocale.class.getName());


	public String getCurrent() {
		return FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
	}

	public void setCurrent(final String value) {
		for (var item: getAvailable()) {
			if (item.toString().equalsIgnoreCase(value))
				FacesContext.getCurrentInstance().getViewRoot().setLocale(item);
		}
	}

	public List<Locale> getAvailable() {
		final var result = new ArrayList<Locale>();
		FacesContext.getCurrentInstance().getApplication().getSupportedLocales().forEachRemaining(result::add);
		Collections.sort(result, (item0, item1) -> item0.toString().compareTo(item1.toString()));
		return result;
	}

	public String getSwitchUrl(final Locale value) {
		final var request = HttpServletRequest.class.cast(FacesContext.getCurrentInstance().getExternalContext().getRequest());
		final var url = request.getRequestURL();
		logger.fine("url: [" + url + "]");
		final var currentPath = (
			request.getServletPath() +
			(request.getPathInfo() != null? request.getPathInfo(): "") +
			(request.getQueryString() == null? "": "?" + request.getQueryString())
		);
		logger.fine("currentPath: [" + currentPath + "]");
		final var supportedLocales = FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
		while (supportedLocales.hasNext()) {
			var item = supportedLocales.next();
			if (currentPath.startsWith("/" + item.toString() + "/")) {
				final var result = (
					buildRelativePath(request.getServletPath()) +
					value.toString() +
					currentPath.substring("/".length() + item.toString().length())
				);
				logger.fine("result: [" + result + "]");
				return result;
			}
		}
		final var result = ("./" + value + currentPath);
		logger.fine("result: [" + result + "]");
		return result;
	}

	protected String buildRelativePath(final String path) {
		var result = new StringBuilder();
		for (var i = 0; i < path.length(); i++) {
			if (path.charAt(i) == '/')
				result.append("../");
		}
		return result.toString();
	}

}


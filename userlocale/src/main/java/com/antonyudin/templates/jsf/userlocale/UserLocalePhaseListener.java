
package com.antonyudin.templates.jsf.userlocale;


import java.util.logging.Logger;
import java.util.logging.Level;

import jakarta.faces.context.FacesContext;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.faces.event.PhaseListener;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseEvent;


public class UserLocalePhaseListener implements PhaseListener {

	private static final Logger logger = Logger.getLogger(UserLocalePhaseListener.class.getName());


	@Override
	public void beforePhase(final PhaseEvent event) {
	}


	@Override
	public void afterPhase(final PhaseEvent event) {

		if (event.getPhaseId().equals(PhaseId.RESTORE_VIEW)) {

			final var facesContext = FacesContext.getCurrentInstance();
			final var request = HttpServletRequest.class.cast(facesContext.getExternalContext().getRequest());

			final var servletPath = request.getServletPath();

			final var indexFirst = servletPath.indexOf("/");
			final var indexNext = servletPath.indexOf("/", indexFirst + 1);

			if (indexFirst < 0)
				return;

			final var locale = servletPath.substring(indexFirst + 1, (indexNext < 0? servletPath.length(): indexNext));

			logger.fine("locale: [" + locale + "]");

			final var supportedLocales = facesContext.getApplication().getSupportedLocales();

			while (supportedLocales.hasNext()) {
				final var supported = supportedLocales.next();
				if (supported.toString().equalsIgnoreCase(locale)) {
					logger.fine("setting locale [" + facesContext.getViewRoot().getLocale() + " -> " + supported + "]");
					facesContext.getViewRoot().setLocale(supported);
				}
			}
		}
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}


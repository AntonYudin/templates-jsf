
package com.antonyudin.templates.jsf.relativeurlhandler;


import java.util.Map;
import java.util.List;

import java.util.logging.Logger;

import jakarta.faces.application.ViewHandlerWrapper;
import jakarta.faces.application.ViewHandler;

import jakarta.faces.context.FacesContext;

import jakarta.ws.rs.core.UriBuilder;


public class Handler extends ViewHandlerWrapper implements java.io.Serializable {

	private final static Logger logger = Logger.getLogger(Handler.class.getName());


	public Handler(final ViewHandler wrapped) {
		super(wrapped);
		this.wrapped = wrapped;
	}

	private final ViewHandler wrapped;

	@Override
	public ViewHandler getWrapped() {
		return wrapped;
	}


	@Override
	public String getActionURL(
		final FacesContext context,
		final String viewId
	) {

		final var currentViewId = context.getViewRoot().getViewId();

		logger.fine("getActionURL(" + context + ", " + viewId + "), currentViewId: " + currentViewId);

		final var originalURL = super.getActionURL(context, viewId);

		logger.fine("\toriginal url:  [" + originalURL + "]");

		if (
			(currentViewId != null) &&
			(viewId != null) &&
			(!currentViewId.equals(viewId))
		) {
			logger.fine("skipping, different view.");
			return originalURL;
		}

		final var newURL = convert(context, originalURL, viewId, true);

		logger.fine("\tusing url:  [" + newURL + "]");
		logger.fine("\tinstead of: [" + originalURL + "]");

		return newURL;
	}


	@Override
	public String getResourceURL(
		final FacesContext context,
		final String viewId
	) {

		logger.fine("getResourceURL(" + context + ", " + viewId + ")");

		final var original = super.getResourceURL(context, viewId);

		logger.fine("getResourceURL(): " + original);

		return convert(context, original, viewId, false);
	}


	@Override
	public String getBookmarkableURL(
		final FacesContext context,
		final String viewId,
		final Map<String, List<String>> parameters,
		final boolean includeViewParams
	) {

		logger.fine("getBookmarkableURL(" + context + ", " + viewId + ")");

		final var original = super.getBookmarkableURL(context, viewId, parameters, includeViewParams);

		logger.fine("getBookmarkableURL(): " + original);

		return convert(context, original, viewId, includeViewParams);
	}


	@Override
	public String getRedirectURL(
		final FacesContext context,
		final String viewId,
		final java.util.Map<String, java.util.List<String>> parameters,
		final boolean includeViewParams
	) {

		logger.fine("getRedirectURL(" + context + ", " + viewId + ", " + parameters + ", " + includeViewParams + ")");

		final var original = super.getRedirectURL(context, viewId, parameters, includeViewParams);

		logger.fine("getRedirectURL(): " + original);

		return original;
		/*

		return convert(context, original);
		*/
	}

	@Override
	public String getWebsocketURL(
		final FacesContext context,
		final String channel
	) {

		logger.fine("getWebsocketURL(" + context + ", " + channel + ")");

		final var original = super.getWebsocketURL(context, channel);

		logger.fine("getWebsocketURL(): " + original);

		return original;
	}


	protected String convert(final FacesContext context, final String url, final String viewId, final boolean includeViewParams) {

		final var result = new StringBuilder();

		result.append(url);

		final var applicationContextPath = context.getExternalContext().getApplicationContextPath();
		final var requestServletPath = context.getExternalContext().getRequestServletPath();
		final var requestPathInfo = context.getExternalContext().getRequestPathInfo();
		final var requestContextPath = context.getExternalContext().getRequestContextPath();
		final var depth = getDepth(requestPathInfo);
		logger.fine("depth: [" + depth + "]");
		logger.fine("applicationContextPath: [" + applicationContextPath + "]");
		final var applicationContextPathWithSlash = (applicationContextPath != null? applicationContextPath + "/": null);

		if ((applicationContextPathWithSlash != null) && (result.indexOf(applicationContextPathWithSlash) == 0)) {
			result.delete(0, applicationContextPathWithSlash.length());
			logger.fine("\tconverted to: [" + result + "]");
		}

		logger.fine("requestPathInfo: [" + requestPathInfo + "]");
		logger.fine("requestServletPath: [" + requestServletPath + "]");
		logger.fine("requestContextPath: [" + requestContextPath + "]");

		final var prefix = (
			requestServletPath.startsWith("/")?
			requestServletPath.substring(1):
			requestServletPath
		) + "/";

		logger.fine("result: [" + result + "]");
		logger.fine("prefix: [" + prefix + "]");

		if (result.indexOf(prefix) == 0) {

			result.delete(0, prefix.length());

			for (int i = 1; i < depth; i++) {
				result.insert(0, "../");
			}
		}

		logger.fine("converted to: [" + result + "]");

		if (includeViewParams) {

			try {
				final var builder = UriBuilder.fromUri(result.toString());

				final var vdl = context.getApplication().getViewHandler().getViewDeclarationLanguage(context, viewId);

				logger.fine("\tvdl: " + vdl);

				if (vdl != null) {

					final var metadata = vdl.getViewMetadata(context, viewId);
					logger.fine("\tmetadata: " + metadata);

					if (metadata != null) {

						final var params = metadata.getViewParameters(context.getViewRoot());
						logger.fine("\tparams: " + params);

						if (!params.isEmpty()) {
							try {
								for (var param: params) {
									final var value = param.getValue();
									logger.fine("\t\tvalue(" + param.getName() + "): [" + value + "]");
									if (value != null)
										builder.queryParam(param.getName(), value.toString());
								}
							} catch (Exception exception) {
								throw new RuntimeException("Unexpected IOException", exception);
							}
						}
					}

				}

				final var uri = builder.build();

				logger.info("built uri: [" + uri + "]");

				return uri.toString();

			} catch (java.lang.Exception exception) {
				logger.warning("ignoring " + exception);
			}
		}

		return result.toString();
	}


	protected int getDepth(final String value) {

		var result = 0;

		if (value == null)
			return result;

		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) == '/')
				result++;
			// XXX check if needed
			if (value.charAt(i) == '?')
				break;
		}

		return result;
	}

}


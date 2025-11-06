
package com.antonyudin.templates.jsf.first;


import java.io.Serializable;

import java.util.logging.Logger;

import jakarta.inject.Named;

import jakarta.enterprise.context.SessionScoped;


@SessionScoped
@Named
public class Menu implements Serializable {

	private final static Logger logger = Logger.getLogger(Menu.class.getName());


	private boolean open = true;

	public boolean isOpen() {
		return open;
	}

	public void setOpen(final boolean value) {
		open = value;
	}

	public void toggleOpen() {
		setOpen(!isOpen());
	}

}


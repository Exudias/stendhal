/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalUI;

import java.util.List;

import marauroa.common.game.RPObject;

public class Sign extends Entity {
	/**
	 * Text property.
	 */
	public final static Object	PROP_TEXT		= new Object();

	/**
	 * The sign text.
	 */
	private String text;


	//
	// Sign
	//

	/**
	 * Get the sign text.
	 *
	 * @return	The sign text.
	 */
	public String getText() {
		return text;
	}


	@Override
	public ActionType defaultAction() {
		return ActionType.READ;
	}


	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		if (changes.has("text")) {
			text = changes.get("text");
			fireChange(PROP_TEXT);
		}
	}


	/**
	 * The object removed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		super.onChangedRemoved(object, changes);

		if (changes.has("text")) {
			text = "";
			fireChange(PROP_TEXT);
		}
	}
}

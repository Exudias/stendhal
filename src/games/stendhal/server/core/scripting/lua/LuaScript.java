/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting.lua;

import java.util.List;

import org.apache.log4j.Logger;
import org.luaj.vm2.LuaValue;

import games.stendhal.server.core.scripting.ScriptInLua;
import games.stendhal.server.core.scripting.ScriptingSandbox;
import games.stendhal.server.entity.player.Player;


/**
 * Lua script representation.
 */
public class LuaScript extends ScriptingSandbox {

	private static final Logger logger = Logger.getLogger(LuaScript.class);


	public LuaScript(final String filename) {
		super(filename);
	}

	@Override
	public boolean load(final Player player, final List<String> args) {
		final LuaValue result = ScriptInLua.get().getGlobals().loadfile(filename).call();
		if (result.isint()) {
			return result.toint() == 0;
		} else if (result.isboolean()) {
			return result.toboolean();
		}

		logger.warn("Lua script return non-zero or \"false\": " + filename);
		return false;
	}
}
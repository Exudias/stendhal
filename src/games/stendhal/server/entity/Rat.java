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
package games.stendhal.server.entity;

import marauroa.common.*;
import marauroa.common.game.*;
import marauroa.server.game.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

import games.stendhal.common.*;
import games.stendhal.server.*;

public class Rat extends Creature
  {
  final private double SPEED=0.25;

  final private static int HP=10;
  final private static int ATK=3;
  final private static int DEF=2;
  final private static int XP=100;//getInitialXP(ATK,DEF,HP);

  public static void generateRPClass()
    {
    try
      {
      RPClass rat=new RPClass("rat");
      rat.isA("npc");
      }
    catch(RPClass.SyntaxException e)
      {
      Logger.thrown("Rat::generateRPClass","X",e);
      }
    }

  public Rat() throws AttributeNotFoundException
    {
    super();
    put("type","rat");
    put("x",0);
    put("y",0);

    setATK(ATK);
    setDEF(DEF);
    setXP(XP);
    setbaseHP(HP);
    setLevel(Level.getLevel(getXP()));
    stop();

    Logger.trace("Rat::Rat","D","Created Rat: "+this.toString());
    }

  public void getArea(Rectangle2D rect, double x, double y)
    {
    rect.setRect(x,y,1,1);
    }

  public double getSpeed()
    {
    return SPEED;
    }
  }

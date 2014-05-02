/**
 * This file is part of JEMMA - http://jemma.energy-home.org
 * (C) Copyright 2014 Telecom Italia (http://www.telecomitalia.it)
 *
 * JEMMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) version 3
 * or later as published by the Free Software Foundation, which accompanies
 * this distribution and is available at http://www.gnu.org/licenses/lgpl.html
 *
 * JEMMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License (LGPL) for more details.
 *
 */
package org.energy_home.jemma.ah.zigbee.zcl.generator;

public class Converter {
	public static int parseInteger(String v) {
		if (v.startsWith("0x")) {
			return Integer.parseInt(v.substring(2), 16);
		}
		if (v.startsWith("0b")) {
			return Integer.parseInt(v.substring(2), 2);
		}
		else {
			return Integer.parseInt(v);
		}
	}

	public static boolean parseBoolean(String value) {
		if (value.equals("true")) {
			return true;
		}
		else {
			return false;
		}
	}
}

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

public class ZclField {

	private String name;
	private String type;
	private boolean isArray = false;

	public ZclField(String name, String type) {
		this.name = name;
		if (isArray(type)) {
			this.type = getArrayType(type);
			isArray = true;
		}
		else {
			this.type = type;
		}
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isArray() {
		return isArray;
	}

	private boolean isArray(String type) {
		if (type.endsWith("[]")) {
			return true;
		}
		return false;
	}

	private String getArrayType(String type) {
		if (isArray(type)) {
			String scalarType;
			// is an array so remove [] and check for base type
			scalarType = type.replace("[", "");
			scalarType = scalarType.replace("]", "");
			return scalarType;
		}

		return type;
	}
}

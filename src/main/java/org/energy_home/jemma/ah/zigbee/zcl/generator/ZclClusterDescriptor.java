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

import java.util.Vector;

public class ZclClusterDescriptor {

	private String name;
	private int id;
	private Vector zclServerCommandsDescriptors = new Vector();
	private Vector zclClientCommandsDescriptors = new Vector();

	private Vector zclServerAttributesDescriptors = new Vector();
	private Vector zclClientAttributesDescriptors = new Vector();
	private String category = null;

	public ZclClusterDescriptor(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public ZclClusterDescriptor(String name, int id, String category) {
		this.name = name;
		this.id = id;
		this.category = category;
	}

	public void addServerCommandDescriptor(ZclCommandDescriptor zclCommandDescriptor) {
		zclServerCommandsDescriptors.add(zclCommandDescriptor);
	}

	public void addClientCommandDescriptor(ZclCommandDescriptor zclCommandDescriptor) {
		zclClientCommandsDescriptors.add(zclCommandDescriptor);
	}

	public void addServerAttributeDescriptor(ZclAttributeDescr zclAttributeDescriptor) {
		zclServerAttributesDescriptors.add(zclAttributeDescriptor);
	}

	public void addClientAttributeDescriptor(ZclAttributeDescr zclAttributeDescriptor) {
		zclClientAttributesDescriptors.add(zclAttributeDescriptor);
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public Vector getZclServerCommandsDescriptors() {
		return zclServerCommandsDescriptors;
	}

	public Vector getZclClientCommandsDescriptors() {
		return zclClientCommandsDescriptors;
	}

	public Vector getZclServerAttributesDescriptors() {
		return zclServerAttributesDescriptors;
	}

	public Vector getZclClientAttributesDescriptors() {
		return zclClientAttributesDescriptors;
	}

	public String getPackageName() {
		return category;
	}
}

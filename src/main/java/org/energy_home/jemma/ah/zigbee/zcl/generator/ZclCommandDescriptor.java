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

public class ZclCommandDescriptor {

	private int id;
	private String name;
	private byte frameType;
	private boolean manufactureSpecific;
	private byte direction;
	private short manufacturerCode;
	private Vector fields = new Vector();
	private String responseName;
	private boolean isResponse;
	private ZclCommandDescriptor response;

	public ZclCommandDescriptor(int id, String name, byte frameType, boolean manufacturerSpecific, byte direction,
			short manufacturerCode) {
		this.id = id;
		this.name = name;
		this.frameType = frameType;
		this.manufactureSpecific = manufacturerSpecific;
		this.direction = direction;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public byte getFrameType() {
		return frameType;
	}

	public boolean isManufactureSpecific() {
		return manufactureSpecific;
	}

	public byte getDirection() {
		return direction;
	}

	public short getManufactureCode() {
		return manufacturerCode;
	}

	public void add(ZclField zclField) {
		fields.add(zclField);
	}

	public Vector getParametersDescriptors() {
		return fields;
	}

	public void setResponseName(String response) {
		this.responseName = response;
	}

	public void isResponse(boolean isResponse) {
		this.isResponse = isResponse;
	}

	public boolean isResponse() {
		return isResponse;
	}

	public String getResponseName() {
		return responseName;
	}

	public void setResponse(ZclCommandDescriptor response) {
		this.response = response;
		this.responseName = null;
	}

	public ZclCommandDescriptor getResponse() {
		return response;
	}
}

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

public class ZclAttributeDescr {

	private int id;
	private String name;
	private int access = ACCESS_RO;
	private String def;
	private boolean mandatory;
	private double factor = 1.0;
	private byte type = (byte) 0xff;
	private String uom;
	private String typeName;
	private boolean reporable = true;
	private int size = 0;
	private boolean reportable = true;

	final static int ACCESS_RW = 0;
	final static int ACCESS_RO = 1;

	public ZclAttributeDescr(int id, String name, String type_name, int size, String access, String def, boolean mandatory,
			double factor, String uom, boolean reportable) throws Exception {
		this.id = id;
		this.name = name;
		this.def = def;
		this.factor = factor;
		this.uom = uom;
		this.typeName = type_name;
		this.size = size;
		this.mandatory = mandatory;
		this.reportable = reportable;

		if (access.equals("r")) {
			this.access = ACCESS_RO;
		}
		else if (access.equals("rw")) {
			this.access = ACCESS_RW;
		}
		else
			throw new Exception("access must be rw or ro");

		int t = 0xff;

		if (type_name.equals("data8")) {
			t = 0x08;
		} else if (type_name.equals("data16")) {
			t = 0x09;
		} else if (type_name.equals("data24")) {
			t = 0x0a;
		} else if (type_name.equals("data32")) {
			t = 0x0b;
		} else if (type_name.equals("boolean")) {
			t = 0x10;
		} else if (type_name.equals("bitmap8")) {
			t = 0x18;
		} else if (type_name.equals("bitmap16")) {
			t = 0x19;
		} else if (type_name.equals("bitmap24")) {
			t = 0x1a;
		} else if (type_name.equals("bitmap32")) {
			t = 0x1b;
		} else if (type_name.equals("uint8")) {
			t = 0x20;
		} else if (type_name.equals("uint16")) {
			t = 0x21;
		} else if (type_name.equals("uint24")) {
			t = 0x22;
		} else if (type_name.equals("uint32")) {
			t = 0x23;
		} else if (type_name.equals("uint48")) {
			// FIXME: return avalue
		} else if (type_name.equals("int8")) {
			t = 0x28;
		} else if (type_name.equals("int16")) {
			t = 0x29;
		} else if (type_name.equals("int24")) {
			t = 0x2a;
		} else if (type_name.equals("int32")) {
			t = 0x2b;
		} else if (type_name.equals("enum8")) {
			t = 0x30;
		} else if (type_name.equals("enum16")) {
			t = 0x31;
		}
		else if (type_name.equals("float2")) {
			t = 0x38;
		} else if (type_name.equals("float4")) {
			t = 0x39;
		} else if (type_name.equals("float8")) {
			t = 0x3a;
		} else if (type_name.equals("octects")) {
			t = 0x41;
		} else if (type_name.equals("string")) {
			t = 0x42;
		} else if (type_name.equals("time")) {
			t = 0xe0;
		} else if (type_name.equals("date")) {
			t = 0xe1;
		} else if (type_name.equals("cluster")) {
			t = 0xe8;
		} else if (type_name.equals("attribute")) {
			t = 0xe9;
		} else if (type_name.equals("bacnetid")) {
			t = 0xea;
		} else if (type_name.equals("ieee")) {
			t = 0xf0;
		}

		type = (byte) t;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public byte getType() {
		return type;
	}

	public int getSize() {
		return size;
	}

	public String getTypeName() {
		return typeName;
	}

	public boolean hasVariableSize() {
		return (size > 0);
	}

	public int getAccess() {
		return access;
	}

	public String getDef() {
		return def;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public double getFactor() {
		return factor;
	}

	public String getUom() {
		return uom;
	}

	public void isReportable(boolean reportable) {
		this.reporable = reportable;
	}

	public boolean isReportable() {
		return reporable;
	}
}

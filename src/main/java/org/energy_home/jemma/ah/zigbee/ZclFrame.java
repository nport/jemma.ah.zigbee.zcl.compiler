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
package org.energy_home.jemma.ah.zigbee;

public class ZclFrame implements IZclFrame {

	public void disableDefaultResponse(boolean disableDefaultResponse) {
		// TODO Auto-generated method stub

	}

	public byte getFrameControlField() {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte getSequenceNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getManufacturerCode() throws ZigBeeException {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte[] getData() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDefaultResponseDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isManufacturerSpecific() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isClientToServer() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isServerToClient() {
		// TODO Auto-generated method stub
		return false;
	}

	public byte getDirection() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setDirection(byte direction) {
		// TODO Auto-generated method stub

	}

	public void setSequence(int sequence) {
		// TODO Auto-generated method stub

	}

	public void setFrameType(byte generalCommand) {
		// TODO Auto-generated method stub

	}

	public IZclFrame createResponseFrame(int payloadSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public void appendUInt8(int uint8) {
		// TODO Auto-generated method stub

	}

	public void appendUInt16(int uint16) {
		// TODO Auto-generated method stub

	}

	public void appendUInt24(int uint24) {
		// TODO Auto-generated method stub

	}

	public void appendUInt32(long uint32) {
		// TODO Auto-generated method stub

	}

	public void appendInt8(short uint8) {
		// TODO Auto-generated method stub

	}

	public void appendInt16(int uint16) {
		// TODO Auto-generated method stub

	}

	public void appendInt24(int uint24) {
		// TODO Auto-generated method stub

	}

	public void appendInt32(long uint32) {
		// TODO Auto-generated method stub

	}

	public void appendBoolean(boolean value) {
		// TODO Auto-generated method stub

	}

	public short parseUInt8() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int parseUInt16() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int parseUInt24() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long parseUInt32() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean parseBoolean() {
		// TODO Auto-generated method stub
		return false;
	}

	public byte[] parseOctets() {
		// TODO Auto-generated method stub
		return null;
	}

	public String parseString() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] parseArray(int len) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCommandId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void appendOctets(byte[] value) {
		// TODO Auto-generated method stub

	}

	public void appendString(String value) {
		// TODO Auto-generated method stub

	}

	public void appendArray(byte[] array) {
		// TODO Auto-generated method stub

	}

	public long parseUTCTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void appendUTCTime(long value) {
		// TODO Auto-generated method stub

	}

	public long parseUInt48() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void appendUInt48(long value) {
		// TODO Auto-generated method stub

	}

	public void setCommandId(int commandId) {
		// TODO Auto-generated method stub

	}

	public byte getFrameType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public short getPayloadSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void shrink() {
		// TODO Auto-generated method stub

	}

	public byte[] parseArray(int byteArrayLength, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	public void appendArray(byte[] ieeeAddress, boolean b) {
		// TODO Auto-generated method stub

	}

}

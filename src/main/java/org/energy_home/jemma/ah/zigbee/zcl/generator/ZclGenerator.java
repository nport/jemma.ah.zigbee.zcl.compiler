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

import gnu.getopt.Getopt;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.energy_home.jemma.ah.hac.ApplianceException;
import org.energy_home.jemma.ah.hac.ApplianceValidationException;
import org.energy_home.jemma.ah.hac.IEndPointRequestContext;
import org.energy_home.jemma.ah.hac.IServiceCluster;
import org.energy_home.jemma.ah.hac.ServiceClusterException;
import org.energy_home.jemma.ah.hac.UnsupportedClusterAttributeException;
import org.energy_home.jemma.ah.hac.lib.ServiceCluster;
import org.energy_home.jemma.ah.internal.zigbee.ZclAttributeDescriptor;
import org.energy_home.jemma.ah.zigbee.IZclFrame;
import org.energy_home.jemma.ah.zigbee.ZCL;
import org.energy_home.jemma.ah.zigbee.ZclFrame;
import org.energy_home.jemma.ah.zigbee.ZigBeeDevice;
import org.energy_home.jemma.ah.zigbee.ZigBeeDeviceListener;
import org.energy_home.jemma.ah.zigbee.ZigBeeException;
import org.energy_home.jemma.ah.zigbee.zcl.IZclAttributeDescriptor;
import org.energy_home.jemma.ah.zigbee.zcl.ZclValidationException;
import org.energy_home.jemma.ah.zigbee.zcl.lib.ZclServiceCluster;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeBitmap16;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeBitmap24;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeBitmap32;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeBitmap8;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeBoolean;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeEnum16;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeEnum8;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeI16;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeI24;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeI32;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeI8;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeIEEEAddress;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeOctets;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeString;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeUI16;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeUI24;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeUI32;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeUI48;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeUI8;
import org.energy_home.jemma.ah.zigbee.zcl.lib.types.ZclDataTypeUTCTime;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JSwitch;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * <ul>
 * <li>TODO set manufacturer id field and check if the response matches</li>
 * <li>TODO better organize the exception. How to map ZigBee status codes into
 * ApplianceException?</li>
 * <li>TODO use constants in exception messages</li>
 * </ul>
 */
public class ZclGenerator {

	public final static String OPT_INTERFACES_PACKAGE_NAME = "it.telecomitalia.ah.zigbee.zcl.generator.interfaces.package";
	public final static String OPT_ZCL_CLASSES_PACKAGE_NAME = "it.telecomitalia.ah.zigbee.zcl.generator.classes.package";
	public final static String OPT_OUTPUT_DIR = "it.telecomitalia.ah.zigbee.zcl.generator.outdir";
	public final static String OPT_INPUT_XML = "it.telecomitalia.ah.zigbee.zcl.generator.xml";
	public final static String OPT_CLUSTER_NAME = "it.telecomitalia.ah.zigbee.zcl.generator.clusters";
	public final static String OPT_GENERATE_PROXY_CLASSES = "it.telecomitalia.ah.zigbee.zcl.generator.generate_proxies";

	private String inputFilename;
	protected String outputPath = null;
	private static ZclGenerator zclGenerator;
	private static String version = "2.0.13-SNAPSHOT";

	private JCodeModel jModel = null;
	private boolean insideServerCommands = false;
	
	private String generatedBy = "This class has been generated by the Jemma ZigBee Cluster Library\ngenerator, version " + version;
	
	private String copyrightClass = 
			 "This file is part of JEMMA - http://jemma.energy-home.org\n" +
			 "(C) Copyright 2014 Telecom Italia (http://www.telecomitalia.it)\n" +
			 "\n" +
			 "JEMMA is free software: you can redistribute it and/or modify\n" +
			 "it under the terms of the GNU Lesser General Public License (LGPL) version 3\n" +
			 "or later as published by the Free Software Foundation, which accompanies\n" +
			 "this distribution and is available at http://www.gnu.org/licenses/lgpl.html\n" +
			 "\n" +
			 "JEMMA is distributed in the hope that it will be useful,\n" +
			 "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
			 "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
			 "GNU Lesser General Public License (LGPL) for more details.\n\n" + 
			 "This class has been generated by the Jemma ZigBee Cluster Library\ngenerator, version " + version;
	
	private String copyrightInterface = 
			 "This file is part of JEMMA - http://jemma.energy-home.org\n" +
			 "(C) Copyright 2014 Telecom Italia (http://www.telecomitalia.it)\n" +
			 "\n" +
			 "JEMMA is free software: you can redistribute it and/or modify\n" +
			 "it under the terms of the GNU Lesser General Public License (LGPL) version 3\n" +
			 "or later as published by the Free Software Foundation, which accompanies\n" +
			 "this distribution and is available at http://www.gnu.org/licenses/lgpl.html\n" +
			 "\n" +
			 "JEMMA is distributed in the hope that it will be useful,\n" +
			 "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
			 "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
			 "GNU Lesser General Public License (LGPL) for more details.\n\n" + 
			 "This interface has been generated by the Jemma ZigBee Cluster Library\ngenerator, version " + version;
			 
	Vector zclCommandFields = new Vector();
	private ZclClusterDescriptor zclClusterDescriptor;

	private Vector zclClusters = new Vector();

	JClass zbDeviceClass = null;
	JClass jClassIterator = null;

	private boolean addJavadoc = true;
	
	ZclCommandDescriptor zclCommandDescriptor = null;

	private Dictionary zclResponsesServerSide = new Hashtable();
	private Dictionary zclResponsesClientSide = new Hashtable();

	private String zclRootPackage = "test.clusters";
	private String interfacesPackageName = "test.clusters";
	private int mode;
	private boolean generateConnectionClass = false;
	private boolean generateProxyClasses = false;

	private boolean contextLast = true;
	private boolean confirmationRequiredField = false;

	JClass jZclValidationException = null;
	JClass jIZclAttributeDescription = null;
	private boolean checkIfAttached = false;

	// set true if you want to add the NAME constant in the interface
	private boolean addClusterName = false;

	// set to true if the cluster interface must extend the IServiceCluster
	// interface. This was an old method!!!
	private boolean ifExtendsIServiceCluster = false;
	private boolean manageAttributeReportings = true;

	// set to true (deprecated!!!) if you want that the generated classes have
	// the zclAttach and zclDetach methods.
	private boolean generateAttachDetach = false;
	private boolean optimizeForSize = true;
	private boolean generateCacheAccess = true;
	private boolean jre15 = false; // set to false if the generated java code
	private boolean generateRecords = false;

	// Decide whether or not to use the default request context.
	// If set to false use "null".
	private boolean useDefaultRequestContext = true;

	// If a command has no response (i.e. empty response) just consume the
	// command even if no stub implementation is present.
	private boolean consumeCommandsWithoutResponse = true;

	// must compile on JRE < 1.5
	public static void main(String args[]) {
		int c;
		int mode = 0;

		String inputFilename = null;

		// LongOpt[] longopts = new LongOpt[1];
		//
		// StringBuffer sb = new StringBuffer();
		// longopts[0] = new LongOpt("s", LongOpt.REQUIRED_ARGUMENT, null, 'z');

		Getopt g = new Getopt("parser", args, "vhm:c:o:s:p:r:x:i:z:");

		g.setOpterr(false);
		Hashtable params = new Hashtable();

		while ((c = g.getopt()) != -1) {
			switch (c) {

			case 'c':
				List clusters = new ArrayList();
				clusters.add(g.getOptarg());
				params.put(OPT_CLUSTER_NAME, clusters);
				break;

			case 'o':
				String outdir = g.getOptarg();
				params.put(OPT_OUTPUT_DIR, outdir);
				break;

			case 'r':
				params.put("it.telecomitalia.ah.zigbee.zcl.generator.generateRecords", Boolean.TRUE);
				break;

			case 'm':
				String modeName = g.getOptarg();
				if (modeName.equals("common")) {
					mode = 0;
				} else if (modeName.equals("zcl")) {
					mode = 1;
				} else {
					printUsage();
				}
				break;

			case 'i':
				String interfacesPackageName = g.getOptarg();
				params.put(OPT_INTERFACES_PACKAGE_NAME, interfacesPackageName);
				break;

			case 'z':
				String classesPackageName = g.getOptarg();
				params.put(OPT_ZCL_CLASSES_PACKAGE_NAME, classesPackageName);
				break;

			case 'x':
				params.put(OPT_GENERATE_PROXY_CLASSES, Boolean.TRUE);
				break;

			case 's':
				String xml = g.getOptarg();
				inputFilename = xml;
				break;

			case 'v':
				logInfo("version: " + version);
				return;

			case 'h':
				printUsage();
				return;

			default:
				System.out.println("getopt() returned " + c);
				break;
			}
		}

		zclGenerator = new ZclGenerator();

		try {
			zclGenerator.setInputFilename(inputFilename);
			zclGenerator.parse();
			zclGenerator.generate(mode, params);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	List clusters;

	public void setInputFilename(String inputFilename) {
		this.inputFilename = inputFilename;
	}

	public String getInputFilename() {
		return this.inputFilename;
	}

	protected void cleanDataStructures() {
		zclClusters.clear();
	}

	/**
	 * Force parsing the InputFilename
	 */
	public void parse() {
		cleanDataStructures();
		Document doc = DOMUtil.parse(this.inputFilename);
		try {
			loadXml(doc);
		} catch (Exception e) {
			System.err.println("exception while generating source: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public List getClusterList() {
		if (this.zclClusters != null) {
			ArrayList clusterList = new ArrayList();
			for (Iterator iterator = this.zclClusters.iterator(); iterator.hasNext();) {
				ZclClusterDescriptor type = (ZclClusterDescriptor) iterator.next();
				clusterList.add(type.getName());
			}
			return clusterList;
		}
		return null;
	}

	public void generate(int mode, Dictionary props) throws Exception {
		zclRootPackage = (String) props.get(OPT_ZCL_CLASSES_PACKAGE_NAME);
		interfacesPackageName = (String) props.get(OPT_INTERFACES_PACKAGE_NAME);
		outputPath = (String) props.get(OPT_OUTPUT_DIR);
		clusters = (List) props.get(OPT_CLUSTER_NAME);
		Boolean generateRecords = (Boolean) props.get("it.telecomitalia.ah.zigbee.zcl.generator.generateRecords");
		Boolean generateProxyClasses = (Boolean) props.get(OPT_GENERATE_PROXY_CLASSES);

		if (generateRecords != null) {
			this.generateRecords = generateRecords.booleanValue();
		}

		if (generateProxyClasses != null) {
			this.generateProxyClasses = generateProxyClasses.booleanValue();
		}

		if (this.zclClusters == null) {
			throw new Exception("no parsed Cluster to generate. Call parse() first");
		}

		this.mode = mode;
		jModel = new JCodeModel();

		// commonly referenced classes
		zbDeviceClass = jModel.ref(ZigBeeDevice.class);
		jZclValidationException = jModel.ref(ZclValidationException.class);
		jIZclAttributeDescription = jModel.ref(IZclAttributeDescriptor.class);

		try {
			buildCodeModel(zclClusters);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (jModel != null) {
			// generates the code of the previous cluster
			dumpCodeModel(jModel);
		}
	}

	private void buildCodeModel(Vector zclClusters) {
		for (int i = 0; i < zclClusters.size(); i++) {
			ZclClusterDescriptor zclClusterDescriptor = (ZclClusterDescriptor) zclClusters.get(i);

			if (clusters != null && clusters.size() != 0 && !clusters.contains(zclClusterDescriptor.getName()))
				continue;

			try {
				if (mode == 0)
					generateClusterSource(jModel, zclClusterDescriptor);
				else if (mode == 1)
					generateZclClusterSource(jModel, zclClusterDescriptor);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Generates the cluster sourcecode for appliances
	 * 
	 * @param object
	 * @throws Exception
	 */
	private void generateClusterSource(JCodeModel jModel, ZclClusterDescriptor zclClusterDescriptor) throws Exception {

		/* Define package */
		String packageName = getInterfaceClassesPackageName(zclClusterDescriptor);

		JPackage jPackage = jModel._package(packageName);

		generateClusterAssets(jModel, jPackage, zclClusterDescriptor, false);
		generateClusterAssets(jModel, jPackage, zclClusterDescriptor, true);
	}

	/**
	 * Generates the cluster sourcecode for ZigBee appliances
	 * 
	 * @param object
	 * @throws Exception
	 */
	private void generateZclClusterSource(JCodeModel jModel, ZclClusterDescriptor zclClusterDescriptor) throws Exception {

		/* Define package */
		String packageName = getZclClassesPackageName(zclClusterDescriptor);

		JPackage jPackage = jModel._package(packageName);
		generateZclClusterAssets(jModel, jPackage, zclClusterDescriptor, true);
		generateZclClusterAssets(jModel, jPackage, zclClusterDescriptor, false);
	}

	/**
	 * Generates any source file related to the server side of a Cluster.
	 * 
	 * @param jModel
	 * @param jPackage
	 * @param zclClusterDescriptor
	 * @throws Exception
	 */

	private void generateClusterAssets(JCodeModel jModel, JPackage jPackage, ZclClusterDescriptor zclClusterDescriptor,
			boolean isServer) throws Exception {
		JDefinedClass jClusterConnectionClass = null;
		
		if (generateConnectionClass)
			jClusterConnectionClass = jPackage._class(getClusterImplementationName(zclClusterDescriptor, isServer));

		JDefinedClass jClusterInterface = jPackage._interface(getClusterInterfaceName(zclClusterDescriptor, isServer));

		if (ifExtendsIServiceCluster)
			jClusterInterface._extends(IServiceCluster.class);

		if (jClusterConnectionClass != null)
			jClusterConnectionClass._extends(IServiceCluster.class);

		if (addJavadoc) {
			if (jClusterConnectionClass != null)
				jClusterConnectionClass.javadoc().add(copyrightClass);

			jClusterInterface.javadoc().add(copyrightInterface);
		}

		//
		// generates the interface part
		//

		// generates the interface constants
		String clusterFullname = getClusterFullname(zclClusterDescriptor);

		if (addClusterName) {
			JFieldVar jFieldClusterName = jClusterInterface.field(JMod.STATIC | JMod.FINAL, String.class, "NAME");

			if (isServer) {
				// TODO: read the name from other variables
				jFieldClusterName.init(JExpr.lit(clusterFullname + "Server"));
			} else {
				// TODO: read the name from other variables
				jFieldClusterName.init(JExpr.lit(clusterFullname + "Client"));
			}
		}

		// generates attribute getters and setters
		Vector zclAttributeDescriptors = null;
		Vector zclCommandsDescriptors = null;
		if (isServer) {
			zclAttributeDescriptors = zclClusterDescriptor.getZclServerAttributesDescriptors();
			zclCommandsDescriptors = zclClusterDescriptor.getZclServerCommandsDescriptors();
		} else {
			zclAttributeDescriptors = zclClusterDescriptor.getZclClientAttributesDescriptors();
			zclCommandsDescriptors = zclClusterDescriptor.getZclClientCommandsDescriptors();
		}

		JDefinedClass jClusterProxyClass = null;
		JFieldVar jFieldProxy = null;

		if (generateProxyClasses) {
			int commands = 0;
			for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
				ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
				if (zclCommandDescriptor.isResponse())
					continue;
				commands++;
			}

			if ((zclAttributeDescriptors.size() + commands) > 0) {
				// The proxy class is generated only if the cluster has at least
				// one command or attribute.
				jClusterProxyClass = jPackage._class(getClusterProxyName(zclClusterDescriptor, isServer));
				jClusterProxyClass._extends(ServiceCluster.class);
				jClusterProxyClass._implements(jClusterInterface);
			}
		}

		// generates implementation of the proxy class
		if (jClusterProxyClass != null) {

			jFieldProxy = jClusterProxyClass.field(JMod.PRIVATE, jClusterInterface, "o");
			jFieldProxy.init(JExpr._null());

			JMethod constructor = jClusterProxyClass.constructor(JMod.PUBLIC);
			JVar o = constructor.param(jClusterInterface, "o");
			constructor.body().invoke("super");
			constructor.body()._if(o.eq(JExpr._null()))._then()
					._throw(JExpr._new(jModel.ref(ApplianceValidationException.class)).arg("Invalid cluster implementation"));
			constructor.body().assign(JExpr._this().ref(jFieldProxy), o);
			constructor._throws(ApplianceException.class);
		}

		// generates the attributes and command names as constants
		for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
			ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);
			String attributeName = zclAttributeDescriptor.getName();

			JFieldVar jField = jClusterInterface.field(JMod.STATIC | JMod.FINAL, String.class,
					getAttributeNameConstant(attributeName));
			jField.init(JExpr.lit(attributeName));
		}

		for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
			if (zclCommandDescriptor.isResponse())
				continue;

			String commandName = zclCommandDescriptor.getName();
			JFieldVar jField = jClusterInterface.field(JMod.STATIC | JMod.FINAL, String.class, getCommandNameConstant(commandName));
			jField.init(JExpr.lit(commandName));
		}

		for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
			ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);
			generateAttributeGetterSignature(jModel, jClusterInterface, zclAttributeDescriptor, true);

			if (jClusterProxyClass != null) {
				// delegation method implementation in the Proxy class
				JMethod jProxyGetterMethod = generateAttributeGetterSignature(jModel, jClusterProxyClass, zclAttributeDescriptor,
						true);

				JInvocation a = jFieldProxy.invoke(jProxyGetterMethod.name());
				JVar[] params = jProxyGetterMethod.listParams();

				for (int j = 0; j < params.length; j++) {
					a.arg(params[j]);
				}

				jProxyGetterMethod.body()._return(a);
			}

			if (zclAttributeDescriptor.getAccess() == ZclAttributeDescr.ACCESS_RW) {
				generateAttributeSetterSignature(jModel, jClusterInterface, zclAttributeDescriptor, true);

				if (jClusterProxyClass != null) {
					JMethod jProxySetterMethod = generateAttributeSetterSignature(jModel, jClusterProxyClass,
							zclAttributeDescriptor, true);

					JInvocation a = jFieldProxy.invoke(jProxySetterMethod.name());
					JVar[] params = jProxySetterMethod.listParams();

					for (int j = 0; j < params.length; j++) {
						a.arg(params[j]);
					}
				}
			}
		}

		// generates command signatures
		for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
			generateCommandSignature(jClusterInterface, zclCommandDescriptor, true);

			if (jClusterProxyClass != null) {
				JMethod jProxyCommandMethod = generateCommandSignature(jClusterProxyClass, zclCommandDescriptor, true);
				if (jProxyCommandMethod == null)
					continue;

				JInvocation a = jFieldProxy.invoke(jProxyCommandMethod.name());
				JVar[] params = jProxyCommandMethod.listParams();

				for (int j = 0; j < params.length; j++) {
					a.arg(params[j]);
				}

				System.out.println(jProxyCommandMethod.type().name());

				if (jProxyCommandMethod.type() == jModel.VOID)
					jProxyCommandMethod.body().add(a);
				else
					jProxyCommandMethod.body()._return(a);
			}
		}

		//
		// generates the implementation part
		//

		// generates constructor
		if (jClusterConnectionClass != null) {
			JMethod constructor = jClusterConnectionClass.constructor(JMod.PUBLIC);
			// constructor.param(IManagedApplianceConnection.class,
			// "applianceConnection");
			constructor.body().directStatement(
					"super(applianceConnection, " + getClusterInterfaceName(zclClusterDescriptor, true) + ".NAME);");

			for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
				ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
				generateCommandImplementation(jClusterConnectionClass, jClusterInterface, zclCommandDescriptor);
			}

			for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
				ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);

				generateAttributeGetterImplementation(jModel, jClusterConnectionClass, jClusterInterface, zclAttributeDescriptor);

				if (zclAttributeDescriptor.getAccess() == ZclAttributeDescr.ACCESS_RW)
					generateAttributeSetterImplementation(jModel, jClusterConnectionClass, jClusterInterface,
							zclAttributeDescriptor);
			}
		}
	}

	/**
	 * Generates any source file related to the server side of a Cluster.
	 * 
	 * @param jModel
	 * @param jPackage
	 * @param zclClusterDescriptor
	 * @throws Exception
	 */

	private void generateServerClusterAssets(JCodeModel jModel, JPackage jPackage, ZclClusterDescriptor zclClusterDescriptor)
			throws Exception {
		JDefinedClass jClusterConnectionClass = null;

		if (generateConnectionClass)
			jClusterConnectionClass = jPackage._class(getClusterImplementationName(zclClusterDescriptor, true));

		JDefinedClass jClusterInterface = jPackage._interface(getClusterInterfaceName(zclClusterDescriptor, true));

		if (ifExtendsIServiceCluster)
			jClusterInterface._extends(IServiceCluster.class);

		if (jClusterConnectionClass != null)
			jClusterConnectionClass._extends(IServiceCluster.class);

		if (addJavadoc) {
			if (jClusterConnectionClass != null)
				jClusterConnectionClass.javadoc().add(copyrightClass);

			jClusterInterface.javadoc().add(copyrightInterface);
		}

		//
		// generates the interface part
		//

		// generates the interface constants
		String clusterFullname = getClusterFullname(zclClusterDescriptor);

		if (addClusterName) {
			JFieldVar jFieldClusterName = jClusterInterface.field(JMod.STATIC | JMod.FINAL, String.class, "NAME");

			// TODO: read the name from other variables
			jFieldClusterName.init(JExpr.lit(clusterFullname + "Server"));
		}

		// generates attribute getters and setters
		Vector zclAttributeDescriptors = zclClusterDescriptor.getZclServerAttributesDescriptors();
		Vector zclCommandsDescriptors = zclClusterDescriptor.getZclServerCommandsDescriptors();

		JDefinedClass jClusterProxyClass = null;
		JFieldVar jFieldProxy = null;

		if (generateProxyClasses) {
			int commands = 0;
			for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
				ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
				if (zclCommandDescriptor.isResponse())
					continue;
				commands++;
			}

			if ((zclAttributeDescriptors.size() + commands) > 0) {
				// The proxy class is generated only if the cluster has at least
				// one command or attribute.
				jClusterProxyClass = jPackage._class(getClusterProxyName(zclClusterDescriptor, true));
				jClusterProxyClass._extends(ServiceCluster.class);
				jClusterProxyClass._implements(jClusterInterface);
			}
		}

		// generates implementation of the proxy class
		if (jClusterProxyClass != null) {

			jFieldProxy = jClusterProxyClass.field(JMod.PRIVATE, jClusterInterface, "o");
			jFieldProxy.init(JExpr._null());

			JMethod constructor = jClusterProxyClass.constructor(JMod.PUBLIC);
			JVar o = constructor.param(jClusterInterface, "o");
			constructor.body().invoke("super");
			constructor.body()._if(o.eq(JExpr._null()))._then()
					._throw(JExpr._new(jModel.ref(ApplianceValidationException.class)).arg("Invalid cluster implementation"));
			constructor.body().assign(JExpr._this().ref(jFieldProxy), o);
			constructor._throws(ApplianceException.class);
		}

		// generates the attributes and command names as constants
		for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
			ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);
			String attributeName = zclAttributeDescriptor.getName();

			JFieldVar jField = jClusterInterface.field(JMod.STATIC | JMod.FINAL, String.class,
					getAttributeNameConstant(attributeName));
			jField.init(JExpr.lit(attributeName));
		}

		for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
			if (zclCommandDescriptor.isResponse())
				continue;

			String commandName = zclCommandDescriptor.getName();
			JFieldVar jField = jClusterInterface.field(JMod.STATIC | JMod.FINAL, String.class, getCommandNameConstant(commandName));
			jField.init(JExpr.lit(commandName));
		}

		for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
			ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);
			generateAttributeGetterSignature(jModel, jClusterInterface, zclAttributeDescriptor, true);

			if (jClusterProxyClass != null) {
				// delegation method implementation in the Proxy class
				JMethod jProxyGetterMethod = generateAttributeGetterSignature(jModel, jClusterProxyClass, zclAttributeDescriptor,
						true);

				JInvocation a = jFieldProxy.invoke(jProxyGetterMethod.name());
				JVar[] params = jProxyGetterMethod.listParams();

				for (int j = 0; j < params.length; j++) {
					a.arg(params[j]);
				}

				jProxyGetterMethod.body()._return(a);
			}

			if (zclAttributeDescriptor.getAccess() == ZclAttributeDescr.ACCESS_RW) {
				generateAttributeSetterSignature(jModel, jClusterInterface, zclAttributeDescriptor, true);

				if (jClusterProxyClass != null) {
					JMethod jProxySetterMethod = generateAttributeSetterSignature(jModel, jClusterProxyClass,
							zclAttributeDescriptor, true);

					JInvocation a = jFieldProxy.invoke(jProxySetterMethod.name());
					JVar[] params = jProxySetterMethod.listParams();

					for (int j = 0; j < params.length; j++) {
						a.arg(params[j]);
					}
				}
			}
		}

		// generates command signatures
		for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
			generateCommandSignature(jClusterInterface, zclCommandDescriptor, true);

			if (jClusterProxyClass != null) {
				JMethod jProxyCommandMethod = generateCommandSignature(jClusterProxyClass, zclCommandDescriptor, true);
				if (jProxyCommandMethod == null)
					continue;

				JInvocation a = jFieldProxy.invoke(jProxyCommandMethod.name());
				JVar[] params = jProxyCommandMethod.listParams();

				for (int j = 0; j < params.length; j++) {
					a.arg(params[j]);
				}

				System.out.println(jProxyCommandMethod.type().name());

				if (jProxyCommandMethod.type() == jModel.VOID)
					jProxyCommandMethod.body().add(a);
				else
					jProxyCommandMethod.body()._return(a);
			}
		}

		//
		// generates the implementation part
		//

		// generates constructor
		if (jClusterConnectionClass != null) {
			JMethod constructor = jClusterConnectionClass.constructor(JMod.PUBLIC);
			// constructor.param(IManagedApplianceConnection.class,
			// "applianceConnection");
			constructor.body().directStatement(
					"super(applianceConnection, " + getClusterInterfaceName(zclClusterDescriptor, true) + ".NAME);");

			for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
				ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
				generateCommandImplementation(jClusterConnectionClass, jClusterInterface, zclCommandDescriptor);
			}

			for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
				ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);

				generateAttributeGetterImplementation(jModel, jClusterConnectionClass, jClusterInterface, zclAttributeDescriptor);

				if (zclAttributeDescriptor.getAccess() == ZclAttributeDescr.ACCESS_RW)
					generateAttributeSetterImplementation(jModel, jClusterConnectionClass, jClusterInterface,
							zclAttributeDescriptor);
			}
		}

	}

	/**
	 * Generates any source file related to the client side of a Cluster.
	 * 
	 * @param jModel
	 * @param jPackage
	 * @param zclClusterDescriptor
	 * @throws Exception
	 */

	private void generateClientClusterAssets(JCodeModel jModel, JPackage jPackage, ZclClusterDescriptor zclClusterDescriptor)
			throws Exception {

		JDefinedClass jClusterClass = null;

		if (generateConnectionClass)
			jClusterClass = jPackage._class(getClusterImplementationName(zclClusterDescriptor, false));
		JDefinedClass jClusterInterface = jPackage._interface(getClusterInterfaceName(zclClusterDescriptor, false));

		if (ifExtendsIServiceCluster)
			jClusterInterface._extends(IServiceCluster.class);

		if (addJavadoc) {
			jClusterClass.javadoc().add(copyrightClass);
			jClusterInterface.javadoc().add(copyrightInterface);
		}

		Vector zclAttributeDescriptors = zclClusterDescriptor.getZclClientAttributesDescriptors();
		Vector zclCommandsDescriptors = zclClusterDescriptor.getZclClientCommandsDescriptors();

		JDefinedClass jClusterProxyClass = null;
		JFieldVar jFieldProxy = null;

		if (generateProxyClasses) {
			int commands = 0;
			for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
				ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
				if (zclCommandDescriptor.isResponse())
					continue;
				commands++;
			}

			if ((zclAttributeDescriptors.size() + commands) > 0) {
				// The proxy class is generated only if the cluster has at least
				// one command or attribute.
				jClusterProxyClass = jPackage._class(getClusterProxyName(zclClusterDescriptor, false));
				jClusterProxyClass._extends(ServiceCluster.class);
				jClusterProxyClass._implements(jClusterInterface);
			}
		}

		// generates implementation of the proxy class
		if (jClusterProxyClass != null) {

			jFieldProxy = jClusterProxyClass.field(JMod.PRIVATE, jClusterInterface, "o");
			jFieldProxy.init(JExpr._null());

			JMethod constructor = jClusterProxyClass.constructor(JMod.PUBLIC);
			JVar o = constructor.param(jClusterInterface, "o");
			constructor.body().invoke("super");
			constructor.body()._if(o.eq(JExpr._null()))._then()
					._throw(JExpr._new(jModel.ref(ApplianceValidationException.class)).arg("Invalid cluster implementation"));
			constructor.body().assign(JExpr._this().ref(jFieldProxy), o);
			constructor._throws(ApplianceException.class);
		}

		//
		// generates the interface part
		//

		// generates the iterface constants
		String clusterFullname = getClusterFullname(zclClusterDescriptor);

		if (this.addClusterName) {
			JFieldVar jFieldClusterName = jClusterInterface.field(JMod.STATIC | JMod.FINAL, String.class, "NAME");
			jFieldClusterName.init(JExpr.lit(clusterFullname + "Client"));
		}

		// generates the attributes and command names as constants
		for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
			ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);
			String attributeName = zclAttributeDescriptor.getName();

			JFieldVar jField = jClusterInterface.field(JMod.STATIC | JMod.FINAL, String.class,
					getAttributeNameConstant(attributeName));
			jField.init(JExpr.lit(attributeName));
		}

		for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
			if (zclCommandDescriptor.isResponse())
				continue;

			String commandName = zclCommandDescriptor.getName();
			JFieldVar jField = jClusterInterface.field(JMod.STATIC | JMod.FINAL, String.class, getCommandNameConstant(commandName));
			jField.init(JExpr.lit(commandName));
		}

		// generates attribute getters and setters
		for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
			ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);
			generateAttributeGetterSignature(jModel, jClusterInterface, zclAttributeDescriptor, true);

			if (jClusterProxyClass != null) {
				// delegation method implementation in the Proxy class
				JMethod jProxyGetterMethod = generateAttributeGetterSignature(jModel, jClusterProxyClass, zclAttributeDescriptor,
						true);

				JInvocation a = jFieldProxy.invoke(jProxyGetterMethod.name());
				JVar[] params = jProxyGetterMethod.listParams();

				for (int j = 0; j < params.length; j++) {
					a.arg(params[j]);
				}

				jProxyGetterMethod.body()._return(a);
			}

			if (zclAttributeDescriptor.getAccess() == ZclAttributeDescr.ACCESS_RW) {
				generateAttributeSetterSignature(jModel, jClusterInterface, zclAttributeDescriptor, true);

				if (jClusterProxyClass != null) {
					JMethod jProxySetterMethod = generateAttributeSetterSignature(jModel, jClusterProxyClass,
							zclAttributeDescriptor, true);

					JInvocation a = jFieldProxy.invoke(jProxySetterMethod.name());
					JVar[] params = jProxySetterMethod.listParams();

					for (int j = 0; j < params.length; j++) {
						a.arg(params[j]);
					}
				}
			}
		}

		// generates command signatures
		for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
			generateCommandSignature(jClusterInterface, zclCommandDescriptor, true);

			if (jClusterProxyClass != null) {
				JMethod jProxyCommandMethod = generateCommandSignature(jClusterProxyClass, zclCommandDescriptor, true);
				if (jProxyCommandMethod == null)
					continue;

				JInvocation a = jFieldProxy.invoke(jProxyCommandMethod.name());
				JVar[] params = jProxyCommandMethod.listParams();

				for (int j = 0; j < params.length; j++) {
					a.arg(params[j]);
				}

				System.out.println(jProxyCommandMethod.type().name());

				if (jProxyCommandMethod.type() == jModel.VOID)
					jProxyCommandMethod.body().add(a);
				else
					jProxyCommandMethod.body()._return(a);
			}
		}

		//
		// generates the implementation part
		//

		// generates constructor
		if (jClusterClass != null) {
			JMethod constructor = jClusterClass.constructor(JMod.PUBLIC);
			// constructor.param(IManagedApplianceConnection.class,
			// "applianceConnection");
			constructor.body().directStatement(
					"super(applianceConnection, " + getClusterInterfaceName(zclClusterDescriptor, false) + ".NAME);");

			for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
				ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);
				generateCommandImplementation(jClusterClass, jClusterInterface, zclCommandDescriptor);
			}

			for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
				ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);

				generateAttributeGetterImplementation(jModel, jClusterClass, jClusterInterface, zclAttributeDescriptor);

				if (zclAttributeDescriptor.getAccess() == ZclAttributeDescr.ACCESS_RW)
					generateAttributeSetterImplementation(jModel, jClusterClass, jClusterInterface, zclAttributeDescriptor);
			}
		}
	}

	private void generateReadAttributeResponseGetSize(JCodeModel jModel, JPackage jPackage, JDefinedClass jClusterClass,
			ZclClusterDescriptor zclClusterDescriptor, boolean isServer) throws Exception {

		Vector zclAttributeDescriptors = null;
		if (isServer)
			zclAttributeDescriptors = zclClusterDescriptor.getZclClientAttributesDescriptors();
		else
			zclAttributeDescriptors = zclClusterDescriptor.getZclServerAttributesDescriptors();

		if (zclAttributeDescriptors.size() == 0)
			return;

		JClass jZCLClass = jModel.ref(ZCL.class);

		JMethod jReadAttibuteMethod = jClusterClass.method(JMod.PROTECTED, int.class, "readAttributeResponseGetSize");
		JVar jAttrIdVar = jReadAttibuteMethod.param(int.class, "attrId");
		jReadAttibuteMethod._throws(ServiceClusterException.class);
		jReadAttibuteMethod._throws(ZclValidationException.class);

		JBlock jBlock = jReadAttibuteMethod.body();

		JClass interfaceClass = getClusterConnectionClass(zclClusterDescriptor, !isServer);
		if (interfaceClass == null) {
			return;
		}

		JSwitch jSwitch = jBlock._switch(jAttrIdVar);

		JBlock jCaseBody = null;
		for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
			ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);

			logDebug(zclClusterDescriptor.getName() + ":" + zclAttributeDescriptor.getName() + ": generating get size");

			jCaseBody = jSwitch._case(JExpr.lit(zclAttributeDescriptor.getId())).body();

			JType type = jTypeForZbTypename(jModel, jClusterClass.getPackage(), zclAttributeDescriptor.getTypeName(),
					zclAttributeDescriptor);

			JClass jClassZigBeeType = getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jClusterClass,
					zclAttributeDescriptor.getTypeName(), zclAttributeDescriptor);

			JExpression nullValue = jNullValue4ZigBeeTypeName(jModel, zclAttributeDescriptor.getTypeName());

			jCaseBody._return(jClassZigBeeType.staticInvoke("zclSize").arg(nullValue));
		}

		jSwitch._default().body()._throw(JExpr._new(jModel.ref(UnsupportedClusterAttributeException.class)));
	}

	private void generateReadAttributeMethod(JCodeModel jModel, JPackage jPackage, JDefinedClass jClusterClass,
			ZclClusterDescriptor zclClusterDescriptor, boolean isServer) throws Exception {

		Vector zclAttributeDescriptors = null;
		if (isServer)
			zclAttributeDescriptors = zclClusterDescriptor.getZclClientAttributesDescriptors();
		else
			zclAttributeDescriptors = zclClusterDescriptor.getZclServerAttributesDescriptors();

		if (zclAttributeDescriptors.size() == 0)
			return;

		JClass jZCLClass = jModel.ref(ZCL.class);

		JMethod jReadAttibuteMethod = jClusterClass.method(JMod.PROTECTED, boolean.class, "fillAttributeRecord");
		JVar jZclFrameVar = jReadAttibuteMethod.param(IZclFrame.class, "zclResponseFrame");
		JVar jAttrIdVar = jReadAttibuteMethod.param(int.class, "attrId");
		jReadAttibuteMethod._throws(ApplianceException.class);
		jReadAttibuteMethod._throws(ServiceClusterException.class);

		JBlock jBlock = jReadAttibuteMethod.body();

		JClass interfaceClass = getClusterConnectionClass(zclClusterDescriptor, !isServer);
		if (interfaceClass == null) {
			return;
		}

		JVar jVarPeer = null;

		JInvocation jInvocation = JExpr.invoke("getSinglePeerCluster")
				.arg(JExpr.direct(interfaceClass.name() + ".class.getName()"));

		jVarPeer = jBlock.decl(interfaceClass, "c").init(JExpr.cast(interfaceClass, jInvocation));

		JSwitch jSwitch = jBlock._switch(jAttrIdVar);

		JBlock jCaseBody = null;
		for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
			ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);

			jCaseBody = jSwitch._case(JExpr.lit(zclAttributeDescriptor.getId())).body();

			JType type = jTypeForZbTypename(jModel, jClusterClass.getPackage(), zclAttributeDescriptor.getTypeName(),
					zclAttributeDescriptor);

			JVar param = jCaseBody.decl(type, "v");

			JInvocation jGetterInvocation = jVarPeer.invoke(this.attributeGetterName(zclAttributeDescriptor));
			if (useDefaultRequestContext) {
				jGetterInvocation.arg(JExpr.ref("endPoint").invoke("getDefaultRequestContext"));
			} else {
				jGetterInvocation.arg(JExpr._null());
			}

			jCaseBody.assign(param, jGetterInvocation);

			JClass jClassZigBeeType = getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jClusterClass,
					zclAttributeDescriptor.getTypeName(), zclAttributeDescriptor);

			jCaseBody.staticInvoke(jModel.ref(ZclDataTypeUI8.class), "zclSerialize").arg(jZclFrameVar)
					.arg(jZCLClass.staticRef("SUCCESS"));
			jCaseBody.invoke(jZclFrameVar, "appendUInt8").arg(jClassZigBeeType.staticRef("ZCL_DATA_TYPE"));
			jCaseBody.staticInvoke(jClassZigBeeType, "zclSerialize").arg(jZclFrameVar).arg(param);

			jCaseBody._break();
		}

		jSwitch._default().body()._return(JExpr.FALSE);
		jBlock._return(JExpr.TRUE);
	}

	private void generateWriteAttributeMethod(JCodeModel jModel, JPackage jPackage, JDefinedClass jClusterClass,
			ZclClusterDescriptor zclClusterDescriptor, boolean isServer) throws Exception {

		Vector zclAttributeDescriptors = null;
		if (isServer)
			zclAttributeDescriptors = zclClusterDescriptor.getZclClientAttributesDescriptors();
		else
			zclAttributeDescriptors = zclClusterDescriptor.getZclServerAttributesDescriptors();

		if (zclAttributeDescriptors.size() == 0)
			return;

		JClass jZCLClass = jModel.ref(ZCL.class);

		JMethod jWriteAttibutesMethod = jClusterClass.method(JMod.PROTECTED, short.class, "writeAttribute");
		JVar jZclFrameVar = jWriteAttibutesMethod.param(IZclFrame.class, "zclFrame");
		JVar jAttrIdVar = jWriteAttibutesMethod.param(int.class, "attrId");
		JVar jDataTypeVar = jWriteAttibutesMethod.param(short.class, "dataType");
		jWriteAttibutesMethod._throws(Exception.class);

		JBlock jBlock = jWriteAttibutesMethod.body();

		JClass interfaceClass = getClusterConnectionClass(zclClusterDescriptor, !isServer);
		if (interfaceClass == null) {
			return;
		}

		JVar jVarPeer = null;

		int rwAttributes = 0;
		for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
			ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);

			if (zclAttributeDescriptor.getAccess() == ZclAttributeDescr.ACCESS_RW) {
				rwAttributes++;
			}
		}

		if (rwAttributes > 0) {
			JInvocation jInvocation = JExpr.invoke("getSinglePeerCluster").arg(
					JExpr.direct(interfaceClass.name() + ".class.getName()"));
			jVarPeer = jBlock.decl(interfaceClass, "c").init(JExpr.cast(interfaceClass, jInvocation));
		}

		JSwitch jSwitch = jBlock._switch(jAttrIdVar);

		JBlock jCaseBody = null;

		// Read Only Attributes
		int roAttributes = 0;
		for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
			ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);

			if (zclAttributeDescriptor.getAccess() == ZclAttributeDescr.ACCESS_RO) {
				jCaseBody = jSwitch._case(JExpr.lit(zclAttributeDescriptor.getId())).body();
				roAttributes++;
			}
		}

		if (roAttributes > 0)
			jCaseBody._return(jZCLClass.staticRef("READ_ONLY"));

		for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
			ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);

			// issue the calls
			if (zclAttributeDescriptor.getAccess() == ZclAttributeDescr.ACCESS_RW) {
				jCaseBody = jSwitch._case(JExpr.lit(zclAttributeDescriptor.getId())).body();

				JType type = jTypeForZbTypename(jModel, jClusterClass.getPackage(), zclAttributeDescriptor.getTypeName(),
						zclAttributeDescriptor);

				JClass jClassZigBeeType = getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jClusterClass,
						zclAttributeDescriptor.getTypeName(), zclAttributeDescriptor);

				JVar param = jCaseBody.decl(type, "v");
				param.init(jClassZigBeeType.staticInvoke("zclParse").arg(jZclFrameVar));

				JInvocation jSetterInvocation = jCaseBody.invoke(jVarPeer, this.attributeSetterName(zclAttributeDescriptor)).arg(
						param);
				if (useDefaultRequestContext) {
					jSetterInvocation.arg(JExpr.ref("endPoint").invoke("getDefaultRequestContext"));
				} else {
					jSetterInvocation.arg(JExpr._null());
				}

				jCaseBody._break();
			}
		}

		// Return false since the notify didn't handle the incoming message.
		jSwitch._default().body()._return(jZCLClass.staticRef("UNSUPPORTED_ATTRIBUTE"));

		// jWriteAttibutesMethod.body()._return(jZCLClass.staticRef("SUCCESS"));
	}

	private void generateNotifyMethod(JCodeModel jModel, JPackage jPackage, JDefinedClass jClusterClass,
			ZclClusterDescriptor zclClusterDescriptor, boolean isServer) throws Exception {

		Vector zclCommandsDescriptors = null;
		if (isServer)
			zclCommandsDescriptors = zclClusterDescriptor.getZclClientCommandsDescriptors();
		else
			zclCommandsDescriptors = zclClusterDescriptor.getZclServerCommandsDescriptors();

		int count = 0;
		for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);

			if (zclCommandDescriptor.isResponse())
				continue;

			count++;
		}

		if (count == 0) {
			// jNotifyMethod.body()._return(JExpr._super().invoke(")));
			// jNotifyMethod.body()._return(JExpr.direct("super.notifyZclFrame(clusterId, zclFrame)"));
			return;
		}

		JMethod jNotifyMethod = jClusterClass.method(JMod.PUBLIC, boolean.class, "notifyZclFrame");
		JVar jClusterIdVar = jNotifyMethod.param(short.class, "clusterId");
		JVar jZclFrameVar = jNotifyMethod.param(IZclFrame.class, "zclFrame");
		jNotifyMethod._throws(Exception.class);

		JBlock jBlock = jNotifyMethod.body();

		// generates command signatures

		JVar jHandledVar = jBlock.decl(jModel.BOOLEAN, "handled");
		jBlock.directStatement(jHandledVar.name() + " = super.notifyZclFrame(clusterId, zclFrame);");

		jBlock._if(jHandledVar)._then()._return(jHandledVar);
		JVar jCommandIdVar = jBlock.decl(jModel.INT, "commandId").init(jZclFrameVar.invoke("getCommandId"));

		if (isServer) {
			jBlock._if(jZclFrameVar.invoke("isClientToServer"))._then()
					._throw(JExpr._new(jZclValidationException).arg("invalid direction field"));
		} else {
			jBlock._if(jZclFrameVar.invoke("isServerToClient"))._then()
					._throw(JExpr._new(jZclValidationException).arg("invalid direction field"));
		}

		JVar jVarResponseZclFrame = jBlock.decl(jModel.ref(IZclFrame.class), "responseZclFrame").init(JExpr._null());
		JVar jVarDevice = jBlock.decl(jModel.ref(ZigBeeDevice.class), "device").init(JExpr.invoke("getZigBeeDevice"));
		JVar jStatusCodeVar = jBlock.decl(jModel.INT, "statusCode").init(jModel.ref(ZCL.class).staticRef("SUCCESS"));

		JClass interfaceClass = getClusterConnectionClass(zclClusterDescriptor, !isServer);
		if (interfaceClass == null) {
			return;
		}

		boolean implementedPeerRequest = true;

		JVar jVarPeer = null;
		if (!implementedPeerRequest) {
			jVarPeer = jBlock.decl(interfaceClass, "c").init(JExpr._null());

			jBlock.directStatement("// TODO retrieve the peer");
		} else {
			JInvocation jInvocation = JExpr.invoke("getSinglePeerCluster").arg(
					JExpr.direct(interfaceClass.name() + ".class.getName()"));

			jVarPeer = jBlock.decl(interfaceClass, "c").init(JExpr.cast(interfaceClass, jInvocation));
		}

		JConditional lastIf = null;

		JSwitch jSwitch = jBlock._switch(jCommandIdVar);

		for (int i = 0; i < zclCommandsDescriptors.size(); i++) {
			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclCommandsDescriptors.get(i);

			if (zclCommandDescriptor.isResponse())
				continue;

			JBlock jCaseBody = jSwitch._case(JExpr.lit(zclCommandDescriptor.getId())).body();
			if (zclCommandDescriptor.getResponse() == null) {
				// The incoming command doesn't have any response (i.e Notify
				// commands), so just behave like the command is consumed.
				jCaseBody.assign(jVarResponseZclFrame,
						JExpr.invoke(getParseCommandName(zclCommandDescriptor)).arg(jVarPeer).arg(jZclFrameVar));

			} else {
				jCaseBody.assign(jVarResponseZclFrame,
						JExpr.invoke(getParseCommandName(zclCommandDescriptor)).arg(jVarPeer).arg(jZclFrameVar));
			}
			jCaseBody._break();
		}

		// Return false since the notify didn't handle the incoming message.
		jSwitch._default().body()._return(JExpr.FALSE);

		// if we need to handle some attributes ...
		JConditional ifBlock = jBlock._if(jVarResponseZclFrame.eq(JExpr._null()));

		JConditional innerIf = ifBlock._then()._if(jZclFrameVar.invoke("isDefaultResponseDisabled").not());

		innerIf._then().assign(jVarResponseZclFrame, JExpr.invoke("getDefaultResponse").arg(jZclFrameVar).arg(jStatusCodeVar));

		JFieldRef jClusterId = jClusterClass.staticRef("CLUSTER_ID");

		ifBlock._else().invoke(jVarDevice, "post").arg(jClusterId).arg(jVarResponseZclFrame);

		// JBlock thenBlock =
		// jBlock._if((jVarResponseZclFrame.eq(JExpr._null()).not()))._then();

		// thenBlock.invoke(jVarDevice,
		// "post").arg(jClusterId).arg(jVarResponseZclFrame);

		jNotifyMethod.body()._return(JExpr.TRUE);
	}

	/**
	 * Generates any source file related to the server side of a Cluster. Zcl
	 * version
	 * 
	 * @param jModel
	 * @param jPackage
	 * @param zclClusterDescriptor
	 * @throws Exception
	 */

	private void generateZclClusterAssets(JCodeModel jModel, JPackage jPackage, ZclClusterDescriptor zclClusterDescriptor,
			boolean isServer) throws Exception {

		boolean needsNotify = true;

		JClass jClusterConnectionClass = getClusterConnectionClass(zclClusterDescriptor, isServer);

		if (jClusterConnectionClass == null) {
			System.out.println("ERROR: class '" + getClusterImplementationName(zclClusterDescriptor, isServer) + "' not found");
			return;
		}

		JDefinedClass jClusterClass = jPackage._class(getZclClusterImplementationName(zclClusterDescriptor, isServer));
		// jClusterClass._implements(jClusterConnectionClassOpposite);
		jClusterClass._implements(jClusterConnectionClass);
		jClusterClass._extends(jModel.ref(ZclServiceCluster.class));

		System.out.println("Generating Zcl Cluster " + jClusterClass.fullName());

		if (needsNotify)
			jClusterClass._implements(jModel.ref(ZigBeeDeviceListener.class));

		if (addJavadoc) {
			jClusterClass.javadoc().add(copyrightClass);
		}

		// CLUSTER_ID static field
		JFieldVar jFieldClusterId = jClusterClass.field(JMod.STATIC | JMod.FINAL | JMod.PUBLIC, short.class, "CLUSTER_ID");
		jFieldClusterId.init(JExpr.lit(zclClusterDescriptor.getId()));

		JFieldVar jFieldAttributesMapByName = null;
		JFieldVar jFieldAttributesMapById = null;

		Vector zclAttributeDescriptors = null;

		if (isServer)
			zclAttributeDescriptors = zclClusterDescriptor.getZclServerAttributesDescriptors();
		else
			zclAttributeDescriptors = zclClusterDescriptor.getZclClientAttributesDescriptors();

		if (zclAttributeDescriptors.size() > 0) {
			jFieldAttributesMapByName = jClusterClass.field(JMod.STATIC, Map.class, "attributesMapByName");
			jFieldAttributesMapByName.init(JExpr._null());

			jFieldAttributesMapById = jClusterClass.field(JMod.STATIC, Map.class, "attributesMapById");
			jFieldAttributesMapById.init(JExpr._null());
		}

		//
		// generates the implementation part
		//

		// generates constructor
		JMethod constructor = jClusterClass.constructor(JMod.PUBLIC);
		constructor.body().directStatement("super();");
		constructor._throws(ApplianceException.class);

		// generate attach, detach methods
		if (needsNotify) {
			if (generateAttachDetach) {
				JMethod jAttachMethod = jClusterClass.method(JMod.PUBLIC, void.class, "zclAttach");
				JMethod jDetachMethod = jClusterClass.method(JMod.PUBLIC, void.class, "zclDetach");
				jAttachMethod.param(ZigBeeDevice.class, "device");
				jDetachMethod.param(ZigBeeDevice.class, "device");

				jAttachMethod.body().directStatement("device.setListener(CLUSTER_ID, this);");
				jDetachMethod.body().directStatement("device.removeListener(CLUSTER_ID, this);");

			}
			// notify method
			generateNotifyMethod(jModel, jPackage, jClusterClass, zclClusterDescriptor, isServer);
		}

		// getClusterId() method
		JMethod jGetClusterIdMethod = jClusterClass.method(JMod.PROTECTED, int.class, "getClusterId");
		jGetClusterIdMethod.body()._return(jFieldClusterId);

		JMethod jGetAttributeDescriptorByNameMethod = null;

		// Generates the getPeerAttributeDescriptors() only if the peer cluster
		// has attributes
		Vector peerAttributeDescriptors = null;
		if (isServer)
			peerAttributeDescriptors = zclClusterDescriptor.getZclClientAttributesDescriptors();
		else
			peerAttributeDescriptors = zclClusterDescriptor.getZclServerAttributesDescriptors();

		if (peerAttributeDescriptors.size() > 0) {
			JMethod jGetPeerClusterAttributeDescriptorsMethod = jClusterClass.method(JMod.PROTECTED,
					jIZclAttributeDescription.array(), "getPeerClusterAttributeDescriptors");
			JClass interfaceClass = getZclClass(zclClusterDescriptor, !isServer);
			if (interfaceClass != null) {
				jGetPeerClusterAttributeDescriptorsMethod.body()._return(interfaceClass.staticRef("attributeDescriptors"));
			}
		}

		if (zclAttributeDescriptors.size() > 0) {
			// getAttributeDescriptor() by name method
			jGetAttributeDescriptorByNameMethod = jClusterClass.method(JMod.PROTECTED, IZclAttributeDescriptor.class,
					"getAttributeDescriptor");

			jGetAttributeDescriptorByNameMethod.param(String.class, "name");
			jGetAttributeDescriptorByNameMethod.body()._return(
					JExpr.direct("(IZclAttributeDescriptor) attributesMapByName.get(name)"));
		}

		// getAttributeDescriptor() by attr id method
		if (manageAttributeReportings && zclAttributeDescriptors.size() > 0) {
			// getAttributeDescriptor() by id method
			jGetAttributeDescriptorByNameMethod = jClusterClass.method(JMod.PROTECTED, IZclAttributeDescriptor.class,
					"getAttributeDescriptor");

			jGetAttributeDescriptorByNameMethod.param(int.class, "attrId");
			jGetAttributeDescriptorByNameMethod.body()._return(
					JExpr.direct("(IZclAttributeDescriptor) attributesMapById.get(attrId)"));
		}

		if (zclAttributeDescriptors.size() > 0) {
			JMethod jGetGetAttributeDescriptorsMethod = jClusterClass.method(JMod.PROTECTED, Collection.class,
					"getAttributeDescriptors");

			// jGetGetAttributeDescriptorsMethod.body()._return(jFieldAttributesMapByName.invoke("values"));
			jGetGetAttributeDescriptorsMethod.body()._return(JExpr.direct("attributesMapByName.values()"));
		}
		// generates command signatures
		Vector zclExecCommandsDescriptors = null;
		Vector zclParseCommandsDescriptors = null;

		if (isServer) {
			zclExecCommandsDescriptors = zclClusterDescriptor.getZclServerCommandsDescriptors();
			zclParseCommandsDescriptors = zclClusterDescriptor.getZclClientCommandsDescriptors();
		} else {
			zclExecCommandsDescriptors = zclClusterDescriptor.getZclClientCommandsDescriptors();
			zclParseCommandsDescriptors = zclClusterDescriptor.getZclServerCommandsDescriptors();
		}

		for (int i = 0; i < zclExecCommandsDescriptors.size(); i++) {
			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclExecCommandsDescriptors.get(i);
			try {
				generateZclCommandImplementation(jModel, jClusterClass, jClusterConnectionClass, zclCommandDescriptor);
			} catch (Exception e) {
				continue;
			}
		}

		for (int i = 0; i < zclParseCommandsDescriptors.size(); i++) {
			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) zclParseCommandsDescriptors.get(i);

			if (!zclCommandDescriptor.isResponse())
				generateZclParseCommandImplementation(jModel, jClusterClass, jClusterConnectionClass, zclClusterDescriptor,
						zclCommandDescriptor, isServer);
		}

		// static block is added only if necessary!

		if (zclAttributeDescriptors.size() > 0) {
			// initialize the attribute descriptors!
			JClass jClassAttributeDescriptor = jModel.ref(ZclAttributeDescriptor.class);
			JBlock jStaticBlockAttributes = jClusterClass.init().block();
			JFieldVar jFieldAttributeDescriptors = jClusterClass.field(JMod.STATIC, jClassAttributeDescriptor.array(),
					"attributeDescriptors");
			jStaticBlockAttributes.assign(jFieldAttributeDescriptors,
					JExpr.newArray(jClassAttributeDescriptor, zclAttributeDescriptors.size()));

			String s = "";
			for (int i = 0; i < zclAttributeDescriptors.size(); i++) {
				ZclAttributeDescr zclAttributeDescriptor = (ZclAttributeDescr) zclAttributeDescriptors.get(i);

				if (zclAttributeDescriptor.getAccess() == ZclAttributeDescr.ACCESS_RW)
					generateZclAttributeSetterImplementation(jModel, jClusterClass, jClusterConnectionClass,
							zclAttributeDescriptor, isServer);

				generateZclAttributeGetterImplementation(jModel, jClusterClass, jClusterConnectionClass, zclAttributeDescriptor,
						isServer);

				// generate the AttributeDescriptor
				JType type = getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jClusterConnectionClass,
						zclAttributeDescriptor.getTypeName(), zclAttributeDescriptor);

				JExpression javaClass = JExpr._null();

				if (type == null) {
					System.out.println("Unable to generate type '" + zclAttributeDescriptor.getTypeName() + "'");
					continue;
				}
				JFieldRef jFieldAttributeName = jClusterClass.staticRef(getAttributeNameConstant(zclAttributeDescriptor.getName()));

				// TODO optimize. The following two invoke differs only for
				// the_new(type)

				JInvocation instance = null;

				// jStaticBlockAttributes.assign(jFieldAttributeDescriptors,
				// JExpr._null());

				jFieldAttributeDescriptors.init(JExpr._null());

				if (!zclAttributeDescriptor.hasVariableSize()) {
					instance = JExpr._new(jClassAttributeDescriptor).arg(JExpr.lit(zclAttributeDescriptor.getId()))
							.arg(jFieldAttributeName).arg(JExpr._new(type)).arg(javaClass)
							.arg(JExpr.lit(zclAttributeDescriptor.isReportable()))
							.arg(JExpr.lit(zclAttributeDescriptor.getAccess()));
				} else {
					instance = JExpr._new(jClassAttributeDescriptor).arg(JExpr.lit(zclAttributeDescriptor.getId()))
							.arg(jFieldAttributeName).arg(JExpr._new(type).arg(JExpr.lit(zclAttributeDescriptor.getSize())))
							.arg(javaClass).arg(JExpr.lit(zclAttributeDescriptor.isReportable()))
							.arg(JExpr.lit(zclAttributeDescriptor.getAccess()));
				}
				if (instance != null) {
					jStaticBlockAttributes.assign(jFieldAttributeDescriptors.component(JExpr.lit(i)), instance);
				}
			}

			JBlock jStaticBlockCreateMaps = jClusterClass.init().block();

			if (jFieldAttributesMapByName != null) {
				jStaticBlockCreateMaps.assign(jFieldAttributesMapByName,
						JExpr.invoke("fillAttributesMapsByName").arg(jFieldAttributeDescriptors).arg(jFieldAttributesMapByName));
			}
			if (jFieldAttributesMapById != null) {
				jStaticBlockCreateMaps.assign(jFieldAttributesMapById,
						JExpr.invoke("fillAttributesMapsById").arg(jFieldAttributeDescriptors).arg(jFieldAttributesMapById));
			}
		}

		generateReadAttributeResponseGetSize(jModel, jPackage, jClusterClass, zclClusterDescriptor, isServer);
		generateReadAttributeMethod(jModel, jPackage, jClusterClass, zclClusterDescriptor, isServer);
		generateWriteAttributeMethod(jModel, jPackage, jClusterClass, zclClusterDescriptor, isServer);
	}

	private JClass getClusterConnectionClass(ZclClusterDescriptor zclClusterDescriptor, boolean isServer) {
		String clusterConnectionFullyQualifiedClassName = this.interfacesPackageName + "." + zclClusterDescriptor.getPackageName()
				+ "." + getClusterInterfaceName(zclClusterDescriptor, isServer);

		JClass jClusterConnectionClass = jModel.ref(clusterConnectionFullyQualifiedClassName);
		return jClusterConnectionClass;
	}

	private JClass getZclClass(ZclClusterDescriptor zclClusterDescriptor, boolean isServer) {
		String zclClassname = this.zclRootPackage + "." + zclClusterDescriptor.getPackageName() + "."
				+ getZclClusterImplementationName(zclClusterDescriptor, isServer);

		JClass jZclClass = jModel.ref(zclClassname);
		return jZclClass;
	}

	private void generateAttributeSetterImplementation(JCodeModel jModel, JDefinedClass jClusterClass,
			JDefinedClass jInterfaceClass, ZclAttributeDescr zclAttributeDescriptor) throws Exception {
		JMethod jMethod = generateAttributeSetterSignature(jModel, jClusterClass, zclAttributeDescriptor, false);

		String invocationStatement = generateConnectionClassCommandDelegationStatement(jClusterClass, jInterfaceClass, jMethod);
		jMethod.body().directStatement(invocationStatement);
	}

	private JMethod generateAttributeGetterImplementation(JCodeModel jModel, JDefinedClass jClusterClass,
			JDefinedClass jInterfaceClass, ZclAttributeDescr zclAttributeDescriptor) throws Exception {

		JMethod jMethod = generateAttributeGetterSignature(jModel, jClusterClass, zclAttributeDescriptor, false);

		String invocationStatement = generateConnectionClassCommandDelegationStatement(jClusterClass, jInterfaceClass, jMethod);
		jMethod.body().directStatement(invocationStatement);

		return jMethod;
	}

	private JMethod generateZclAttributeGetterImplementation(JCodeModel jModel, JDefinedClass jClusterClass, JClass jParentClass,
			ZclAttributeDescr zclAttributeDescriptor, boolean isServer) throws Exception {

		JMethod jMethod = generateZclAttributeGetterSignature(jModel, jClusterClass, jParentClass, zclAttributeDescriptor, true);

		// parse return value
		JClass type = getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jParentClass, zclAttributeDescriptor.getTypeName(),
				zclAttributeDescriptor);

		int attrId = zclAttributeDescriptor.getId();
		JBlock jBlock = jMethod.body();

		JVar jContextParamVar = null;

		if (generateCacheAccess || optimizeForSize) {
			JVar[] jParams = jMethod.listParams();
			jContextParamVar = jParams[jParams.length - 1];
		}

		JType cachedType = null;

		if (generateCacheAccess) {
			cachedType = getJDKJClass(jModel, jMethod.type());

			if (cachedType != null) {
				// the return value is a basic type, so we have to handle the
				// 'class' version of the type (i.e Boolean for boolean)
				JBlock thenBlock = jBlock._if(jContextParamVar.ne(JExpr._null()))._then();
				JVar objectResultJVar = thenBlock.decl(cachedType, "objectResult").init(JExpr._null());

				thenBlock.assign(
						objectResultJVar,
						JExpr.cast(
								cachedType,
								JExpr.invoke("getValidCachedAttributeObject").arg(JExpr.lit(attrId))
										.arg(jContextParamVar.invoke("getMaxAgeForAttributeValues"))));

				if (jre15) {
					thenBlock._if(objectResultJVar.ne(JExpr._null()))._then()._return(objectResultJVar);
				} else {
					String typename = objectResultJVar.type().name();
					String prefix;
					if (typename.equals("Short") || typename.equals("Long") || typename.equals("Boolean")) {
						prefix = typename.toLowerCase();
					} else if (typename.equals("Integer")) {
						prefix = "int";
					} else {
						throw new Exception("Unknown base type!!!");
					}

					thenBlock._if(objectResultJVar.ne(JExpr._null()))._then()._return(objectResultJVar.invoke(prefix + "Value"));
				}

			} else {
				// the same as above but the return type is a complex type so we
				// use this complex type
				JBlock thenBlock = jBlock._if(jContextParamVar.ne(JExpr._null()))._then();
				JVar objectResultJVar = thenBlock.decl(jMethod.type(), "objectResult").init(JExpr._null());

				thenBlock.assign(
						objectResultJVar,
						JExpr.cast(
								jMethod.type(),
								JExpr.invoke("getValidCachedAttributeObject").arg(JExpr.lit(attrId))
										.arg(jContextParamVar.invoke("getMaxAgeForAttributeValues"))));
				thenBlock._if(objectResultJVar.ne(JExpr._null()))._then()._return(objectResultJVar);
			}
		}

		JTryBlock jTryBlock = null;
		JBlock jTryBlockBody = null;

		if (optimizeForSize) {
			jTryBlockBody = jBlock;
		} else {
			jTryBlock = jBlock._try();
			jTryBlockBody = jTryBlock.body();
		}

		JInvocation readAttributeInvocation = null;

		if (optimizeForSize) {
			readAttributeInvocation = JExpr.invoke("readAttribute").arg(JExpr.lit(attrId)).arg(jContextParamVar);
		} else {
			readAttributeInvocation = JExpr.invoke("readAttribute").arg(jClusterClass.staticRef("CLUSTER_ID"))
					.arg(JExpr.lit(attrId)).arg(JExpr.lit(true));
		}

		JVar jZclFrameVar = jTryBlockBody.decl(jModel.ref(IZclFrame.class), "zclFrame").init(readAttributeInvocation);

		if (!optimizeForSize) {
			if (isServer)
				jTryBlockBody.directStatement("if (zclFrame.isClientToServer())");
			else
				jTryBlockBody.directStatement("if (zclFrame.isServerToClient())");

			jTryBlockBody.directStatement("	throw new ApplianceException(\"bad direction field in ZigBee\");");
		}

		if (generateCacheAccess) {
			JVar jZclTypeVar = jTryBlockBody.decl(jMethod.type(), "v").init(type.staticInvoke("zclParse").arg(jZclFrameVar));
			if (cachedType != null) {
				jTryBlockBody.invoke("setCachedAttributeObject").arg(JExpr.lit(attrId))
						.arg(JExpr._new(cachedType).arg(jZclTypeVar));
			} else {
				jTryBlockBody.invoke("setCachedAttributeObject").arg(JExpr.lit(attrId)).arg(jZclTypeVar);
			}

			jTryBlockBody._return(jZclTypeVar);
		} else {
			jTryBlockBody._return(type.staticInvoke("zclParse").arg(jZclFrameVar));
		}

		if (!optimizeForSize) {
			JCatchBlock jCatchZigBeeExceptionBlock = jTryBlock._catch(jModel.ref(ZigBeeException.class));

			jCatchZigBeeExceptionBlock.body()._throw(
					JExpr._new(jModel.ref(ApplianceException.class)).arg(JExpr.direct("ERROR_PARSING_MESSAGE")));

			JCatchBlock JCatchZclBeeValidatonExceptionBlock = jTryBlock._catch(jModel.ref(ZclValidationException.class));
			JCatchZclBeeValidatonExceptionBlock.body().directStatement(
					"throw new ApplianceException(\"Zcl Validation Exception\");");
		}
		return jMethod;
	}

	private JMethod generateZclAttributeSetterImplementation(JCodeModel jModel, JDefinedClass jClusterClass, JClass jParentClass,
			ZclAttributeDescr zclAttributeDescriptor, boolean isServer) throws Exception {

		JMethod jMethod = generateZclAttributeSetterSignature(jModel, jClusterClass, jParentClass, zclAttributeDescriptor, true);

		JBlock jBlock = jMethod.body();

		JVar[] jParams = jMethod.listParams();
		JVar jContextParamVar = jParams[jParams.length - 1];
		JType jTypeZclFrame = jModel._ref(ZclFrame.class);

		// ATTENTION: use getPackage() otherwise it don't use import but
		// references the
		// type with a f
		JType type = getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jParentClass, zclAttributeDescriptor.getTypeName(),
				zclAttributeDescriptor);

		if (checkIfAttached) {
			JVar jDeviceVar = jBlock.decl(zbDeviceClass, "device").init(JExpr.invoke("getZigBeeDevice"));

			jBlock._if(jDeviceVar.eq(JExpr._null()))._then()
					._throw(JExpr._new(jModel.ref(ApplianceException.class)).arg("Not attached"));
		}

		int attrId = zclAttributeDescriptor.getId();

		JVar jVarAttribute = null;
		JVar[] params = jMethod.listParams();
		for (int i = 0; i < params.length; i++) {
			if (params[i].name().equals(zclAttributeDescriptor.getName())) {
				jVarAttribute = params[i];
				break;
			}
		}

		if (jVarAttribute == null) {
			System.err.println("Internal ERROR");
		}

		JClass jClassZigBeeType = this.getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jParentClass,
				zclAttributeDescriptor.getTypeName(), zclAttributeDescriptor);

		boolean oldVersion = false;

		JVar jVarAttrId = jBlock.decl(jModel._ref(int.class), "attrId").init(JExpr.lit(attrId));

		// payload size (attrId + dataType)
		JVar jVarSize = jBlock.decl(jModel._ref(int.class), "size").init(JExpr.lit(3));

		jBlock.assignPlus(jVarSize, jClassZigBeeType.staticInvoke("zclSize").arg(jVarAttribute));

		JVar jVarZclFrame = jBlock.decl(jModel._ref(IZclFrame.class), "zclFrame").init(
				JExpr._new(jTypeZclFrame).arg(JExpr.lit(0x00)).arg(jVarSize));

		// jBlock.directStatement("zclFrame.setSequence(sequence++);");
		// jBlock.invoke(jVarZclFrame, "setCommandId").arg(JExpr.lit(0x02));
		jBlock.invoke(jVarZclFrame, "appendUInt16").arg(jVarAttrId);
		jBlock.invoke(JExpr.ref("zclFrame"), "appendUInt8").arg(jClassZigBeeType.staticRef("ZCL_DATA_TYPE"));
		jBlock.staticInvoke(jClassZigBeeType, "zclSerialize").arg(jVarZclFrame).arg(JExpr.ref(zclAttributeDescriptor.getName()));

		if (oldVersion) {
			jBlock.directStatement("if (sync) {");

			JTryBlock jTryBlockInvoke = jBlock._try();
			jTryBlockInvoke.body().directStatement("	zclResponseFrame =  device.invoke(CLUSTER_ID, zclFrame);");

			jTryBlockInvoke._catch(jModel.ref(ZigBeeException.class)).body()
					._throw(JExpr._new(jModel.ref(ApplianceException.class)).arg(JExpr.direct("INVOKE_ERROR_MESSAGE")));

			if (isServer)
				jBlock.directStatement("	if (zclResponseFrame.isClientToServer())");
			else
				jBlock.directStatement("	if (zclResponseFrame.isServerToClient())");

			jBlock.directStatement("		throw new ApplianceException(BAD_DIRECTION_MESSAGE);");
			// jBlock._if(jZclResponseFrame.invoke("getCommandId").ne(JExpr.lit(0x05)))._then().directStatement(
			// "		throw new ApplianceException(BAD_RESPONSE_COMMAND_ID_MESSAGE + zclResponseFrame.getCommandId() + \"'\");");
			jBlock.directStatement("	return;");
			jBlock.directStatement("}");
			jBlock.directStatement("else {");
			jBlock.directStatement("	boolean res = device.post(CLUSTER_ID, zclFrame);");
			jBlock.directStatement("	if (!res) {");
			jBlock.directStatement("		throw new ApplianceException(POST_FAILED_MESSAGE);");
			jBlock.directStatement("	}");
			jBlock.directStatement("}");
		} else {
			jBlock.invoke("issueSet").arg(jClusterClass.staticRef("CLUSTER_ID")).arg(jVarZclFrame).arg(jVarAttrId)
					.arg(jContextParamVar);
		}
		return jMethod;
	}

	private JMethod generateAttributeGetterSignature(JCodeModel jModel, JDefinedClass jClusterInterface,
			ZclAttributeDescr zclAttributeDescriptor, boolean addContext) throws Exception {

		String getterName = attributeGetterName(zclAttributeDescriptor);
		String attributeTypeName = zclAttributeDescriptor.getTypeName();
		JMethod jMethodAttributeGetterSignature = null;

		try {
			JType jType = jTypeForZbTypename(jModel, jClusterInterface.getPackage(), attributeTypeName, zclAttributeDescriptor);
			jMethodAttributeGetterSignature = jClusterInterface.method(JMod.PUBLIC, jType, getterName);
		} catch (Exception e) {
			return jMethodAttributeGetterSignature;
		}

		if (addContext)
			jMethodAttributeGetterSignature.param(IEndPointRequestContext.class, "context");

		jMethodAttributeGetterSignature._throws(ApplianceException.class);
		jMethodAttributeGetterSignature._throws(ServiceClusterException.class);

		return jMethodAttributeGetterSignature;
	}

	private JMethod generateZclAttributeGetterSignature(JCodeModel jModel, JDefinedClass jClusterInterface, JClass jParentClass,
			ZclAttributeDescr zclAttributeDescriptor, boolean addContext) throws Exception {

		String getterName = attributeGetterName(zclAttributeDescriptor);
		String attributeTypeName = zclAttributeDescriptor.getTypeName();
		JMethod jMethodAttributeGetterSignature = null;

		try {
			JType jType = jTypeForZbTypename(jModel, jParentClass._package(), attributeTypeName, zclAttributeDescriptor);
			jMethodAttributeGetterSignature = jClusterInterface.method(JMod.PUBLIC, jType, getterName);
		} catch (Exception e) {
			return jMethodAttributeGetterSignature;
		}

		if (addContext)
			jMethodAttributeGetterSignature.param(IEndPointRequestContext.class, "context");

		jMethodAttributeGetterSignature._throws(ApplianceException.class);
		jMethodAttributeGetterSignature._throws(ServiceClusterException.class);

		return jMethodAttributeGetterSignature;
	}

	private JMethod generateAttributeSetterSignature(JCodeModel jModel, JDefinedClass jClass,
			ZclAttributeDescr zclAttributeDescriptor, boolean addContext) throws Exception {

		String setterName = attributeSetterName(zclAttributeDescriptor);
		String attributeTypeName = zclAttributeDescriptor.getTypeName();

		JMethod jMethodAttributeSetterSignature = jClass.method(JMod.PUBLIC, jModel.VOID, setterName);

		try {
			JType jType = jTypeForZbTypename(jModel, jClass.getPackage(), attributeTypeName, zclAttributeDescriptor);
			jMethodAttributeSetterSignature.param(jType, zclAttributeDescriptor.getName());
		} catch (Exception e) {
			return jMethodAttributeSetterSignature;
		}

		if (addContext)
			jMethodAttributeSetterSignature.param(IEndPointRequestContext.class, "context");

		jMethodAttributeSetterSignature._throws(ApplianceException.class);
		jMethodAttributeSetterSignature._throws(ServiceClusterException.class);
		return jMethodAttributeSetterSignature;
	}

	private JMethod generateZclAttributeSetterSignature(JCodeModel jModel, JDefinedClass jClass, JClass jParentClass,
			ZclAttributeDescr zclAttributeDescriptor, boolean addContext) throws Exception {

		String setterName = attributeSetterName(zclAttributeDescriptor);
		String attributeTypeName = zclAttributeDescriptor.getTypeName();

		JMethod jMethodAttributeSetterSignature = jClass.method(JMod.PUBLIC, jModel.VOID, setterName);

		try {
			JType jType = jTypeForZbTypename(jModel, jParentClass._package(), attributeTypeName, zclAttributeDescriptor);
			jMethodAttributeSetterSignature.param(jType, zclAttributeDescriptor.getName());
		} catch (Exception e) {
			return jMethodAttributeSetterSignature;
		}

		if (addContext)
			jMethodAttributeSetterSignature.param(IEndPointRequestContext.class, "context");

		jMethodAttributeSetterSignature._throws(ApplianceException.class);
		jMethodAttributeSetterSignature._throws(ServiceClusterException.class);
		return jMethodAttributeSetterSignature;
	}

	/**
	 * Generates the source code that implements a command
	 * 
	 * @param jClusterClass
	 * @param zclCommandDescriptor
	 * @throws Exception
	 */

	private void generateCommandImplementation(JDefinedClass jClusterClass, JDefinedClass jInterfaceClass,
			ZclCommandDescriptor zclCommandDescriptor) throws Exception {
		JMethod jMethod = generateCommandSignature(jClusterClass, zclCommandDescriptor, false);
		if (jMethod == null)
			return;

		String invocationStatement = generateConnectionClassCommandDelegationStatement(jClusterClass, jInterfaceClass, jMethod);
		jMethod.body().directStatement(invocationStatement);
	}

	private JMethod generateZclCommandImplementation(JCodeModel jModel, JDefinedClass jClusterClass, JClass jParentClass,
			ZclCommandDescriptor zclCommandDescriptor) throws Exception {

		JMethod jMethod = generateZclCommandSignature(jClusterClass, jParentClass, zclCommandDescriptor, true);
		if (jMethod == null)
			return jMethod;

		boolean oldVersion = false;

		JBlock jBlock = jMethod.body();

		if (oldVersion)
			jBlock.decl(zbDeviceClass, "device");

		JType jTypeZclFrame = jModel._ref(ZclFrame.class);

		// JVar jZclTypeVar = jBlock.decl(type, "dummy");

		if (oldVersion) {
			JVar jZclResponseFrame = jBlock.decl(jModel._ref(IZclFrame.class), "zclResponseFrame");
		}

		// JVar jVarZclFrame = jBlock.decl(jModel._ref(IZclFrame.class),
		// "zclFrame");

		JVar jVarSize = null;

		Vector zclParametersDescriptors = zclCommandDescriptor.getParametersDescriptors();

		if ((optimizeForSize) && (zclParametersDescriptors.size() == 0)) {
			// size variable is not necessary!
		} else {
			jVarSize = jBlock.decl(jModel._ref(int.class), "size").init(JExpr.lit(0));
		}

		if (oldVersion) {
			jBlock.directStatement("device = getZigBeeDevice();");
			jBlock.directStatement("if (device == null)");
			jBlock.directStatement("	throw new ApplianceException(\"Not attached\");");

			jBlock.directStatement("boolean sync = true;");
		}

		int commandId = zclCommandDescriptor.getId();

		JBlock jCalculateSizeBlock = jMethod.body().block();

		for (int i = 0; i < zclParametersDescriptors.size(); i++) {
			ZclField zclParameterDescriptor = (ZclField) zclParametersDescriptors.get(i);

			try {
				JClass jClassZigBeeType = getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jParentClass,
						zclParameterDescriptor.getType(), zclParameterDescriptor);
				if (jClassZigBeeType == null) {
					jBlock.directStatement("// FIXME: unable handle parameter '" + zclParameterDescriptor.getName() + "' of type '"
							+ zclParameterDescriptor.getType() + "'");
					continue;
				}

				if (zclParameterDescriptor.isArray()) {
					JVar methodParam = jMethod.listParams()[i];

					// TODO: optimize
					jCalculateSizeBlock.assignPlus(jVarSize,
							methodParam.ref("length").mul(jClassZigBeeType.staticInvoke("zclSize").arg(JExpr.lit(0))));

					// FIXME: the 'JExpr.lit(0)' above, must be replaced with a
					// 0 of the base type of the jClassZigBeeType
					// If this type is for intance a uint8 it must be casted to
					// (short).

					jCalculateSizeBlock.assignPlus(jVarSize, JExpr.lit(0x01));

				} else {
					jCalculateSizeBlock.assignPlus(jVarSize,
							jClassZigBeeType.staticInvoke("zclSize").arg(JExpr.ref(zclParameterDescriptor.getName())));
				}

			} catch (Exception e) {
				jCalculateSizeBlock.directStatement("// FIXME: unable handle parameter '" + zclParameterDescriptor.getName()
						+ "' of type '" + zclParameterDescriptor.getType() + "'");
			}
		}
		JVar jVarZclFrame = null;

		if (jVarSize != null) {
			// FrameType is "Cluster Specific Command"
			jVarZclFrame = jBlock.decl(jTypeZclFrame, "zclFrame").init(
					JExpr._new(jTypeZclFrame).arg(JExpr.lit(0x01)).arg(JExpr.ref("size")));
		} else {
			// Zero frame size!
			jVarZclFrame = jBlock.decl(jTypeZclFrame, "zclFrame").init(JExpr._new(jTypeZclFrame).arg(JExpr.lit(0x01)));
		}

		// jBlock.directStatement("zclFrame.setSequence(sequence++);");
		jBlock.invoke(jVarZclFrame, "setCommandId").arg(JExpr.lit(commandId));
		JBlock jSerializeBlock = jMethod.body().block();
		JVar loopVar = null;

		for (int i = 0; i < zclParametersDescriptors.size(); i++) {
			ZclField zclParameterDescriptor = (ZclField) zclParametersDescriptors.get(i);
			JClass jClassZigBeeType = getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jParentClass,
					zclParameterDescriptor.getType(), zclParameterDescriptor);

			if (jClassZigBeeType == null) {
				jBlock.directStatement("// FIXME: unable handle parameter '" + zclParameterDescriptor.getName() + "' of type '"
						+ zclParameterDescriptor.getType() + "'");
				continue;
			}

			if (zclParameterDescriptor.isArray()) {
				JVar methodParam = jMethod.listParams()[i];
				JExpression jVarArraySize = methodParam.ref("length");

				// jBlock.assignPlus(jVarSize,
				// jVarArraySize.mul(jClassZigBeeType.staticInvoke("zclSize").arg(JExpr._null())));

				if (loopVar == null) {
					loopVar = jBlock.decl(jModel._ref(int.class), "i");
				}

				JForLoop forLoop = jBlock._for();
				forLoop.test(loopVar.lt(jVarArraySize));
				forLoop.init(loopVar, JExpr.lit(0));
				forLoop.update(loopVar.incr());
				forLoop.body().staticInvoke(jClassZigBeeType, "zclSerialize").arg(jVarZclFrame)
						.arg(JExpr.ref(zclParameterDescriptor.getName()).component(loopVar));
			} else {
				jSerializeBlock.staticInvoke(jClassZigBeeType, "zclSerialize").arg(jVarZclFrame)
						.arg(JExpr.ref(zclParameterDescriptor.getName()));
			}
		}

		JType jReturnZclType = null;

		ZclCommandDescriptor response = zclCommandDescriptor.getResponse();

		if (response != null)
			jReturnZclType = getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jParentClass, response.getName(), response);

		if (oldVersion) {
			jBlock.directStatement("if (sync) {");

			JTryBlock jTryBlockInvoke = jBlock._try();
			jTryBlockInvoke.body().directStatement("	zclResponseFrame =  device.invoke(CLUSTER_ID, zclFrame);");

			jTryBlockInvoke._catch(jModel.ref(ZigBeeException.class)).body()
					._throw(JExpr._new(jModel.ref(ApplianceException.class)).arg(JExpr.direct("INVOKE_ERROR_MESSAGE")));

			jBlock.directStatement("	if (zclResponseFrame.isClientToServer())");
			jBlock.directStatement("		throw new ApplianceException(BAD_DIRECTION_MESSAGE);");
		}

		int expectedCommandId = 0x00; // FIXME: it must be the default response
		// command id
		// response!!!!

		// FIXME: corretto?
		if (zclCommandDescriptor.getResponse() != null)
			expectedCommandId = zclCommandDescriptor.getResponse().getId();
		else
			expectedCommandId = ZCL.ZclDefaultRsp;

		JInvocation issueExec = null;

		JVar[] jParams = jMethod.listParams();
		JVar jContextParamVar = jParams[jParams.length - 1];

		if (optimizeForSize) {
			issueExec = JExpr.invoke("issueExec").arg(jVarZclFrame).arg(JExpr.lit(expectedCommandId)).arg(jContextParamVar);
		} else {
			issueExec = JExpr.invoke("issueExec").arg(jClusterClass.staticRef("CLUSTER_ID")).arg(jVarZclFrame)
					.arg(JExpr.lit(expectedCommandId)).arg(jContextParamVar);
		}

		if (jReturnZclType != null) {
			JVar jVarResponseFrame = jBlock.decl(jModel._ref(IZclFrame.class), "zclResponseFrame").init(issueExec);
			jBlock._return(JExpr.direct(jReturnZclType.name() + ".zclParse(zclResponseFrame)"));
		} else {
			jBlock.add(issueExec);
		}
		return jMethod;
	}

	private JMethod generateZclParseCommandImplementation(JCodeModel jModel, JDefinedClass jClusterClass, JClass jParentClass,
			ZclClusterDescriptor zclClusterDescriptor, ZclCommandDescriptor zclCommandDescriptor, boolean isServer)
			throws Exception {

		boolean useTryBlock = false;

		JMethod jMethod = jClusterClass.method(JMod.PROTECTED, IZclFrame.class, getParseCommandName(zclCommandDescriptor));

		JClass interfaceClass = getClusterConnectionClass(zclClusterDescriptor, !isServer);
		if (interfaceClass == null) {
			return jMethod;
		}

		JVar clientParam = jMethod.param(interfaceClass, "o");
		JVar inputFrame = jMethod.param(IZclFrame.class, "zclFrame");
		jMethod._throws(ApplianceException.class);

		if (!useTryBlock)
			jMethod._throws(ServiceClusterException.class);

		JBlock jBlock = jMethod.body();
		Vector zclParametersDescriptors = zclCommandDescriptor.getParametersDescriptors();

		JVar loopVar = null;

		// generates the code that parses the received command
		JInvocation jInvocation = clientParam.invoke(this.getCommandName(zclCommandDescriptor));
		for (int i = 0; i < zclParametersDescriptors.size(); i++) {
			ZclField zclParameterDescriptor = (ZclField) zclParametersDescriptors.get(i);
			JType type = jTypeForZbTypename(jModel, jParentClass._package(), zclParameterDescriptor.getType(),
					zclParameterDescriptor);

			JClass jClassZigBeeType = getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jParentClass,
					zclParameterDescriptor.getType(), zclParameterDescriptor);

			if (jClassZigBeeType == null) {
				jBlock.directStatement("// FIXME: unable handle parameter '" + zclParameterDescriptor.getName() + "' of type '"
						+ zclParameterDescriptor.getType() + "'");
				continue;
			}

			JVar param;

			if (zclParameterDescriptor.isArray()) {
				JVar jVarArraySize = jBlock.decl(jModel._ref(int.class), "size");
				JInvocation parseSize = jModel.ref(ZclDataTypeUI8.class).staticInvoke("zclParse").arg(inputFrame);
				jBlock.assign(jVarArraySize, parseSize);

				param = jBlock.decl(type.array(), zclParameterDescriptor.getName());

				System.out.println(param.name());
				jBlock.directStatement(param.name() + " = new " + type.name() + "[" + jVarArraySize.name() + "];");

				if (loopVar == null) {
					loopVar = jBlock.decl(jModel._ref(int.class), "i");
				}

				JForLoop forLoop = jBlock._for();
				forLoop.test(loopVar.lt(jVarArraySize));
				forLoop.init(loopVar, JExpr.lit(0));
				forLoop.update(loopVar.incr());
				forLoop.body().assign(JExpr.ref(zclParameterDescriptor.getName()).component(loopVar),
						jClassZigBeeType.staticInvoke("zclParse").arg(inputFrame));
			} else {
				param = jBlock.decl(type, zclParameterDescriptor.getName());
				param.init(jClassZigBeeType.staticInvoke("zclParse").arg(inputFrame));
			}

			jInvocation.arg(param);
		}

		if (useDefaultRequestContext) {
			jInvocation.arg(JExpr.ref("endPoint").invoke("getDefaultRequestContext"));
		} else {
			jInvocation.arg(JExpr._null());
		}

		JClass jReturnZclType = null;
		JType jReturnType = null;

		ZclCommandDescriptor responseCommand = zclCommandDescriptor.getResponse();

		if (responseCommand != null) {
			jReturnZclType = getZclDataTypeJClass(jModel, jClusterClass.getPackage(), jParentClass, responseCommand.getName(),
					responseCommand);
			jReturnType = jTypeForZigBeeTypeName(jModel, jParentClass._package(), jParentClass, responseCommand.getName());
		}

		JVar jResultVar = null;

		JBlock jTryBody = null;

		if (useTryBlock) {
			JTryBlock jTryBlock = jBlock._try();
			jTryBlock._catch(jModel.ref(Exception.class));

			jTryBody = jTryBlock.body();
		} else {
			jTryBody = jBlock;
		}

		if ((jReturnZclType != null) && (jReturnType != null)) {
			// the exec has a non-void return type, we need to assign it
			jResultVar = jTryBody.decl(jReturnType, "r").init(jInvocation);
		} else {
			if (consumeCommandsWithoutResponse) {
				jTryBody._if(clientParam.eq(JExpr._null()))._then()._return(JExpr._null());
			}
			jTryBody.add(jInvocation);
		}

		JVar jZclResponseFrame = null;

		if (jResultVar != null) {
			// assign to size variable the response size
			JVar jSizeVar = jTryBody.decl(jModel._ref(int.class), "size").init(
					jReturnZclType.staticInvoke("zclSize").arg(jResultVar));

			// creates the zclResponseFrame variable
			jZclResponseFrame = jTryBody.decl(jModel._ref(IZclFrame.class), "zclResponseFrame");
			jZclResponseFrame.init(inputFrame.invoke("createResponseFrame").arg(jSizeVar));

			int responseCommandId = responseCommand.getId();

			jTryBody.invoke(jZclResponseFrame, "setCommandId").arg(JExpr.lit(responseCommandId));

			jTryBody.staticInvoke(jReturnZclType, "zclSerialize").arg(jZclResponseFrame).arg(jResultVar);
			jTryBody._return(jZclResponseFrame);
		}

		if (jZclResponseFrame == null)
			jBlock._return(JExpr._null());

		return jMethod;
	}

	private String generateConnectionClassCommandDelegationStatement(JDefinedClass jClusterClass, JDefinedClass jInterfaceClass,
			JMethod jMethod) {
		jMethod.listVarParamType();

		JType[] jTypes = jMethod.listParamTypes();
		JVar[] jVars = jMethod.listParams();

		String invocation = "";

		String a = jMethod.type().name();
		if (!a.equals("void"))
			invocation = "return ";

		invocation += "((" + jInterfaceClass.name() + ")clusterEndPoint)." + jMethod.name() + "(getRequestContext()";
		for (int i = 0; i < jTypes.length; i++) {
			String name = jVars[i].name();
			invocation += ", " + name;
		}

		invocation += ");";

		return invocation;
	}

	/**
	 * Generates a command prototype. This implementation is common to Client
	 * and Server side.
	 * 
	 * @param jClusterClass
	 * @param zclCommandDescriptor
	 * @throws Exception
	 */

	private JMethod generateCommandSignature(JDefinedClass jClusterInterface, ZclCommandDescriptor zclCommandDescriptor,
			boolean addContext) throws Exception {

		if (zclCommandDescriptor.isResponse())
			return null;

		// FIXME: the return type depends on the returned response!!!!
		JType jReturnType = null;

		ZclCommandDescriptor responseCommand = zclCommandDescriptor.getResponse();
		if (responseCommand != null)
			jReturnType = jTypeForZbTypename(jModel, jClusterInterface.getPackage(), responseCommand.getName(), responseCommand);
		else
			jReturnType = jModel.VOID;

		JMethod jMethodCommandSignature = jClusterInterface.method(JMod.PUBLIC, jReturnType, getCommandName(zclCommandDescriptor));
		jMethodCommandSignature._throws(ApplianceException.class);
		jMethodCommandSignature._throws(ServiceClusterException.class);

		if (addContext && !contextLast)
			jMethodCommandSignature.param(IEndPointRequestContext.class, "context");

		Vector zclParametersDescriptors = zclCommandDescriptor.getParametersDescriptors();

		for (int i = 0; i < zclParametersDescriptors.size(); i++) {
			ZclField zclParameterDescriptor = (ZclField) zclParametersDescriptors.get(i);
			JType type = jTypeForZbTypename(jModel, jClusterInterface.getPackage(), zclParameterDescriptor.getType(),
					zclParameterDescriptor);

			if (zclParameterDescriptor.isArray()) {
				jMethodCommandSignature.param(type.array(), zclParameterDescriptor.getName());
			} else {
				jMethodCommandSignature.param(type, zclParameterDescriptor.getName());
			}
		}

		if (addContext && contextLast)
			jMethodCommandSignature.param(IEndPointRequestContext.class, "context");

		if (confirmationRequiredField)
			jMethodCommandSignature.param(boolean.class, "confirmationRequired");

		return jMethodCommandSignature;
	}

	private JMethod generateZclCommandSignature(JDefinedClass jZclClass, JClass jParentClass,
			ZclCommandDescriptor zclCommandDescriptor, boolean addContext) throws Exception {

		if (zclCommandDescriptor.isResponse())
			return null;

		// FIXME: the return type depends on the returned response!!!!
		JType jReturnType = null;

		ZclCommandDescriptor responseCommand = zclCommandDescriptor.getResponse();
		if (responseCommand != null)
			jReturnType = jTypeForZigBeeTypeName(jModel, jParentClass._package(), jParentClass, responseCommand.getName());
		else
			jReturnType = jModel.VOID;

		JMethod jMethodCommandSignature = jZclClass.method(JMod.PUBLIC, jReturnType, getCommandName(zclCommandDescriptor));
		jMethodCommandSignature._throws(ApplianceException.class);
		jMethodCommandSignature._throws(ServiceClusterException.class);

		if (addContext && !contextLast)
			jMethodCommandSignature.param(IEndPointRequestContext.class, "context");

		Vector zclParametersDescriptors = zclCommandDescriptor.getParametersDescriptors();

		for (int i = 0; i < zclParametersDescriptors.size(); i++) {
			ZclField zclParameterDescriptor = (ZclField) zclParametersDescriptors.get(i);
			JType type = jTypeForZbTypename(jModel, jParentClass._package(), zclParameterDescriptor.getType(),
					zclParameterDescriptor);

			if (zclParameterDescriptor.isArray()) {
				jMethodCommandSignature.param(type.array(), zclParameterDescriptor.getName());
			} else {
				jMethodCommandSignature.param(type, zclParameterDescriptor.getName());
			}
		}

		if (addContext && contextLast)
			jMethodCommandSignature.param(IEndPointRequestContext.class, "context");

		if (confirmationRequiredField)
			jMethodCommandSignature.param(boolean.class, "confirmationRequired");

		return jMethodCommandSignature;
	}

	/**
	 * Parses the ZCL description xml file and build metadata.
	 */

	public void loadXml(Node node) throws Exception {
		int nodeType = node.getNodeType();

		switch (nodeType) {
		case Node.DOCUMENT_NODE:
			loadXml(((Document) node).getDocumentElement());
			break;

		// print element with attributes
		case Node.ELEMENT_NODE:
			NamedNodeMap attrs = node.getAttributes();
			String tag = node.getNodeName();

			if (tag == "cluster") {
				// This is a device definition or extension
				int id = Converter.parseInteger(attrs.getNamedItem("id").getNodeValue());
				String name = attrs.getNamedItem("name").getNodeValue();
				name = transformName(name);

				if (clusters != null && clusters.size() != 0 && !clusters.contains(name))
					return;

				String status = "d";
				String packageName = null;

				if (attrs.getNamedItem("package") != null)
					packageName = attrs.getNamedItem("package").getNodeValue();

				if (attrs.getNamedItem("status") != null)
					status = attrs.getNamedItem("status").getNodeValue();

				if (status.equals("u")) {
					System.out.println("skipping still unimplemented cluster '" + name + "'");
					return;
				}

				zclClusterDescriptor = new ZclClusterDescriptor(name, id, packageName);
				zclClusters.add(zclClusterDescriptor);
			} else if (tag == "attribute") {
				// This is a cluster attribute definition

				int id = Converter.parseInteger(attrs.getNamedItem("id").getNodeValue());
				String name = attrs.getNamedItem("name").getNodeValue();
				String type = attrs.getNamedItem("type").getNodeValue();
				Node accessItem = attrs.getNamedItem("access");
				Node defItem = attrs.getNamedItem("default");
				Node mandatoryItem = attrs.getNamedItem("mandatory");
				Node reportableItem = attrs.getNamedItem("reportable");
				Node sizeItem = attrs.getNamedItem("size");

				int size = 0;

				boolean reportable = true;

				name = transformName(name);

				String uom = "";
				double factor = 1.0;

				try {
					uom = attrs.getNamedItem("uom").getNodeValue();
				} catch (Exception e) {
				}

				try {
					String sFactor = attrs.getNamedItem("factor").getNodeValue();
					factor = Double.parseDouble(sFactor);
				} catch (NullPointerException e) {
					// pass here when the factory attribute is not present
				}

				// default values
				boolean mandatory = true;
				String access = "r";
				String defValue = "";

				if (accessItem != null) {
					access = accessItem.getNodeValue();
				}

				if (mandatoryItem != null) {
					mandatory = Converter.parseBoolean(mandatoryItem.getNodeValue());
				}

				if (reportableItem != null) {
					reportable = Converter.parseBoolean(reportableItem.getNodeValue());
				}

				if (defItem != null) {
					defValue = defItem.getNodeValue();
				}

				if (sizeItem != null) {
					try {
						size = Integer.parseInt(sizeItem.getNodeValue());
					} catch (Exception e) {
						System.err.println("error parsing size attribute in attribute " + name);
						return;
					}
				}

				ZclAttributeDescr zclAttributeDescriptor = new ZclAttributeDescr(id, name, type, size, access, defValue, mandatory,
						factor, "", reportable);

				if (insideServerCommands)
					zclClusterDescriptor.addServerAttributeDescriptor(zclAttributeDescriptor);
				else
					zclClusterDescriptor.addClientAttributeDescriptor(zclAttributeDescriptor);

				zclAttributeDescriptor.isReportable(reportable);

			} else if (tag == "client") {
				// client commands begins
				insideServerCommands = false;
			} else if (tag == "server") {
				insideServerCommands = true;
			} else if (tag.equals("command")) {
				int id = Converter.parseInteger(attrs.getNamedItem("id").getNodeValue());
				String name = attrs.getNamedItem("name").getNodeValue();
				Node mandatoryItem = attrs.getNamedItem("mandatory");
				Node manufactureCodeItem = attrs.getNamedItem("manufacturerCode");
				boolean mandatory = true;
				boolean manufacturerSpecific = false;
				short manufacturerCode = 0x00;
				byte frameType = 0x01; // cluster specific
				byte direction = 0x01; // direction server to client

				Node responseNameItem = attrs.getNamedItem("response_name");
				Node isResponseItem = attrs.getNamedItem("response");

				if (mandatoryItem != null) {
					mandatory = Converter.parseBoolean(mandatoryItem.getNodeValue());
				}

				name = transformName(name);

				if (manufactureCodeItem != null) {
					manufacturerCode = (short) Converter.parseInteger(manufactureCodeItem.getNodeValue());
					manufacturerSpecific = true;
				}

				Dictionary zclResponses;

				if (insideServerCommands) {
					zclResponses = zclResponsesServerSide;
					direction = 0x00; // client to server direction
				} else {
					zclResponses = zclResponsesClientSide;

					direction = 0x01; // server to client direction
				}

				String responseName = null;
				if (responseNameItem != null)
					responseName = responseNameItem.getNodeValue();

				zclCommandDescriptor = new ZclCommandDescriptor(id, name, frameType, manufacturerSpecific, direction,
						manufacturerCode);

				if (responseName != null) {
					zclCommandDescriptor.setResponseName(transformName(responseName));
				}

				boolean isResponse = false;

				if (isResponseItem != null)
					isResponse = Converter.parseBoolean(isResponseItem.getNodeValue());

				if (direction == 0x00)
					zclClusterDescriptor.addServerCommandDescriptor(zclCommandDescriptor);
				else
					zclClusterDescriptor.addClientCommandDescriptor(zclCommandDescriptor);

				if (isResponse) {
					zclCommandDescriptor.isResponse(true);
					zclResponses.put(zclCommandDescriptor.getName(), zclCommandDescriptor);
				}

			} else if (tag.equals("param")) {
				String name = attrs.getNamedItem("name").getNodeValue();
				String type = attrs.getNamedItem("type").getNodeValue();
				name = transformName(name);

				zclCommandDescriptor.add(new ZclField(name, type));
			} else if (tag.equals("record") || tag.equals("array")) {
				if (!this.generateRecords)
					return;

				// generate the record (shall we use the inner classes?)

			} else if (tag.equals("types")) {
				return;
			}

			NodeList children = node.getChildNodes();

			if (children != null) {
				int len = children.getLength();
				for (int i = 0; i < len; i++)
					loadXml(children.item(i));

				if (tag.equals("command")) {

				}
			}

			if (tag.equals("cluster")) {
				// resolves responses
				Vector zclServerCommandsDescriptors = zclClusterDescriptor.getZclServerCommandsDescriptors();
				Iterator it = zclServerCommandsDescriptors.iterator();
				while (it.hasNext()) {
					ZclCommandDescriptor zclServerCommandDescriptor = (ZclCommandDescriptor) it.next();
					System.out.println("Server Command Descriptor " + zclServerCommandDescriptor.getName());

					if (zclServerCommandDescriptor.getResponseName() != null) {
						// look for the response
						ZclCommandDescriptor response = (ZclCommandDescriptor) zclResponsesClientSide
								.get(zclServerCommandDescriptor.getResponseName());

						if (response != null)
							zclServerCommandDescriptor.setResponse(response);
					}
				}

				Vector zclClientCommandsDescriptors = zclClusterDescriptor.getZclClientCommandsDescriptors();
				it = zclClientCommandsDescriptors.iterator();
				while (it.hasNext()) {
					ZclCommandDescriptor zclClientCommandDescriptor = (ZclCommandDescriptor) it.next();
					if (zclClientCommandDescriptor.getResponseName() != null) {
						// look for the response
						ZclCommandDescriptor response = (ZclCommandDescriptor) zclResponsesServerSide
								.get(zclClientCommandDescriptor.getResponseName());

						if (response != null)
							zclClientCommandDescriptor.setResponse(response);
					}
				}
			}
			break;

		case Node.TEXT_NODE:
			break;
		}
	}

	private JType zb2javaType(JCodeModel jCodeModel, String type) throws Exception {
		if (type.endsWith("8")) {
			return jCodeModel.SHORT;
		} else if (type.endsWith("16")) {
			return jCodeModel.INT;
		} else if (type.endsWith("24")) {
			return jCodeModel.INT;
		} else if (type.endsWith("32")) {
			return jCodeModel.LONG;
		} else if (type.equals("octets")) {
			return jCodeModel.BYTE.array();
		} else
			throw new Exception("Unsupported type " + type);
	}

	private Class getJdkClassForZbTypeName(String type) throws Exception {
		if (type.endsWith("48")) {
			return BigInteger.class;
		} else if (type.endsWith("8")) {
			return Short.class;
		} else if (type.endsWith("16")) {
			return Integer.class;
		} else if (type.endsWith("24")) {
			return Integer.class;
		} else if (type.endsWith("32")) {
			return Long.class;
		} else if (type.equals("octets")) {
			return byte[].class;
		} else if (type.equals("boolean")) {
			return Boolean.class;
		} else
			throw new Exception("Unsupported type " + type);
	}

	private JType jTypeForZbTypename(JCodeModel jCodeModel, JPackage jPackage, String type, Object o) throws Exception {
		if (type.endsWith("48")) {
			return jCodeModel.LONG;
		} else if (type.endsWith("8")) {
			return jCodeModel.SHORT;
		} else if (type.endsWith("16")) {
			return jCodeModel.INT;
		} else if (type.endsWith("24")) {
			return jCodeModel.INT;
		} else if (type.endsWith("32")) {
			return jCodeModel.LONG;
		} else if (type.equals("octets")) {
			return jCodeModel.parseType("byte[]");
		} else if (type.equals("boolean")) {
			return jCodeModel.BOOLEAN;
		} else if (type.equals("string")) {
			return jCodeModel._ref(String.class);
		} else if (type.equals("utctime")) {
			return jCodeModel.LONG;
		} else if (type.equals("ieee")) {
			return jCodeModel.parseType("byte[]");
		} else {
			// creates the new type if not existent
			JClass jClass = null;
			try {
				System.out.println("Check for type 1 " + jPackage.name() + "." + type);
				jClass = jPackage.ref(type);
				// jClass = jCodeModel.ref(jPackage.name() + "." + type);
			} catch (Exception e) {
				// class not present, creates it
				System.out.println("Generating type  '" + type + "'");
				return generateType(jCodeModel, jPackage, type, o);
			}
			return jClass;
		}
	}

	private JClass getZclDataTypeJClass(JCodeModel jCodeModel, JPackage jPackage, JClass jClass, String type, Object o)
			throws Exception {

		if (type.equals("bitmap8")) {
			return jModel.ref(ZclDataTypeBitmap8.class);
		} else if (type.equals("bitmap16")) {
			return jModel.ref(ZclDataTypeBitmap16.class);
		} else if (type.equals("bitmap24")) {
			return jModel.ref(ZclDataTypeBitmap24.class);
		} else if (type.equals("bitmap32")) {
			return jModel.ref(ZclDataTypeBitmap32.class);
		} else if (type.equals("bitmap8")) {
			return jModel.ref(ZclDataTypeBitmap8.class);
		} else if (type.equals("enum8")) {
			return jModel.ref(ZclDataTypeEnum8.class);
		} else if (type.equals("enum16")) {
			return jModel.ref(ZclDataTypeEnum16.class);
		} else if (type.equals("uint8")) {
			return jModel.ref(ZclDataTypeUI8.class);
		} else if (type.equals("uint16")) {
			return jModel.ref(ZclDataTypeUI16.class);
		} else if (type.equals("uint24")) {
			return jModel.ref(ZclDataTypeUI24.class);
		} else if (type.equals("uint32")) {
			return jModel.ref(ZclDataTypeUI32.class);
		} else if (type.equals("uint48")) {
			return jModel.ref(ZclDataTypeUI48.class);
		} else if (type.equals("int8")) {
			return jModel.ref(ZclDataTypeI8.class);
		} else if (type.equals("int16")) {
			return jModel.ref(ZclDataTypeI16.class);
		} else if (type.equals("int24")) {
			return jModel.ref(ZclDataTypeI24.class);
		} else if (type.equals("int32")) {
			return jModel.ref(ZclDataTypeI32.class);
		} else if (type.equals("octets")) {
			return jModel.ref(ZclDataTypeOctets.class);
		} else if (type.equals("boolean")) {
			return jModel.ref(ZclDataTypeBoolean.class);
		} else if (type.equals("string")) {
			return jModel.ref(ZclDataTypeString.class);
		} else if (type.equals("utctime")) {
			return jModel.ref(ZclDataTypeUTCTime.class);
		} else if (type.equals("ieee")) {
			return jModel.ref(ZclDataTypeIEEEAddress.class);
		} else if (type.startsWith("uint") || type.startsWith("int") || type.startsWith("bitmap") || type.startsWith("enum")) {
			throw new Exception("Unsupported standard type '" + type + "'");
		} else if (type.startsWith("array of")) {
			return null;
		} else {
			// creates the new type if not existent
			JClass jZclClass = null;
			try {
				System.out.println("Check for type 2 " + jPackage.name() + "." + type);
				jZclClass = jPackage.ref("Zcl" + type); // it is the only way I
				// was able to make it
				// work!
			} catch (Exception e) {
				// class not present, creates it
				System.out.println("Generating Zcl type 'Zcl" + type + "'");
				return generateZclType(jPackage, jClass, type, o);
			}
			return jZclClass;
		}
	}

	private JClass jTypeForZigBeeTypeName(JCodeModel jCodeModel, JPackage jPackage, JClass jClass, String type) throws Exception {
		if (type.endsWith("48")) {
			return jModel.ref(long.class);
		} else if (type.endsWith("8")) {
			return jModel.ref(short.class);
		} else if (type.endsWith("16")) {
			return jModel.ref(int.class);
		} else if (type.endsWith("24")) {
			return jModel.ref(int.class);
		} else if (type.endsWith("32")) {
			return jModel.ref(long.class);
		} else if (type.equals("octets")) {
			return jModel.ref(byte[].class);
		} else if (type.equals("boolean")) {
			return jModel.ref(boolean.class);
		} else if (type.equals("string")) {
			return jModel.ref(String.class);
		} else if (type.equals("ieee")) {
			return jModel.ref(byte[].class);
		} else {
			// creates the new type if not existent
			JClass jZclClass = null;
			try {
				System.out.println("Check for type 3 " + jPackage.name() + "." + type);
				jZclClass = jModel.ref(jPackage.name() + "." + type);
			} catch (Exception e) {
				// class not present, creates it
				System.out.println("Generating type1 '" + type + "'");
				return jPackage._class(type);
			}

			return jZclClass;
		}
	}

	private JExpression jNullValue4ZigBeeTypeName(JCodeModel jCodeModel, String type) throws Exception {
		if (type.endsWith("48")) {
			return JExpr.lit(0);
		} else if (type.endsWith("8")) {
			return JExpr.lit(0);
		} else if (type.endsWith("16")) {
			return JExpr.lit(0);
		} else if (type.endsWith("24")) {
			return JExpr.lit(0);
		} else if (type.endsWith("32") || type.equals("utctime")) {
			return JExpr.lit(0);
		} else if (type.equals("octets")) {
			return JExpr.lit(null);
		} else if (type.equals("boolean")) {
			return JExpr.lit(true);
		} else if (type.equals("string")) {
			return JExpr.lit(0);
		} else if (type.equals("ieee")) {
			return JExpr.lit(0);
		} else {
			// creates the new type if not existent
			JClass jZclClass = null;
			return null;
		}
	}

	private JType getJDKJClass(JCodeModel jCodeModel, JType type) throws Exception {
		if (type.equals(jCodeModel.LONG)) {
			return jModel.ref(Long.class);
		} else if (type.equals(jCodeModel.INT)) {
			return jModel.ref(Integer.class);
		} else if (type.equals(jCodeModel.SHORT)) {
			return jModel.ref(Short.class);
		} else if (type.equals(jCodeModel.BOOLEAN)) {
			return jModel.ref(Boolean.class);
		} else
			return null;
	}

	private JType generateType(JCodeModel jModel, JPackage jPackage, String type, Object o) throws Exception {

		JDefinedClass jDefinedClass;
		try {
			jDefinedClass = jPackage._class(type);
		} catch (Exception e) {
			return jModel.ref(jPackage.name() + "." + type);
		}

		if (o instanceof ZclCommandDescriptor) {
			boolean isArray = false;

			jDefinedClass.constructor(JMod.PUBLIC);
			JMethod constructor = jDefinedClass.constructor(JMod.PUBLIC);

			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) o;
			Vector zclParametersDescriptors = zclCommandDescriptor.getParametersDescriptors();
			for (int i = 0; i < zclParametersDescriptors.size(); i++) {
				ZclField zclParameterDescriptor = (ZclField) zclParametersDescriptors.get(i);
				String paramDescriptorType = zclParameterDescriptor.getType();

				if (paramDescriptorType.endsWith("[]")) {
					// this is an array
					isArray = true;
				}

				JType jType = jTypeForZbTypename(jModel, jPackage, zclParameterDescriptor.getType(), null);
				jDefinedClass.field(JMod.PUBLIC, jType, zclParameterDescriptor.getName());
				constructor.param(jType, zclParameterDescriptor.getName());

				String source = "this." + zclParameterDescriptor.getName() + " = " + zclParameterDescriptor.getName() + ";";
				constructor.body().directStatement(source);
			}
		}

		System.out.println("Generated class " + type);

		return jDefinedClass;
	}

	protected JClass generateZclType(JPackage jPackage, JClass jClass, String type, Object o) throws Exception {

		JDefinedClass jZclDefinedClass = null;

		try {
			jZclDefinedClass = jPackage._class("Zcl" + type);
		} catch (Exception e) {
			return jPackage._getClass("Zcl" + type);
		}

		// JDefinedClass jDefinedClass = null;

		// try {
		// jDefinedClass = jPackage._class(type);
		// } catch (Exception e) {
		// System.out.println("AAA");
		// return jModel.ref(jPackage.name() + "." + type);
		// }

		JClass jType = jModel.ref(jClass._package().name() + "." + type);
		JType jZclFrameInterface = jModel._ref(IZclFrame.class);

		JMethod jZclParseMethod = jZclDefinedClass.method(JMod.PUBLIC | JMod.STATIC, jType, "zclParse");
		JVar jVarZclFrameParseMethod = jZclParseMethod.param(jZclFrameInterface, "zclFrame");
		jZclParseMethod._throws(ZclValidationException.class);

		JBlock jBodyParseMethod = jZclParseMethod.body();

		JMethod jZclSerializeMethod = jZclDefinedClass.method(JMod.PUBLIC | JMod.STATIC, jModel.VOID, "zclSerialize");
		JVar jZclFrameSerializeVar = jZclSerializeMethod.param(jZclFrameInterface, "zclFrame");
		JVar jZclParamSerializeMethod = jZclSerializeMethod.param(jType, "r");
		jZclSerializeMethod._throws(ZclValidationException.class);

		JMethod jZclSizeMethod = jZclDefinedClass.method(JMod.PUBLIC | JMod.STATIC, jModel.INT, "zclSize");
		JVar jZclParamSizeMethod = jZclSizeMethod.param(jType, "r");
		jZclSizeMethod._throws(ZclValidationException.class);

		if (o instanceof ZclCommandDescriptor) {

			JVar jParseMethodVar = jBodyParseMethod.decl(jType, "r").init(JExpr._new(jType));

			ZclCommandDescriptor zclCommandDescriptor = (ZclCommandDescriptor) o;
			Vector zclParametersDescriptors = zclCommandDescriptor.getParametersDescriptors();

			JVar jSizeVar = jZclSizeMethod.body().decl(jModel.INT, "size").init(JExpr.lit(0));

			for (int i = 0; i < zclParametersDescriptors.size(); i++) {
				ZclField zclParameterDescriptor = (ZclField) zclParametersDescriptors.get(i);

				JClass jZclFieldType = getZclDataTypeJClass(jModel, jPackage, jClass, zclParameterDescriptor.getType(), null);
				if (jZclFieldType == null) {
					jZclParseMethod.body().directStatement(
							"// FIXME: unable handle parameter '" + zclParameterDescriptor.getName() + "' of type '"
									+ zclParameterDescriptor.getType() + "'");
					continue;
				}

				jZclParseMethod.body().assign(jParseMethodVar.ref(zclParameterDescriptor.getName()),
						jZclFieldType.staticInvoke("zclParse").arg(jVarZclFrameParseMethod));

				jZclSerializeMethod.body().staticInvoke(jZclFieldType, "zclSerialize").arg(jZclFrameSerializeVar)
						.arg(jZclParamSerializeMethod.ref(zclParameterDescriptor.getName()));

				// NEWWWWWWW
				jZclSizeMethod.body().assignPlus(jSizeVar,
						jZclFieldType.staticInvoke("zclSize").arg(jZclParamSizeMethod.ref(zclParameterDescriptor.getName())));

			}
			jZclParseMethod.body()._return(jParseMethodVar);
			jZclSizeMethod.body()._return(jSizeVar); // NEWWWWW

			// jZclSizeMethod.body().directStatement("// FIXME: put here the correct size");
			// jZclSizeMethod.body()._throw(
			// JExpr._new(jModel.ref(ZclValidationException.class)).arg("Not completely implemented type"));
			// jZclSizeMethod.body()._return(jSizeVar);
		}

		return jZclDefinedClass;
	}

	private Class jClassForZigBeeDataTypeName(JCodeModel jCodeModel, JPackage jPackage, JClass jClass, String type)
			throws Exception {
		if (type.endsWith("48")) {
			return ZclDataTypeUI48.class;
		} else if (type.endsWith("8")) {
			return ZclDataTypeUI8.class;
		} else if (type.endsWith("16")) {
			return ZclDataTypeUI16.class;
		} else if (type.endsWith("24")) {
			return ZclDataTypeUI24.class;
		} else if (type.endsWith("32")) {
			return ZclDataTypeUI32.class;
		} else if (type.equals("octets")) {
			return ZclDataTypeOctets.class;
		} else if (type.equals("boolean")) {
			return ZclDataTypeBoolean.class;
		} else if (type.equals("string")) {
			return ZclDataTypeString.class;
		} else if (type.equals("utctime")) {
			return ZclDataTypeUTCTime.class;
		} else if (type.equals("ieee")) {
			return ZclDataTypeIEEEAddress.class;
		} else {
			System.out.println("DI QUI NON DOVREBBE PASSARE!!!!" + type);
			// return jClass;
			return ZclDataTypeUTCTime.class;
		}
	}

	// Method to get JType based on any String Value
	public JType getTypeDetailsForCodeModel(JCodeModel jCodeModel, String type) {
		if (type.equals("Unsigned32")) {
			return jCodeModel.LONG;
		} else if (type.equals("Unsigned64")) {
			return jCodeModel.LONG;
		} else if (type.equals("Integer32")) {
			return jCodeModel.INT;
		} else if (type.equals("Integer64")) {
			return jCodeModel.LONG;
		} else if (type.equals("Enumerated")) {
			return jCodeModel.INT;
		} else if (type.equals("Float32")) {
			return jCodeModel.FLOAT;
		} else if (type.equals("Float64")) {
			return jCodeModel.DOUBLE;
		} else {
			return null;
		}
	}

	/**
	 * Transform the passed string in a valid java identifier. It does:
	 * <ul>
	 * <li>remove blanks</li>
	 * <li>removes parenthesis</li>
	 * </ul>
	 * 
	 * @param identifier
	 * @return a valid java identifier
	 */

	private String transformName(String identifier) {
		// String sText = identifier.replaceAll("\\s+", "");
		String sText = identifier.replaceAll("/", " ");
		sText = sText.replaceAll("\\(", " ");
		sText = sText.replaceAll("\\)", " ");
		String[] components = sText.split(" ");

		sText = "";
		for (int i = 0; i < components.length; i++) {
			if (components[i].length() > 0)
				sText += components[i].substring(0, 1).toUpperCase() + components[i].substring(1);
		}

		return sText;
	}

	private void dumpCodeModel(JCodeModel clusterModel) {
		File file = new File(outputPath);
		file.mkdirs();
		try {
			clusterModel.build(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getClusterFullname(ZclClusterDescriptor zclClusterDescriptor) {
		return zclClusterDescriptor.getPackageName().toLowerCase() + "." + zclClusterDescriptor.getName();
	}

	private String getClusterImplementationName(ZclClusterDescriptor zclClusterDescriptor, boolean isServer) {
		if (isServer)
			return zclClusterDescriptor.getName() + "ServerConnection";
		else
			return zclClusterDescriptor.getName() + "ClientConnection";
	}

	private String getClusterProxyName(ZclClusterDescriptor zclClusterDescriptor, boolean isServer) {
		if (isServer)
			return zclClusterDescriptor.getName() + "ServerCluster";
		else
			return zclClusterDescriptor.getName() + "ClientCluster";
	}

	private String getZclClusterImplementationName(ZclClusterDescriptor zclClusterDescriptor, boolean isServer) {
		if (isServer)
			return "Zcl" + zclClusterDescriptor.getName() + "Server";
		else
			return "Zcl" + zclClusterDescriptor.getName() + "Client";
	}

	private String getClusterInterfaceName(ZclClusterDescriptor zclClusterDescriptor, boolean isServer) {
		if (isServer)
			return zclClusterDescriptor.getName() + "Server";
		else
			return zclClusterDescriptor.getName() + "Client";
	}

	private String getZclClassesPackageName(ZclClusterDescriptor zclClusterDescriptor) {
		return (zclRootPackage + "." + zclClusterDescriptor.getPackageName()).toLowerCase();
	}

	private String getInterfaceClassesPackageName(ZclClusterDescriptor zclClusterDescriptor) {
		return (interfacesPackageName + "." + zclClusterDescriptor.getPackageName()).toLowerCase();
	}

	private String getCommandName(ZclCommandDescriptor zclCommandDescriptor) {
		return "exec" + zclCommandDescriptor.getName();
	}

	private String getParseCommandName(ZclCommandDescriptor zclCommandDescriptor) {
		return "parse" + zclCommandDescriptor.getName();
	}

	private String attributeGetterName(ZclAttributeDescr zclAttributeDescriptor) {
		return "get" + zclAttributeDescriptor.getName();
	}

	private String attributeSetterName(ZclAttributeDescr zclAttributeDescriptor) {
		return "set" + zclAttributeDescriptor.getName();
	}

	private String getAttributeNameConstant(String attributeName) {
		return "ATTR_" + attributeName + "_NAME";
	}

	private String getCommandNameConstant(String commandName) {
		return "CMD_" + commandName + "_NAME";
	}

	private static void printUsage() {
		// System.out.println("version " + version);
		String usage = "";
		usage += "Usage: java -jar <this jar>.jar [options]\n";
		usage += "\tPossible Options are:\n";
		usage += "\t-z <zigbee package>\tpackage name of the output marshalling/unmarshalling classes\n";
		usage += "\t-i <interfaces pakage>\tpackage name of the generated interface classes\n";
		usage += "\t-s <source xml>\t\txml file containing the clusters definitions\n";
		usage += "\t-m <mode>\t\twhere <mode> can be 'common' or 'zcl'\n";
		usage += "\t-o <output directory>\tThe root directory of the generated files\n";
		usage += "\t-c <cluster name>\tgenerate only <cluster name> cluster. Default: generate all clusters\n";
		usage += "\t-x\t\t\tgenerates also the proxy classes. Proxy classes are not used anymore in SDK 3.0.x (default: do not generate)\n";
		usage += "\t-r\t\t\tgenerates the records (default: do not generate records)\n";
		usage += "\t-v\t\t\tdisplay the generator version\n";

		System.out.println(usage);
	}

	private void logDebug(String s) {
		System.out.println(s);
	}

	private static void logInfo(String s) {
		System.out.println(s);
	}
}

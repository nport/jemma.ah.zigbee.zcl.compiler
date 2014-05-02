jemma.ah.zigbee.zcl.compiler
============================

This project is part of JEMMA (https://github.com/ismb/jemma).

It contains the a Java application for generating from an xml file representation, the ZigBee Cluster Client/Server classes and interfaces suitable for being handled in JEMMA. 

NOTE: The provided documentation is still incomplete.

### License

The JEMMA code-base has been developed since 2010 by [Telecom Italia](http://www.telecomitalia.it/) which holds the Copyright on the original code base.

The full JEMMA source code, unless specified otherwise in specific files, have been released under the GNU Lesser General Public License (**LGPL**) version 3. LGPL conditions can be found on the [GNU website](https://www.gnu.org/licenses/lgpl.html).

JEMMA is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

### Getting the code

To get the you can type:

	git clone https://github.com/nport/jemma.ah.zigbee.zcl.compiler.git 

### Build the Jemma ZCL clusters compiler

To compile the code and generate the Eclipse projects you need Maven 3.x installed on your machine

To generate Eclipse project, run the command below from the git workspace:

	mvn clean package eclipse:eclipse -D eclipse.pde 

### Import the Eclipse project

This section will help you importing project you've generated at the previous step with Maven, into your Eclipse workspace.

To debug the project in Eclipse, you need to issue the following command, that adds the M2_REPO variable to the workspace:

	mvn -Declipse.workspace=<path-to-your-eclipse-workspace> eclipse:add-maven-repo

Where the <path-to-your-eclipse-workspace> is the path to the workspace you are going to import the project.

Then, open the Eclipse workspace and follows these steps:

1. Select "Import..." from "File" menu.
2. Select "General" -> "Existing Projects into Workspace"
3. Select the git workspace root as the root for the project.
5. Click "Finish".

### Run the compiler

In order to run the compiler, issue the above step "Compile the JEMMA ZCL Classes Compiler" and cd in the target directory. Then move to step "Generate the sourcecode" below.

### Installing the compiler

To install the compiler somewhere else you need to 
1. copy the main jar target/jemma.ah.zigbee.zcl.compiler-<version>.jar somewhere in your filesystem (let say <compilerr-dir>).

2. Put the codemodel-2.6.jar and java-getopt-1.0.9.jar libraries into your JVM classpath. A good place where to copy them is inside JRE installation directory, under the lib/ext folder. You can also put these libraries under <generator-dir>/libs, since the MANIFEST.MF file inside the generator jarfile, adds the libs directory to the jar classpath.

4. Open a command console and cd in the <compiler-dir> directory.

### Generate the classes from the cluster descriptions

For generating the interfaces sources issue the following command from the jemma.ah.zigbee.zcl.compiler:

	java -jar jemma.ah.zigbee.zcl.compiler-<version>.jar -m common -s <ZCL description file> -o <output dir> -i org.energy_home.jemma.ah.cluster.zigbee -c OnOff

Where: 
	 <ZCL description file> file where the OnOff cluster is defined

In order to generate the marshalling/unmarshalling classes (see the jemma project jemma.osgi.ah.zigbee) issue the following command:

	java -jar jemma.ah.zigbee.zcl.generator-<version>.jar -m zcl -s <ZCL description file> -o <output dir> -z org.energy_home.jemma.zigbee.zcl.cluster -i org.energy_home.jemma.ah.cluster.zigbee -c Test1

For instance, under Windows, to generate the interfaces issue:

	java -jar  jemma.ah.zigbee.zcl.compiler-2.0.13-SNAPSHOT.jar -m common -s test.xml -o .\output -i org.energy_home.jemma.ah.cluster.zigbee -c Test1

To generate the marshalling/unmarshalling classes:

	java -jar  jemma.ah.zigbee.zcl.compiler-2.0.13-SNAPSHOT.jar -m zcl -s test.xml -o .\output -z org.energy_home.jemma.ah.zigbee.zcl.cluster -i org.energy_home.jemma.ah.cluster.zigbee -c Test1

### XML description files

These files describes the ZCL clusters. zcl.xsd contains the schema to be used for these files. The test.xml file under the resources folder shows some valid exmples.

The <cluster> xml tag permits to specify a "status" attribute that is used by the generator in this way:
 
* "u" means that the cluster definition is still work In progress.
* "c" means that the cluster definition is complete.
* "t" means that the cluster definition is complete but not tested, yet.

The generator generates only those classes and interfaces that has the status attribute set to "c" or "t". The default value for the "status" attribute is "u".
  
This is the default value of some attributes:

Tag <attribute>:
	mandatory = "true" (by default an attribute is mandatory)
	reportable = "true" (by default an attribute is reportable)
	access = "r" (by default an attribute is ReadOnly)

Tag <command>
	mandatory = "true"
  
### Inportant Notes

The java interfaces and classes created by the compiler are compatibile with Jemma version 0.1.1 or earlier.

The tool has the following limitations:
	* No support for commands with variable fields
	* Doesn't support all the ZCL data types





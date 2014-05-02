Version 2.0.12.qualifier
------------------------

* ### Changes
    * Added missing classes. Now the generator is self contained
	* Added doc/templates folder for target templates.
	* Added -i <package name> command line option for specifying the
	  package of generated interfaces
	* Added -z <package name> command line option for specifying the 
	  package of generated marshalling/unmarshalling Zcl classes. 
      For generating zcl classes it is necessary to specify both 
	  the -z and -i command line options.
	* Generating source code for managing Read/Write attribute 
	  primitives.
	* Refactoring to remove the differences between client and 
	  server clusters.
	* Removed any hard-coded package name that were previously.
	* Added the test.xml file
	
* ### Bugs
	* Fixed some bugs into the zcl.xml file

Version 2.0.12
--------------

* ### New Features
	* Added CurrentSummationReceived attribute to the Simple Metering Cluster.
	* Added file zll.xml with the Color Control Cluster from Zigbee Light Link Spec.
	* Added missing attributes to Power Configuration Server cluster.
	* Added wulian.xml that contains some clusters specific to Wulian ZigBee devices
	* Added zcl.xsd
	* Re-Indexed the zcl.xml file according the Cluster ID. 
	* Added Poll Control, Door Lock and "Thermostat User Interface Configuration" Clusters
	* Extended On/Off and Thermostat Clusters
	* Renamed cluster name "Appliance Control" to "EN50523 Appliance Control"
	* Renamed cluster name "Appliance Identification" to "EN50523 Appliance Identification"
	* Renamed cluster name "Appliance Events and Alerts" in "EN50523 Appliance events and Alert"
	* Renabled cluster name "Simple Metering" in "Metering" 
	* Added all these clusters that were missing in the list even if they still are 
	  marked as "undefined"
	
	
* ### Bugs
	* changed zcl.xml in order to be compliant to the zcl.zsd schema file.
	* Added parameter IdentifyTime to command Identify of the Identify server cluster.
	* Time, TimeStatus, TimeZone, DstStart, DstEnd, DstShift attributes of 
	  Time Cluster must be read/write.
	
Version 2.0.11
--------------

* ### Bugs:
	* Aligned Appliance Statistics E@H specs v95 rev 1.10

Version 2.0.10
--------------
* ### Bugs
	* Added size in LocationDescription attribute
	* Added in Basic Server Cluster the DisableLocalConfig attribute
	* Aligned Appliance Control and Appliance Event and Alerts clusters to E@H specs v95 rev 1.10
	* if a parameter type is <type>[] it generates the right marshalling and unmarshalling code.
	
* ### Known Bugs:
	* The marshalling and unmarshalling code generated in Response classes is not correct,
	  if array types are used.

Version 2.0.9
-------------	
  * By default doesn't generate proxy classes anymore (i.e. <clustername>ServerCluster and <clustername>ClientCluster
  * Added -x command line option to enable proxy classes generation to be compatible with SDK [2.2.x, 3.0.0)
  
Version 2.0.8
-------------
  - Refactoring with the main purpose to use it embedded in a java user interface
 
Version 2.0.7
-------------

* ### New Features
    - Generates also complex types
    - Changed commmand line adding gnu-like options:
	    -p <output package>
	    -s <source xml>
	    -m <mode>, mode can be common or zcl
	    -o <output directory>
	    -c <cluster name> (Default: generate all clusters)
	    -r generate also record types

  - Some bugfixes on PowerProfile Cluster and on Appliance Statistics
  - Added Partitioning Cluster (not yet tested)

Version 2.0.6
-------------

  - Aligned PowerProfileCluster with 0.95 specs
  - Changed mapping of array parameters
  - Skipping array/record xml tags, since they are not supported yet
  - Added "[]" modifier in param types to represent arrays
  
Version 2.0.5
-------------

  - ### Bugfixes
 
    - Data types of Energy/Power/Time attribute of 
      Simple Metering 4Noks server cluster
    - Typo: Manifacturer -> Manufacturer
      
  - ### Changes
    
    - CLUSTER_ID field in ZigBee mapping is now public
    
  - ### New features
	- Generated classes are now OSGi Minimum 1.0 compatible

Version 2.0.4
-------------

- ### New Features
    - Zcl classes have been simplified by removing unnecessary try/catch blocks.
    - Zcl classes now implements the cache mechanism on setters and getters.
    - Generating Cluster (wrapper) classes on those clusters having at 
      least one Attribute or Command.
  


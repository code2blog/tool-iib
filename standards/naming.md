
<pre>
Rest API base path should contain version number. This way you can deploy multiple versions of the api into same integration server without conflict. 
Example: http://localhost:7800/someapi/v2/employees/
		base path -> /someapi/v2
		resource path -> /employees

Project naming should follow this template  
	Application ->  _{version}_APP
	Integration Service -> {functionality}_{version}_IS
	Rest api -> {resource}_{version}_API
	Shared library -> _{version}_SLIB
	Static library -> _{version}_LIB
	Message flow project -> _MFP
	Message set project -> _MSP
	
Naming Message Set or other project types
	Project Type indicator can be placed at end of the name such as MyAmazingProject_MSET or it can be placed at the start such as MSET_MyAmazingProject.
	Former is preferred as different project types for one business functionality such as PurchaseOrder will appear in close proxity. Sample: 
		PurchaseOrder_APP
		PurchaseOrder_JP
		PurchaseOrder_MSET
	You would not want to see project types in close proximity as shown below
		MSET_Warranty
		MSET_PurchaseOrder
		MSET_Insurance
</pre>
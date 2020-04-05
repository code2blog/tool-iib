
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
	
</pre>
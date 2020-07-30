# tool-iib
I will be using this repository to store ibm integration bus code samples that I blog about in this site https://code2blog.wordpress.com/ and for the videos that I post to my youtube channel code2blog. Link https://youtube.com/results?search_query=code2blog 

<pre>
Here are some of the projects in this repository

Person_V2_API
	per-1 -> expose rest api to interact with a database
	per-2 -> create rest api that can cater to json and non-json data
	
HelloWorld_V1_APP
	hwa-1 -> expose http endpoint and reply with static xml message of hello-user-from-iib
	hwa-2 -> demo the use of trace node
	hwa-3 -> demo pagination and looping using mq nodes
	
HelloWorld_V1_IS
	hwi-1 -> expose soap webservice
	
code2blog_dfdl_v2_slib
	dfdl-1 -> create message model for fixed length data
	
EsqlFunctions_V1_IS
	ef-1 -> demo programming in esql
	ef-2 -> demo use of translate function
	ef-3 -> regex through esql external proceduer call to java
	
NodeFeatures_V1_IS
	nf-1 : expose webservice to demo xslt feature
	nf-2 : demo the use of mapping node
	
CSV_MFP
	csv-1 : change xml message to csv format

code2blog_cobol_copybook_V1_SLIB
	cpy-1 : convert cobol copy book to iib dfdl
	
LargeMessages
	lm-1 : read a large xml and split it into smaller xmls using jcn

WindowsCommands_V1_IS
	wc-1 : write syslog events

KafkaDemo_V1_APP
	kd-1 : demo the use of kafka producer and consumer node

ParseZipFile_MFP
	zip-1 : read zip file with pdf and create multiple xml files.

SendEmail_V1_APP
	se-1 : use email output node to send email
	se-2 : send excel file as attachment
	
WSSecurityDemo_V1_APP
	wss-1 : invoke soap service and use security profile config servie to load credentials into soap header

ImageInCdata_APP
	iica-1 : extract image from xml cdata and save as png file

code2blog_node_collector_v1_app
	cn-1 : demo the use of collector node
	cn-2 : use collector node to batch received messages based on a trigger/control message
	
JdbcConnectionTester_V1_API
	jdbc-1 : create a flow that tests connection details defined in jdbc config service
	jdbc-2 : use rest api path parameter instead of query parameter
	
MessageParsing_V1_APP
	mp-1 : parse xml using ondemand parsing option
	mp-2 : parse dfdl using immediate parse option
	
Retry_APP
	r-1 : build automated retry mechanism using timer notification node
	r-2 : build scheduled retry with timeout control node 
	
jenkins
	gvy-1 : pipeline script to build and deploy bar file

standards
	std-1 : variable naming standards
	
</pre>

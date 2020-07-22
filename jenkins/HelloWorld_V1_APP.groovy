pipeline {
   agent any

   stages {
      stage('checkout code') {
         steps {
            deleteDir()
            git 'https://github.com/code2blog/tool-iib.git'
         }
      }
      stage('build bar') {
        steps {
        bat label: '', script: '''
echo off
set projectName=HelloWorld_V1_APP
"C:\\Program Files\\IBM\\IIB\\10.0.0.0\\tools\\mqsicreatebar.exe" -data . -b %projectName%.bar -a %projectName% -cleanBuild
'''
}
      }
      stage('deploy bar') {
 steps {
 bat label: '', script: '''
echo off
set workspace=%cd%
cd C:\\Program Files\\IBM\\IIB\\10.0.0.0\\server\\bin\\
call .\\mqsiprofile.cmd
cd %workspace%
mqsideploy TESTNODE_VISHNU -e default -a HelloWorld_V1_APP.bar
'''

archiveArtifacts 'HelloWorld_V1_APP.bar'
}
 }
   }
}

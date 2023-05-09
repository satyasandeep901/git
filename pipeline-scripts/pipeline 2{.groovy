pipeline {
    agent any
    environment {
        PATH = "$PATH:/usr/share/maven/bin"
    }
    stages {
        stage('git checkout') {
            steps {
                git 'https://github.com/ashokitschool/maven-web-app.git'
            }
        }
        stage('Maven Build') {
            steps {
                sh "mvn clean install"
            }
        }
        stage('SonarQube analysis') {
            steps {
            withSonarQubeEnv('Soner-Server-8.9.10') {
            sh "mvn sonar:sonar"
            }
            }
        }
        stage('Artifact') {
            steps {
               rtServer (
    id: 'Artifactory-1',
    url: 'http://13.234.48.110:8082/ui/repos/tree/General/MYJFROGREPO',
        // If you're using username and password:
    username: 'admin',
    password: 'Sandeep901@',
        // If Jenkins is configured to use an http proxy, you can bypass the proxy when using this Artifactory server:
        bypassProxy: true,
        // Configure the connection timeout (in seconds).
        // The default value (if not configured) is 300 seconds: 
        timeout: 300
)
            }
        }
        stage('upload') {
            steps {
                rtUpload (
    serverId: 'Artifactory-1',
    spec: '''{
          "files": [
            {
              "pattern": ".war",
              "target": "maven-web-app-libs-snapshot-local"
            }
         ]
    }''',

    // Optional - Associate the uploaded files with the following custom build name and build number,
    // as build artifacts.
    // If not set, the files will be associated with the default build name and build number (i.e the 
    // the Jenkins job name and number).
    buildName: 'myjob1',
    buildNumber: '32',
)
            }
        }
    }
}
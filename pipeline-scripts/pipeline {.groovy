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
        stage('jfrog repo') {
            steps {
            withJFrogEnv('frog') {
                rtserver (
                    id: "jfrog",
                    url: 'http://43.205.119.245:8081',
                    bypassProxy: true,
                    timeout: 300
                        )
            }
            }
        }
        stage('Upload') {
            steps{
                rtUpload (
                serverId: 'jfrog',
                spec: '''{
                 "files": [
                    {
                    "pattern": "*.war",
                    "target": "maven-web-app-libs-snapshot-local"
                    }
                          ]
                         }''',
                      )
            }
        }
        stage ('public build info') {
            steps {
                rtPublishBuildInfo (
                    serverId: "jfrog"
                )
            }
        }
    }
}
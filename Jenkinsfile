pipeline {
    agent any
   environment {
        PROJECT_NAME = 'Foyer'
        VERSION = '1.4.0-SNAPSHOT'
        JAR_FILE = "target/${PROJECT_NAME}-${VERSION}.jar"
    }
    triggers {
        // Trigger the pipeline every 5 minutes (you might want to change this)
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean') {
            steps {
                sh 'rm -rf target/*'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube analysis') {
            steps {
                echo "Code analysis"
                sh '''
                    mvn sonar:sonar \
                      -Dsonar.host.url=http://172.26.160.39:9000 \
                      -Dsonar.login=3f503a6dd7d75937d89375265c03df9e2478fabc
                '''
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

       stage('Upload to Nexus') {
            steps {
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: 'my.nexus.address',           // Replace with your Nexus URL
                    groupId: 'tn.esprit.spring',
                    version: "${VERSION}",
                    repository: 'RepositoryName',           // Replace with your Nexus repo name
                    credentialsId: 'CredentialsId',         // Replace with your Jenkins credentials ID
                    artifacts: [
                        [
                            artifactId: "${PROJECT_NAME}",
                            classifier: '',
                            file: "${JAR_FILE}",
                            type: 'jar'
                        ]
                    ]
                )
            }
        }
    }
    }

    post {
        success {
            echo 'Pipeline terminée avec succès !'
        }
        failure {
            echo 'La pipeline a échoué.'
        }
    }
}

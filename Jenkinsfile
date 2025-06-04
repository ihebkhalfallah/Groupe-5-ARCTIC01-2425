pipeline {
    agent any

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
                    nexusUrl: 'http://172.26.160.39:8081/',
                    groupId: 'com.mycompany.app',
                    version: '3.1.5',  // consider using readMavenPom().getVersion()
                    repository: 'maven-releases',
                    credentialsId: 'd2a4ff90-1e10-479f-8069-aaf9733697f4',
                    artifacts: [
                        [
                            artifactId: 'Foyer',
                            classifier: '',
                            file: 'target/Foyer-1.4.0-SNAPSHOT.jar',
                            type: 'jar'
                        ]
                    ]
                )
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

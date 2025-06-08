pipeline {
    agent any

    triggers {
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
                // You can remove this and rely on mvn clean
                sh 'rm -rf target/* || true'
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


stage("Upload Artifact") {
    steps {
        nexusArtifactUploader(
            nexusVersion: 'nexus3',
            protocol: 'http',
            nexusUrl: 'http://172.26.160.39:8081',
            groupId: 'QA',
            version: "${env.BUILD_ID}-${env.BUILD_TIMESTAMP}",
            repository: 'release-seconde',
            credentialsId: 'nexus',
            artifacts: [
                [
                    artifactId: 'vproapp',
                    classifier: '',
                    file: 'target/vprofile-v2.war',
                    type: 'war'
                ]
            ]
        )
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

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
                       script {
                           def pom = readMavenPom file: 'pom.xml'
                           def artifactId = pom.artifactId
                           def version = pom.version
                           def groupId = pom.groupId
                           def jarFile = "target/${artifactId}-${version}.jar"

                           nexusArtifactUploader(
                               nexusVersion: 'nexus3',
                               protocol: 'http',
                               nexusUrl: 'http://172.26.160.39:8081/',
                               groupId: groupId,
                               version: version,
                               repository: version.endsWith('-SNAPSHOT') ? 'maven-snapshots' : 'maven-releases',
                               credentialsId: 'd2a4ff90-1e10-479f-8069-aaf9733697f4',
                               artifacts: [
                                   [
                                       artifactId: artifactId,
                                       classifier: '',
                                       file: jarFile,
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

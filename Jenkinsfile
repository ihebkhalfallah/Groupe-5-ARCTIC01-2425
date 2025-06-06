pipeline {
    agent any

    triggers {
        // H/5 * * * * : Polls SCM every 5 minutes
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                // mvn clean compile will clean the target directory
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
                // Using withCredentials for sensitive SonarQube token
                // Ensure 'sonar-token' Secret text credential exists in Jenkins
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh '''
                        mvn sonar:sonar \
                          -Dsonar.host.url=http://172.26.160.39:9000 \
                          -Dsonar.login=$SONAR_TOKEN
                    '''
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests' // Tests should ideally run in the 'Test' stage, so skipping them here is fine.
            }
        }

        stage('Upload to Nexus') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    // Using the actual version from pom.xml
                    def deployVersion = pom.version
                    def artifactId = pom.artifactId
                    def groupId = pom.groupId
                    def jarFile = "target/${artifactId}-${deployVersion}.jar" // Constructing the exact JAR path

                    echo "Uploading artifact: ${jarFile}"

                    // Ensure 'nexus-creds' is a Username with password credential in Jenkins
                    // and its ID is exactly 'nexus-creds'
                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: '172.26.160.39:8081',
                        groupId: groupId, // Use variable from POM
                        version: deployVersion, // Use variable from POM
                        repository: (deployVersion.endsWith('-SNAPSHOT') ? 'maven-snapshots' : 'maven-releases'), // Dynamic repo selection
                        credentialsId: 'nexus-creds',
                        artifacts: [
                            [artifactId: artifactId,
                             classifier: '', // Leave empty if no classifier
                             file: jarFile,
                             type: 'jar'],
                            // Also upload the POM file
                            [artifactId: artifactId,
                             classifier: '',
                             file: "pom.xml", // The POM itself is usually deployed
                             type: 'pom']
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
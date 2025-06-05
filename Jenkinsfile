pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/ihebkhalfallah/Groupe-5-ARCTIC01-2425.git'
        BRANCH = 'mayssen-foyer'
        GIT_CREDENTIALS_ID = 'jenkins-pipeline'
        SONARQUBE_SERVER = 'http://172.26.160.39:9000/'
        SONAR_TOKEN = credentials('3f503a6dd7d75937d89375265c03df9e2478fabc')
        DOCKERHUB_CREDENTIALS = 'jenkins'
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Clone repository') {
            steps {
                echo "Cloning branch '${BRANCH}' from '${GIT_REPO}'"
                git credentialsId: "${GIT_CREDENTIALS_ID}", branch: "${BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Clean') {
            steps {
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
                echo "Running SonarQube analysis"
                sh """
                    mvn sonar:sonar \
                      -Dsonar.host.url=${SONARQUBE_SERVER} \
                      -Dsonar.login=${SONAR_TOKEN}
                """
            }
        }

        stage('Build JAR') {
            steps {
                echo "Packaging the application (build JAR)..."
                sh 'mvn package -DskipTests'
            }
        }

        stage('Upload to Nexus') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version
                    def artifactId = pom.artifactId
                    def groupId = pom.groupId
                    def jarFile = "target/${artifactId}-${version}.jar"

                    echo "Uploading artifact: ${jarFile}"

                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: '172.26.160.39:8081',
                        groupId: groupId,
                        version: version,
                        repository: 'maven-releases',
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
            echo '✅ Pipeline terminée avec succès !'
        }
        failure {
            echo '❌ La pipeline a échoué.'
        }
    }
}

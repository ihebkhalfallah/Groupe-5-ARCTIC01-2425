pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/ihebkhalfallah/Groupe-5-ARCTIC01-2425.git'
        BRANCH = 'foyer-mayssen'
        GIT_CREDENTIALS_ID = 'ce4c016c-23f3-4d95-8efb-716e9aacd9cc'
        SONARQUBE_SERVER = 'http://localhost:9000/'
        SONAR_TOKEN = 'b6c4f4e1c1c5cce48fed642c92ac36d93b98f6f0'
        VERSION = "1.4.${BUILD_ID}-SNAPSHOT"
        DOCKER_HUB_CREDENTIALS = 'mayssendockerhub'
        IMAGE_NAME = 'backendtest'
        IMAGE_TAG = "1.4.${BUILD_ID}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: "${BRANCH}", credentialsId: "${GIT_CREDENTIALS_ID}", url: "${GIT_REPO}"
            }
        }

        stage('Set Version') {
            steps {
                sh "mvn versions:set -DnewVersion=${VERSION}"
            }
        }

        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo "🔍 Code analysis with SonarQube"
                sh "mvn sonar:sonar -Dsonar.projectKey=Foyer -Dsonar.host.url=${SONARQUBE_SERVER} -Dsonar.login=${SONAR_TOKEN}"
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version
                    def repo = version.contains("SNAPSHOT") ? "maven-snapshots" : "maven-releases"
                    def repoUrl = "http://172.26.160.39:8081/repository/${repo}"

                    sh "mvn deploy -DskipTests"
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                echo '📦 Push Docker image to Docker Hub'
                withCredentials([usernamePassword(
                    credentialsId: DOCKER_HUB_CREDENTIALS,
                    usernameVariable: 'DOCKERHUB_USERNAME',
                    passwordVariable: 'DOCKERHUB_PASSWORD'
                )]) {
                    retry(2) {
                        timeout(time: 3, unit: 'MINUTES') {
                            sh "echo ${DOCKERHUB_PASSWORD} | docker login -u ${DOCKERHUB_USERNAME} --password-stdin"
                            sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
                            sh "docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
                            sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest"
                            sh "docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest"
                        }
                    }
                }
            }
        }

        stage('Docker Compose Up') {
            steps {
                echo '🚀 Starting services with Docker Compose'
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
            }
        }

        stage('Clean Docker Images') {
            steps {
                sh 'docker image prune -f'
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
        always {
            cleanWs()
        }
    }
}

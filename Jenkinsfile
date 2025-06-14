pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/ihebkhalfallah/Groupe-5-ARCTIC01-2425.git'
        BRANCH = 'zouhour-bloc-TEST'
        GIT_CREDENTIALS_ID = 'Github token'
        SONARQUBE_SERVER = 'http://localhost:9000/'
        SONAR_TOKEN = 'sqa_4dd18f4166c832547eabb4ec673277d9c0471341'
        DOCKERHUB_CREDENTIALS = 'dockerhub-credentials'
        IMAGE_NAME = 'foyer'
        IMAGE_TAG = 'latest'
    }

    stages {
        stage('Clone repository') {
            steps {
                echo "Cloning branch '${BRANCH}' from '${GIT_REPO}'"
                git credentialsId: "${GIT_CREDENTIALS_ID}", branch: "${BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Clean target directory') {
            steps {
                echo "Cleaning target directory..."
                sh 'mvn clean'
            }
        }

        stage('Run database') {
            steps {
                echo "Starting services with Docker Compose..."
                // sh 'docker compose stop mysql'
                sh 'docker compose up -d database phpmyadmin --build'
                sh 'sleep 60'
            }
        }


        stage('Compile source code') {
            steps {
                echo "Compiling source code..."
                sh 'mvn compile'
            }
        }

        stage('SonarQube analysis') {
            steps {
                echo "Code analysis"
                sh "mvn sonar:sonar -Dsonar.url=${SONARQUBE_SERVER} -Dsonar.login=${SONAR_TOKEN}"
            }
        }

        stage('Test') {
            steps {
                echo "Running tests..."
                sh 'mvn test'
            }
        }

        stage('Build JAR') {
            steps {
                echo "Packaging the application..."
                sh 'mvn package -DskipTests'
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying artifact to remote repository..."
                sh 'mvn deploy -DskipTests'
            }
        }


        stage('Building image') {
            steps {
                echo 'building ...'
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Push docker image') {
            steps {
                echo 'push stage ... >>>>>>>>>>'
                withCredentials([usernamePassword(
                    credentialsId: DOCKERHUB_CREDENTIALS,
                    usernameVariable: 'DOCKERHUB_USERNAME',
                    passwordVariable: 'DOCKERHUB_PASSWORD'
                )]) {
                    echo 'Login ...'
                    sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
                    echo 'Tagging image...'
                    sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
                    echo 'Pushing docker image to docker hub...'
                    sh "docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }

        stage('Run docker compose') {
            steps {
                echo "Starting services with Docker Compose..."
                // sh 'docker compose stop backend'
                sh 'docker compose up -d --build app'
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Please check the logs.'

        }
        always {
            echo '🔁 Pipeline execution finished.'
            echo 'Cleaning up Docker containers...'

        }
    }
}
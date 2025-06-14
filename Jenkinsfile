def get_change_html() {
    def changeLogSets = currentBuild.changeSets
    def html = ""
    
    for (changeLogSet in changeLogSets) {
        for (entry in changeLogSet.items) {
            html += "<li>${entry.commitId.substring(0,8)}: ${entry.msg} - <em>${entry.author}</em></li>"
        }
    }
    
    if (html == "") {
        html = "<li>No changes detected</li>"
    }
    return html
}

pipeline {
    agent any
    
    options {
        skipDefaultCheckout()
    }

    environment {
        GIT_REPO = 'https://github.com/ihebkhalfallah/Groupe-5-ARCTIC01-2425.git'
        BRANCH = 'iheb-universite'
        GIT_CREDENTIALS_ID = 'jenkins-pipeline'
        SONARQUBE_SERVER = 'http://localhost:9000/'
        SONAR_TOKEN = '708fc9b61e2b497e28e25d1af64c293236651373'
        DOCKERHUB_CREDENTIALS = 'token-docker-hub'
        IMAGE_NAME = 'backend'
        IMAGE_TAG = 'latest'
        EMAIL_RECIPIENT = 'khalfallahiheb@gmail.com'
        BUILD_DISPLAY_NAME = "${JOB_NAME} #${BUILD_NUMBER}"
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
                echo "Starting database service with docker compose..."
                sh 'docker compose up -d database phpmyadmin --build'
                sh 'sleep 30'
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
                sh 'mvn package'
            }
        }
        
        stage('Deploy') {
            steps {
                echo "Deploying artifact to remote repository..."
                sh 'mvn deploy -DskipTests'
            }
        }
        
        stage('Building image'){
            steps{
                echo 'building ...';
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }
        
        stage('Push docker image'){
            steps{
                withCredentials([usernamePassword(credentialsId: DOCKERHUB_CREDENTIALS,
                usernameVariable: 'DOCKERHUB_USERNAME', 
                passwordVariable: 'DOCKERHUB_PASSWORD')]){
                    echo 'Login ...'
                    sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"

                    echo 'Tagging image...'
                    sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"

                    echo 'Pushing docker image to docker hub...'
                    sh "docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }
        
        stage('Run prod docker compose') {
            steps {
                echo "Starting services with Docker Compose..."
                sh 'docker compose up -d --build backend front'
            }
        }
    }

    post {
success {
            echo '✅ Pipeline completed successfully!'
            emailext (
                subject: "SUCCESS: Pipeline '${BUILD_DISPLAY_NAME}'",
                body: """
                    <h2>✅ Jenkins Pipeline Succeeded</h2>
                    <p><b>Project:</b> ${JOB_NAME}</p>
                    <p><b>Build Number:</b> <a href="${BUILD_URL}">#${BUILD_NUMBER}</a></p>
                    <p><b>Status:</b> <span style="color:green;font-weight:bold">SUCCESS</span></p>
                    <p><b>Duration:</b> ${currentBuild.durationString}</p>
                    <p><b>Changes:</b></p>
                    <ul>
                    ${get_change_html()}
                    </ul>
                    <p>View full logs: <a href="${BUILD_URL}console">${BUILD_URL}console</a></p>
                """,
                to: "${EMAIL_RECIPIENT}",
                mimeType: 'text/html'
            )
        }
        failure {
            echo '❌ Pipeline failed. Please check the logs.'
            emailext (
                subject: "FAILED: Pipeline '${BUILD_DISPLAY_NAME}'",
                body: """
                    <h2>❌ Jenkins Pipeline Failed</h2>
                    <p><b>Project:</b> ${JOB_NAME}</p>
                    <p><b>Build Number:</b> <a href="${BUILD_URL}">#${BUILD_NUMBER}</a></p>
                    <p><b>Status:</b> <span style="color:red;font-weight:bold">FAILURE</span></p>
                    <p><b>Duration:</b> ${currentBuild.durationString}</p>
                    <p><b>Last Changes:</b></p>
                    <ul>
                    ${get_change_html()}
                    </ul>
                    <p><b>Failed Stage:</b> ${currentBuild.currentResult}</p>
                    <p>View error logs: <a href="${BUILD_URL}console">${BUILD_URL}console</a></p>
                    <p>Investigate immediately to prevent deployment issues.</p>
                """,
                to: "${EMAIL_RECIPIENT}",
                mimeType: 'text/html'
            )
        }
    }
}




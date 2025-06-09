pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/ihebkhalfallah/Groupe-5-ARCTIC01-2425.git'
        BRANCH = 'molka-etudiant'
        GIT_CREDENTIALS_ID = 'github-token'       // Your Jenkins credential ID for GitHub token
        SONARQUBE_SERVER_NAME = 'sq1'              // The name you gave your SonarQube server in Jenkins system config
        SONAR_PROJECT_KEY = 'groupe5-arctic01-2425-molka-etudiant'
    }

    stages {
        stage('Clone Git Repository') {
            steps {
                echo "Cloning branch '${BRANCH}' from repository '${GIT_REPO}'"
                git credentialsId: "${GIT_CREDENTIALS_ID}", branch: "${BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Clean and Compile') {
            steps {
                echo "Running mvn clean compile"
                sh 'mvn clean compile'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo "Running SonarQube analysis on project '${SONAR_PROJECT_KEY}'"
                withSonarQubeEnv("${SONARQUBE_SERVER_NAME}") {
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY}
                    """
                }
            }
        }

        stage('Post Actions') {
            steps {
                echo 'Pipeline completed successfully!'
            }
        }
    }
}

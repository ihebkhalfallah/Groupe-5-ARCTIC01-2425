pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/ihebkhalfallah/Groupe-5-ARCTIC01-2425.git'
        BRANCH = 'molka-etudiant'
        GIT_CREDENTIALS_ID = 'github-token'

        SONARQUBE_SERVER_NAME = 'sq1'
        SONAR_PROJECT_KEY = 'groupe5-arctic01-2425-molka-etudiant'

        NEXUS_REPO_URL = 'http://localhost:8081/repository/jenkins-nexus/'
        NEXUS_CREDENTIALS_ID = 'nexus'

        DOCKER_IMAGE_NAME = 'groupe5-arctic01-2425'
        DOCKER_IMAGE_TAG = 'latest'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                echo "Checking out branch '${BRANCH}' from '${GIT_REPO}'"
                git credentialsId: "${GIT_CREDENTIALS_ID}", branch: "${BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Get code from Repo') {
            steps {
                echo "Cloned repo and ready to build"
            }
        }

        stage('Maven Clean') {
            steps {
                echo "Running mvn clean"
                sh 'mvn clean'
            }
        }

        stage('Maven Build') {
            steps {
                echo "Running mvn compile"
                sh 'mvn compile'
            }
        }

        stage('Maven Test') {
            steps {
                echo "Running mvn test"
                sh 'mvn test'
            }
        }

        stage('Sonar Test') {
            steps {
                echo "Running SonarQube analysis"
                withSonarQubeEnv("${SONARQUBE_SERVER_NAME}") {
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY}
                    """
                }
            }
        }

        stage('Create Package') {
            steps {
                echo "Creating package (jar/war)"
                sh 'mvn package'
            }
        }

        stage('Publish to Nexus') {
            steps {
                echo "Publishing artifact to Nexus"
                withCredentials([usernamePassword(credentialsId: "${NEXUS_CREDENTIALS_ID}", usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh '''
                        mkdir -p ~/.m2
                        cat > ~/.m2/settings.xml <<EOF
<settings>
  <servers>
    <server>
      <id>nexus</id>
      <username>${NEXUS_USER}</username>
      <password>${NEXUS_PASS}</password>
    </server>
  </servers>
</settings>
EOF
                    '''
                }

                sh """
                    mvn deploy -DaltDeploymentRepository=nexus::default::${NEXUS_REPO_URL}
                """
            }
        }
    
        stage('Docker Build') {
                steps {
                    echo "Building Docker image ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ."
                }


        stage('Docker Push') {
            steps {
                echo "Pushing Docker image (local push)"
                sh 'echo "No external registry configured, image built locally."'
            }
        }

        stage('Docker Compose') {
            steps {
                echo "Running Docker Compose"
                sh 'docker compose up -d'
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed (post block).'
        }
        success {
            echo '✔️ Pipeline finished successfully.'
        }
        failure {
            echo '❌ Pipeline failed. Check logs for details.'
        }
    }
}

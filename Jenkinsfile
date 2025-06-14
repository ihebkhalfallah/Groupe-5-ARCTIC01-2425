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
                echo "Workspace is: ${env.WORKSPACE}"
                sh 'ls -la'
            }
        }

        stage('Maven Clean') {
            steps {
                echo "Running mvn clean"
                sh 'mvn clean'
            }
        }

        stage('Maven Compile') {
            steps {
                echo "Running mvn compile"
                sh 'mvn compile'
            }
        }

        stage('Maven Verify (tests + jacoco report)') {
            steps {
                echo "Running mvn verify with test profile (generates jacoco report)"
                sh 'mvn verify -Dspring.profiles.active=test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_SERVER_NAME}") {
                    echo "Running SonarQube analysis"
                    sh """
                        ls -l target/site/jacoco/jacoco.xml
                        mvn sonar:sonar \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.host.url=$SONAR_HOST_URL \
                            -Dsonar.login=$SONAR_AUTH_TOKEN \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        stage('Package') {
            steps {
                echo "Packaging without tests"
                sh 'mvn package -Dmaven.test.skip=true'
            }
        }

        stage('Publish to Nexus') {
            steps {
                echo "Publishing artifact to Nexus (skipping tests)"
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
                    mvn deploy -Dmaven.test.skip=true -DaltDeploymentRepository=nexus::default::${NEXUS_REPO_URL}
                """
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker image ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
                sh "docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ."
            }
        }

        stage('Docker Push') {
            steps {
                echo "Pushing Docker image (local push)"
                sh 'echo "No external registry configured, image built locally."'
            }
        }

        stage('Docker Compose') {
            steps {
                echo "Stopping any existing backend container and starting new containers with docker-compose"
                sh '''
                    docker rm -f foyer-backend || true
                    BUILD_ID=${BUILD_ID} docker compose up -d
                '''
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

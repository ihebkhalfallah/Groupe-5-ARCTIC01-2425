pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-11'
    }

    environment {
        DOCKER_IMAGE = "zouhourrouissi/foyer:latest"
        DOCKER_TAG = "${BUILD_NUMBER}"
        SONAR_SCANNER_HOME = tool 'SonarQube-Scanner'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-token',
                    url: 'https://github.com/ihebkhalfallah/Groupe-5-ARCTIC01-2425.git'
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
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    publishCoverage adapters: [jacocoAdapter('target/site/jacoco/jacoco.xml')], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        $SONAR_SCANNER_HOME/bin/sonar-scanner \
                        -Dsonar.projectKey=demo \
                        -Dsonar.projectName=demo \
                        -Dsonar.projectVersion=1.0 \
                        -Dsonar.sources=src/main/java \
                        -Dsonar.tests=src/test/java \
                        -Dsonar.language=java \
                        -Dsonar.java.binaries=target/classes \
                        -Dsonar.java.test.binaries=target/test-classes \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USERNAME')]) {
                    sh '''
                        mvn deploy -DskipTests \
                        -Dmaven.repo.local=/tmp/.m2 \
                        -s /tmp/settings.xml
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://registry-1.docker.io/v2/', 'dockerhub-credentials') {
                        def image = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                        image.push()
                        image.push("latest")
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh '''
                    # Update docker-compose.yml with new image tag
                    sed -i "s|image: ${DOCKER_IMAGE}:latest|image: ${DOCKER_IMAGE}:${DOCKER_TAG}|g" docker-compose.yml

                    # Deploy services
                    docker-compose down || true
                    docker-compose up -d

                    # Wait for services to be ready
                    sleep 30
                '''
            }
        }

        stage('API Tests') {
            steps {
                script {
                    // Wait for application to be ready
                    sh '''
                        timeout 300 bash -c '
                        while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' localhost:8080/api/users)" != "200" ]]; do
                            echo "Waiting for application to be ready..."
                            sleep 5
                        done'
                    '''

                    // Test API endpoints
                    sh '''
                        # Test POST - Create user
                        curl -X POST http://localhost:8080/api/users \
                        -H "Content-Type: application/json" \
                        -d '{"name":"Test User","email":"test@example.com"}' \
                        -w "HTTP Status: %{http_code}\\n"

                        # Wait a moment
                        sleep 2

                        # Test GET - Retrieve users
                        curl -X GET http://localhost:8080/api/users \
                        -H "Content-Type: application/json" \
                        -w "HTTP Status: %{http_code}\\n"
                    '''
                }
            }
        }
    }

    post {
        always {
            sh 'docker-compose logs app || true'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
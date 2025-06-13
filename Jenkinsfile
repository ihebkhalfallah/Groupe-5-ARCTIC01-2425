pipeline {

    agent any

    environment {

        GIT_REPO = 'https://github.com/ihebkhalfallah/Groupe-5-ARCTIC01-2425.git'

        BRANCH = 'foyer-mayssen'

        GIT_CREDENTIALS_ID = 'ce4c016c-23f3-4d95-8efb-716e9aacd9cc'

        SONARQUBE_SERVER = 'http://localhost:9000/'

        SONAR_TOKEN = 'b6c4f4e1c1c5cce48fed642c92ac36d93b98f6f0'

        VERSION = "1.4.0-${env.BUILD_ID}-SNAPSHOT"

        DOCKER_HUB_CREDENTIALS = 'dockerhubtahani'

        IMAGE_NAME = 'backendtesttoutou'

        IMAGE_TAG = 'latest'

        // Docker configuration

        DOCKER_BUILDKIT = '1'

        BUILDKIT_PROGRESS = 'plain'

    }

    stages {

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

                echo "Code analysis with SonarQube"

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

                script {

                    echo 'Building Docker image...'

                    sh """

                        # Build with BuildKit for better performance

                        docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .

                        # Verify image was built

                        docker images | grep ${IMAGE_NAME}

                    """

                }

            }

        }

        stage('Push Docker Image') {

            steps {

                script {

                    echo 'Starting Docker push stage...'

                    withCredentials([usernamePassword(

                        credentialsId: DOCKER_HUB_CREDENTIALS,

                        usernameVariable: 'DOCKERHUB_USERNAME',

                        passwordVariable: 'DOCKERHUB_TOKEN'

                    )]) {

                        sh """

                            set -e

                            echo 'Logging in to Docker Hub...'

                            echo '${DOCKERHUB_TOKEN}' | docker login -u '${DOCKERHUB_USERNAME}' --password-stdin

                            echo 'Tagging image...'

                            docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}

                            echo 'Image details:'

                            docker inspect ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} --format='{{.Size}}' | numfmt --to=iec

                            echo 'Pushing image in chunks...'

                            # Try pushing with specific registry

                            docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}

                        """

                        // Verify push was successful

                        sh """

                            echo 'Verifying push...'

                            docker pull ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} > /dev/null 2>&1 && echo 'Push verified successfully' || echo 'Push verification failed'

                        """

                    }

                }

            }

            post {

                always {

                    sh 'docker logout || true'

                }

                failure {

                    script {

                        echo 'Docker push failed. Trying alternative approach...'

                        // Alternative: Save image as tar and push

                        withCredentials([usernamePassword(

                            credentialsId: DOCKER_HUB_CREDENTIALS,

                            usernameVariable: 'DOCKERHUB_USERNAME',

                            passwordVariable: 'DOCKERHUB_TOKEN'

                        )]) {

                            sh """

                                echo 'Attempting alternative push method...'

                                # Save image to tar

                                docker save ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} > image.tar

                                # Load and push

                                docker load < image.tar

                                # Login again

                                echo '${DOCKERHUB_TOKEN}' | docker login -u '${DOCKERHUB_USERNAME}' --password-stdin

                                # Try push again with longer timeout

                                timeout 600 docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}

                                # Cleanup

                                rm -f image.tar

                                docker logout

                            """

                        }

                    }

                }

            }

        }

        stage('Docker Compose') {

            steps {

                sh 'docker-compose down || true'

                sh 'docker-compose up -d'

            }

        }

    }

    post {

        always {

            // Clean up Docker resources

            sh '''

                docker system prune -f || true

                docker volume prune -f || true

            '''

        }

        success {

            echo '✅ Pipeline completed successfully!'

        }

        failure {

            echo '❌ Pipeline failed.'

            // Additional debugging information

            sh '''

                echo "=== Docker Info ==="

                docker info || true

                echo "=== Network Info ==="

                netstat -rn || true

                echo "=== DNS Info ==="

                nslookup registry-1.docker.io || true

            '''

        }

    }

}

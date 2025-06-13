pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/ihebkhalfallah/Groupe-5-ARCTIC01-2425.git'
        BRANCH = 'foyer-mayssen'
        GIT_CREDENTIALS_ID = 'ce4c016c-23f3-4d95-8efb-716e9aacd9cc'
        SONARQUBE_SERVER = 'http://localhost:9000/'
        SONAR_TOKEN = 'b6c4f4e1c1c5cce48fed642c92ac36d93b98f6f0'
        VERSION = "1.4.0-${env.BUILD_ID}-SNAPSHOT"
        DOCKER_HUB_CREDENTIALS = 'dockerhub'
        IMAGE_NAME = 'backendtest'
        IMAGE_TAG = 'latest'
    }

   // triggers {
     //   pollSCM('H/5 * * * *')
    //}

    stages {



     /*   stage('Checkout') {
            steps {
                checkout scm
            }
        }*/

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
                 sh """
                     docker system prune -af || true
                     docker build -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} .
                 """
             }
         }

         stage('Push Docker Image') {
             steps {
                 withCredentials([usernamePassword(
                     credentialsId: DOCKER_HUB_CREDENTIALS,
                     usernameVariable: 'DOCKERHUB_USERNAME',
                     passwordVariable: 'DOCKERHUB_PASSWORD'
                 )]) {
                     sh """
                         echo ${DOCKERHUB_PASSWORD} | docker login --username ${DOCKERHUB_USERNAME} --password-stdin
                         docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}
                     """
                 }
             }
         }

         stage('Docker Compose Up') {
             steps {
                 sh """
                     docker-compose down || true
                     docker-compose up -d --build
                 """
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

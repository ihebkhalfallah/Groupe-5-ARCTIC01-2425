pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/ihebkhalfallah/Groupe-5-ARCTIC01-2425.git'
        BRANCH = 'molka-etudiant'
        GIT_CREDENTIALS_ID = 'jenkins-pipeline'
        SONARQUBE_SERVER = 'http://localhost:9000'
        SONAR_TOKEN = 'e8361cc9ef5279f90b3a753e89690a1ec86aba19'
    }

    stages {
        stage('Cloner le dépôt Git') {
            steps {
                echo "Clonage de la branche '${BRANCH}' depuis le dépôt '${GIT_REPO}'"
                git credentialsId: "${GIT_CREDENTIALS_ID}", branch: "${BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Nettoyage et compilation') {
            steps {
                echo "Exécution de mvn clean compile"
                sh 'mvn clean compile'
            }
        }

        stage('Analyse SonarQube') {
            steps {
                echo "Lancement de l’analyse SonarQube"
                sh """
                    mvn sonar:sonar \
                        -Dsonar.projectKey=molka-projet \
                        -Dsonar.host.url=${SONARQUBE_SERVER} \
                        -Dsonar.login=${SONAR_TOKEN}
                """
            }
        }

        stage('Lancer un conteneur MySQL') {
            steps {
                echo "Démarrage d’un conteneur MySQL pour les tests"
                sh '''
                    docker run --rm --name mysql-test-container \
                        -e MYSQL_ROOT_PASSWORD=root \
                        -e MYSQL_DATABASE=testdb \
                        -e MYSQL_USER=testuser \
                        -e MYSQL_PASSWORD=testpass \
                        -p 3306:3306 -d mysql:5.7
                '''
                echo "Pause pour laisser MySQL démarrer correctement"
                sh 'sleep 25'
            }
        }

        stage('Exécution des tests unitaires') {
            steps {
                echo "Lancement des tests avec Maven"
                sh 'mvn test'
            }
        }

        stage('Création du package') {
            steps {
                echo "Génération du package (JAR/WAR)"
                sh 'mvn package'
            }
        }

        stage('Déploiement avec Docker Compose') {
            steps {
                echo "Exécution de docker-compose up"
                sh '''
                    docker-compose down || true
                    docker-compose up -d --build
                '''
            }
        }

        stage('Déploiement final') {
            steps {
                echo "Déploiement de l’application via Maven deploy"
                sh 'mvn deploy'
            }
        }
    }

    post {
        success {
            echo 'Pipeline exécuté avec succès.'
        }
        failure {
            echo 'Le pipeline a échoué.'
        }
        always {
            echo 'Nettoyage post-exécution.'
            sh 'docker-compose down || true'
        }
    }
}
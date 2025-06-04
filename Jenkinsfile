pipeline {
    agent any
environment {
    DOCKER_HOST = 'unix:///var/run/docker.sock'
}

    triggers {
        // Déclenche la pipeline à chaque push sur la branche principale
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean') {
            steps {
                sh 'rm -rf target/*'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }
stage('Docker version') {
  steps {
    sh 'docker version'
  }
}

        stage('Start MySQL Container') {
            steps {
                sh '''
                    docker rm -f mysql-container || true
                    docker run --name mysql-container \
                        -e MYSQL_DATABASE=foyer \
                        -e MYSQL_ALLOW_EMPTY_PASSWORD=yes \
                        -p 3306:3306 \
                        -d mysql:5.7

                    # Wait for MySQL to be ready
                    echo "Waiting for MySQL to start..."
                    for i in {1..10}; do
                        docker exec mysql-container mysqladmin ping --silent && break
                        sleep 5
                    done
                '''
            }
        }

        stage('Test') {
            steps {
                // Exécution des tests unitaires (Mockito, JUnit, etc.)
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                // Création du livrable (ex: jar, war...)
                sh 'mvn package -DskipTests'
            }
        }
    }

    post {
        always {
            echo 'Arrêt et suppression du conteneur MySQL...'
            sh 'docker rm -f mysql-container || true'
        }
        success {
            echo 'Pipeline terminée avec succès !'
        }
        failure {
            echo 'La pipeline a échoué.'
        }
    }
}

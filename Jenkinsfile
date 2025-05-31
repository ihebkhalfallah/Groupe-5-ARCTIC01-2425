pipeline {
    agent any

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

        stage('Test') {
            steps {
                // Exécution des tests unitaires (Mockito, JUnit, etc.)
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                // Création du livrable après avoir validé les tests
                sh 'mvn package'
            }
        }
    }

    post {
        success {
            echo 'Pipeline terminée avec succès !'
        }
        failure {
            echo 'La pipeline a échoué.'
        }
    }
}

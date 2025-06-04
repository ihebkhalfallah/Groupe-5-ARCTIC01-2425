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
                // Création du livrable (ex: jar, war...)
                sh 'mvn package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Ici, 'SonarQubeServer' doit correspondre au nom donné dans la config Jenkins > Manage Jenkins > Configure System > SonarQube servers
                withSonarQubeEnv('SonarQubeServer') {
                    sh 'mvn sonar:sonar'
                }
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

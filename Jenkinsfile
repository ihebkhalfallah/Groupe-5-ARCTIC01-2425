pipeline {
    agent any
    environment {
        SONARQUBE_SERVER = 'http://172.26.160.39/:9000/'
        SONAR_TOKEN ='3f503a6dd7d75937d89375265c03df9e2478fabc'
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

        stage('SonarQube analysis') {
            steps {
                echo "Code analysis"
                sh "mvn sonar:sonar -Dsonar.url=${SONARQUBE_SERVER} -Dsonar.login=${SONAR_TOKEN}"
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

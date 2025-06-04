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
stage('SonarQube analysis') {
    steps {
        echo "Code analysis"
        sh '''
            mvn sonar:sonar \
              -Dsonar.host.url=http://172.26.160.39:9000 \
              -Dsonar.login=3f503a6dd7d75937d89375265c03df9e2478fabc
        '''
    }
}

         stage('Package') {
                   steps {
                       // Création du livrable (ex: jar, war...)
                       sh 'mvn package  -DskipTests'
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

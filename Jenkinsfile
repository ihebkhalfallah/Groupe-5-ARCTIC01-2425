pipeline {
    agent any

    triggers {
        // Déclenche la pipeline à chaque push sur la branche principale (ex: master)
        pollSCM('H/5 * * * *') // vérifie toutes les 5 minutes, tu peux aussi utiliser webhook côté Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                // Récupération du code source depuis Git
                checkout scm
            }
        }

        stage('Clean') {
            steps {
                // Suppression du contenu du dossier target
                sh 'rm -rf target/*'
            }
        }

        stage('Compile') {
            steps {
                // Compilation - exemple avec Maven
                sh 'mvn clean compile'
            }
        }

        stage('Package') {
            steps {
                // Création du livrable (ex: jar, war...)
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

pipeline {
    agent any

    stages {
        stage('Hello') {
            steps {
                echo 'Hello World'
            }
        }

        stage('Checkout GIT') {
            steps {
                echo 'Pulling...'
                git branch: 'main',
                    url: 'https://github.com/ihebkhalfallah/Groupe-5-ARCTIC01-2425.git'
                // Ajoute `credentialsId` ici si besoin
            }
        }

        stage('Testing Maven') {
            steps {
                sh 'mvn -version'
            }
        }
    }
}


pipeline {
    agent any

    environment {
        IMAGE_NAME = 'carpool-matcher:latest'
        CONTAINER_NAME = 'carpool-matcher-container'
        MAVEN_HOME = 'C:\\Apache\\maven'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Maven Build and Test') {
            steps {
                bat '"C:\\Apache\\maven\\bin\\mvn.cmd" clean test package'
            }
        }

        stage('Verify Frontend in Build') {
            steps {
                bat 'dir target\\classes\\static'
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t %IMAGE_NAME% .'
            }
        }

        stage('Deploy Container') {
            steps {
                bat 'docker rm -f %CONTAINER_NAME% 2>NUL || exit /b 0'
                bat 'docker run -d --name %CONTAINER_NAME% -p 8083:8080 %IMAGE_NAME%'
            }
        }

        stage('Verify Deployment') {
            steps {
                bat 'docker ps'
            }
        }
    }

    post {
        success {
            echo 'Campus Carpool Matcher CI/CD Pipeline completed successfully!'
        }

        failure {
            echo 'CI/CD Pipeline failed.'
        }
    }
}

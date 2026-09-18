pipeline {
    agent any

    environment {
        IMAGE_NAME = 'student-management:latest'
        CONTAINER_NAME = 'student-management-container'
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

        stage('Docker Build') {
            steps {
                bat 'docker build -t %IMAGE_NAME% .'
            }
        }

        stage('Deploy Container') {
            steps {
                bat 'docker rm -f %CONTAINER_NAME% 2>NUL || exit /b 0'
                bat 'docker run -d --name %CONTAINER_NAME% -p 8081:8080 %IMAGE_NAME%'
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
            echo 'CI/CD Pipeline completed successfully!'
        }

        failure {
            echo 'CI/CD Pipeline failed.'
        }
    }
}
pipeline {
    agent any

    environment {
        APP_NAME = 'premium-calculator'
        IMAGE_NAME = 'premium-calculator'
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'
        DOCKER_IMAGE_TAG = "${env.BUILD_NUMBER ?: 'latest'}"
        DEPLOY_PORT = '8080'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    stages {

        stage('1. Checkout') {
            steps {
                echo "--> Stage 1: Checking out source code from Git repository..."
                checkout scm
                script {
                    echo "Checked out commit: ${env.GIT_COMMIT ?: 'Local Workspace'}"
                    echo "Branch: ${env.BRANCH_NAME ?: env.GIT_BRANCH ?: 'develop'}"
                }
            }
        }

        stage('2. Verify Java & Maven') {
            steps {
                echo "--> Stage 2: Verifying runtime toolchains (Java & Maven Wrapper)..."
                script {
                    if (isUnix()) {
                        sh 'java -version'
                        sh './mvnw -v'
                    } else {
                        bat 'java -version'
                        bat 'call mvnw.cmd -v'
                    }
                }
            }
        }

        stage('3. Run Tests') {
            steps {
                echo "--> Stage 3: Running Unit and Integration Tests..."
                script {
                    if (isUnix()) {
                        sh './mvnw clean test'
                    } else {
                        bat 'call mvnw.cmd clean test'
                    }
                }
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
                failure {
                    echo "ERROR: Unit or Integration tests failed. Halting pipeline execution."
                }
            }
        }

        stage('4. Package Application') {
            steps {
                echo "--> Stage 4: Packaging Spring Boot JAR (skipping test repetition)..."
                script {
                    if (isUnix()) {
                        sh './mvnw package -DskipTests'
                    } else {
                        bat 'call mvnw.cmd package -DskipTests'
                    }
                }
            }
        }

        stage('5. Archive Artifact') {
            steps {
                echo "--> Stage 5: Archiving packaged Spring Boot executable JAR..."
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('6. Build Docker Image') {
            steps {
                echo "--> Stage 6: Building Docker container image (${IMAGE_NAME}:${DOCKER_IMAGE_TAG})..."
                script {
                    if (isUnix()) {
                        sh "docker build -t ${IMAGE_NAME}:${DOCKER_IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
                    } else {
                        bat "docker build -t ${IMAGE_NAME}:${DOCKER_IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
                    }
                }
            }
        }

        stage('7. Deploy Container') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                echo "--> Stage 7: Deploying container on target environment..."
                script {
                    if (isUnix()) {
                        sh """
                            docker stop ${APP_NAME} || true
                            docker rm ${APP_NAME} || true
                            docker run -d --name ${APP_NAME} -p ${DEPLOY_PORT}:8080 --restart unless-stopped ${IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                        """
                    } else {
                        bat """
                            docker stop ${APP_NAME} || ver>nul
                            docker rm ${APP_NAME} || ver>nul
                            docker run -d --name ${APP_NAME} -p ${DEPLOY_PORT}:8080 --restart unless-stopped ${IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                        """
                    }
                }
            }
        }

        stage('8. Verify Health') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                echo "--> Stage 8: Verifying application health via Actuator endpoint..."
                sleep time: 10, unit: 'SECONDS'
                script {
                    if (isUnix()) {
                        sh 'curl --retry 5 --retry-delay 3 --fail http://localhost:8080/actuator/health || exit 1'
                    } else {
                        bat 'curl --retry 5 --retry-delay 3 --fail http://localhost:8080/actuator/health'
                    }
                }
            }
        }

    }

    post {
        success {
            echo "SUCCESS: Premium Calculator CI/CD pipeline finished successfully!"
        }
        failure {
            echo "FAILURE: Premium Calculator pipeline encountered an error. Check logs above."
        }
    }
}

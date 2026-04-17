pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'Java21'
    }

    environment {
        DOCKERHUB_REPO = 'dhanushreddy17/ecommerce-order-management'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        K8S_NAMESPACE = 'default'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn -B clean verify'
                    } else {
                        bat 'mvn -B clean verify'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            docker build -t ${DOCKERHUB_REPO}:${IMAGE_TAG} .
                            docker tag ${DOCKERHUB_REPO}:${IMAGE_TAG} ${DOCKERHUB_REPO}:latest
                        '''
                    } else {
                        bat '''
                            docker build -t %DOCKERHUB_REPO%:%IMAGE_TAG% .
                            docker tag %DOCKERHUB_REPO%:%IMAGE_TAG% %DOCKERHUB_REPO%:latest
                        '''
                    }
                }
            }
        }

        stage('Push Image to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    script {
                        if (isUnix()) {
                            sh '''
                                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                                docker push ${DOCKERHUB_REPO}:${IMAGE_TAG}
                                docker push ${DOCKERHUB_REPO}:latest
                                docker logout
                            '''
                        } else {
                            powershell '''
                                $env:DOCKER_PASS | docker login -u $env:DOCKER_USER --password-stdin
                                docker push $env:DOCKERHUB_REPO`:$env:IMAGE_TAG
                                docker push $env:DOCKERHUB_REPO`:latest
                                docker logout
                            '''
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            when {
                branch 'main'
            }
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG_FILE')]) {
                    script {
                        if (isUnix()) {
                            sh '''
                                export KUBECONFIG="$KUBECONFIG_FILE"
                                kubectl apply -f k8s/deployment.yaml -n ${K8S_NAMESPACE}
                                kubectl apply -f k8s/service.yaml -n ${K8S_NAMESPACE}
                                kubectl set image deployment/ecommerce-order-management ecommerce-order-management=${DOCKERHUB_REPO}:${IMAGE_TAG} -n ${K8S_NAMESPACE}
                                kubectl rollout status deployment/ecommerce-order-management -n ${K8S_NAMESPACE}
                            '''
                        } else {
                            powershell '''
                                $env:KUBECONFIG = $env:KUBECONFIG_FILE
                                kubectl apply -f k8s/deployment.yaml -n $env:K8S_NAMESPACE
                                kubectl apply -f k8s/service.yaml -n $env:K8S_NAMESPACE
                                kubectl set image deployment/ecommerce-order-management ecommerce-order-management=$env:DOCKERHUB_REPO`:$env:IMAGE_TAG -n $env:K8S_NAMESPACE
                                kubectl rollout status deployment/ecommerce-order-management -n $env:K8S_NAMESPACE
                            '''
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
        }
    }
}
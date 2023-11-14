pipeline {
    agent any
    tools {
        maven 'M395'
    }
    stages {
        stage ('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Ben-Israel/java-k8s.git'
                echo '======= Echo Git Checkout PASSED! ======='
            }
        }
        stage ('Maven Build & Test') {
            steps {
                sh 'mvn clean package'
                echo '======= Echo Maven Build PASSED! ======='
            }
        }
        stage ('SonarQube Code Analysis') {
            // environment {
            //     SONAR_URL = "http://34.123.19.40:9000/"
            // }
            steps {
                withSonarQubeEnv(credentialsId: 'sonarqube', installationName: 'mySonarQube') {
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.0.2155:sonar'
                    echo "Sonar Host is ${env.SONAR_HOST_URL}"
                    echo '======= Echo SONAR Code Analysis PASSED! ======='
                }
                // withCredentials([string(credentialsId: 'sonarqube', variable: 'SONAR_AUTH_TOKEN')]) {
                //     sh 'cd springboot-app && mvn sonar:sonar -Dsonar.login=$SONAR_AUTH_TOKEN -Dsonar.host.url=${SONAR_URL}'
                // }
            }
        }
        stage ('Docker Build & Push Image') {
            environment {
                DOCKER_IMAGE = "benisrael/java-k8s:${BUILD_NUMBER}"
                REGISTRY_CREDENTIALS = credentials('docker-cred')
            }
            steps {
                script {
                    sh 'docker build -t ${DOCKER_IMAGE} .'
                    def dockerImage = docker.image("${DOCKER_IMAGE}")
                    docker.withRegistry('https://index.docker.io/v1/',"docker-cred") {
                        dockerImage.push()
                    }
                    echo '======= Echo DOCKER Build and Push PASSED! ======='
                }
                // withCredentials([usernamePassword(credentialsId: 'docker-cred', passwordVariable: 'dockerpwd', usernameVariable: 'dockerusername')]) {
                //     sh 'docker build -t benisrael/java-k8s .'
                // }
            }
        }
        stage ('Update Kubernetes Deployment File') {
            environment {
                GIT_REPO_NAME = "java-k8s"
                GIT_USER_NAME = "Ben-Israel"
            }
            steps {
                withCredentials([string(credentialsId: 'github-cred', variable: 'githubcred')]) {
                    sh '''
                    git config user.email "benny.dh@gmail.com"
                    git config user.name "Ben Israel"
                    BUILD_NUMBER=${BUILD_NUMBER}
                    sed -i "s/replaceImageTag/${BUILD_NUMBER}/g" deployment.yaml
                    git add deployment.yaml
                    git commit -m "Update deployment image to version ${BUILD_NUMBER}"
                    git push https://${githubcred}@github.com/${GIT_USER_NAME}/${GIT_REPO_NAME} HEAD:main
                    '''
                }
                echo '======= Kubernetes Deployment File Update PASSED! ======='
            }
        }
    }
}
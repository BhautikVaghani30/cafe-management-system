// Jenkinsfile - Final Secure Version

pipeline {
    agent any

    environment {
        PROJECT_ID               = "code-with-intellij-gemini"
        GCP_REGION               = "us-central1"
        REPO_NAME                = "cafe-management-repo"
        IMAGE_NAME               = "cafe-management-system"
        STAGING_SERVICE_NAME     = "cafe-management-system-staging"
        // New: The dedicated service account for the app to use at runtime
        APP_SERVICE_ACCOUNT      = "cafe-app-runner@code-with-intellij-gemini.iam.gserviceaccount.com"
        GCP_CREDENTIALS_ID       = "gcp-jenkins-creds"
        INSTANCE_CONNECTION_NAME = "code-with-intellij-gemini:us-central1:namaste-village-001"
        DB_NAME                  = "namastevillage"
        DB_USER                  = "namaste-village-001"
        MAIL_USER                = "codewithbhautik01@gmail.com"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                checkout scm
            }
        }

        stage('Static Code Analysis') {
            steps {
                echo "Running static code analysis with Checkstyle..."
                sh 'mvn checkstyle:check'
            }
        }

        stage('Build & Unit Test') {
            steps {
                echo "Building with Maven and skipping integration tests..."
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    def DOCKER_IMAGE = "${GCP_REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:${env.BUILD_NUMBER}"
                    echo "Building and pushing Docker image: ${DOCKER_IMAGE}"

                    sh "docker build -t ${DOCKER_IMAGE} ."

                    withCredentials([file(credentialsId: GCP_CREDENTIALS_ID, variable: 'GCP_KEY_FILE')]) {
                        sh "gcloud auth activate-service-account --key-file=${GCP_KEY_FILE}"
                        sh "gcloud auth configure-docker ${GCP_REGION}-docker.pkg.dev -q"
                        sh "docker push ${DOCKER_IMAGE}"
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                script {
                    def DOCKER_IMAGE = "${GCP_REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:${env.BUILD_NUMBER}"
                    echo "Deploying to Staging Environment..."
                    withCredentials([file(credentialsId: GCP_CREDENTIALS_ID, variable: 'GCP_KEY_FILE')]) {
                        sh "gcloud auth activate-service-account --key-file=${GCP_KEY_FILE}"
                        sh """
                        gcloud run deploy ${STAGING_SERVICE_NAME} \\
                            --image=${DOCKER_IMAGE} \\
                            --region=${GCP_REGION} \\
                            --platform=managed \\
                            --service-account=${APP_SERVICE_ACCOUNT} \\
                            --allow-unauthenticated \\
                            --add-cloudsql-instances=${INSTANCE_CONNECTION_NAME} \\
                            --set-env-vars=INSTANCE_CONNECTION_NAME=${INSTANCE_CONNECTION_NAME},DB_NAME=${DB_NAME},spring.datasource.username=${DB_USER},spring.mail.username=${MAIL_USER} \\
                            --set-secrets=spring.datasource.password=db-password:latest,jwt.secret=jwt-secret:latest,spring.mail.password=mail-password:latest \\
                            --project=${PROJECT_ID}
                        """
                    }
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                script {
                    def DOCKER_IMAGE = "${GCP_REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:${env.BUILD_NUMBER}"
                    echo "Deploying to Production Environment..."
                    withCredentials([file(credentialsId: GCP_CREDENTIALS_ID, variable: 'GCP_KEY_FILE')]) {
                        sh "gcloud auth activate-service-account --key-file=${GCP_KEY_FILE}"
                        sh """
                        gcloud run deploy ${IMAGE_NAME} \\
                            --image=${DOCKER_IMAGE} \\
                            --region=${GCP_REGION} \\
                            --platform=managed \\
                            --service-account=${APP_SERVICE_ACCOUNT} \\
                            --allow-unauthenticated \\
                            --add-cloudsql-instances=${INSTANCE_CONNECTION_NAME} \\
                            --set-env-vars=INSTANCE_CONNECTION_NAME=${INSTANCE_CONNECTION_NAME},DB_NAME=${DB_NAME},spring.datasource.username=${DB_USER},spring.mail.username=${MAIL_USER} \\
                            --set-secrets=spring.datasource.password=db-password:latest,jwt.secret=jwt-secret:latest,spring.mail.password=mail-password:latest \\
                            --project=${PROJECT_ID}
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up workspace..."
            cleanWs()
        }
    }
}
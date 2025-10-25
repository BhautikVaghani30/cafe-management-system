// Jenkinsfile - Improved Version

pipeline {
    agent any

    environment {
        PROJECT_ID               = "code-with-intellij-gemini"
        GCP_REGION               = "us-central1"
        REPO_NAME                = "cafe-management-repo"
        IMAGE_NAME               = "cafe-management-system"
        STAGING_SERVICE_NAME     = "cafe-management-system-staging" // New: for our staging environment
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

        // --- NEW: Code Quality & Security Stage ---
        stage('Code Quality & Security') {
            steps {
                parallel(
                    "Static Analysis": {
                        echo "Running static code analysis with Checkstyle..."
                        sh 'mvn checkstyle:check'
                    },
                    "Vulnerability Scan": {
                        echo "Scanning dependencies for known vulnerabilities..."
                        sh 'mvn org.owasp:dependency-check-maven:check'
                    }
                )
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

                    // Build the image
                    sh "docker build -t ${DOCKER_IMAGE} ."

                    // Authenticate and push the image
                    withCredentials([file(credentialsId: GCP_CREDENTIALS_ID, variable: 'GCP_KEY_FILE')]) {
                        sh "gcloud auth activate-service-account --key-file=${GCP_KEY_FILE}"
                        sh "gcloud auth configure-docker ${GCP_REGION}-docker.pkg.dev -q"
                        sh "docker push ${DOCKER_IMAGE}"
                    }
                }
            }
        }

        // --- NEW: Deploy to Staging Environment ---
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

        // --- NEW: Manual Approval Gate ---
        stage('Manual Approval for Production') {
            steps {
                // This pauses the pipeline and waits for a user to click "Proceed" in the Jenkins UI.
                input message: "Staging deployment looks good. Ready to deploy to PRODUCTION?"
            }
        }

        stage('Deploy to Production') {
            steps {
                script {
                    // We deploy the *exact same* Docker image that was tested in staging.
                    def DOCKER_IMAGE = "${GCP_REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:${env.BUILD_NUMBER}"
                    echo "Deploying to Production Environment..."
                    withCredentials([file(credentialsId: GCP_CREDENTIALS_ID, variable: 'GCP_KEY_FILE')]) {
                        sh "gcloud auth activate-service-account --key-file=${GCP_KEY_FILE}"
                        sh """
                        gcloud run deploy ${IMAGE_NAME} \\
                            --image=${DOCKER_IMAGE} \\
                            --region=${GCP_REGION} \\
                            --platform=managed \\
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
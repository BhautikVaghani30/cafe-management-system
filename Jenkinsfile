// Jenkinsfile

pipeline {
    agent any // Runs on any available Jenkins agent

    // Define environment variables used throughout the pipeline
    environment {
        PROJECT_ID              = "code-with-intellij-gemini" // <-- CONFIRM THIS IS YOUR GCP PROJECT ID
        GCP_REGION              = "us-central1"
        REPO_NAME               = "cafe-management-repo"
        IMAGE_NAME              = "cafe-management-system"
        GCP_CREDENTIALS_ID      = "gcp-jenkins-creds" // The ID you created in Jenkins
        INSTANCE_CONNECTION_NAME= "code-with-intellij-gemini:us-central1:namaste-village-001"
        DB_NAME                 = "namastevillage"
        DB_USER                 = "namaste-village-001"
        MAIL_USER               = "codewithbhautik01@gmail.com"
    }

    stages {
        stage('Checkout') {
            steps {
                // This stage is handled by Jenkins automatically when using "Pipeline script from SCM"
                echo "Checking out source code..."
            }
        }

         stage('Build Application') {
            steps {
                // Build the Java application, but skip tests that require a live DB connection.
                echo "Building with Maven and skipping integration tests..."
                sh 'mvn clean package -DskipTests'
            }
         }

        stage('Build Docker Image') {
            steps {
                script {
                    // Define the full image tag using the Jenkins build number for versioning
                    def DOCKER_IMAGE = "${GCP_REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:${env.BUILD_NUMBER}"
                    echo "Building Docker image: ${DOCKER_IMAGE}"
                    
                    // Build the Docker image
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }

        stage('Push to Artifact Registry') {
            steps {
                script {
                    def DOCKER_IMAGE = "${GCP_REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:${env.BUILD_NUMBER}"
                    echo "Pushing Docker image to Artifact Registry..."
                    
                    // Authenticate Docker with GCP using the service account credentials
                    withCredentials([file(credentialsId: GCP_CREDENTIALS_ID, variable: 'GCP_KEY_FILE')]) {
                        sh "gcloud auth activate-service-account --key-file=${GCP_KEY_FILE}"
                        sh "gcloud auth configure-docker ${GCP_REGION}-docker.pkg.dev -q"
                        
                        // Push the image
                        sh "docker push ${DOCKER_IMAGE}"
                    }
                }
            }
        }

        stage('Deploy to Cloud Run') {
            steps {
                script {
                    def DOCKER_IMAGE = "${GCP_REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:${env.BUILD_NUMBER}"
                    echo "Deploying to Cloud Run..."
                    
                    // Use the same credentials to deploy to Cloud Run
                    withCredentials([file(credentialsId: GCP_CREDENTIALS_ID, variable: 'GCP_KEY_FILE')]) {
                        sh "gcloud auth activate-service-account --key-file=${GCP_KEY_FILE}"
                        
                        // Run the gcloud deploy command, mirroring your cloudbuild.yaml
                        sh """
                        gcloud run deploy ${IMAGE_NAME} \\
                            --image=${DOCKER_IMAGE} \\
                            --region=${GCP_REGION} \\
                            --platform=managed \\
                            --allow-unauthenticated \\
                            --add-cloudsql-instances=${INSTANCE_CONNECTION_NAME} \\
                            --set-env-vars=DB_NAME=${DB_NAME},spring.datasource.username=${DB_USER},spring.mail.username=${MAIL_USER} \\
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
            // Clean up workspace after build
            echo "Cleaning up workspace..."
            cleanWs()
        }
    }
}
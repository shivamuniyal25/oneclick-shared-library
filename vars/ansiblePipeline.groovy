def call(Map config = [:]) {

    pipeline {
        agent any

        stages {

            stage('Load Config') {
                steps {
                    script {
                        envConfig = readProperties file: 'resources/config/pipeline.conf'
                    }
                }
            }

            stage('Clone Repository') {
                steps {
                    echo "Cloning repository..."
                }
            }

            stage('User Approval') {
                when {
                    expression { envConfig.KEEP_APPROVAL_STAGE == 'true' }
                }
                steps {
                    input message: "Do you want to proceed with deployment to ${envConfig.ENVIRONMENT}?"
                }
            }

            stage('Config Loaded') {
                steps {
                    echo "Environment: ${envConfig.ENVIRONMENT}"
                    echo "Code base path: ${envConfig.CODE_BASE_PATH}"
                    echo "Approval enabled: ${envConfig.KEEP_APPROVAL_STAGE}"
                }
            }

            stage('Run Ansible Playbook') {
                steps {
                    echo "Executing Ansible playbook for ${envConfig.ENVIRONMENT}"

                    sh """
                    ansible-playbook \
                    -i ${envConfig.CODE_BASE_PATH}/inventory \
                    ${envConfig.CODE_BASE_PATH}/playbook.yml
                    """
                }
            }

        }
    }
}

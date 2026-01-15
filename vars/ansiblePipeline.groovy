def call(Map config = [:]) {

    def envConfig = [:]

    pipeline {
        agent any

        stages {

            stage('Load Config') {
                steps {
                    script {
                        def configText = libraryResource 'config/pipeline.conf'
                        envConfig = readProperties text: configText
                    }
                }
            }

            stage('Clone Repository') {
                steps {
                    git url: 'https://github.com/shivamuniyal25/elasticsearch-automation.git',
                        branch: 'main'
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
                      ${envConfig.CODE_BASE_PATH}/playbook.yml \
                      --ssh-common-args='-o StrictHostKeyChecking=no'
                    """
                }
            }

            stage('Notification') {
                steps {
                    slackSend(
                        channel: "#${envConfig.SLACK_CHANNEL_NAME}",
                        color: "good",
                        message: """
                        :white_check_mark: *Elasticsearch Deployment Successful*

                        *Environment:* ${envConfig.ENVIRONMENT}
                        *Job:* ${env.JOB_NAME}
                        *Build Number:* ${env.BUILD_NUMBER}
                        *Status:* SUCCESS

                        <${env.BUILD_URL}|View Build Logs>
                        """
                    )
                }
            }
        }
    }
}

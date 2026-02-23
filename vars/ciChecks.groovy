def call() {
    stage('CI Checks') {
        parallel(
            'Terraform Format': {
                dir('terraform') {
                    sh 'terraform fmt -check'
                }
            },
            'Terraform Validate': {
                dir('terraform') {
                    sh 'terraform init -input=false'
                    sh 'terraform validate'
                }
            },
            'Ansible Lint': {
                sh 'ansible-lint ansible/site.yml'
            },
            'Gitleaks': {
                sh 'gitleaks detect --source . --verbose'
            }
        )
    }
}

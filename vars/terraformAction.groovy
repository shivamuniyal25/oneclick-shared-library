def call(String action) {
    dir('terraform') {
        sh 'terraform init -input=false'
        if (action == 'plan') {
            sh 'terraform plan'
        } else if (action == 'apply') {
            sh 'terraform apply -auto-approve'
        } else if (action == 'destroy') {
            sh 'terraform destroy -auto-approve'
        }
    }
}

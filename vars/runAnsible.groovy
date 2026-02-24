def call(String playbook, String inventory) {
    sh """
        cd ansible
        BASTION_IP=\$(cd ../terraform && terraform output -raw bastion_public_ip)
        ansible-playbook -i ${inventory} ${playbook} \
        -e bastion_ip=\$BASTION_IP
    """
}

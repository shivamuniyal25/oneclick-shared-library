def call(String playbook, String inventory) {
    sh """
        cd ansible
        ansible-playbook -i ${inventory} ${playbook}
    """
}

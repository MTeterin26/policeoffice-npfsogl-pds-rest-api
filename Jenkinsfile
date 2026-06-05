pipeline {
    agent any

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['runner.CalculatePolicy1', 'runner.CalculatePolicy2', 'runner.CalculatePolicy1,runner.CalculatePolicy2'],
            description: 'Выберите набор тестов для запуска'
        )
    }

    tools {
        maven 'Maven-3.9'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run Tests') {
            steps {
                sh "mvn clean api -Dtest=\"${params.TEST_SUITE}\""
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
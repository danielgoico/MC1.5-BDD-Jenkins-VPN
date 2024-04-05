pipeline {
    agent any

    environment {
        XRAY_USER = credentials('23D3917504204EB7AB83F7C2AB5D70D0')
        XRAY_API_KEY = credentials('b46b5ee9b9c00dce402a6e449634c352ab8fa4166b16d2cd237f24e4bcd31568')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    def mvnHome = tool name: 'maven_3_9_6', type: 'maven'
                    withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
                        sh "${mvnHome}/bin/mvn clean package"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    def mvnHome = tool name: 'maven_3_9_6', type: 'maven'
                    withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
                        sh "${mvnHome}/bin/mvn test"
                    }
                }
            }
        }

        stage('Upload to Xray') {
            steps {
                script {
                    // Se deberían leer los reportes generados por tus pruebas
                    // y generar un JSON dinámico para cargar los resultados en Xray
                    def xrayResults = generateXrayResults()

                    // Luego, enviar los resultados a Xray
                    sh '''
                        curl -X POST -u $XRAY_USER:$XRAY_API_KEY \
                        -H "Content-Type: application/json" \
                        -d '${xrayResults}' \
                        https://xray.cloud.xpand-it.com/api/internal/import/execution
                    '''
                }
            }
        }
    }
}
// Función para generar el JSON de resultados de Xray
def generateXrayResults() {
    def xrayResults = '''{
        "info": "Test results",
        "tests": [
            {
                "testKey": "KEY1",
                "status": "PASS"
            },
            {
                "testKey": "KEY2",
                "status": "FAIL"
            }
        ]
    }'''

    return xrayResults
}



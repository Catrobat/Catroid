#!groovy

class DockerParameters {
    def fileName = 'Dockerfile.jenkins'

    // 'docker build' would normally copy the whole build-dir to the container, changing the
    // docker build directory avoids that overhead
    def dir = 'docker'

    // Pass the uid and the gid of the current user (jenkins-user) to the Dockerfile, so a
    // corresponding user can be added. This is needed to provide the jenkins user inside
    // the container for the ssh-agent to work.
    // Another way would be to simply map the passwd file, but would spoil additional information
    // Also hand in the group id of kvm to allow using /dev/kvm.
    def buildArgs = '--build-arg USER_ID=$(id -u) --build-arg GROUP_ID=$(id -g) --build-arg KVM_GROUP_ID=$(getent group kvm | cut -d: -f3)'

    def args = '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle -v /var/local/container_shared/huawei:/home/user/huawei -m=14G'
    def label = 'LimitedEmulator'
}
 
def d = new DockerParameters()

def junitAndCoverage(String jacocoReportDir, String jacocoReportXml, String coverageName) {
    // Consume all test xml files. Otherwise tests would be tracked multiple
    // times if this function was called again.
    String testPattern = '**/*TEST*.xml'
    junit testResults: testPattern, allowEmptyResults: true
    cleanWs patterns: [[pattern: testPattern, type: 'INCLUDE']]

    publishJacocoHtml jacocoReportDir, jacocoReportXml, coverageName
}

def postEmulator(String coverageNameAndLogcatPrefix) {
    sh './gradlew stopEmulator'

    def jacocoReportDir = 'catroid/build/reports/coverage/catroid/debug'
    junitAndCoverage jacocoReportDir, 'report.xml', coverageNameAndLogcatPrefix

    archiveArtifacts "${coverageNameAndLogcatPrefix}_logcat.txt"
}

def webTestUrlParameter() {
    return env.WEB_TEST_URL?.isEmpty() ? '' : "-PwebTestUrl='${params.WEB_TEST_URL}'"
}

def allFlavoursParameters() {
    return env.BUILD_ALL_FLAVOURS?.toBoolean() ? 'assembleCreateAtSchoolDebug ' +
            'assembleLunaAndCatDebug assemblePhiroDebug assembleEmbroideryDesignerDebug ' +
            'assemblePocketCodeBetaDebug' : ''
}

def debugUnitTests() {
    return env.UNIT_TEST_DEBUG?.toBoolean() ? '-i' : ''
}

def useDebugLabelParameter(defaultLabel) {
    return env.DEBUG_LABEL?.trim() ? env.DEBUG_LABEL : defaultLabel
}

def useDockerLabelParameter(dockerImage, defaultLabel) {
    def label = env.DOCKER_LABEL?.trim() ? env.DOCKER_LABEL : defaultLabel
    return dockerImage + ':' + label
}

def archiveApkArtifact(pathToApk, apkName) {
    sh 'mv ' + pathToApk + '/' + apkName + '.apk ' + pathToApk + '/' + apkName + "-${env.BRANCH_NAME}-${env.BUILD_NUMBER}.apk"
    archiveArtifacts '' + pathToApk + "/" + apkName + "*.apk"
}

def archiveAllFlavoursApkArtifact() {
    archiveApkArtifact('catroid/build/outputs/apk/createAtSchool/debug', 'catroid-createAtSchool-debug')
    archiveApkArtifact('catroid/build/outputs/apk/embroideryDesigner/debug', 'catroid-embroideryDesigner-debug')
    archiveApkArtifact('catroid/build/outputs/apk/lunaAndCat/debug', 'catroid-lunaAndCat-debug')
    archiveApkArtifact('catroid/build/outputs/apk/phiro/debug', 'catroid-phiro-debug')
    archiveApkArtifact('catroid/build/outputs/apk/pocketCodeBeta/debug', 'catroid-pocketCodeBeta-debug')
}

pipeline {
    agent none

    parameters {
        string name: 'WEB_TEST_URL', defaultValue: '', description: 'When set, all the archived ' +
                'APKs will point to this Catrobat web server, useful for testing web changes. E.g https://web-test.catrob.at'
        booleanParam name: 'BUILD_ALL_FLAVOURS', defaultValue: false, description: 'When selected all flavours are built and archived as artifacts that can be installed alongside other versions of the same APK.'
        booleanParam name: 'UNIT_TEST_DEBUG', defaultValue: false, description: 'When selected the Unit Test suite prints the currently running tests and any output that it might produce'
        booleanParam name: 'INCLUDE_HUAWEI_FILES', defaultValue: false, description: 'Embed any huawei files that are needed'
        booleanParam name: 'BUILD_WITH_PAINTROID', defaultValue: false, description: 'When set to \'yes\' then the current Catroid build will be build with the current develop branch of Paintroid'
        string name: 'DEBUG_LABEL', defaultValue: '', description: 'For debugging when entered will be used as label to decide on which slaves the jobs will run.'
        string name: 'DOCKER_LABEL', defaultValue: '', description: 'When entered will be used as label for docker catrobat/catroid-android image to build'
    }

    options {
        timeout(time: 2, unit: 'HOURS')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: env.BRANCH_NAME == 'master' ? '10' :
                                                env.BRANCH_NAME == 'develop' ? '5' : '2',
                                  artifactNumToKeepStr: env.BRANCH_NAME == 'master' ? '2' :
                                                        env.BRANCH_NAME == 'develop' ? '2' : '1'
        ))
    }

    triggers {
        cron(env.BRANCH_NAME == 'develop' ? '@midnight' : '')
        issueCommentTrigger('.*test this please.*')
    }

    stages {
        stage('All') {
            parallel {
                stage('1') {
                    agent {
                        dockerfile {
                            filename d.fileName
                            dir d.dir
                            additionalBuildArgs d.buildArgs
                            args d.args
                            label useDebugLabelParameter(d.label)
                        }
                    }

                    stages {
                        stage('Build with Paintroid') {
                            when {
                                expression {
                                    params.BUILD_WITH_PAINTROID
                                }
                            }
                            steps {
                                sh 'rm -rf Paintroid; mkdir Paintroid'
                                dir('Paintroid') {
                                    git branch: 'develop', url: 'https://github' +
                                            '.com/Catrobat/Paintroid.git'
                                    sh "./gradlew -Pindependent='#$env.BUILD_NUMBER $env.BRANCH_NAME' assembleDebug"
                                    archiveArtifacts'app/build/outputs/apk/debug/paintroid-debug*.apk'
                                    sh './gradlew publishToMavenLocal -Psnapshot'
                                }
                                sh "./gradlew -PpaintroidLocal assembleCatroidDebug ${allFlavoursParameters()}"
                                archiveApkArtifact('catroid/build/outputs/apk/catroid/debug', 'catroid-catroid-debug')
                                script {
                                    if (params.BUILD_ALL_FLAVOURS) {
                                        archiveAllFlavoursApkArtifact()
                                    }
                                }
                            }
                        }

                        stage('APKs') {
                            steps {
                                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                                    script {
                                        def additionalParameters = [webTestUrlParameter(), allFlavoursParameters()].findAll {
                                            it
                                        }.collect()
                                        if (additionalParameters) {
                                            currentBuild.description = "<p>Additional APK build parameters: <b>${additionalParameters.join(' ')}</b></p>"
                                        }
                                        if(env.INCLUDE_HUAWEI_FILES?.toBoolean()) {
                                            sh "cp /home/user/huawei/agconnect-services.json catroid/src/agconnect-services.json"
                                        }
                                    }

                                    // Checks that the creation of standalone APKs (APK for a Pocketcode app) works, reducing the risk of breaking gradle changes.
                                    // The resulting APK is not verified itself.
                                    sh """./gradlew copyAndroidNatives assembleStandaloneDebug ${webTestUrlParameter()} -Papk_generator_enabled=true -Psuffix=generated817.catrobat \
                                                -Pdownload='https://share.catrob.at/pocketcode/download/817.catrobat'"""

                                    // Build the flavors so that they can be installed next independently of older versions.
                                    sh "./gradlew ${webTestUrlParameter()} -Pindependent='#$env.BUILD_NUMBER $env.BRANCH_NAME' assembleCatroidDebug ${allFlavoursParameters()}"

                                    script {
                                        if (params.BUILD_WITH_PAINTROID) {
                                            archiveApkArtifact('catroid/build/outputs/apk/catroid/debug', 'catroid-catroid-debug')
                                            if (params.BUILD_ALL_FLAVOURS) {
                                                archiveAllFlavoursApkArtifact()
                                            }
                                        }
                                    }
                                    archiveApkArtifact('catroid/build/outputs/apk/standalone/debug', 'catroid-standalone-debug')
                                }
                            }
                        }

                        stage('Static Analysis') {
                            steps {
                                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                                    sh './gradlew pmd checkstyle lintCatroidDebug detekt'
                                }
                            }

                            post {
                                always {
                                    recordIssues aggregatingResults: true, enabledForFailure: true, qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]],
                                            tools: [androidLintParser(pattern: 'catroid/build/reports/lint*.xml'),
                                                    checkStyle(pattern: 'catroid/build/reports/checkstyle.xml'),
                                                    pmdParser(pattern: 'catroid/build/reports/pmd.xml'),
                                                    detekt(pattern: 'catroid/build/reports/detekt/detekt.xml')]
                                }
                            }
                        }

                        stage('Unit Tests') {
                            steps {
                                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {                                   
                                    sh """./gradlew ${debugUnitTests()} -PenableCoverage jacocoTestCatroidDebugUnitTestReport --full-stacktrace"""
                                    sh 'mkdir -p catroid/build/reports/jacoco/jacocoTestCatroidDebugUnitTestReport/'
                                    sh 'touch catroid/build/reports/jacoco/jacocoTestCatroidDebugUnitTestReport/jacocoTestCatroidDebugUnitTestReport.xml'
                                    junitAndCoverage 'catroid/build/reports/jacoco/jacocoTestCatroidDebugUnitTestReport', 'jacocoTestCatroidDebugUnitTestReport.xml', 'unit'
                                }
                            }
                        }

                        stage('Instrumented Unit Tests') {
                            steps {
                                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                                    sh '''./gradlew -PenableCoverage -PlogcatFile=instrumented_unit_logcat.txt -Pemulator=android28 \
                                            startEmulator createCatroidDebugAndroidTestCoverageReport \
                                            -Pandroid.testInstrumentationRunnerArguments.class=org.catrobat.catroid.testsuites.LocalHeadlessTestSuite'''
                                }
                            }

                            post {
                                always {
                                    postEmulator 'instrumented_unit'
                                }
                            }
                        }

                        stage('Testrunner Tests') {
                            steps {
                                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                                    sh '''./gradlew -PenableCoverage -PlogcatFile=testrunner_logcat.txt -Pemulator=android28 \
                                                startEmulator createCatroidDebugAndroidTestCoverageReport \
                                                -Pandroid.testInstrumentationRunnerArguments.package=org.catrobat.catroid.catrobattestrunner'''
                                }
                            }

                            post {
                                always {
                                    postEmulator 'testrunner'
                                }
                            }
                        }

                        stage('Quarantined Tests') {
                            when {
                                expression { isJobStartedByTimer() }
                            }

                            steps {
                                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                                    sh '''./gradlew -PenableCoverage -PlogcatFile=quarantined_logcat.txt -Pemulator=android28 \
                                            startEmulator createCatroidDebugAndroidTestCoverageReport \
                                            -Pandroid.testInstrumentationRunnerArguments.class=org.catrobat.catroid.testsuites.UiEspressoQuarantineTestSuite'''
                                }
                            }

                            post {
                                always {
                                    postEmulator 'quarantined'
                                }
                            }
                        }

                        stage('RTL Tests') {
                            steps {
                                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                                    sh '''./gradlew -PenableCoverage -PlogcatFile=rtltests_logcat.txt -Pemulator=android28 \
                                            startEmulator createCatroidDebugAndroidTestCoverageReport \
                                            -Pandroid.testInstrumentationRunnerArguments.class=org.catrobat.catroid.testsuites.UiEspressoRtlTestSuite'''
                                }
                            }

                            post {
                                always {
                                    postEmulator 'rtltests'
                                }
                            }
                        }
                    }

                    post {
                        always {
                            stash name: 'logParserRules', includes: 'buildScripts/log_parser_rules'
                        }
                    }
                }

                stage('2') {
                    agent {
                        dockerfile {
                            filename d.fileName
                            dir d.dir
                            additionalBuildArgs d.buildArgs
                            args d.args
                            label useDebugLabelParameter(d.label)
                        }
                    }

                    stages {
                        stage('Pull Request Suite') {
                            steps {
                                catchError(buildResult: 'FAILURE' ,stageResult: 'FAILURE') {
                                    sh '''./gradlew copyAndroidNatives -PenableCoverage -PlogcatFile=pull_request_suite_logcat.txt -Pemulator=android28 \
                                            startEmulator createCatroidDebugAndroidTestCoverageReport \
                                            -Pandroid.testInstrumentationRunnerArguments.class=org.catrobat.catroid.testsuites.UiEspressoPullRequestTriggerSuite'''
                                }
                            }

                            post {
                                always {
                                    postEmulator 'pull_request_suite'
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        changed {
            node('master') {
                notifyChat()
            }
        }
    }
}

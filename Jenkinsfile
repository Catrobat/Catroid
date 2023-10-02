#!groovy

class DockerParameters {
    // 'docker build' would normally copy the whole build-dir to the container, changing the
    // docker build directory avoids that overhead
    def dir = 'docker'
    def args = '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle -v /var/local/container_shared/huawei:/home/user/huawei -m=8G'
    def label = 'LimitedEmulator'
    def image = 'catrobat/catrobat-android:stable'
}

def d = new DockerParameters()

def junitAndCoverage(String jacocoReportDir, String jacocoReportXml, String coverageName) {
    // Consume all test xml files. Otherwise tests would be tracked multiple
    // times if this function was called again.
    String testPattern = '**/*TEST*.xml'
    junit testResults: testPattern, allowEmptyResults: true, skipPublishingChecks: true
    cleanWs patterns: [[pattern: testPattern, type: 'INCLUDE']]

    publishJacocoHtml jacocoReportDir, jacocoReportXml, coverageName
}

def postEmulator(String coverageNameAndLogcatPrefix) {
    sh './gradlew stopEmulator'

    def jacocoReportDir = 'catroid/build/reports/coverage/catroid/debug'
    if (fileExists('catroid/build/reports/coverage/catroid/debug/report.xml')){
        junitAndCoverage jacocoReportDir, 'report.xml', coverageNameAndLogcatPrefix
        archiveArtifacts "${coverageNameAndLogcatPrefix}_logcat.txt"
    }
}

def webTestUrlParameter() {
    return env.WEB_TEST_URL?.isEmpty() ? '' : "-PwebTestUrl='${params.WEB_TEST_URL}'"
}

def allFlavoursParameters() {
    return env.BUILD_ALL_FLAVOURS?.toBoolean() ? 'assembleCreateAtSchoolDebug ' +
            'assembleLunaAndCatDebug assemblePhiroDebug assembleEmbroideryDesignerDebug ' +
            'assemblePocketCodeBetaDebug assembleMindstormsDebug' : ''
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

pipeline {
    agent none

    parameters {
        string name: 'WEB_TEST_URL', defaultValue: '', description: 'When set, all the archived ' +
                'APKs will point to this Catrobat web server, useful for testing web changes. E.g https://web-test.catrob.at'
        booleanParam name: 'BUILD_ALL_FLAVOURS', defaultValue: false, description: 'When selected all flavours are built and archived as artifacts that can be installed alongside other versions of the same APK.'
        booleanParam name: 'UNIT_TEST_DEBUG', defaultValue: false, description: 'When selected the Unit Test suite prints the currently running tests and any output that it might produce'
        booleanParam name: 'INCLUDE_HUAWEI_FILES', defaultValue: false, description: 'Embed any huawei files that are needed'
        string name: 'DEBUG_LABEL', defaultValue: '', description: 'For debugging when entered will be used as label to decide on which slaves the jobs will run.'
        string name: 'DOCKER_LABEL', defaultValue: '', description: 'When entered will be used as label for docker catrobat/catroid-android image to build'
        separator(name: "Build with Paintroid",
                separatorStyle: "border-width: 0",
                sectionHeaderStyle: """
				background-color: #ffff00;
				text-align: center;
				padding: 4px;
				color: #000000;
				font-size: 20px;
				font-weight: normal;
				font-family: 'Orienta', sans-serif;
				letter-spacing: 1px;
				font-style: italic;
			""")
        booleanParam name: 'BUILD_WITH_PAINTROID', defaultValue: true, description: 'Builds ' +
                'catroid with paintroid develop or specified branch'
        string name: 'PAINTROID_BRANCH', defaultValue: 'develop', description: 'The branch which ' +
                'to build paintroid with, when BUILD_WITH_PAINTROID is enabled.'
        separator(name: "TEST_STAGES", sectionHeader: "Test Stages - CAUTION: The PR needs to be rebuild again with all test stages enabled before Code Review!!",
                separatorStyle: "border-width: 0",
                sectionHeaderStyle: """
                background-color: #ffff00;
                text-align: center;
                padding: 4px;
                color: #000000;
                font-size: 20px;
                font-weight: normal;
                font-family: 'Orienta', sans-serif;
                letter-spacing: 1px;
                font-style: italic;
                """)
        booleanParam name: 'PULL_REQUEST_SUITE', defaultValue: true, description: 'Enables Pull ' +
                'request suite'
        booleanParam name: 'STANDALONE', defaultValue: true, description: 'When selected, ' +
                'standalone APK will be built'
        booleanParam name: 'UNIT_TESTS', defaultValue: true, description: 'Enables Unit Tests'
        booleanParam name: 'INSTRUMENTED_UNIT_TESTS', defaultValue: true, description: 'Enables ' +
                'Instrumented Unit Tests'
        booleanParam name: 'TESTRUNNER_TESTS', defaultValue: true, description: 'Enables ' +
                'Testrunner Tests'
        booleanParam name: 'QUARANTINED_TESTS', defaultValue: true, description: 'Enables ' +
                'Quarantined Tests'
        booleanParam name: 'RTL_TESTS', defaultValue: true, description: 'Enables RTL Tests'
        booleanParam name: 'OUTGOING_NETWORK_CALL_TESTS', defaultValue: false, description: 'Enables' +
                'start Outgoing web tests'
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
                        docker {
                            image d.image
                            args d.args
                            label d.label
                            alwaysPull true
                        }
                    }

                    stages {
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
                                        if (env.INCLUDE_HUAWEI_FILES?.toBoolean()) {
                                            sh "cp /home/user/huawei/agconnect-services.json catroid/src/agconnect-services.json"
                                        }
                                    }

                                    // Build the flavors so that they can be installed next independently of older versions.
                                    sh "./gradlew ${webTestUrlParameter()} -Pindependent='#$env.BUILD_NUMBER $env.BRANCH_NAME' assembleCatroidDebug ${allFlavoursParameters()}"

                                    renameApks("${env.BRANCH_NAME}-${env.BUILD_NUMBER}")
                                    archiveArtifacts '**/*.apk'
                                }
                            }
                        }

                        stage('Build with Paintroid') {
                            when {
                                expression {
                                    params.BUILD_WITH_PAINTROID
                                }
                            }

                            steps {
                                sh 'rm -rf Paintroid; mkdir Paintroid'
                                dir('Paintroid') {
                                    git branch: params.PAINTROID_BRANCH, url: 'https://github' +
                                            '.com/Catrobat/Paintroid.git'
                                    sh "./gradlew assembleDebug"

                                    sh 'rm -f ../catroid/src/main/libs/*.aar'
                                    sh 'mv -f colorpicker/build/outputs/aar/colorpicker-debug.aar ../catroid/src/main/libs/colorpicker-LOCAL.aar'
                                    sh 'mv -f Paintroid/build/outputs/aar/Paintroid-debug.aar ../catroid/src/main/libs/Paintroid-LOCAL.aar'

                                    archiveArtifacts '../catroid/src/main/libs/colorpicker-LOCAL.aar'
                                    archiveArtifacts '../catroid/src/main/libs/Paintroid-LOCAL.aar'}
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
                            when {
                                expression { params.UNIT_TESTS == true }
                            }
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
                            when {
                                expression { params.INSTRUMENTED_UNIT_TESTS == true }
                            }
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
                            when {
                                expression { params.TESTRUNNER_TESTS == true }
                            }
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
                                expression { params.QUARANTINED_TESTS == true }
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

                        stage('Outgoing Network Call Tests') {
                            when {
                                expression { params.OUTGOING_NETWORK_CALL_TESTS == true }
                            }
                            steps {
                                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE')
                                {
                                    sh '''./gradlew -PenableCoverage -Pemulator=android28 \
                                       startEmulator createCatroidDebugAndroidTestCoverageReport \
                                       -Pandroid.testInstrumentationRunnerArguments.class=org.catrobat.catroid.testsuites.OutgoingNetworkCallsTestSuite'''
                                }
                            }
                            post {
                                always {
                                   junit '**/*TEST*.xml'
                                         sh './gradlew stopEmulator clearAvdStore'
                                         archiveArtifacts 'logcat.txt'
                                       }
                            }
                        }

                        stage('RTL Tests') {
                            when {
                                expression { params.RTL_TESTS == true }
                            }
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
                        docker {
                            image d.image
                            args d.args
                            label d.label
                            alwaysPull true
                        }
                    }

                    stages {
                        stage('Pull Request Suite') {
                            when {
                                expression { params.PULL_REQUEST_SUITE == true }
                            }
                            steps {
                                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
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
#!groovy

class DockerParameters {
    // 'docker build' would normally copy the whole build-dir to the container, changing the
    // docker build directory avoids that overhead
    def dir = 'docker'
    def args = '--device /dev/kvm:/dev/kvm ' +
            ' -v /var/local/container_shared/huawei:/home/user/huawei -m=8G '
    def label = 'LimitedEmulator'
    def image = 'catrobat/catrobat-android:api33'
}

def d = new DockerParameters()

def startEmulator(String android_version, String stageName) {
    sh "adb start-server"
    // creates a new avd, and if it already exists it does nothing.
    sh "echo no | avdmanager create avd -f --name android${android_version} --package " +
            "'system-images;android-${android_version};google_apis;x86_64' || true"

    sh "/home/user/android/sdk/emulator/emulator -avd android${android_version} -wipe-data -no-window -no-boot-anim -noaudio" +
            " -camera-back emulated -camera-front emulated " +
            " -no-snapshot-save -gpu swiftshader_indirect  > ${stageName}_emulator.log 2>&1 &"
}

def waitForEmulatorAndPressWakeUpKey() {
    sh 'adb devices'
    sh "timeout 5m adb wait-for-device"
    sh '''#!/bin/bash
adb devices
timeout 5m adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; 
done'
echo "Emulator started"
'''
    // In case the device went to sleep
    sh 'adb shell input keyevent KEYCODE_WAKEUP'
}

def runTestsWithEmulator(String testClass) {
    sh " ./gradlew compileCatroidDebugSources compileCatroidDebugAndroidTestSources"

    waitForEmulatorAndPressWakeUpKey()

    sh " ./gradlew disableAnimations -PenableCoverage createCatroidDebugAndroidTestCoverageReport" +
            " -Pandroid.testInstrumentationRunnerArguments.class=${testClass} "

}

def postEmulator(String coverageNameAndLogcatPrefix) {
    archiveArtifacts "${coverageNameAndLogcatPrefix}_emulator.log"
    zip zipFile: "${coverageNameAndLogcatPrefix}_logcat.zip", dir: "catroid/build/outputs/androidTest-results/connected/flavors/", archive: true
    def jacocoReportDir = 'catroid/build/reports/coverage/androidTest/catroid/debug/connected'
    if (fileExists('catroid/build/reports/coverage/androidTest/catroid/debug/connected/report.xml')) {
        junitAndCoverage jacocoReportDir, 'report.xml', coverageNameAndLogcatPrefix
    }
}

def junitAndCoverage(String jacocoReportDir, String jacocoReportXml, String coverageName) {
    // Consume all test xml files. Otherwise tests would be tracked multiple
    // times if this function was called again.
    String testPattern = '**/*TEST*.xml'
    junit testResults: testPattern, allowEmptyResults: true, skipPublishingChecks: true
    cleanWs patterns: [[pattern: testPattern, type: 'INCLUDE']]

    publishJacocoHtml jacocoReportDir, jacocoReportXml, coverageName
}

def killRunningEmulator() {
    sh '''adb emu kill || true'''
    sh '''#!/bin/bash 
while : 
do
    output=$(eval "adb devices")
    if [[ $output != *"emulator"* ]]; then
        echo "All Emulators are killed"
        echo $output
        break
    fi
    
    echo "Emulator is still running"
    sleep 2 
done
'''
    sh "adb kill-server"
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

def renameApks(suffix) {
    suffix = suffix.replaceAll(/[\\:#\/]/, '_')
    def apkFiles = sh script: 'find -name "*.apk"', returnStdout: true
    apkFiles.trim().split('\n').each { oldPath ->
        def newPath = oldPath.replaceAll(/\.apk$/, "-${suffix}.apk")
        sh "mv '$oldPath' '$newPath'"
    }
}

pipeline {
    agent none

    environment {
        ANDROID_VERSION = 33
    }

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
        booleanParam name: 'BUILD_WITH_PAINTROID', defaultValue: false, description: 'Builds ' +
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
                                    sh "./gradlew ${debugUnitTests()} -PenableCoverage " +
                                            "jacocoTestCatroidDebugUnitTestReport --full-stacktrace"
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
                                    startEmulator(ANDROID_VERSION, 'instrumented_unit')
                                    runTestsWithEmulator("org.catrobat.catroid.testsuites.LocalHeadlessTestSuite")
                                }
                            }

                            post {
                                always {
                                    killRunningEmulator()
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
                                    startEmulator(ANDROID_VERSION, 'testrunner')
                                    runTestsWithEmulator("org.catrobat.catroid.catrobattestrunner.CatrobatTestRunner")
                                }
                            }

                            post {
                                always {
                                    killRunningEmulator()
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
                                    startEmulator(ANDROID_VERSION, "quarantined")
                                    runTestsWithEmulator("org.catrobat.catroid.testsuites.UiEspressoQuarantineTestSuite")
                                }
                            }

                            post {
                                always {
                                    killRunningEmulator()
                                    postEmulator 'quarantined'
                                }
                            }
                        }

                        stage('Outgoing Network Call Tests') {
                            when {
                                expression { params.OUTGOING_NETWORK_CALL_TESTS == true }
                            }
                            steps {
                                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                                    startEmulator(ANDROID_VERSION, 'networktest')
                                    runTestsWithEmulator("org.catrobat.catroid.testsuites.OutgoingNetworkCallsTestSuite")
                                }
                            }
                            post {
                                always {
                                    killRunningEmulator()
                                    postEmulator('networktest')
                                }
                            }
                        }

                        stage('RTL Tests') {
                            when {
                                expression { params.RTL_TESTS == true }
                            }
                            steps {
                                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                                    startEmulator(ANDROID_VERSION, 'rtltests')
                                    runTestsWithEmulator("org.catrobat.catroid.testsuites.UiEspressoRtlTestSuite")
                                }
                            }

                            post {
                                always {
                                    killRunningEmulator()
                                    postEmulator('rtltests')
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
                                    startEmulator(ANDROID_VERSION, 'pull_request_suite')
                                    runTestsWithEmulator("org.catrobat.catroid.testsuites.UiEspressoPullRequestTriggerSuite")
                                }
                            }

                            post {
                                always {
                                    killRunningEmulator()
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

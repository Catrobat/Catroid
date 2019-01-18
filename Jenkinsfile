#!groovy

// place the cobertura xml files relative to the source, so that the source can be found
def javaSrc = 'catroid/src/main/java'

def junitAndCoverage(String jacocoXmlFile, String coverageName, String javaSrcLocation) {
    // Consume all test xml files. Otherwise tests would be tracked multiple
    // times if this function was called again.
    String testPattern = '**/*TEST*.xml'
    junit testResults: testPattern, allowEmptyResults: true
    cleanWs patterns: [[pattern: testPattern, type: 'INCLUDE']]

    String coverageFile = "$javaSrcLocation/coverage_${coverageName}.xml"
    // Convert the JaCoCo coverate to the Cobertura XML file format.
    // This is done since the Jenkins JaCoCo plugin does not work well.
    // See also JENKINS-212 on jira.catrob.at
    sh "./buildScripts/cover2cover.py '$jacocoXmlFile' '$coverageFile'"
}

def postEmulator(String coverageNameAndLogcatPrefix, String javaSrcLocation) {
    sh './gradlew stopEmulator clearAvdStore'

    def jacocoXml = 'catroid/build/reports/coverage/catroid/debug/report.xml'
    junitAndCoverage jacocoXml, coverageNameAndLogcatPrefix, javaSrcLocation

    archiveArtifacts "${coverageNameAndLogcatPrefix}_logcat.txt"
}

pipeline {
    agent {
        dockerfile {
            filename 'Dockerfile.jenkins'
            // 'docker build' would normally copy the whole build-dir to the container, changing the
            // docker build directory avoids that overhead
            dir 'docker'
            // Pass the uid and the gid of the current user (jenkins-user) to the Dockerfile, so a
            // corresponding user can be added. This is needed to provide the jenkins user inside
            // the container for the ssh-agent to work.
            // Another way would be to simply map the passwd file, but would spoil additional information
            additionalBuildArgs '--build-arg USER_ID=$(id -u) --build-arg GROUP_ID=$(id -g)'
            // Currently there are two different NDK behaviors in place, one to keep NDK r16b, which
            // was needed because of the removal of armeabi and MIPS support and one to always use the
            // latest NDK, which is the suggestion from the NDK documentations.
            // Therefore two different SDK locations on the host are currently in place:
            // NDK r16b  : /var/local/container_shared/android-sdk
            // NDK latest: /var/local/container_shared/android-sdk-ndk-latest
            // As android-sdk was used from the beginning and is already 'released' this can't be changed
            // to eg android-sdk-ndk-r16b and must be kept to the previously used value
            args "--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle/:/.gradle -v /var/local/container_shared/android-sdk-ndk-latest:/usr/local/android-sdk -v /var/local/container_shared/android-home:/.android -v /var/local/container_shared/emulator_console_auth_token:/.emulator_console_auth_token -v /var/local/container_shared/analytics.settings:/analytics.settings"
        }
    }

    parameters {
        booleanParam name: 'BUILD_ALL_FLAVOURS', defaultValue: false, description: 'When selected all flavours are built and archived as artifacts that can be installed alongside other versions of the same APK.'
    }

    environment {
        //////// Define environment variables to point to the correct locations inside the container ////////
        //////////// Most likely not edited by the developer
        ANDROID_SDK_ROOT = "/usr/local/android-sdk"
        // Deprecated: Still used by the used gradle version, once gradle respects ANDROID_SDK_ROOT, this can be removed
        ANDROID_HOME = "/usr/local/android-sdk"
        ANDROID_SDK_HOME = "/"
        // Needed for compatibiliby to current Jenkins-wide Envs. Can be removed, once all builds are migrated to Pipeline
        ANDROID_SDK_LOCATION = "/usr/local/android-sdk"
        ANDROID_NDK = ""
        // This is important, as we want the keep our gradle cache, but we can't share it between containers
        // the cache could only be shared if the gradle instances could comunicate with each other
        // imho keeping the cache per executor will have the least space impact
        GRADLE_USER_HOME = "/.gradle/${env.EXECUTOR_NUMBER}"
        // Otherwise user.home returns ? for java applications
        JAVA_TOOL_OPTIONS = "-Duser.home=/tmp/"
    }

    options {
        timeout(time: 2, unit: 'HOURS')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    triggers {
        cron(env.BRANCH_NAME == 'develop' ? '@midnight' : '')
        issueCommentTrigger('.*test this please.*')
    }

    stages {
        stage('Setup Android SDK') {
            steps {
                // Install Android SDK
                lock("update-android-sdk-on-${env.NODE_NAME}") {
                    sh './gradlew -PinstallSdk'
                }
            }
        }

        stage('APKs') {
            steps {
                // Checks that the creation of standalone APKs (APK for a Pocketcode app) works, reducing the risk of breaking gradle changes.
                // The resulting APK is not verified itself.
                sh '''./gradlew assembleStandaloneDebug -Papk_generator_enabled=true -Psuffix=generated817.catrobat \
                            -Pdownload='https://share.catrob.at/pocketcode/download/817.catrobat' '''

                // Build the flavors so that they can be installed next independently of older versions.
                sh """./gradlew -Pindependent='#$env.BUILD_NUMBER $env.BRANCH_NAME' assembleCatroidDebug \
                    ${env.BUILD_ALL_FLAVOURS ? 'assembleCreateAtSchoolDebug assembleLunaAndCatDebug assemblePhiroDebug' : ''}"""

                archiveArtifacts '**/*.apk'
            }
        }

        stage('Static Analysis') {
            steps {
                sh './gradlew pmd checkstyle lint'
            }

            post {
                always {
                    pmd         canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "catroid/build/reports/pmd.xml",        unHealthy: '', unstableTotalAll: '0'
                    checkstyle  canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "catroid/build/reports/checkstyle.xml", unHealthy: '', unstableTotalAll: '0'
                    androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "catroid/build/reports/lint*.xml",      unHealthy: '', unstableTotalAll: '0'
                }
            }
        }

        stage('Tests') {
            stages {
                stage('Unit Tests') {
                    steps {
                        sh './gradlew -PenableCoverage jacocoTestCatroidDebugUnitTestReport'
                    }

                    post {
                        always {
                            junitAndCoverage 'catroid/build/reports/jacoco/jacocoTestCatroidDebugUnitTestReport/jacocoTestCatroidDebugUnitTestReport.xml', 'unit', javaSrc
                        }
                    }
                }

                stage('Instrumented Unit Tests') {
                    steps {
                        sh '''./gradlew -PenableCoverage -PlogcatFile=instrumented_unit_logcat.txt -Pemulator=android24 \
                                    startEmulator createCatroidDebugAndroidTestCoverageReport \
                                    -Pandroid.testInstrumentationRunnerArguments.package=org.catrobat.catroid.test'''
                    }

                    post {
                        always {
                            postEmulator 'instrumented_unit', javaSrc
                        }
                    }
                }

                stage('Pull Request Suite') {
                    steps {
                        sh '''./gradlew -PenableCoverage -PlogcatFile=pull_request_suite_logcat.txt -Pemulator=android24 \
                                    startEmulator createCatroidDebugAndroidTestCoverageReport \
                                    -Pandroid.testInstrumentationRunnerArguments.class=org.catrobat.catroid.uiespresso.testsuites.PullRequestTriggerSuite'''
                    }

                    post {
                        always {
                            postEmulator 'pull_request_suite', javaSrc
                        }
                    }
                }

                stage('Quarantined Tests') {
                    when {
                        expression { isJobStartedByTimer() }
                    }

                    steps {
                        sh '''./gradlew -PenableCoverage -PlogcatFile=quarantined_logcat.txt -Pemulator=android24 \
                                    startEmulator createCatroidDebugAndroidTestCoverageReport \
                                    -Pandroid.testInstrumentationRunnerArguments.class=org.catrobat.catroid.uiespresso.testsuites.QuarantineTestSuite'''
                    }

                    post {
                        always {
                            postEmulator 'quarantined', javaSrc
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            cobertura autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: "$javaSrc/coverage*.xml", failUnhealthy: false, failUnstable: false, maxNumberOfBuilds: 0, onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false, failNoReports: false
            step([$class: 'LogParserPublisher', failBuildOnError: true, projectRulePath: 'buildScripts/log_parser_rules', unstableOnWarning: true, useProjectRule: true])
        }
        changed {
            notifyChat()
        }
    }
}

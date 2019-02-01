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

    def args = '--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle_cache/$EXECUTOR_NUMBER:/home/user/.gradle -m=6.5G'
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

pipeline {
    agent none

    parameters {
        booleanParam name: 'BUILD_ALL_FLAVOURS', defaultValue: false, description: 'When selected all flavours are built and archived as artifacts that can be installed alongside other versions of the same APK.'
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
        stage('All') {
            parallel {
                stage('1') {
                    agent {
                        dockerfile {
                            filename d.fileName
                            dir d.dir
                            additionalBuildArgs d.buildArgs
                            args d.args
                            label d.label
                        }
                    }

                    stages {
                        stage('APKs') {
                            steps {
                                // Checks that the creation of standalone APKs (APK for a Pocketcode app) works, reducing the risk of breaking gradle changes.
                                // The resulting APK is not verified itself.
                                sh '''./gradlew assembleStandaloneDebug -Papk_generator_enabled=true -Psuffix=generated817.catrobat \
                                            -Pdownload='https://share.catrob.at/pocketcode/download/817.catrobat' '''

                                // Checks that the job builds with the parameters to have unique APKs, reducing the risk of breaking gradle changes.
                                // The resulting APK is not verified on itself.
                                sh "./gradlew assembleCatroidDebug -Pindependent='#$env.BUILD_NUMBER $env.BRANCH_NAME'"
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

                        stage('Unit Tests') {
                            steps {
                                sh './gradlew -PenableCoverage jacocoTestCatroidDebugUnitTestReport'
                            }

                            post {
                                always {
                                    junitAndCoverage 'catroid/build/reports/jacoco/jacocoTestCatroidDebugUnitTestReport', 'jacocoTestCatroidDebugUnitTestReport.xml', 'unit'
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
                                    postEmulator 'instrumented_unit'
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
                                    postEmulator 'quarantined'
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
                            label d.label
                        }
                    }

                    stages {
                        stage('Pull Request Suite') {
                            steps {
                                sh '''./gradlew -PenableCoverage -PlogcatFile=pull_request_suite_logcat.txt -Pemulator=android24 \
                                            startEmulator createCatroidDebugAndroidTestCoverageReport \
                                            -Pandroid.testInstrumentationRunnerArguments.class=org.catrobat.catroid.uiespresso.testsuites.PullRequestTriggerSuite'''
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
        always {
            node('master') {
                unstash 'logParserRules'
                step([$class: 'LogParserPublisher', failBuildOnError: true, projectRulePath: 'buildScripts/log_parser_rules', unstableOnWarning: true, useProjectRule: true])
            }
        }
        changed {
            node('master') {
                notifyChat()
            }
        }
    }
}

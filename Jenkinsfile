#!groovy

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
			args "--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle/:/.gradle -v /var/local/container_shared/android-sdk:/usr/local/android-sdk -v /var/local/container_shared/android-home:/.android -v /var/local/container_shared/emulator_console_auth_token:/.emulator_console_auth_token -v /var/local/container_shared/analytics.settings:/analytics.settings"
		}
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

		//// jenkins-android-helper related variables
		// set to any value to debug jenkins_android* scripts
		ANDROID_EMULATOR_HELPER_DEBUG = ""
		// get stdout of called subprocesses immediately
		PYTHONUNBUFFERED = "true"

		//////// Build specific variables ////////
		//////////// May be edited by the developer on changing the build steps
		// modulename
		GRADLE_PROJECT_MODULE_NAME = "catroid"

		// APK build output locations
		APK_LOCATION_DEBUG = "${env.GRADLE_PROJECT_MODULE_NAME}/build/outputs/apk/catroid/debug/catroid-catroid-debug.apk"
		APK_LOCATION_STANDALONE = "${env.GRADLE_PROJECT_MODULE_NAME}/build/outputs/apk/standalone/debug/catroid-standalone-debug.apk"

		// share.catrob.at
		CATROBAT_SHARE_UPLOAD_BRANCH = "develop"
		CATROBAT_SHARE_APK_NAME = "org.catrobat.catroid_debug_${env.CATROBAT_SHARE_UPLOAD_BRANCH}_latest.apk"
	}

	options {
		timeout(time: 2, unit: 'HOURS')
		timestamps()
		buildDiscarder(logRotator(numToKeepStr: '30'))
	}

	stages {
		stage('Setup Android SDK') {
			steps {
				// Install Android SDK
				lock("update-android-sdk-on-${env.NODE_NAME}") {
					sh "./buildScripts/build_step_install_android_sdk"
				}
			}
		}

		stage('Static Analysis') {
			steps {
				sh "./buildScripts/build_step_run_static_analysis"
			}

			post {
				always {
					pmd         canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "${env.GRADLE_PROJECT_MODULE_NAME}/build/reports/pmd.xml",        unHealthy: '', unstableTotalAll: '0'
					checkstyle  canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "${env.GRADLE_PROJECT_MODULE_NAME}/build/reports/checkstyle.xml", unHealthy: '', unstableTotalAll: '0'
					androidLint canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: "${env.GRADLE_PROJECT_MODULE_NAME}/build/reports/lint*.xml",      unHealthy: '', unstableTotalAll: '0'
				}
			}
		}

		stage('Unit and Device tests') {
			steps {
				// Run local unit tests
				sh "./buildScripts/build_step_run_unit_tests__all_tests"
				// ensure that the following test run does not overwrite the results
				sh "mv ${env.GRADLE_PROJECT_MODULE_NAME}/build ${env.GRADLE_PROJECT_MODULE_NAME}/build-unittest"

				// Run device tests for package: org.catrobat.catroid.test
				sh "./buildScripts/build_step_run_tests_on_emulator__test_pkg"
				// ensure that the following test run does not overwrite the results
				sh "mv ${env.GRADLE_PROJECT_MODULE_NAME}/build ${env.GRADLE_PROJECT_MODULE_NAME}/build-test-test-pkg"

				// Run device tests for class: org.catrobat.catroid.uiespresso.testsuites.PullRequestTriggerSuite
				sh "./buildScripts/build_step_run_tests_on_emulator__pr_test_suite"
			}

			post {
				always {
					junit '**/*TEST*.xml'

					// stop/kill emulator
					sh "./buildScripts/build_helper_stop_emulator"
				}
			}
		}

		stage('Standalone-APK') {
			// It checks that the creation of standalone APKs (APK for a Pocketcode app) works, reducing the risk of breaking gradle changes.
			// The resulting APK is not verified itself.
			steps {
				sh "./buildScripts/build_step_create_standalone_apk"
				archiveArtifacts "${env.APK_LOCATION_STANDALONE}"
			}
		}

		stage('Independent-APK') {
			// It checks that the job builds with the parameters to have unique APKs, reducing the risk of breaking gradle changes.
			// The resulting APK is not verified on itself.
			steps {
				sh "./buildScripts/build_step_create_independent_apk"
				stash name: "apk-independent", includes: "${env.APK_LOCATION_DEBUG}"
				archiveArtifacts "${env.APK_LOCATION_DEBUG}"
			}
		}

		stage('Upload to share') {
			when {
				branch "${env.CATROBAT_SHARE_UPLOAD_BRANCH}"
			}

			steps {
				unstash "apk-independent"
				script {
					uploadFileToShare "${env.APK_LOCATION_DEBUG}", "${env.CATROBAT_SHARE_APK_NAME}"
				}
			}
		}
	}

	post {
		always {
			step([$class: 'LogParserPublisher', failBuildOnError: true, projectRulePath: 'buildScripts/log_parser_rules', unstableOnWarning: true, useProjectRule: true])

			// Send notifications with standalone=false
			script {
				sendNotifications false
			}
		}
	}
}

#!groovy

pipeline {
	agent {
		docker {
			image 'redeamer/jenkins-android-helper:latest'
			args "--device /dev/kvm:/dev/kvm -v /var/local/container_shared/gradle/:/.gradle -v /var/local/container_shared/android-sdk:/usr/local/android-sdk -v /var/local/container_shared/android-home:/.android -v /var/local/container_shared/emulator_console_auth_token:/.emulator_console_auth_token -v /var/local/container_shared/analytics.settings:/analytics.settings -v /var/local/container_shared/analytics.settings:/analytics.settings"
		}
	}

	environment {
		ANDROID_SDK_ROOT = "/usr/local/android-sdk"
		ANDROID_SDK_HOME = "/"
		// This is important, as we want the keep our gradle cache, but we can't share it between containers
		// the cache could only be shared if the gradle instances could comunicate with each other
		// imho keeping the cache per executor will have the least space impact
		GRADLE_USER_HOME = "/.gradle/${env.EXECUTOR_NUMBER}"
		// Otherwise user.home returns ? for java applications
		JAVA_TOOL_OPTIONS = "-Duser.home=/tmp/"
		ANDROID_EMULATOR_IMAGE = "system-images;android-24;default;x86_64"

		// modulename
		GRADLE_PROJECT_MODULE_NAME = "catroid"

		// APK build output locations
		APK_LOCATION_DEBUG = "${env.GRADLE_PROJECT_MODULE_NAME}/build/outputs/apk/catroid/debug/catroid-catroid-debug.apk"
		APK_LOCATION_STANDALONE = "${env.GRADLE_PROJECT_MODULE_NAME}/build/outputs/apk/standalone/debug/catroid-standalone-debug.apk"

		// share.catrob.at
		CATROBAT_SHARE_UPLOAD_BRANCH = "develop"
		CATROBAT_SHARE_APK_NAME = "org.catrobat.catroid_debug_${env.CATROBAT_SHARE_UPLOAD_BRANCH}_latest.apk"

		// set to any value to debug jenkins_android* scripts
		ANDROID_EMULATOR_HELPER_DEBUG = ""
		// Needed for compatibiliby to current Jenkins-wide Envs
		// Can be removed, once all builds are migrated to Pipeline
		ANDROID_HOME = "/usr/local/android-sdk"
		ANDROID_SDK_LOCATION = "/usr/local/android-sdk"
		ANDROID_NDK = ""
	}

	options {
		timeout(time: 2, unit: 'HOURS')
		timestamps()
	}

	stages {
		stage('Setup Android SDK') {
			steps {
				// Install Android SDK
				lock("update-android-sdk-on-${env.NODE_NAME}") {
					sh "jenkins_android_sdk_installer -g '${WORKSPACE}/${env.GRADLE_PROJECT_MODULE_NAME}/build.gradle' -s '${ANDROID_EMULATOR_IMAGE}'"
				}
			}
		}

		stage('Static Analysis') {
			steps {
				sh "./gradlew pmd checkstyle lint"
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
				// create emulator
				sh "jenkins_android_emulator_helper -C -P 'hw.camera:yes' -P 'hw.ramSize:800' -P 'hw.gpu.enabled:yes' -P 'hw.camera.front:emulated' -P 'hw.camera.back:emulated' -P 'hw.gps:yes' -i '${ANDROID_EMULATOR_IMAGE}' -s xhdpi"
				// start emulator
				sh "jenkins_android_emulator_helper -S -r 768x1280 -l en_US -c '-gpu swiftshader_indirect -no-boot-anim -noaudio'"
				// wait for emulator startup
				sh "jenkins_android_emulator_helper -W"
				// Run Unit and device tests for package: org.catrobat.catroid.test
				sh "jenkins_android_cmd_wrapper -I ./gradlew test connectedCatroidDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=org.catrobat.catroid.test"
				// ensure that the following test run does not overwrite the results
				sh "mv ${env.GRADLE_PROJECT_MODULE_NAME}/build/outputs/androidTest-results ${env.GRADLE_PROJECT_MODULE_NAME}/build/outputs/androidTest-results1"
				// Run Unit and device tests for class: org.catrobat.catroid.uiespresso.testsuites.PullRequestTriggerSuite
				sh "jenkins_android_cmd_wrapper -I ./gradlew test connectedCatroidDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=org.catrobat.catroid.uiespresso.testsuites.PullRequestTriggerSuite"
				// stop emulator
				sh "jenkins_android_emulator_helper -K"
			}

			post {
				always {
					junit '**/*TEST*.xml'

					// kill emulator
					sh "jenkins_android_emulator_helper -K"
				}
			}
		}

		stage('Standalone-APK') {
			// It checks that the creation of standalone APKs (APK for a Pocketcode app) works, reducing the risk of breaking gradle changes.
			// The resulting APK is not verified itself.
			steps {
				sh "./gradlew assembleStandaloneDebug -Pdownload='https://pocketcode.org/download/817.catrobat' -Papk_generator_enabled=true -Psuffix='generated821'"
				archiveArtifacts "${env.APK_LOCATION_STANDALONE}"
			}
		}

		stage('Independent-APK') {
			// It checks that the job builds with the parameters to have unique APKs, reducing the risk of breaking gradle changes.
			// The resulting APK is not verified on itself.
			steps {
				sh "./gradlew assembleCatroidDebug -Pindependent='Code Nightly #${BUILD_NUMBER}'"
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
			// Send notifications with standalone=false
			script {
				sendNotifications false
			}
		}
	}
}

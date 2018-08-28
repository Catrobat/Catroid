# does all of this for you:
# https://confluence.catrob.at/display/KNOWHOW/How+to+create+a+release+standalone+Pocket+Code+apk-file
# plus generates the apk and copies it to this folder
import shutil
import os
import time
import sys

def getVersion():
    buildGradle = open("../catroid/build.gradle", "r").read()
    versionCode = buildGradle.split('versionName "')[1].split('"')[0]
    return versionCode

def updateBuildGradle():
    shutil.copyfile("../catroid/build.gradle", "../catroid/build.gradle_backup")
    buildGradle = open("../catroid/build.gradle", "r").read()
    buildGradle = buildGradle.replace("appId = 'org.catrobat.catroid'", "appId = 'org.catrobat.testapk'")
    debugBuildTypesBlock = buildGradle.split("debug {")[1].split("}")[0]
    releaseBuildTypesBlock = buildGradle.split("release {")[1].split("}")[0]
    buildGradle = buildGradle.replace(debugBuildTypesBlock, releaseBuildTypesBlock)
    open("../catroid/build.gradle","w").write(buildGradle)

def updateAndroidManifest(versionName):
    shutil.copyfile("../catroid/src/main/AndroidManifest.xml", "../catroid/src/main/AndroidManifest.xml_backup")
    androidManifest = open("../catroid/src/main/AndroidManifest.xml","r").read()
    applicationTag = androidManifest.split("<application")[1].split(">")[0]
    newApplicationTag = applicationTag.replace('android:label="${appName}"','android:label="' + versionName + '"')
    androidManifest = androidManifest.replace(applicationTag, newApplicationTag)
    open("../catroid/src/main/AndroidManifest.xml","w").write(androidManifest)

def buildApk():
    os.chdir("../")
    os.system("./gradlew assembleDebug")
    os.chdir("automationScripts")

    timestamp = time.strftime("%Y-%m-%d_%Hh%M")

    shutil.copyfile("../catroid/build/outputs/apk/catroid/debug/catroid-catroid-debug.apk", "./catroid-catroid-debug"
                    + timestamp + ".apk")

    shutil.copyfile("../catroid/build/outputs/apk/lunaAndCat/debug/catroid-lunaAndCat-debug.apk", "./catroid-lunaAndCat-debug"
                        + timestamp + ".apk")

def restoreBuildGradleAndAndroidManifest():
    shutil.move("../catroid/build.gradle_backup", "../catroid/build.gradle")
    shutil.move("../catroid/src/main/AndroidManifest.xml_backup", "../catroid/src/main/AndroidManifest.xml")

# ###############################################
def main():
    versionName = getVersion()

    updateBuildGradle()
    updateAndroidManifest(versionName + "beta")
    buildApk()
    restoreBuildGradleAndAndroidManifest()

# ###############################################
if __name__ == "__main__":
    main()

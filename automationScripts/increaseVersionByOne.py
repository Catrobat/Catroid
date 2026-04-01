# does part of step 2, increasing version code:
# https://catrobat.atlassian.net/wiki/spaces/KNOWHOW/pages/18286436/How+to+RELEASE

# step 1. build.gradle changes
buildGradle = open("../catroid/build.gradle", "r").read()

oldVersionCode = buildGradle.split('versionName "')[1].split('"')[0]
newVersionCode = "0.9." + str(int(oldVersionCode.lstrip("0.9.")) + 1)
buildGradle = buildGradle.replace(oldVersionCode, newVersionCode)

open("../catroid/build.gradle","w").write(buildGradle)

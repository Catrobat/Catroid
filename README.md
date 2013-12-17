# Catroid #

Catroid, also known as **Pocket Code**, is an on-device visual programming system for Android devices.

**Catrobat** is a visual programming language and set of creativity tools for smartphones, tablets, and mobile browsers. 
Catrobat programs can be written by using the Catroid programming system on Android phones and tablets.

For more information oriented towards developers please visit our [developers page](http://developer.catrobat.org/).

Nightly builds can be found [here](http://files.catrob.at).

# First Steps #

There are currently two ways to import the project:
  * [**old way** without gradle in eclipse](#eclipse)
  * [**new way** with gradle](#gradle)
  
It is recommended to use the [**gradle way**](#gradle) to import the project since the old version won't be supported any longer from us.

## Eclipse ##
1. Get the repo via shell: `git clone git@github.com:Catrobat/Catroid.git`
2. Drink some coffee - the repo size is about 100 MBs (2013-12-17)
3. Open Eclipse and select a workspace of your choice
4. Import every project from the cloned repo (File > Import... > General > Existing Projects into Workspace > Next > Select root directory (browse to it, confirm and wait until Eclipse shows you all projects below) > Finish
5. Wait until Eclipse reads/compiles everything
6. Maybe some cleanups will help to clear any compile errors - if any (Project > Clean... > Clean all projects > OK)
7. Also take care that a Project Build Target is chosen (Project > Properties > Android > choose any Build Target of your choice > OK)

### Warning ###
 
egit (Eclipse extension for git) will ignore some git settings and will make trouble with line endings!

## Gradle ##
You should have following path variables defined:
  * ```JAVA_HOME```
  * ```ANDROID_HOME```
  * and your git location in ```PATH```

After above settings are done it should be pretty easy:

1. Get the repo via shell: `git clone git@github.com:Catrobat/Catroid.git`
2. Drink some coffee - the repo size is about 100 MBs (2013-12-17)
3. (optional:) Open your command line tool in the cloned folder and execute ```gradlew tasks``` (```gradlew.bat tasks``` for windows user)
4. Import the ```Catroid``` folder into your IDE

**It's recommended to use the gradle-wrapper (```gradlew```) from within the project!**


# Resources and links #
* [Google Play Store Download](https://play.google.com/store/apps/details?id=org.catrobat.catroid)
* [Community website with sample programs](https://pocketcode.org/)
* [Installation Instructions](https://github.com/Catrobat/Catroid/wiki/Installation-Instructions)
* [Frequently Asked Questions](https://github.com/Catrobat/Catroid/wiki/Frequently-Asked-Questions)
* [Release history](https://github.com/Catrobat/Catroid/wiki/Release-History) (including links to videos of older versions of the programs)
* [Credits](http://developer.catrobat.org/credits)
* [Statistics on Ohloh](https://www.ohloh.net/p/catrobat/)
* [Twitter](http://twitter.com/Catroid)
* Visit us on IRC: [#catroid](http://webchat.freenode.net/?channels=catroid&uio=d4) on [freenode](http://freenode.net/)
* [Our Google group](https://groups.google.com/forum/?fromgroups#!forum/catrobat)
* [Catroid project blog](http://blog.catroid.org/)

# License #
[License](http://developer.catrobat.org/licenses) of our project (mainly AGPL v3).

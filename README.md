# Catroid #

**Catroid** is a visual coding IDE and interpreter for Android for the Catrobat programming language.

**Catrobat** is a visual programming language and a set of creativity tools for smartphones. 
Catrobat projects can be created using Catrobat's Android apps available on [Google Play](https://play.google.com/store/apps/developer?id=Catrobat) and iPhone apps available on [Apple's app store](https://apps.apple.com/us/developer/international-catrobat-association-verein-zur-foerderung/id1117935891).

For more information oriented towards developers, check out our [developers page](https://developer.catrobat.org/).

# Issues #

For reporting issues use our [JIRA Bugtracking System](https://catrobat.atlassian.net/jira/). Before that, please browse our currently open issues [here](https://catrobat.atlassian.net/jira/software/c/projects/IDE/issues/IDE-234?filter=allissues&jql=project%20in%20%28%22Catroid%20IDE%22%2C%20%22Catroid%20Stage%22%29%20ORDER%20BY%20created%20DESC).


# Contributing #

If you want to contribute, we recommend starting by [forking](https://help.github.com/articles/fork-a-repo/) the repository and exploring the codebase.  Then you can look at our [Issue-Tracker](https://catrobat.atlassian.net/jira/software/c/projects/IDE/issues?filter=allissues&jql=project%20in%20(%22Catroid%20IDE%22%2C%20%22Catroid%20Stage%22)%20ORDER%20BY%20created%20DESC) and start with fixing one ticket. We strictly use [Test-Driven Development](http://c2.com/cgi/wiki?TestDrivenDevelopment) and [Clean Code](http://www.planetgeek.ch/wp-content/uploads/2013/06/Clean-Code-V2.2.pdf), so first read everything you can about these development methods. Code developed in a different style will not be accepted. 
After you create a pull request, we will review your code and run the required tests on your branch.

Start setting up the working environment by following the instructions: https://github.com/Catrobat/Catroid/wiki/Setup-working-environment

Also make sure to read our guidelines for [creating a pull request](https://github.com/Catrobat/Catroid/wiki/Creating-a-pull-request)

A first training ticket usually consists of writing a new Catrobat language test, see https://catrobat.atlassian.net/browse/CATROID-390 for details.

## Catrobat Language Tests ##

Catrobat Language Tests (CLTs) are Catrobat projects with *Assert* bricks. They live in
`catroid/src/androidTest/assets/catrobatTests/` and are run by `CatrobatTestRunner`. Besides
Catroid, the web player and Catty (the iOS implementation) run them too. A CLT therefore checks
the behavior of the Catrobat language on every platform at once. Hence:

1. **New features include their CLTs in the same pull request**, just like unit and UI tests.
   Cover the language behavior of every new brick, sensor and function with CLTs. The *Testing*
   bricks (for example *Tap at* and *Simulate gamepad button*) let a CLT simulate input.
2. **Do not duplicate CLTs with unit tests.** If a CLT already covers a behavior, do not add a
   Kotlin or Java unit test for the same purpose. Keep unit and UI tests for what a CLT cannot
   check, such as Android-specific input handling, the editor UI and serialization details.
3. **Increase the Catrobat language version in the same pull request** whenever the pull request
   changes the language, for example with a new brick, script, sensor or function. The version
   is `CURRENT_CATROBAT_LANGUAGE_VERSION` in `Constants.java`. The new CLTs must already carry
   the new version, so it cannot wait until the release. Mark such pull requests with the
   *XML Change* label.


# Resources and links #
* [Apple Store Download](https://apps.apple.com/us/developer/international-catrobat-association-verein-zur-foerderung/id1117935891)
* [Google Play Store Download](https://play.google.com/store/apps/developer?id=Catrobat)
* [Community website with sample programs](https://share.catrobat.org/)
* [Frequently Asked Questions](https://github.com/Catrobat/Catroid/wiki/Frequently-Asked-Questions-(Developers))
* [Credits](https://developer.catrobat.org/contributors/)
* [Statistics on OpenHub](https://www.openhub.net/p/catrobat/)
* [Our Google group](https://groups.google.com/forum/?fromgroups#!forum/catrobat)

# License #
[License](https://developer.catrobat.org/pages/legal/licenses/) of our project (mainly AGPL v3).

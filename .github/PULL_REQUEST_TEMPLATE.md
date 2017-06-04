Thank you very much for contributing :+1::tada: Before creating a Pull Request, please check following points.

* git commit messages are conform with our guidelines. https://github.com/Catrobat/Catroid/wiki/Commit-Message-Guidelines
* Don't commit any unrelated changes (e.g. gradle, iml or .idea/* files).
* Changes are well tested.
* Gradle tasks 'check' and 'test' runs without any violations (./gradlew clean check test).
* The package org.catrobat.catroid.test in the (androidTest) module runs without any failures (./gradlew -Pandroid.testInstrumentationRunnerArguments.package=org.catrobat.catroid.test connectedCatroidDebugAndroidTest).
* Before finally submitting your Pull Request, please review your code like we would do.

From time to time tests break. Mostly because of different hardware or timing problems. If a test fails and your new changes have nothing to do with it, please take a look at https://jira.catrob.at/issues/?jql=labels%20%3D%20Broken_Test If the failing test isn't listed there, please create a new Jira ticket and label it Brokent_Test.

Thanks for reading, remove the whole text and type what you need.

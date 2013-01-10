The script takes an archive of a Catroid project and compiles and signs it.

Requirements:
Python 2.6 or greater
Apache Ant
Android SDK

Installation:
To install the script one should update the Ant local properties by running the following command
<path-to-android-sdk>/tools/android update project --path <path-to-catroid>

Executuion:
Execute the following command:
python handle_project.py <path_to_project> <path_to_catroid> <project_id> <output_folder>
	<path_to_project>  Path to the zip archive of the project
	<path_to_catroid>  Path to the Catroid source
	<project_id>       Unique project id
	<output_folder>     Path to the folder in which the resulting apk will be placed


The key store, alias and passwords for signing the apps are stored in the catroid/build.properties file
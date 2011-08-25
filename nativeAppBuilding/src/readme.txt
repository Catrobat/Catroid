The script takes the archive of a Catroid project and compiles it and signs it.

Requierments:
Python 2.6 or greater
Apache Ant
Android SDK

Installation:
To install the script one should update the Ant local propreties by running the following command
<path-to-android-sdk>/tools/android update project --path <path-to-catroid>

Executuion:
Execute the following command:
python handle_project.py <path_to_project> <path_to_catroid> <project_id> <ouput_folder>
	<path_to_project>  Path to the zip archive of the project
	<path_to_catroid>  Path to the Catroid source
	<project_id>       Unique project id
	<ouput_folder>     Path to the folder in which the resulting apk will be placed


The key store, alias and passwords for signing the apps is strored in the catroid/build.propreties file
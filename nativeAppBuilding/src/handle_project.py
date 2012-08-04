'''
Catroid: An on-device graphical programming language for Android devices
Copyright (C) 2010-2011 The Catroid Team
(<http://code.google.com/p/catroid/wiki/Credits>)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
'''

import os
import sys
import zipfile
import shutil
import fileinput
import hashlib
import xml.dom.minidom

'''
Automatically build and sign Catroid application.

python handle_project.py <path_to_project> <path_to_catroid> <project_id> <output_folder>

Example:
python handle_project.py test.zip ~/hg/catroid 42 .
'''

def unzip_project(archive_name):
    project_name = os.path.splitext(archive_name)[0]
    zipfile.ZipFile(archive_name).extractall(project_name)

def verify_checksum(path_to_file):
    filename = os.path.basename(path_to_file)
    checksum = filename.split('_', 1)[0]
    file_contents = open(os.path.join(path_to_file), 'rb').read()
    if checksum == hashlib.md5(file_contents).hexdigest().upper():
        return True
    else:
        return False

def rename_file_in_project(old_name, new_name, project_file_path, resource_type):
    doc = xml.dom.minidom.parse(project_file_path)

    if resource_type == 'images':
        tag_name = 'costumeFileName'
    elif resource_type == 'sounds':
        tag_name = 'fileName'

    for node in doc.getElementsByTagName(tag_name):
        if node.childNodes[0].nodeValue == old_name:
            node.childNodes[0].nodeValue = new_name
       
    f = open(project_file_path, 'wb')
    doc.writexml(f)
    f.close()

def rename_resources(path_to_project, project_name):
    os.rename(os.path.join(path_to_project, project_name + '.xml'),\
              os.path.join(path_to_project, 'project.xml'))
    res_token = 'resource'
    res_count = 0
    for resource_type in ['images', 'sounds']:
        path = os.path.join(path_to_project, resource_type)
        for filename in os.listdir(path):
            if filename == '.nomedia':
                continue
            basename, extension = os.path.splitext(filename)
            if verify_checksum(os.path.join(path, filename)):
                new_filename = res_token + str(res_count) + extension
                rename_file_in_project(filename, new_filename,\
                                    os.path.join(path_to_project, 'project.xml'),\
                                    resource_type)
                os.rename(os.path.join(path, filename),\
                           os.path.join(path, new_filename))
                res_count = res_count + 1
            else:
                print 'Wrong checksum for file', filename
                exit(1)

def copy_project(path_to_catroid, path_to_project):
    shutil.copytree(path_to_catroid, os.path.join(path_to_project, 'catroid'))

    for resource_type in ['images', 'sounds']:
        if not os.path.exists(os.path.join(path_to_project, 'catroid', 'assets', resource_type)):
            os.makedirs(os.path.join(path_to_project, 'catroid', 'assets', resource_type))
        for filename in os.listdir(os.path.join(path_to_project, resource_type)):
            shutil.move(os.path.join(path_to_project, resource_type, filename),\
                    os.path.join(path_to_project, 'catroid', 'assets', resource_type, filename))

    shutil.move(os.path.join(path_to_project, 'project.xml'),\
                    os.path.join(path_to_project, 'catroid', 'assets', 'project.xml'))

def set_project_name(new_name, path_to_file):
    doc = xml.dom.minidom.parse(path_to_file)

    for node in doc.getElementsByTagName('string'):
        if node.attributes.item(0).value == 'app_name':
            node.childNodes[0].nodeValue = new_name
    
    f = open(path_to_file, 'wb')
    doc.writexml(f)
    f.close()
    
def get_project_name(project_filename):
    for node in xml.dom.minidom.parse(project_filename).getElementsByTagName('name'):
        if node.parentNode.nodeName == 'Content.Project':
            return node.childNodes[0].nodeValue

def rename_package(path_to_project, new_package):
    catroid_package = 'at.tugraz.ist.catroid'
    path_to_source = os.path.join(path_to_project, 'catroid', 'src', 'at', 'tugraz', 'ist')
    os.rename(os.path.join(path_to_source, 'catroid'),\
              os.path.join(path_to_source, new_package))
    os.mkdir(os.path.join(path_to_source, 'catroid'))
    shutil.move(os.path.join(path_to_source, new_package),\
                os.path.join(path_to_source, 'catroid'))
    for root, dirs, files in os.walk(path_to_project):
        for name in files:
            if os.path.splitext(name)[1] in ('.java', '.xml'):
                for line in fileinput.input(os.path.join(root, name), inplace=1):
                    if catroid_package in line:
                        line = line.replace(catroid_package, catroid_package + '.' + new_package)
                    sys.stdout.write(line)

def edit_manifest(path_to_project):
    path_to_manifest = os.path.join(path_to_project, 'catroid', 'AndroidManifest.xml')
    doc = xml.dom.minidom.parse(path_to_manifest)

    for node in doc.getElementsByTagName('uses-permission'):
        node.parentNode.removeChild(node)

    for node in doc.getElementsByTagName('activity'):
        for i in range(0, node.attributes.length):
            if node.attributes.item(i).name == 'android:name':
                if node.attributes.item(i).value == '.ui.MainMenuActivity':
                   node.attributes.item(i).value = '.stage.NativeAppActivity'        

    f = open(path_to_manifest, 'wb')
    doc.writexml(f)
    f.close()
    

def main():
    if len(sys.argv) != 5:
        print 'Invalid arguments. Correct usage:'
        print 'python handle_project.py <path_to_project> <path_to_catroid> <project_id> <output_folder>'
        return 1
    path_to_project, archive_name = os.path.split(sys.argv[1])
    path_to_catroid = sys.argv[2]
    project_id = sys.argv[3]
    output_folder = sys.argv[4]
    project_filename = os.path.splitext(archive_name)[0]
    if os.path.exists(os.path.join(path_to_project, project_filename)):
        shutil.rmtree(os.path.join(path_to_project, project_filename))
    unzip_project(os.path.join(path_to_project, archive_name))
    path_to_project = os.path.join(path_to_project, project_filename)
    rename_resources(path_to_project, project_filename)
    project_name = get_project_name(os.path.join(path_to_project, 'project.xml'))
    copy_project(path_to_catroid, path_to_project)
    if os.path.exists(os.path.join(path_to_project, 'catroid', 'gen')):
        shutil.rmtree(os.path.join(path_to_project, 'catroid', 'gen'))
    edit_manifest(path_to_project)
    rename_package(path_to_project, 'app_' + str(project_id))
    set_project_name(project_name, os.path.join(path_to_project, 'catroid', 'res', 'values', 'common.xml'))
    os.system('ant release -f ' + os.path.join(path_to_project, 'catroid', 'build.xml'))
    for filename in os.listdir(os.path.join(path_to_project, 'catroid', 'bin')):
        if filename.endswith('release.apk'):
            shutil.move(os.path.join(path_to_project, 'catroid', 'bin', filename),\
                        os.path.join(output_folder, project_filename + '.apk'))
    shutil.rmtree(path_to_project)
    return 0

if __name__ == '__main__':
    main()

import os
import sys
import zipfile
import shutil
import hashlib
import xml.dom.minidom

'''
Automatically build and sign Catroid application.

python handle_project.py <path_to_project> <path_to_catroid>

Example:
python handle_project.py test.zip ~/hg/catroid
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

def rename_file_in_project(old_name, new_name, project_file_path,resource_type):
    doc = xml.dom.minidom.parse(project_file_path)

    if resource_type == 'images':
        tag_name = 'imageName'
    elif resource_type == 'sounds':
        tag_name = 'soundfileName'

    for node in doc.getElementsByTagName(tag_name):
        if node.childNodes[0].nodeValue == old_name:
            node.childNodes[0].nodeValue = new_name
       
    f = open(project_file_path, 'wb')
    doc.writexml(f)
    f.close()

def rename_resources(project_name):
    os.rename(os.path.join(project_name, project_name + '.xml'),\
              os.path.join(project_name, 'project.xml'))
    res_token = 'resource'
    res_count = 0
    for resource_type in ['images', 'sounds']:
        path = os.path.join(project_name, resource_type)
        for filename in os.listdir(path):
            basename, extension = os.path.splitext(filename)
            if verify_checksum(os.path.join(path, filename)):
                new_filename = res_token + str(res_count)
                rename_file_in_project(filename, new_filename,\
                                    os.path.join(project_name, 'project.xml'),\
                                    resource_type)
                shutil.move(os.path.join(path, filename),\
                           os.path.join(project_name, new_filename + extension))
                res_count = res_count + 1
    shutil.rmtree(os.path.join(project_name, 'sounds'))
    shutil.rmtree(os.path.join(project_name, 'images'))

def copy_project(path_to_catroid, project_name):
    shutil.copytree(path_to_catroid, os.path.join(project_name, 'catroid'))
    for filename in os.listdir(project_name):
        if not os.path.isdir(os.path.join(project_name, filename)):
            shutil.move(os.path.join(project_name, filename),\
                os.path.join(project_name, 'catroid', 'res', 'raw', filename))

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
        if node.parentNode.nodeName == 'project':
            return node.childNodes[0].nodeValue

def main():
    if len(sys.argv) != 3:
        print 'Invalid arguments. Correct usage:'
        print 'python handle_project.py <path_to_project> <path_to_catroid>'
        return 1
    archive_name = sys.argv[1]
    path_to_catroid = sys.argv[2]
    project_filename = os.path.splitext(archive_name)[0]
    if os.path.exists(project_filename):
        shutil.rmtree(project_filename)
    unzip_project(archive_name)
    rename_resources(project_filename)
    project_name = get_project_name(os.path.join(project_filename, 'project.xml'))
    copy_project(path_to_catroid, project_filename)
    set_project_name(project_name, os.path.join(project_filename, 'catroid', 'res', 'values', 'common.xml'))
    os.system('ant release -f ' + os.path.join(project_filename, 'catroid', 'build.xml'))
    shutil.move(os.path.join(project_filename, 'catroid', 'bin', 'NativeAppActivity-release.apk'),\
                project_filename + '.apk')
    shutil.rmtree(project_filename)
    return 0

if __name__ == '__main__':
    main()

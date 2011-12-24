import unittest
import sys
import os
import shutil
import filecmp

sys.path.append(sys.path[0])

from src.handle_project import verify_checksum
from src.handle_project import get_project_name
from src.handle_project import rename_file_in_project
from src.handle_project import set_project_name

class TesthandleProject(unittest.TestCase):

    def setUp(self):
        self.bad_checksum_filename = os.path.join('test_resources',\
                            '11A111E1AE8B0BA34BF5CE0A519DB327_badchecksum.png')
        self.good_checksum_filename = os.path.join('test_resources',\
                            '7EEAEE698F586C4D13D261D8CB647132_goodchecksum.png')
        self.project_filename = os.path.join('test_resources', 'project.xml')
        self.new_project_filename = os.path.join('test_resources', 'modified_project.xml')
        self.project_name = 'Test Project Name'
        self.new_project_name = 'New Project Name'
        self.old_image_name_1 = 'old_image_name_1'
        self.old_image_name_2 = 'old_image_name_2'
        self.old_image_name_3 = 'old_image_name_3'
        self.old_sound_name = 'old_sound_name'
        self.new_image_name_1 = 'new_image_name_1'
        self.new_image_name_2 = 'new_image_name_2'
        self.new_image_name_3 = 'new_image_name_3'
        self.new_sound_name = 'new_sound_name'

    def test_checksum(self):
        self.assertFalse(verify_checksum(self.bad_checksum_filename))
        self.assertTrue(verify_checksum(self.good_checksum_filename))
        
    def test_get_project_name(self):
        self.assertEquals(get_project_name(self.project_filename),\
                            self.project_name)
    
    def test_rename_resources_and_project_name(self):
        temp_project_filename = os.path.join('test_resources', 'temp_project.xml')
        shutil.copyfile(self.new_project_filename, temp_project_filename)
        rename_file_in_project(self.old_image_name_1, self.new_image_name_1,\
                            temp_project_filename, 'images')
        rename_file_in_project(self.old_image_name_2, self.new_image_name_2,\
                            temp_project_filename, 'images')
        rename_file_in_project(self.old_image_name_3, self.new_image_name_3,\
                            temp_project_filename, 'images')
        rename_file_in_project(self.old_sound_name, self.new_sound_name,\
                            temp_project_filename, 'sounds')
        set_project_name(self.new_project_name, temp_project_filename)
        self.assertTrue(filecmp.cmp(temp_project_filename, self.new_project_filename))
        os.remove(temp_project_filename)

if __name__ == '__main__':
    unittest.main()

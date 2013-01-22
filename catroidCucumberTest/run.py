#!/usr/bin/env python
# -*- encoding: utf-8 -*-

import os, subprocess, argparse

def main ():
    arg_parser = argparse.ArgumentParser(description='Catroid Cucumber Test')
    arg_parser.add_argument('-c', '--clean', action='store_true', dest='build_clean',
        help='Run ant clean before building the apk.', default=False)
    arg_parser.add_argument('-nb', '--no-build', action='store_true', dest='skip_build',
        help='Do not run ant build (for repeated testing).', default=False)
    args = arg_parser.parse_args()

    catroid_dir = os.path.abspath('..%scatroid' % os.sep)
    apk_path = catroid_dir + '%sbin%scatroid-debug.apk' % (os.sep, os.sep)

    if args.build_clean:
        proc = subprocess.Popen(['ant', 'clean'], cwd=catroid_dir)
        proc.wait()

    if not args.skip_build:
        proc = subprocess.Popen(['ant', 'debug'], cwd=catroid_dir)
        proc.wait()
    elif not os.path.isfile(apk_path):
        print("APK not found. You need to build it first.")
        return

    proc = subprocess.Popen(['calabash-android', 'run', apk_path])
    proc.wait()

if __name__=='__main__':
    try:
        main ()
    except KeyboardInterrupt:
        pass

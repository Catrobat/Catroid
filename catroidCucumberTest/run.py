#!/usr/bin/env python
# -*- encoding: utf-8 -*-
#
# TODO: translate this script into a Rakefile

import os, subprocess, argparse

def main():
    arg_parser = argparse.ArgumentParser()
    arg_parser.add_argument('-b', '--build', action='store_true', dest='build_catroid',
        help='build the Catroid APK', default=False)
    arg_parser.add_argument('-c', '--clean', action='store_true', dest='build_clean',
        help='additionaly run ant clean before building', default=False)
    arg_parser.add_argument('-bc', '--build-calabash', action='store_true', dest='build_calabash',
        help='build and install calabash-android', default=False)
    arg_parser.add_argument('-dr', '--dry-run', action='store_true', dest='dry_run',
        help='skip running calabash-android', default=False)
    args = arg_parser.parse_args()

    calabash_dir = os.path.abspath('calabash-android%sruby-gem' % os.sep)
    catroid_dir = os.path.abspath('..%scatroid' % os.sep)
    apk_path = catroid_dir + '%sbin%scatroid-debug.apk' % (os.sep, os.sep)

    # --build-calabash: build and install the calabash-android gem.
    if args.build_calabash:
        proc = subprocess.Popen(['rake', 'build'], cwd=calabash_dir)
        proc.wait()

    try:
        subprocess.check_output('calabash-android')
    except OSError:
        print "calabash-android not found. You need to build it first (--build-calabash)."

    # --clean: run ant clean for Catroid.
    if args.build_clean:
        proc = subprocess.Popen(['ant', 'clean'], cwd=catroid_dir)
        proc.wait()

    # --build: build the Catroid APK.
    if args.build_catroid:
        proc = subprocess.Popen(['ant', 'debug'], cwd=catroid_dir)
        proc.wait()

    if not os.path.isfile(apk_path):
        print "Catroid APK not found. You need to build it first (--build)."
        return

    # Finally, run calabash with the Catroid apk.
    if not args.dry_run:
        proc = subprocess.Popen(['calabash-android', 'run', apk_path])
        proc.wait()

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        pass

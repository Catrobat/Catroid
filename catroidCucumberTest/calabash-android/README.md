The purpose of this directory is to contain Cucumber tests for Catroid, run via calabash-android.

#### Setup:

1. Install [Ruby](http://www.ruby-lang.org)
2. Install [RVM](https://rvm.io)
3. Install gems: `cd calabash-android/ruby-gem/ && bundle`

You should use RVM with the `.ruby-*` files provided in this directory.

You may also need to install [bundler](http://rubygems.org/gems/bundler) if you don't have it. It is required to build the calabash-android gem.

#### Running tests:

Use the Rakefile to run tests (e.g. `rake test`). Run rake -T to list available tasks.

# These steps were automatically generated.

Given /^there is one sprite$/ do
  performAction('there_is_one_sprite')
end

And /^the sprite has one script$/ do
  performAction('the_sprite_has_one_script')
end

And /^the script has one (\w+Brick) with (-?\d+)$/ do |name, arg|
  performAction('the_script_has_one_brick_with_int', name, arg)
end

And /^the script has one (\w+Brick)$/ do |name|
  performAction('the_script_has_one_brick', name)
end

When /^I run the script$/ do
  performAction('run_the_script')
end

And /^I wait (\d+) ms$/ do |millis|
  performAction('wait_milliseconds', millis)
end

Then /^the sprite has a ([x,y,z]) position of (-?\d+)$/ do |axis, value|
  performAction('sprite_has_position', axis, value)
end

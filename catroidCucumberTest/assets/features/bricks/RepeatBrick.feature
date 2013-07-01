Feature: Repeat Brick
  A Repeat Brick repeats no brick, one brick or a sequence of bricks a given number of times.

  Background:
    Given an empty program
    And an object 'cuke'
    And a StartScript

  Scenario: Repeat 0 bricks 0 times.
    Given a RepeatBrick with 0 iterations
    When the script is executed
    Then the elapsed time is at least 0 ms

  Scenario: Repeat 0 bricks 1 time.
    Given a RepeatBrick with 1 iteration
    When the script is executed
    Then the elapsed time is at least 20 ms

  Scenario: Repeat 1 bricks 1 times.
    Given a RepeatBrick with 1 iterations
    And a ChangeYByNBrick with 10
    When the script is executed
    Then the elapsed time is at least 20 ms
    And the object 'cuke' has a y position of 10

  Scenario: Repeat 2 bricks 16 times.
    Given a RepeatBrick with 16 iterations
    And a ChangeXByNBrick with 10
    And a ChangeYByNBrick with 10
    When the script is executed
    Then the elapsed time is at least 320 ms
    And the object 'cuke' has a x position of 160
    And the object 'cuke' has a y position of 160

#  Scenario: Changing an object's position inside a loop
#    Given I have a program with the name 'cucumber'
#    And a background 'background' that has a StartScript with these bricks:
#      | SetLookBrick | background |
#    And an object 'cuke' that has a StartScript with these bricks:
#      | SetLookBrick | default_image |
#    And a WhenTappedScript with these bricks:
#      | RepeatBrick     | 8    |
#      | ChangeYByNBrick | 1    |
#      | LoopEndBrick    | null |
#    When I start the program
#    Then the object 'cuke' has a y position of 0
#    When I tap the object 'cuke'
#    And I wait 200 milliseconds
#    Then the object 'cuke' has a y position of 8

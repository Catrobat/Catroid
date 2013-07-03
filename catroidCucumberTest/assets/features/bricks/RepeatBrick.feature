Feature: Repeat Brick
  A Repeat Brick repeats no brick, one brick or a sequence of bricks a given number of times.

  Background:
    Given an empty program
    And an object 'cuke'
    And a StartScript

  Scenario: Repeat 0 bricks 0 times.
    Given a RepeatBrick with 0 iterations
    And a RepeatEndBrick
    When the script is executed
    And the script terminates
    Then the elapsed time is at least 0 ms
    And the elapsed time is at most 1 ms

  Scenario: Repeat 0 bricks 1 time.
    Given a RepeatBrick with 1 iteration
    And a RepeatEndBrick
    When the script is executed
    And the script terminates
    Then the elapsed time is at least 20 ms
    And the elapsed time is at most 21 ms

  Scenario: Repeat 1 bricks 1 times.
    Given a RepeatBrick with 1 iterations
    And a ChangeYByNBrick with 10
    And a RepeatEndBrick
    When the script is executed
    And the script terminates
    Then the elapsed time is at least 20 ms
    And the elapsed time is at most 21 ms
    And the object 'cuke' has a y position of 10

  Scenario: Repeat 2 bricks 16 times.
    Given a RepeatBrick with 4 iterations
    And a ChangeXByNBrick with 10
    And a ChangeYByNBrick with 10
    And a RepeatEndBrick
    When the script is executed
    And the script terminates
    Then the elapsed time is at least 80 ms
    And the elapsed time is at most 81 ms
    And the object 'cuke' has a x position of 40
    And the object 'cuke' has a y position of 40

  Scenario: Repeat 0 bricks -2 times.
    Given a RepeatBrick with -2 iterations
    And a RepeatEndBrick
    When the script is executed
    And the script terminates
    Then the elapsed time is at least 0 ms
    And the elapsed time is at most 1 ms

  Scenario: Repeat 1 SetVariableBrick 4 times.
    Given a SetVariableBrick with 'x' and 4
    And a RepeatBrick with 'x' iterations
    And a ChangeVariableBrick with 'x' and -1
    And a RepeatEndBrick
    When the script is executed
    And the script terminates
    Then the elapsed time is at least 80 ms
    And the elapsed time is at most 81 ms
    Then the variable 'x' is 0

  Scenario: Repeat a shorter WaitBrick 4 times.
    Given a RepeatBrick with 4 iterations
    And a WaitBrick with 0.015 seconds
    And a RepeatEndBrick
    When the script is executed
    And the script terminates
    Then the elapsed time is at least 80 ms
    And the elapsed time is at most 81 ms

  Scenario: Repeat a longer WaitBrick 4 times.
    Given a RepeatBrick with 4 iterations
    And a WaitBrick with 0.5 seconds
    And a RepeatEndBrick
    When the script is executed
    And the script terminates
    Then the elapsed time is at least 2000 ms
    And the elapsed time is at most 2001 ms

  Scenario: Repeat a brick 2.99 times
    Given a RepeatBrick with 2.99 iterations
    And a ChangeYByNBrick with 10
    And a RepeatEndBrick
    When the script is executed
    And the script terminates
    Then the elapsed time is at least 40 ms
    And the elapsed time is at most 41 ms
    And the object 'cuke' has a x position of 40

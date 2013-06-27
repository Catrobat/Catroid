Feature: Loop Brick

  Scenario: Changing an object's position inside a loop
    Given I have a program with the name 'cucumber'
    And a background 'background' that has a StartScript with these bricks:
      | SetLookBrick | background |
    And an object 'cuke' that has a StartScript with these bricks:
      | SetLookBrick | default_image |
    And a WhenTappedScript with these bricks:
      | RepeatBrick     | 8    |
      | ChangeYByNBrick | 1    |
      | LoopEndBrick    | null |
    When I start the program
    Then the object 'cuke' has a y position of 0
    When I tap the object 'cuke'
    And I wait 200 milliseconds
    Then the object 'cuke' has a y position of 8

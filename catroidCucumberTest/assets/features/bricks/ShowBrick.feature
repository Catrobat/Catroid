Feature: Show Brick

  Scenario: Tapping the background shows a hidden object
    Given I have a program with the name 'cucumber'
    And a background 'background' that has a StartScript with these bricks:
      | SetLookBrick | background |
    And a WhenTappedScript with these bricks:
      | BroadcastBrick | show |
    And an object 'cuke' that has a StartScript with these bricks:
      | SetLookBrick | default_image |
    And a WhenTappedScript with these bricks:
      | HideBrick | null |
    And a BroadcastScript for the show message with these bricks:
      | ShowBrick | null |
    When I start the program
    And I tap the object 'background'
    Then the object 'cuke' is visible
    When I tap the object 'cuke'
    Then the object 'cuke' is invisible
    When I tap the object 'background'
    Then the object 'cuke' is visible

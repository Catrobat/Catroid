Feature: Broadcast brick
  A Broadcast brick should send a message and When scripts should react to it.

  Background:
    Given I have a Program
    And this program has an Object 'Object'

  Scenario: A Broadcast brick sends a message in a program with one When script

    Given 'Object' has a Start script
    And this script has a Broadcast 'hello' brick
    And this script has a Print brick with
    """
    I am the Start script.
    """

    Given 'Object' has a When 'hello' script
    And this script has a Wait 100 milliseconds brick
    And this script has a Print brick with
    """
    I am the When 'hello' script.
    """

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output
    """
    I am the Start script.
    I am the When 'hello' script.

    """

  Scenario: A Broadcast brick sends a message in a program with two When scripts

    Given 'Object' has a Start script
    And this script has a Broadcast 'hello' brick
    And this script has a Wait 100 milliseconds brick
    And this script has a Print brick with
    """
    I am the Start script.
    """

    Given 'Object' has a When 'hello' script
    And this script has a Print brick with
    """
    I am the first When 'hello' script.
    """

    Given 'Object' has a When 'hello' script
    And this script has a Wait 200 milliseconds brick
    And this script has a Print brick with
    """
    I am the second When 'hello' script.
    """

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output
    """
    I am the first When 'hello' script.
    I am the Start script.
    I am the second When 'hello' script.

    """

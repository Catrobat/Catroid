Feature: Broadcast & Wait Blocking Behavior (like in Scratch)

  If a broadcast is sent while a Broadcast Wait brick is waiting for the same message, the
  responding When scripts should be restarted and the Broadcast Wait brick should stop waiting
  and immediately continue executing the rest of the script.

  Background:
    Given I have a Program
    And this program has an Object 'Object'

  Scenario: A waiting BroadcastWait brick is unblocked when the same broadcast message is resent

    Given 'Object' has a Start script
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with '-S1-'

    Given 'Object' has a Start script
    And this script has a Wait 200 milliseconds brick
    And this script has a Broadcast 'hello' brick

    Given 'Object' has a When 'hello' script
    And this script has a Print brick with '-W1-'
    And this script has a Wait 400 milliseconds brick
    And this script has a Print brick with '-W2-'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output '-W1--S1--W1--W2-'

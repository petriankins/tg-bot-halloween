# tg-bot-halloween

# Admin functions

The admin telegram user id is set through environment variable ${ADMIN_ID}.
The person whose id is set is able to execute a few more commands

## Leaderboard

```
/stats
```

Shows the leaderboard of the ones who has finished the game.

## /sendmany

```
/sendmany <id1>,<id2>,<id3> <The message>
```

- 1st argument - id's of the users, separated by comma, __WITHOUT__ spaces
- 2nd argument - the message to be sent

User ids are in the <code> block, so they will be copied on click.


---

# TBD

## Scoring system

- finding the best ending - 150 or something
- finding good ending - 100
- divide total points by number of attempts?
-
    - easter egg with grey mare for extra prize?

## Restore the ability to config random scenarios order

Set of the scenarios.
Max scenarios per one game.
Boolean isRandomScenariosEnabled.
# alea-iacta-sys-vampire5
A RPG system module for Alea Iacta Est implementing Vampire the Masquerade - 5th Edition

## Description
This command will roll several d10 and will check for successes, this will also check for critical successes. If a hunger value is passed it will consider this for the roll (ex. if 8 dice are requested and a hunger value of 3 is passed it will roll 5 normal dice and 3 hunger dice), and eventual chance for Messy Criticals, or Bestial Failures will be communicated.

### Roll modifiers
Passing these parameters, the associated modifier will be enabled:

* `-v` : Will enable a more verbose mode that will show a detailed version of every result obtained in the roll.
* `-r` : Will do a reroll - based on the last result obtained by the user - according to the 'Willpower' rule. Requires that the last roll of the user was of this system and that was done in the last 2 minutes.

## Help print
```
Vampire the Masquerade 5th Edition [ vampire-5th | vt5 ]

Usage: vt5 -n <numberOfDice>
   or: vt5 -r

Description:
This command will roll several d10 and will check for successes,
this will also check for critical successes. If a hunger value
is passed it will consider this for the roll (ex. if 8 dice are
requested and a hunger value of 3 is passed it will roll 5 normal
dice and 3 hunger dice), and eventual chance for Messy Criticals,
or Bestial Failures will be communicated.

Options:
  -n, --number=diceNumber    Number of dice in the pool
  -u, --hunger=hungerValue   Hunger value
  -r, --reroll               Enable Will usage for reroll
  -h, --help                 Print the command help
  -v, --verbose              Enable verbose output
```

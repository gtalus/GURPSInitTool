Command Mode
============

'Command Mode' is a single-key shortcut mode intended to be used during an encounter when quick operation is needed. In this mode, when a text entry field does not have the focus keystrokes are converted into commands to streamline combat operations. This superceeds certain other keyboard operations, such as using arrow keys to navigate the table or using keystrokes to start editing of table cells.

.. note:: Command Mode is only active in the main Initiative window and the defense dialog: it does not impact the Group manager.
	  
This mode is activated either by pressing the Command Mode button (which also doubles as an indicator of its state) in the toolbar or using the Ctrl+Q shortcut.

Main Window
~~~~~~~~~~~

Editing of Injury and Fatigue columns is not automatically started by pressing a number key while in command mode. Only the '+/=' or '-' keys activate the quick delta function.

Initiative Control:

- **Right Arrow Key**: Step to the next active combatant
- **Down Arrow Key**: Step to the end of the round
- **Up Arrow Key**: Step to the start of the next round

Search:

- **/**: Start/End search. While search is active, command mode is suspended.
- **.** or **>**: Next search result
- **,** or **<**: Previous search result

General Functions:

- **d**: Top selected row defends
- **t**: Refresh tags for all rows
- **g**: Open Group Manager
  
All selected rows:

- **k**: Attack using default attack
- **1** to **0**: Attack using specified attack #, if it exists (0 is 10). Ex: 2 will trigger the second attack in each combatant's attack table.
- **s**: Set posture to standing (remove Kneeling/Prone)
- **n**: Set posture to Kneeling
- **p**: Set posture to Prone
- **h**: Toggle Physical Stun status
- **m**: Toggle Mental Stun status
- **r**: Toggle Stun Recovering status
- **a**: Toggle Attacking status
- **i**: Toggle Disarmed status
- **u**: Toggle Unconscious status
- **x**: Toggle Dead status
  


Defense Dialog
~~~~~~~~~~~~~~

In the Defense dialog, the following commands are used:

- **p**: Set Parry defense
- **b**: Set Block defense
- **g**: Set Dodge defense
- **n**: Set None defense

- **e**: Toggle Extra Effort option
- **r**: Toggle Retreat option
- **s**: Toggle Side Attack option
- **t**: Toggle Stun option
- **h**: Toggle Shield option
- **d** or **o**: Decrease Other modifier by 1
- **D** or **O**: Increase Other modifier by 1

Number keys and the '/' key are typed normally, allowing editing of the Other modifier, Roll, and DR.

If focus is in the Damage input field AND there is text in the field, then command mode is temporarily suspended to allow entry of the damage text. If the focus is on the Location drop down, then command mode is also suspended to allow keyboard selection of the location.

Because the starting focus of the Defend dialog is the damage field but it also starts empty, command mode is initially active which allows selection of defense type and options. Damage strings must start with a number so typing damage after defense selection is as normal. The Tab key moves focus to the Location dropdown, where command mode is similarly suspended and selection can be done using the keyboard.



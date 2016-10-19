Combat Tracking
=================


Automation
----------

Which types of combatants are subject to automation is user configurable through the Settings dialog, which is accessible under the Edit menu of the main window. By default, combatants of type 'ally', 'neutral', and 'enemy' are automated. Each automation function can also be disabled or enabled individually.

.. image:: _static/20_automation_options.png
   :align: left

.. note:: These automations *only* apply to combatants with a type that has had automation enabled.

Attack
~~~~~~
Combatants with the 'Attacking' status that are not stunned or otherwise disabled will make their default attack at the start of their turn. Keep in mind that the attack is only printed to the log.

Unconsciousness Checks
~~~~~~~~~~~~~~~~~~~~~~
If the combatant is at 0 or fewer remaining HP, they make a consciousness roll at the start of their turn (before attacking, if enabled). If they fail their status is set to Disarmed, Prone and Unconscious and their turn is skipped.

Knockdown and Stunning
~~~~~~~~~~~~~~~~~~~~~~
The Defense dialog prints Knockdown/Stunning calculations to the log. If this option is set, the results of those calculations are also applied to the combatant's status.

Stun Recovery
~~~~~~~~~~~~~
Combatants with mental or physical stun statuses will make checks at the start of their turn to recover. If they have the 'StunRecover' state, then they will automatically lose that state at the start of their next turn.

Attacks
-------

Besides automated attacks, attacks can be triggered manually as well. Both the toolbar attack button and the initiative table right-click menu attack option cause all selected rows to report their default attack. The Attacks section of the detail panel also has an attack button which triggers the selected attack in the table.

When attacks are reported, they do not trigger any further automation to apply the results to a target. This must be done manually or through the defense dialog.

Attacks take into account the attacker's current posture. Shock is also applied if that option has been enabled.

.. note:: Shock caused by damage is tracked by the tool even if the option to apply it to attacks is disabled. You can see the tracked values in the Temp/Debug section of the details panel. 

Defenses
--------

.. image:: _static/21_defense_dialog.png
   :align: left

Pressing the shield button or selecting the 'defend' right-click menu option brings up the Defense dialog. This is a very useful tool for speeding defense evaluation and/or injury calculation. Keyboard shortcuts are available for most operations, and can be discovered in the tooltips.

The top half of the dialog handles defense evaluation. Each defense is calculated based on the base value and how many times it has been used this turn (for Parry and Block) or the current injury/fatigue level (for Dodge) with the best defense initially selected. Defense options and modifiers are on the left side. The current posture is selected and the 'Shield' option is initially selected if its DB is greater than 0.

The bottom section deals with damage and injury. Damage is accepted in many forms: '9', '12 (2) pi', or a die expression like '2d+2 cr'. Note that the damage type is assumed to be 'cut' if not specified. DR is copied from the main sheet. All basic hit locations and some extended locations are available and fully modeled.

.. image:: _static/22_defense_damage.png
   :align: left

Injury calculation is comprehensive. Penetrating basic damage is converted to injury based on the location and any injury tolerances, then limited by the crippling thresholds for extremities.

.. note:: Crippling injury to an eye is not reported.
.. note:: Posture is not automatically modified for crippling injuries.

Any part of the defense calculation can be modified and the results will be immediately updated to show the results. Once you are done, the ESC key or Cancel button will discard the results while the Enter key or OK button will accept them. Any injury and/or fatigue (for extra effort usage) will be applied to the defender and log messages printed showing the details, including knockdown/stunning calculations if approprite.

.. image:: _static/23_defense_log.png

.. note:: Death checks from damage are not currently reported or automated by the tool. The penalty to consciousness checks in the HT column of the initiative table can be used to track when HP thresholds are crossed.

Critical Failures
~~~~~~~~~~~~~~~~~

Critically failing a defense triggers either a message indicating the appropriate additional impact (for parry and block defenses) or , a message is printed in the log indicating what should happen but no changes are made to the state of the combatant. For dodge, posture is set to 'prone' (a message will not be printed unless you have enabled 'Verbose Status Changes' in the options).

If extra effort is used in a critical defense failure, then one extra injury is applied. This extra injury is applied separately from the injury taken from the attack, and is not included in the defense dialog result summary. It will be shown in the log.

Shields
~~~~~~~
The Damage to Shields rules are used: if the margin of success on a defense is less than the DB of a shield used in that defense the the blow is considered to have squarely hit the shield, and may damage it or even penetrate to hit the defender.

.. image:: _static/24_defense_shield.png
   :align: left
	   
Cover DR of the shield is calculated and applied to the damage, and then any excess damage ('overpenetration') is applied to the selected hit location.

Injury to the shield is also calculated and tracked in a simplified manner. Basic damage to the shield is limited to its base HP/4, matching the cover DR calculation. Injury to the shield is calculated assuming the Injury Tolerance:Homogenous trait. Once a shield's current HP is reduced to 0, the shield defense option will no longer be selected by default. The current stats of a shield are shown in the tooltip of the shield defense option.

.. note:: On a shield hit the defender hit location for any penetrating damage is not changed to the arm automatically.

Injury & Fatigue
~~~~~~~~~~~~~~~~

Injury and fatigue are tracked as positive values: when you lose HP that is noted as an increase of 'Injury', and when you are healed that is noted as a reduction in injury. The 'HP' and 'FP' columns always show the base HP and FP, without any injury or fatigue applied. You can customize the columns to include 'currFP' and 'currHP' if you want to see the adjusted values.

Changes to injury or fatigue while a combat is running will be displayed in the log. The rules for losing FP (ie. an increase in 'Fatigue') while at 0 FP or less are applied: additional fatigue causes injury on a point-per-point basis. Fatigue is limited to 2*FP, but further loses are still applied as injury.

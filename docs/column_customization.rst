Customizing Columns
===================

The columns displayed in both the initiative table in the main window and the group table in the group manager can be customized. You can get to the customization dialog either through the 'View' menu or by right clicking on the table header and selecting 'Customize Columns'. This will bring up the following dialog:

.. image:: _static/40_column_customizer.png

Using the Column Customizer dialog, you can select which combatant traits are displayed as columns in the table you are customizing. The initial list of available traits are those that are guaranteed to be present for every combatant.

You may add columns for additional traits by clicking the 'Add Custom Trait' button. These columns show whether or not a particular combatant has that trait, or it's value if it has one. For example, you can add a column for the trait 'Combat Reflexes' (or 'CR'). Custom traits will be removed from the list if they are not displayed.

.. note:: Trait names are case sensitive. Short form aliases are supported (such as 'HPT' for 'High Pain Threshold'). A list of aliases can be found in :ref:`trait-aliases`.

.. image:: _static/41_customized_columns.png
	   
.. note:: Columns can also be temporarily re-ordered by dragging the header cell. This method of ordering is not saved between runs.

.. note:: Certain traits cannot be edited from the table. This includes traits that are calculated from other traits, custom traits, and the 'Notes' field. This restriction is indicated by tinting the cell gray.

Special Columns
---------------

Certain columns have special behavior, such as the 'Move' and 'HT' columns displaying the impact of injury.

Move & Dodge
~~~~~~~~~~~~

Displays effective move/dodge (after applying modifiers for <1/3 health or fatigue). Original value is listed in parentheses. A yellow triangle icon is also shown for half move/dodge, and a red circle for 1/4 move/dodge. If edited to have a non-integer value, text will be shown in red.

HT
~~

If injury is equal to or greater than HP, the HT column will show a yellow triangle icon indicating consciousness rolls are required each turn. If injury is at least 2*HP, then the appropriate modifier for the consciousness roll will be shown in brackets. If edited to have a non-integer value, the text will be shown in red.

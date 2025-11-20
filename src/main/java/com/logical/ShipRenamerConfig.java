package com.logical;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("shiprenamer")
public interface ShipRenamerConfig extends Config
{
	@ConfigItem(
		keyName = "ship1name",
		name = "Ship 1's Name",
		description = "The name for the first ship"
	)
	default String ship1name()
	{
		return "SS Earl Grey";
	}

	@ConfigItem(
	keyName = "ship2name",
	name = "Ship 2's Name",
	description = "The name for the second ship"
	)
	default String ship2name()
	{
		return "Indiana Harbor";
	}

	@ConfigItem(
	keyName = "ship3name",
	name = "Ship 3's Name",
	description = "The name for the third ship"
	)
	default String ship3name()
	{
		return "Tregurtha";
	}

	@ConfigItem(
	keyName = "ship4name",
	name = "Ship 4's Name",
	description = "The name for the fourth ship"
	)
	default String ship4name()
	{
		return "Boaty McBoatface";
	}

	@ConfigItem(
	keyName = "ship5name",
	name = "Ship 5's Name",
	description = "The name for the fifth ship"
	)
	default String ship5name()
	{
		return "Titanic 2: On Fire";
	}
}

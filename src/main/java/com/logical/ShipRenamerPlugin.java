package com.logical;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.DBTableID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Ship Renamer"
)
public class ShipRenamerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ShipRenamerConfig config;

	private String[] trueNames;

	@Override
	protected void startUp() throws Exception
	{
		log.info("ShipRenamer started!");

		trueNames = new String[5];

		clientThread.invoke(() -> {
			if(client.getGameState() == GameState.LOGGED_IN) {
				for (int i = 1; i < 6; i++) {
					saveTrueName(i);
				}

				setSailingPanel();
			}
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("ShipRenamer stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if(gameStateChanged.getGameState() == GameState.LOGGED_IN) {

		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		int id = varbitChanged.getVarbitId();

		if (id == VarbitID.SAILING_BOAT_1_NAME_1 || id == VarbitID.SAILING_BOAT_1_NAME_2 || id == VarbitID.SAILING_BOAT_1_NAME_3)
			saveTrueName(1);
		if (id == VarbitID.SAILING_BOAT_2_NAME_1 || id == VarbitID.SAILING_BOAT_2_NAME_2 || id == VarbitID.SAILING_BOAT_2_NAME_3)
			saveTrueName(2);
		if (id == VarbitID.SAILING_BOAT_3_NAME_1 || id == VarbitID.SAILING_BOAT_3_NAME_2 || id == VarbitID.SAILING_BOAT_3_NAME_3)
			saveTrueName(3);
		if (id == VarbitID.SAILING_BOAT_4_NAME_1 || id == VarbitID.SAILING_BOAT_4_NAME_2 || id == VarbitID.SAILING_BOAT_4_NAME_3)
			saveTrueName(4);
		if (id == VarbitID.SAILING_BOAT_5_NAME_1 || id == VarbitID.SAILING_BOAT_5_NAME_2 || id == VarbitID.SAILING_BOAT_5_NAME_3)
			saveTrueName(5);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		int id = widgetLoaded.getGroupId();

		if (id == InterfaceID.SAILING_BOAT_SELECTION) {
			clientThread.invokeLater(() -> setBoatSelection());
		}

		if (id == InterfaceID.SAILING_BOAT_CARGOHOLD) {
			clientThread.invokeLater(() -> setCargoHold());
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired scriptPostFired)
	{
		int id = scriptPostFired.getScriptId();

		if (id == 8712) { // Sailing Panel Loaded. This also handles refreshes.
			setSailingPanel();
		}

		if (id == 8640) { // Ship expanded/collapsed
			setBoatSelection();
		}

		if (id == 8643) { // Cargo Panel Loaded.
			setSelectionCargo();
		}
	}

	private void setSelectionCargo() {
		Widget frame = client.getWidget(InterfaceID.SailingBoatSelection.CARGO_HOLD_CONTENT_FRAME);
		if (frame == null) return;
		for (Widget child : frame.getChildren()) {
			if (child.getType() != WidgetType.TEXT) continue;
			
			String trim = "'s Cargo Hold";
			String text = child.getText();
			if (text == null || !text.endsWith(trim)) 
				continue;

			String name = getReplacment(text.replace(trim, ""));
			child.setText(name + trim);
		}
	}

	@Provides
	ShipRenamerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShipRenamerConfig.class);
	}
	
	private String getReplacment(String match)
	{
		for (var i = 0; i < 5; i++) {
			if (trueNames.length < i - 1) 
				break;

			if (!match.equals(trueNames[i]))
				continue;

			return getName(i + 1);
		}

		return match;
	}

	private void saveTrueName(int boatnum)
	{
		int vbit1, vbit2, vbit3;

		switch (boatnum) {
			case 1:
				vbit1 = client.getVarbitValue(VarbitID.SAILING_BOAT_1_NAME_1);
				vbit2 = client.getVarbitValue(VarbitID.SAILING_BOAT_1_NAME_2);
				vbit3 = client.getVarbitValue(VarbitID.SAILING_BOAT_1_NAME_3);
				break;
			case 2:
				vbit1 = client.getVarbitValue(VarbitID.SAILING_BOAT_2_NAME_1);
				vbit2 = client.getVarbitValue(VarbitID.SAILING_BOAT_2_NAME_2);
				vbit3 = client.getVarbitValue(VarbitID.SAILING_BOAT_2_NAME_3);
				break;
			case 3:
				vbit1 = client.getVarbitValue(VarbitID.SAILING_BOAT_3_NAME_1);
				vbit2 = client.getVarbitValue(VarbitID.SAILING_BOAT_3_NAME_2);
				vbit3 = client.getVarbitValue(VarbitID.SAILING_BOAT_3_NAME_3);
				break;
			case 4:
				vbit1 = client.getVarbitValue(VarbitID.SAILING_BOAT_4_NAME_1);
				vbit2 = client.getVarbitValue(VarbitID.SAILING_BOAT_4_NAME_2);
				vbit3 = client.getVarbitValue(VarbitID.SAILING_BOAT_4_NAME_3);
				break;
			case 5:
				vbit1 = client.getVarbitValue(VarbitID.SAILING_BOAT_5_NAME_1);
				vbit2 = client.getVarbitValue(VarbitID.SAILING_BOAT_5_NAME_2);
				vbit3 = client.getVarbitValue(VarbitID.SAILING_BOAT_5_NAME_3);
				break;
			default:
				return;
		}

		log.info(String.format("[%s][%s][%s]", vbit1, vbit2, vbit3));

		int opt = vbit1 == 0 ? DBTableID.SailingBoatNameOptions.COL_DEFAULT : DBTableID.SailingBoatNameOptions.COL_OPTION;
		int num = vbit1 == 0 ? 0 : vbit1 - 1;
		Object[] res = client.getDBTableField(DBTableID.SailingBoatNameOptions.Row.SAILING_BOAT_NAME_PREFIX_OPTIONS, opt, 0);
		String prefix = res == null || res.length < num || res[num] == null ? "" : (String)res[num];

		opt = vbit2 == 0 ? DBTableID.SailingBoatNameOptions.COL_DEFAULT : DBTableID.SailingBoatNameOptions.COL_OPTION;
		num = vbit2 == 0 ? 0 : vbit2 - 1;
		res = client.getDBTableField(DBTableID.SailingBoatNameOptions.Row.SAILING_BOAT_NAME_DESCRIPTOR_OPTIONS, opt, 0);
		String middle = res == null || res.length < num || res[num] == null ? "" : (String)res[num];

		opt = vbit3 == 0 ? DBTableID.SailingBoatNameOptions.COL_DEFAULT : DBTableID.SailingBoatNameOptions.COL_OPTION;
		num = vbit3 == 0 ? 0 : vbit3 - 1;
		res = client.getDBTableField(DBTableID.SailingBoatNameOptions.Row.SAILING_BOAT_NAME_NOUN_OPTIONS, opt, 0);
		String last = res == null || res.length < num || res[num] == null ? "" : (String)res[num];

		String total = "";
		if (prefix != null && prefix.length() > 0) {
			total += prefix;
		}
		if (middle != null && middle.length() > 0) {
			if (total.length() > 0) total += " ";
			total += middle;
		}
		if (last != null && last.length() > 0) {
			if (total.length() > 0) total += " ";
			total += last;
		}

		if(trueNames.length < boatnum) {
			log.error("Could not update boat num: " + boatnum);
			return; // Safety check shouldn't happen.
		}

		log.info(String.format("Recording ship: [%s]", total));
		trueNames[boatnum - 1] = total;
	}

	private void setSailingPanel()
	{
		log.info("Setting panel");
		int boatId = client.getVarbitValue(VarbitID.SAILING_BOAT_SPAWNED);
		String boardedName = getName(boatId);

		Widget sailingPanel = client.getWidget(InterfaceID.SailingSidepanel.BOAT_NAME);
		if (sailingPanel == null) return;
		 
		Widget child = sailingPanel.getChild(0);
		if (child == null) return;
				
		if (child.getText().equals("Not on boat"))
			return;

		log.info(String.format("id: [%s], name: [%s], panel:[%s]", boatId, boardedName, child.getText())); 
		child.setText(boardedName);

	}

	private void replaceBoat(Widget widget)
	{
		Widget[] children = widget.getChildren();

		for (Widget child : children) {
			if(child == null) continue;

			int type = child.getType();
			if (type == WidgetType.TEXT) {
				String existing = child.getText();
				if (existing == null || existing.equals("")) 
					continue;

				for(int i = 0; i < trueNames.length; i++) {
					if(trueNames[i] == null || trueNames[i].equals(""))
						continue;

					if (existing.equals(trueNames[i])) {
						child.setText(getName(i + 1));
						break;
					}
					if (existing.endsWith(trueNames[i])) {
						child.setText(existing.substring(0, 4) + getName(i + 1));
						break;
					}
				}
			}

			String name = child.getName();
			if (name == null || !name.startsWith("<col=")) continue;
			String trueName = name.substring(12, name.length() - 6);
			child.setName(name.substring(0, 12) + getReplacment(trueName) + "</col>");
		}
	}

	private void setBoatSelection()
	{
		Widget recent = client.getWidget(InterfaceID.SailingBoatSelection.BOATS_CONTAINER_RECENT);
		if (recent != null) replaceBoat(recent);	

		Widget list = client.getWidget(InterfaceID.SailingBoatSelection.BOATS_CONTAINER);
		if (list != null) replaceBoat(list);
	}

	private void setCargoHold()
	{
		log.info("Setting panel");

		Widget frame = client.getWidget(InterfaceID.SailingBoatCargohold.FRAME);
		for (Widget child : frame.getChildren()) {
			if (child.getType() != 4) continue;
			String old = child.getText();
			
			if (!old.startsWith("Cargo Hold: "))
				continue;

			child.setText("Cargo Hold: " + getReplacment(old.substring(12)));
		}
	}

	private String getName(int boatNum)
	{
		switch (boatNum) {
			case 1:
				return config.ship1name();
			case 2:
				return config.ship2name();
			case 3:
				return config.ship3name();
			case 4:
				return config.ship4name();
			case 5:
				return config.ship5name();
			default:
				return "Boat";
		}
	}
}

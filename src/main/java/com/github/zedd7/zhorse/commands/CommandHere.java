package com.github.zedd7.zhorse.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandHere extends AbstractCommand {

	public CommandHere(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()
				&& parseArgument(ArgumentEnum.HORSE_NAME, ArgumentEnum.PLAYER_NAME)) {
			if (!idMode) {
				if (!targetMode) {
					horseID = zh.getDM().getPlayerFavoriteHorseID(p.getUniqueId(), true, null).toString();
					execute(p.getUniqueId(), horseID);
				}
				else {
					sendCommandUsage();
				}
			}
			else {
				execute(targetUUID, horseID);
			}
		}
	}

	private void execute(UUID ownerUUID, String horseID) {
		if (isRegistered(ownerUUID, horseID)) {
			horse = zh.getHM().getHorse(ownerUUID, Integer.parseInt(horseID), true);
			if (isHorseLoaded(true)) {
				execute();
			}
		}
	}

	private void execute() {
		if (isOwner(true) && isWorldCrossable(p.getWorld()) && isNotOnHorse() && !isHorseMounted() && !isHorseLeashed() && isHorseInRangeHere()) {
			Location playerLocation = getGroundedLocation(p.getLocation());
			horse = zh.getHM().teleportHorse(horse, playerLocation, true);
			if (horse != null) {
				zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_TELEPORTED) {{ setHorseName(horseName); }});
				zh.getCmdM().updateCommandHistory(s, command);
				zh.getEM().payCommand(p, command);
			}
			else {
				zh.getMM().sendMessage(s, ChatColor.RED + "It seems that horses cannot spawn here, please report this to your server's staff.");
			}
		}
	}

}
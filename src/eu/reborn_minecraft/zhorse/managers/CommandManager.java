package eu.reborn_minecraft.zhorse.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.commands.ZClaim;
import eu.reborn_minecraft.zhorse.commands.ZFree;
import eu.reborn_minecraft.zhorse.commands.ZGive;
import eu.reborn_minecraft.zhorse.commands.ZHeal;
import eu.reborn_minecraft.zhorse.commands.ZHelp;
import eu.reborn_minecraft.zhorse.commands.ZHere;
import eu.reborn_minecraft.zhorse.commands.ZInfo;
import eu.reborn_minecraft.zhorse.commands.ZKill;
import eu.reborn_minecraft.zhorse.commands.ZList;
import eu.reborn_minecraft.zhorse.commands.ZLock;
import eu.reborn_minecraft.zhorse.commands.ZProtect;
import eu.reborn_minecraft.zhorse.commands.ZReload;
import eu.reborn_minecraft.zhorse.commands.ZRename;
import eu.reborn_minecraft.zhorse.commands.ZSettings;
import eu.reborn_minecraft.zhorse.commands.ZShare;
import eu.reborn_minecraft.zhorse.commands.ZTame;
import eu.reborn_minecraft.zhorse.commands.ZTp;

public class CommandManager implements CommandExecutor {
	ZHorse zh;
	List<String> commandList;
	List<String> settingsCommandList;
	
	public CommandManager(ZHorse zh) {
		this.zh = zh;
		commandList = new ArrayList<String>();
		settingsCommandList = new ArrayList<String>();
		commandList.add(zh.getLM().claim);
		commandList.add(zh.getLM().free);
		commandList.add(zh.getLM().give);
		commandList.add(zh.getLM().heal);
		commandList.add(zh.getLM().help);
		commandList.add(zh.getLM().here);
	    commandList.add(zh.getLM().info);
		commandList.add(zh.getLM().kill);
		commandList.add(zh.getLM().list);
		commandList.add(zh.getLM().lock);
		commandList.add(zh.getLM().rename);
		commandList.add(zh.getLM().protect);
		commandList.add(zh.getLM().reload);
		commandList.add(zh.getLM().settings);
		commandList.add(zh.getLM().share);
		commandList.add(zh.getLM().tame);
		commandList.add(zh.getLM().tp);
		settingsCommandList.add(zh.getLM().language);
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (a.length == 0) {
			a = new String[1];
			a[0] = zh.getLM().help;
		}
		String command = a[0];
		if (command.equalsIgnoreCase(zh.getLM().claim)) {
			new ZClaim(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().free)) {
			new ZFree(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().give)) {
			new ZGive(zh, s ,a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().heal)) {
			new ZHeal(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().help)) {
			new ZHelp(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().here)) {
			new ZHere(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().info)) {
			new ZInfo(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().kill)) {
			new ZKill(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().list)) {
			new ZList(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().lock)) {
			new ZLock(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().rename)) {
			new ZRename(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().protect)) {
			new ZProtect(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().reload)) {
			new ZReload(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().settings)) {
			new ZSettings(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().share)) {
			new ZShare(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().tame)) {
			new ZTame(zh, s, a);
		}
		else if (command.equalsIgnoreCase(zh.getLM().tp)) {
			new ZTp(zh, s, a);
		}
		else {
			if (!zh.getCM().isConsoleMuted()) {
				zh.getMM().sendMessageValue(s, zh.getLM().unknownCommand, command);
			}
		}
		return true;
	}
	
	public List<String> getCommandList() {
		return commandList;
	}
	
	public List<String> getSettingsCommandList() {
		return settingsCommandList;
	}
	
}

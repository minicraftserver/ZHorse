package eu.reborn_minecraft.zhorse.managers;

import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.reborn_minecraft.zhorse.ZHorse;

public class EconomyManager {
	private ZHorse zh;
	private Economy econ;
	
	public EconomyManager(ZHorse zh) {
		this.zh = zh;
		this.econ = zh.getEcon();
	}
	
	public boolean canAffordCommand(Player p, String command) {
		if (p != null) {
			int amount = zh.getCM().getCommandCost(command);
			if (isCommandFree(p, command) || econ.has(zh.getServer().getOfflinePlayer(p.getUniqueId()), amount)) {
				return true;
			}
			zh.getMM().sendEconomyAmountValue((CommandSender)p, zh.getLM().notEnoughMoney, Integer.toString(amount), zh.getLM().getEconomyAnswer(zh.getUM().getLanguage(p.getUniqueId()), zh.getLM().currencySymbol, true));
		}
		return false;
	}
	
	public boolean isCommandFree(UUID playerUUID, String command) {
		if (isPlayerOnline(playerUUID)) {
			Player p = zh.getServer().getPlayer(playerUUID);
			return isCommandFree(p, command);
		}
		return false;
	}
	
	public boolean isCommandFree(Player p, String command) {
		int cost = zh.getCM().getCommandCost(command);
		if (cost == 0 || zh.getPerms().has(p, zh.getLM().zhPrefix + command + zh.getLM().freeSuffix)) {
			return true;
		}
		return false;
	}
	
	private boolean isPlayerOnline(UUID playerUUID) {
		for (Player p : zh.getServer().getOnlinePlayers()) {
			if (p.getUniqueId().equals(playerUUID)) {
				return true;
			}
		}
		return false;
	}
	
	public void payCommand(Player p, String command) {
		int amount = zh.getCM().getCommandCost(command);
		if (!isCommandFree(p, command)) {
			econ.withdrawPlayer(zh.getServer().getOfflinePlayer(p.getUniqueId()), amount);
			zh.getMM().sendEconomyAmountValue((CommandSender)p, zh.getLM().commandPaid, Integer.toString(amount), zh.getLM().getEconomyAnswer(zh.getUM().getLanguage(p.getUniqueId()), zh.getLM().currencySymbol, true));
		}
	}

}

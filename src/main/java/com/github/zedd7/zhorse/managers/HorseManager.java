package com.github.zedd7.zhorse.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.HorseInventoryRecord;
import com.github.zedd7.zhorse.database.HorseRecord;
import com.github.zedd7.zhorse.database.HorseStatsRecord;
import com.github.zedd7.zhorse.enums.HorseVariantEnum;
import com.github.zedd7.zhorse.utils.CallbackListener;
import com.github.zedd7.zhorse.utils.CallbackResponse;
import com.github.zedd7.zhorse.utils.ChunkLoad;
import com.github.zedd7.zhorse.utils.ChunkUnload;

public class HorseManager {

	public static final String DUPLICATE_METADATA = "zhorse_duplicate";

	private ZHorse zh;
	private Map<UUID, AbstractHorse> trackedHorses = new HashMap<>(); // Horses standing in a loaded chunk

	public HorseManager(ZHorse zh) {
		this.zh = zh;
	}

	public AbstractHorse getFavoriteHorse(UUID playerUUID) {
		return getHorse(playerUUID, zh.getDM().getPlayerFavoriteHorseID(playerUUID, true, null));
	}

	public AbstractHorse getHorse(UUID playerUUID, Integer horseID){
		return getHorse(playerUUID, horseID, false);
	}

	public AbstractHorse getHorse(UUID playerUUID, Integer horseID, boolean ignoreHorseTrackedCache) {
		AbstractHorse horse = null;
		if (playerUUID != null && horseID != null) {
			UUID horseUUID = zh.getDM().getHorseUUID(playerUUID, horseID, true, null);
			HorseRecord horseRecord = zh.getDM().getHorseRecord(horseUUID, true, null);
			horse = getHorse(horseRecord, ignoreHorseTrackedCache);
		}
		return horse;
	}

	public AbstractHorse getHorse(HorseRecord horseRecord, boolean ignoreHorseTrackedCache) {
		AbstractHorse horse = null;
		if (horseRecord != null) {
			UUID horseUUID = UUID.fromString(horseRecord.getUUID());
			if (isHorseTracked(horseUUID) && !ignoreHorseTrackedCache) {
				horse = getTrackedHorse(horseUUID);
			}
			else {
				Location location = new Location(
						zh.getServer().getWorld(horseRecord.getLocationWorld()),
						horseRecord.getLocationX(),
						horseRecord.getLocationY(),
						horseRecord.getLocationZ()
				);
				horse = getHorseFromLocation(horseUUID, location);
				if (horse != null) {
					boolean hasMoved = (
							horse.getLocation().getBlockX() != location.getBlockX() ||
									horse.getLocation().getBlockY() != location.getBlockY() ||
									horse.getLocation().getBlockZ() != location.getBlockZ()
					);
					if (hasMoved) {
						zh.getDM().updateHorseLocation(horseUUID, horse.getLocation(), false, false, null);
					}
				}
				else if (zh.getCM().shouldRespawnMissingHorse()) {
					HorseInventoryRecord inventoryRecord = zh.getDM().getHorseInventoryRecord(horseUUID, true, null);
					HorseStatsRecord statsRecord = zh.getDM().getHorseStatsRecord(horseUUID, true, null);
					if (inventoryRecord != null && statsRecord != null) { // Do not spawn if exact copy is impossible
						horse = spawnHorse(location, inventoryRecord, statsRecord, true, horseUUID, horseRecord.getId(), horseRecord.getName());
					}
				}
			}
		}
		return horse;
	}

	private AbstractHorse getHorseFromLocation(UUID horseUUID, Location location) {
		AbstractHorse horse = null;
		if (location != null) {
			if (loadChunk(location)) { // Ensure the chunk is loaded
				horse = getHorseInChunk(horseUUID, location.getChunk());
				if (horse == null) {
					List<Chunk> neighboringChunks = getChunksInRegion(location, 2, false);
					horse = getHorseInRegion(horseUUID, neighboringChunks);
				}
			}
		}
		return horse;
	}

	private List<Chunk> getChunksInRegion(Location center, int chunkRange, boolean includeCentralChunk) {
		World world = center.getWorld();
		Location NWCorner = new Location(world, center.getX() - 16 * chunkRange, 0, center.getZ() - 16 * chunkRange);
		Location SECorner = new Location(world, center.getX() + 16 * chunkRange, 0, center.getZ() + 16 * chunkRange);
		List<Chunk> chunkList = new ArrayList<Chunk>();
		for (int x = NWCorner.getBlockX(); x <= SECorner.getBlockX(); x += 16) {
			for (int z = NWCorner.getBlockZ(); z <= SECorner.getBlockZ(); z += 16) {
				if (center.getBlockX() != x || center.getBlockZ() != z || includeCentralChunk) {
					Location chunkLocation = new Location(world, x, 0, z);
					Chunk chunk = world.getChunkAt(chunkLocation); // w.getChunkAt(x, z) uses chunk coordinates (loc / 16)
					chunkList.add(chunk);
				}
			}
		}
		return chunkList;
	}

	private AbstractHorse getHorseInChunk(UUID horseUUID, Chunk chunk) {
		for (Entity entity : chunk.getEntities()) {
			if (entity.getUniqueId().equals(horseUUID)) {
				return (AbstractHorse) entity;
			}
		}
		return null;
	}

	private AbstractHorse getHorseInRegion(UUID horseUUID, List<Chunk> region) {
		AbstractHorse horse = null;
		for (Chunk chunk : region) {
			horse = getHorseInChunk(horseUUID, chunk);
			if (horse != null) {
				return horse;
			}
		}
		return horse;
	}
	public AbstractHorse getTrackedHorse(UUID horseUUID) {
		return trackedHorses.get(horseUUID);
	}

	public Map<UUID, AbstractHorse> getTrackedHorses() {
		return trackedHorses;
	}

	public boolean isHorseTracked(UUID horseUUID) {
		return trackedHorses.containsKey(horseUUID);
	}

	public synchronized void trackHorse(AbstractHorse horse) {
		UUID horseUUID = horse.getUniqueId();
		if (!isHorseTracked(horseUUID)) {
			trackedHorses.put(horseUUID, horse);
		}
	}

	public void trackHorses() {
		for (World world : zh.getServer().getWorlds()) {
			for (Chunk chunk : world.getLoadedChunks()) {
				new ChunkLoad(zh, chunk);
			}
		}
	}

	public synchronized void untrackHorse(UUID horseUUID) {
		trackedHorses.remove(horseUUID);
	}

	public synchronized void untrackHorses() {
		Iterator<Entry<UUID, AbstractHorse>> itr = trackedHorses.entrySet().iterator();
		while (itr.hasNext()) {
			AbstractHorse horse = itr.next().getValue();
			updateHorse(horse, true);
			itr.remove();
		}
	}

	public void updateHorse(AbstractHorse horse, boolean sync) {
		UUID horseUUID = horse.getUniqueId();
		Location horseLocation = horse.getLocation();
		HorseInventoryRecord inventoryRecord = new HorseInventoryRecord(horse);
		HorseStatsRecord statsRecord = new HorseStatsRecord(horse);
		zh.getDM().updateHorseLocation(horseUUID, horseLocation, true, sync, null);
		zh.getDM().updateHorseInventory(inventoryRecord, sync, null);
		zh.getDM().updateHorseStats(statsRecord, sync, null);
	}

	public AbstractHorse teleportHorse(AbstractHorse sourceHorse, Location destination, boolean sync) {
		if (zh.getCM().shouldUsePaperAPITeleportMethod()) {
			sourceHorse.teleport(destination);
			zh.getDM().updateHorseLocation(sourceHorse.getUniqueId(), destination, false, false, null);
			return sourceHorse;
		}
		else {
			synchronized(ChunkUnload.class) { // Synchronize with HorseManager::teleport to avoid updating outdated horse
				AbstractHorse copyHorse = (AbstractHorse) destination.getWorld().spawnEntity(destination, sourceHorse.getType());
				if (copyHorse != null) {
					UUID oldHorseUUID = sourceHorse.getUniqueId();
					UUID newHorseUUID = copyHorse.getUniqueId();
					zh.getDM().getOwnerUUID(oldHorseUUID, false, new CallbackListener<UUID>() {

						@Override
						public void callback(CallbackResponse<UUID> response) {
							if (response.getResult() != null) {
								UUID ownerUUID = response.getResult();
								HorseStatsRecord statsRecord = new HorseStatsRecord(sourceHorse);
								assignStats(copyHorse, statsRecord, ownerUUID);
								copyInventory(sourceHorse, copyHorse, statsRecord.isCarryingChest());
								removeLeash(sourceHorse);
								untrackHorse(sourceHorse.getUniqueId());
								trackHorse(copyHorse);
								removeHorse(sourceHorse);
								zh.getDM().updateHorseUUID(oldHorseUUID, newHorseUUID, false, new CallbackListener<Boolean>() {

									@Override
									public void callback(CallbackResponse<Boolean> response) {
										if (response.getResult()) {
											zh.getDM().updateHorseLocation(newHorseUUID, destination, false, false, null);
										}
									}

								});
							}
						}

					});
				}
				return copyHorse;
			}
		}
	}

	public void assignStats(AbstractHorse horse, HorseStatsRecord statsRecord, UUID ownerUUID) {
		// DB stats
		if (statsRecord.getAge() != null) horse.setAge(statsRecord.getAge());
		if (statsRecord.canBreed() != null) horse.setBreed(statsRecord.canBreed());
		if (statsRecord.canPickupItems() != null) horse.setCanPickupItems(statsRecord.canPickupItems());
		if (statsRecord.getCustomName() != null) horse.setCustomName(statsRecord.getCustomName());
		if (statsRecord.isCustomNameVisible() != null) horse.setCustomNameVisible(statsRecord.isCustomNameVisible());
		if (statsRecord.getDomestication() != null) horse.setDomestication(statsRecord.getDomestication());
		if (statsRecord.getFireTicks() != null) horse.setFireTicks(statsRecord.getFireTicks());
		if (statsRecord.isGlowing() != null) horse.setGlowing(statsRecord.isGlowing());
		if (statsRecord.getMaxHealth() != null) horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(statsRecord.getMaxHealth());
		if (statsRecord.getHealth() != null) horse.setHealth(statsRecord.getHealth()); // Define maxHealt before current health
		if (statsRecord.getJumpStrength() != null) horse.setJumpStrength(statsRecord.getJumpStrength());
		if (statsRecord.getNoDamageTicks() != null) horse.setNoDamageTicks(statsRecord.getNoDamageTicks());
		if (statsRecord.getRemainingAir() != null) horse.setRemainingAir(statsRecord.getRemainingAir());
		if (statsRecord.isTamed() != null) horse.setTamed(statsRecord.isTamed());
		if (statsRecord.getTicksLived() != null) horse.setTicksLived(statsRecord.getTicksLived());
		if (statsRecord.getSpeed() != null) horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(statsRecord.getSpeed());
		if (statsRecord.getType() != null) {
			if (statsRecord.getType().equals(EntityType.HORSE.name())) {
				if (statsRecord.getColor() != null) ((Horse) horse).setColor(Horse.Color.valueOf(statsRecord.getColor()));
				if (statsRecord.getStyle() != null) ((Horse) horse).setStyle(Horse.Style.valueOf(statsRecord.getStyle()));
			}
			else if (statsRecord.getType().equals(EntityType.LLAMA.name())) {
				if (statsRecord.getColor() != null) ((Llama) horse).setColor(Llama.Color.valueOf(statsRecord.getColor()));
				if (statsRecord.getStrength() != null) ((Llama) horse).setStrength(statsRecord.getStrength());
			}
		}

		// Entity stats
		if (statsRecord.isAdult() != null && statsRecord.isAdult()) horse.setAdult();
		if (statsRecord.isBaby() != null && statsRecord.isBaby()) horse.setBaby();
		if (ownerUUID != null) horse.setOwner(zh.getServer().getOfflinePlayer(ownerUUID));
	}

	private void assignInventory(AbstractHorse horse, HorseInventoryRecord inventoryRecord, boolean isCarryingChest) {
		if (horse instanceof ChestedHorse) {
			((ChestedHorse) horse).setCarryingChest(isCarryingChest);
		}
		horse.getInventory().setContents(inventoryRecord.getItems());
	}

	private void copyInventory(AbstractHorse sourceHorse, AbstractHorse copyHorse, boolean isCarryingChest) {
		if (copyHorse instanceof ChestedHorse) {
			((ChestedHorse) copyHorse).setCarryingChest(isCarryingChest);
		}
		copyHorse.getInventory().setContents(sourceHorse.getInventory().getContents());
	}

	private void removeLeash(AbstractHorse horse) {
		if (horse.isLeashed()) {
			Entity leashHolder = horse.getLeashHolder();
			if (leashHolder instanceof LeashHitch) {
				leashHolder.remove();
			}
			horse.setLeashHolder(null);
			ItemStack leash = new ItemStack(Material.LEAD);
			horse.getWorld().dropItem(horse.getLocation(), leash);
		}
	}

	public void removeHorse(AbstractHorse horse) {
		boolean chunkWasLoaded = loadChunk(horse.getLocation());
		Location horseLocation = horse.getLocation();
		horse.setMetadata(DUPLICATE_METADATA, new FixedMetadataValue(zh, horse.getUniqueId()));
		horse.remove(); // Entity::remove would fail if the chunk was not loaded
		if (!chunkWasLoaded) {
			unloadChunk(horseLocation);
		}
	}

	public AbstractHorse spawnHorse(Location location, HorseInventoryRecord inventoryRecord, HorseStatsRecord statsRecord, boolean claimedHorse, UUID oldHorseUUID, Integer horseID, String horseName) {
		EntityType type;
		if (statsRecord.getType() != null) {
			type = EntityType.valueOf(statsRecord.getType());
		}
		else {
			HorseVariantEnum[] variantArray = HorseVariantEnum.values();
			type = variantArray[new Random().nextInt(variantArray.length)].getEntityType();
		}
		boolean isCarryingChest = statsRecord.isCarryingChest() != null ? statsRecord.isCarryingChest() : false;

		boolean chunkWasLoaded = loadChunk(location);
		AbstractHorse horse = (AbstractHorse) location.getWorld().spawnEntity(location, type);
		if (horse != null) {
			UUID ownerUUID = null;
			if (claimedHorse) {
				ownerUUID = zh.getDM().getOwnerUUID(oldHorseUUID, true, null);
				zh.getDM().updateHorseUUID(oldHorseUUID, horse.getUniqueId(), false, new CallbackListener<Boolean>() {

					@Override
					public void callback(CallbackResponse<Boolean> response) {
						if (response.getResult()) {
							zh.getDM().updateHorseID(horse.getUniqueId(), horseID, false, null);
							zh.getDM().updateHorseName(horse.getUniqueId(), horseName, false, null);
							zh.getDM().updateHorseLocation(horse.getUniqueId(), location, true, false, null);
						}
					}

				});
			}
			assignStats(horse, statsRecord, ownerUUID);
			assignInventory(horse, inventoryRecord, isCarryingChest);
		}
		if (chunkWasLoaded) {
			if (horse != null && claimedHorse) {
				trackHorse(horse);
			}
		}
		else {
			unloadChunk(location);
		}
		return horse;
	}

	private boolean loadChunk(Location location) {
		World world = location.getWorld();
		int chunkX = location.getChunk().getX();
		int chunkZ = location.getChunk().getZ();

		if (!world.isChunkLoaded(chunkX, chunkZ)) {
			world.getChunkAt(chunkX, chunkZ);
			return false;
		}
		return true;
	}

	private void unloadChunk(Location location) {
		int chunkXCoordinate = toChunkCoordinate(location.getBlockX());
		int chunkZCoordinate = toChunkCoordinate(location.getBlockZ());
		location.getWorld().unloadChunkRequest(chunkXCoordinate, chunkZCoordinate);
	}

	private int toChunkCoordinate(int coordinate) {
		if (coordinate >= 0) {
			return coordinate / 16;
		}
		else {
			return (coordinate / 16) - 1;
		}
	}

}

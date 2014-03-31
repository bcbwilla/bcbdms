package net.electronexchange.plugins.bcbdms;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;


public class DeathListener implements Listener {
	private BcbDMS plugin;
	// These are the mobs which will produce a death message when killed.
	private List<String> hostileMobs = Arrays.asList("BLAZE","CAVE_SPIDER","CREEPER","ENDER_DRAGON","ENDERMAN",
											 "GHAST","GIANT","IRON_GOLEM","MAGMA_CUBE","PIG_ZOMBIE",
											 "SILVERFISH","SKELETON","SLIME","SPIDER","WITCH","WITHER",
											 "ZOMBIE");
	
	public DeathListener(BcbDMS plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMobDeath(EntityDeathEvent event) {
		LivingEntity mob = event.getEntity();
		String mobType = mob.getType().toString();

		if(mob.getLastDamageCause().getEventName().equalsIgnoreCase("EntityDamageByEntityEvent") && hostileMobs.contains(mobType)){

			// Get encounter information for the death message
			String mobName = mobType.replace("_", " ").toLowerCase();
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) mob.getLastDamageCause();
			
			String killerName = "";
			String weaponType = "";
			String displayName = "";
			if(mob.getKiller() instanceof Player){
				Player killer = (Player) mob.getKiller();
				killerName = killer.getDisplayName();
		
				if(killer.getItemInHand().getType() != Material.AIR) {
					ItemStack weapon = killer.getItemInHand();
					weaponType = weapon.getType().toString().replace("_", " ").toLowerCase();
					if(weapon.getItemMeta().hasDisplayName()) {
						displayName = " named " + weapon.getItemMeta().getDisplayName();
					}
				} else {
					weaponType = "fist";
				}
			}
			
			// If bow kill, try to get kill distance.
			String shotDistance = "";
			DamageCause damageCause = mob.getLastDamageCause().getCause();
			if(damageCause == DamageCause.PROJECTILE) {
				Entity arrow = damageEvent.getDamager();
				if(arrow.hasMetadata("shooter") && arrow.hasMetadata("shotFromX") && arrow.hasMetadata("shotFromY") && arrow.hasMetadata("shotFromZ")) {
					Location shotFrom = new Location(arrow.getWorld(), arrow.getMetadata("shotFromX").get(0).asDouble(), arrow.getMetadata("shotFromY").get(0).asDouble(), arrow.getMetadata("shotFromZ").get(0).asDouble());
					shotDistance = "(" + String.valueOf((int) mob.getLocation().distance(shotFrom)) + " blocks)";
					
					weaponType = "bow";
					killerName = arrow.getMetadata("shooter").get(0).asString();
				
				}	
			}
			
			// Generate death message
			String mobArticle = "a";
			if(StringUtil.startsWithVowel(mobName)) {
				mobArticle = "an";
			}
			
			String weaponArticle = "a";
			if(StringUtil.startsWithVowel(weaponType)) {
				weaponArticle = "an";
			}
			String dms = killerName + " killed " + mobArticle + " " + mobName + " with " + weaponArticle + " " + weaponType + displayName + " " + shotDistance;
			Bukkit.broadcastMessage(dms);
		}
	}
	
	// Keep track of where arrows are fired from so the shot distance can be calculated.
	@EventHandler
	public void onArrowShot(EntityShootBowEvent event) {
		LivingEntity entity = event.getEntity();
		if(entity instanceof Player) {
			Entity arrow = event.getProjectile();
			Location shotFrom = entity.getLocation();
			arrow.setMetadata("shotFromX", new FixedMetadataValue(this.plugin, shotFrom.getBlockX()));
			arrow.setMetadata("shotFromY", new FixedMetadataValue(this.plugin, shotFrom.getBlockY()));
			arrow.setMetadata("shotFromZ", new FixedMetadataValue(this.plugin, shotFrom.getBlockZ()));
			arrow.setMetadata("shooter", new FixedMetadataValue(this.plugin, ((Player)entity).getDisplayName()));
			
		}
	}
		
}

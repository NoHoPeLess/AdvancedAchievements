package com.hm.achievement.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.hm.achievement.AdvancedAchievements;

public class AchieveDeathListener implements Listener {

	private AdvancedAchievements plugin;

	public AchieveDeathListener(AdvancedAchievements plugin) {

		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {

		Player player = event.getEntity();

		if (player == null)
			return;

		// Update player's location when he dies not to yield incorrect results.
		if (plugin.getAchieveDistanceRunnable() != null)
			plugin.getAchieveDistanceRunnable().getPlayerLocations().put(player, player.getLocation());

		if (!player.hasPermission("achievement.count.deaths") || plugin.isInExludedWorld(player)
				|| plugin.getDisabledCategorySet().contains("Deaths"))
			return;

		int deaths = plugin.getPoolsManager().getPlayerDeathAmount(player) + 1;

		plugin.getPoolsManager().getDeathHashMap().put(player.getUniqueId().toString(), deaths);

		String configAchievement = "Deaths." + deaths;
		if (plugin.getReward().checkAchievement(configAchievement)) {

			plugin.getAchievementDisplay().displayAchievement(player, configAchievement);
			plugin.getDb().registerAchievement(player, plugin.getPluginConfig().getString(configAchievement + ".Name"),
					plugin.getPluginConfig().getString(configAchievement + ".Message"));
			plugin.getReward().checkConfig(player, configAchievement);
		}
	}
}

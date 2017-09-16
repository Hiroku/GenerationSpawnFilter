package com.hiroku.generationspawnfilter.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

import com.hiroku.generationspawnfilter.config.GenerationConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

public class SpawnListener
{
	@Listener
	public void onSpawn(SpawnEntityEvent event)
	{
		// I'm unsure on whether sending out Pokémon is considered a spawn, but I'm not risking it.
		if (event.getCause().containsType(Player.class))
			return;
		
		event.filterEntities(e ->
		{
			// I'm not going to filter out the non-Pokémon entities.
			if (!(e instanceof EntityPixelmon))
				return true;
			
			return GenerationConfig.canSpawn((EntityPixelmon)e);
		});
	}
}

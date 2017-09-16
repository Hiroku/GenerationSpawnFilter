package com.hiroku.generationspawnfilter;

import java.util.Arrays;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import com.hiroku.generationspawnfilter.command.GenerationSpawnFilterCmd;
import com.hiroku.generationspawnfilter.config.GenerationConfig;
import com.hiroku.generationspawnfilter.listener.SpawnListener;

@Plugin
(
		id = "generationspawnfilter", 
		name = "Generation Spawn Filter",
		version = "1.0.0",
		authors = "Hiroku", 
		description = "Allows specification of which generations of Pokémon may spawn in what world. ",
		dependencies = @Dependency(id = "pixelmon")
)
public class GenerationSpawnFilter
{
	@Listener
	public void onGameStart(GameStartedServerEvent event)
	{
		GenerationConfig.load();
		Sponge.getEventManager().registerListeners(this, new SpawnListener());
		Sponge.getCommandManager().register(Arrays.asList("generationspawnfilter", "gsf"), GenerationSpawnFilterCmd.spec());
	}
}

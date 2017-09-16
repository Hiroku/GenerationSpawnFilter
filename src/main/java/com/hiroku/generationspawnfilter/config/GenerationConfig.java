package com.hiroku.generationspawnfilter.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

public class GenerationConfig
{
	/** Path to GenerationSpawnFilter config. */
	private static final String PATH = "config/generationSpawnFilter.json";
	/** JSON serialization object. */
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	/** A mapping from world UUID to a list of generations represented by Integers that may spawn in said world. */
	private static HashMap<UUID, ArrayList<Integer>> worldGenerations = new HashMap<>();
	
	/** 
	 * This is just the JSON-friendly version. The static field is constructed on server started, and is used in references.
	 * The reason why I do this is because the world name is easier for the user than the UUID, but checking the UUID in the
	 * plugin's functionality is faster than a string comparison. It's unlikely the performance implications are particularly
	 * serious but this minimization won't hurt. 
	 */
	HashMap<String, ArrayList<Integer>> worldNameGenerations = new HashMap<>();
	
	/**
	 * Loads the worldGenerations mapping.
	 */
	public static void load()
	{
		worldGenerations.clear();
		
		File file = new File(PATH);
		
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
				GenerationConfig cfg = new GenerationConfig();
				
				// If the config hasn't been created yet, fill it in with the hocon default for all worlds.
				ArrayList<Integer> defaultGenerations = new ArrayList<>();
				if (PixelmonConfig.Gen1)
					defaultGenerations.add(1);
				if (PixelmonConfig.Gen2)
					defaultGenerations.add(2);
				if (PixelmonConfig.Gen3)
					defaultGenerations.add(3);
				if (PixelmonConfig.Gen4)
					defaultGenerations.add(4);
				if (PixelmonConfig.Gen5)
					defaultGenerations.add(5);
				if (PixelmonConfig.Gen6)
					defaultGenerations.add(6);
				// Everyone wants gen 7, let's be fair... Also we never had a config option for gen 7.
				defaultGenerations.add(7);
				
				for (World world : Sponge.getServer().getWorlds())
					cfg.worldNameGenerations.put(world.getName(), defaultGenerations);
				
				String json = GSON.toJson(cfg);
				PrintWriter pw = new PrintWriter(file);
				pw.print(json);
				pw.flush();
				pw.close();
				
				Sponge.getServer().getConsole().sendMessage(Text.of("[GenerationSpawnFilter]: Created config successfully."));
			}
			else
			{
				BufferedReader br = new BufferedReader(new FileReader(file));
				GenerationConfig cfg = GSON.fromJson(br, GenerationConfig.class);
				br.close();
				for (Entry<String, ArrayList<Integer>> entry : cfg.worldNameGenerations.entrySet())
				{
					Optional<World> optWorld = Sponge.getServer().getWorld(entry.getKey());
					if (optWorld.isPresent())
						worldGenerations.put(optWorld.get().getUniqueId(), entry.getValue());
					else
						Sponge.getServer().getConsole().sendMessage(Text.of("[GenerationSpawnFilter]: Warning, unknown world: " + entry.getKey() + "."));
				}
				Sponge.getServer().getConsole().sendMessage(Text.of("[GenerationSpawnFilter]: Loaded config successfully."));
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			Sponge.getServer().getConsole().sendMessage(Text.of("[GenerationSpawnFilter]: Failed to load config."));
		}
	}
	
	/**
	 * Can you even believe that we never had a function like this?
	 * 
	 * Gets the generation of the given Pokémon as an integer (maximum is 7)
	 */
	public static int getGeneration(EntityPixelmon p)
	{
		int pokedexNum = EntityPixelmon.getPokedexNumber(p.getPokemonName());
		int generation = 7;
		if (pokedexNum <= 151)
			generation = 1;
		else if (pokedexNum <= 251)
			generation = 2;
		else if (pokedexNum <= 386)
			generation = 3;
		else if (pokedexNum <= 493)
			generation = 4;
		else if (pokedexNum <= 649)
			generation = 5;
		else if (pokedexNum <= 721)
			generation = 6;
		return generation;
	}
	
	/**
	 * Determines whether a particular Pokémon may spawn, taking into account its world and generation.
	 * @param p = The Pokémon attempting to spawn.
	 * @return <code>true</code> if it may spawn, otherwise <code>false</code>.
	 */
	public static boolean canSpawn(EntityPixelmon p)
	{
		ArrayList<Integer> generations = worldGenerations.get(((Entity)p).getWorld().getUniqueId());
		
		// If this world makes no mention of which generations can spawn, allow all. This is
		// for the emergency case in which the config has failed to load worldGenerations,
		// or if the user of this plugin is, as we say in the industry, a bird-brain.
		if (generations == null)
			return true;
		
		return generations.contains(getGeneration(p));
	}
}

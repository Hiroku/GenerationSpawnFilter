package com.hiroku.generationspawnfilter.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.hiroku.generationspawnfilter.config.GenerationConfig;

public class GenerationSpawnFilterCmd implements CommandExecutor
{
	public static CommandSpec spec()
	{
		return CommandSpec.builder()
				.description(Text.of("Reloads the config"))
				.permission("generationspawnfilter.command")
				.arguments(GenericArguments.literal(Text.of("reload"), "reload"))
				.executor(new GenerationSpawnFilterCmd())
				.build();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
	{
		GenerationConfig.load();
		src.sendMessage(Text.of(TextColors.GRAY, "Reloaded config."));
		return CommandResult.success();
	}
}

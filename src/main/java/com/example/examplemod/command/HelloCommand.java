package com.example.examplemod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.EntityArgument;

import javax.swing.*;
import java.util.Collection;

public class HelloCommand {
  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(
      Commands.literal("halo")
      .then(
        Commands.argument("nama", StringArgumentType.word())
        .executes(context -> {

          String nama = StringArgumentType.getString(context, "nama");
          context.getSource().sendSuccess(
            () -> Component.literal("Halo " + nama + "!"),
            false
          );
          return 1;
        })
      )
    );

    dispatcher.register(
      Commands.literal("get")
      .then(
        Commands.argument("attribut", StringArgumentType.word())
        .suggests((context, builder) -> {
          String[] attributes = {
            "health",
            "hunger",
            "level"
          };

          for(String attribute : attributes){
            builder.suggest(attribute);
          }
          return builder.buildFuture();
        })
        .executes(context -> {
          ServerPlayer player = context.getSource().getPlayerOrException();
          String attribut = StringArgumentType.getString(context, "attribut");
          String textContent = switch (attribut){
            case "health" -> "HP: " + String.format("%.2f",player.getHealth());
            case "hunger" -> "Hunger: %d".formatted(player.getFoodData().getFoodLevel());
            case "level" -> "Level: %d".formatted(player.experienceLevel);
            default -> {
              context.getSource().sendFailure(
                Component.literal("Maaf, attribut tidak dikenali!")
              );
              yield null;
            }
          };

          if (textContent == null) return 0;

          context.getSource().sendSuccess(
            () -> Component.literal(textContent),
            false
          );
          return 1;
        })
      )
    );

    dispatcher.register(
      Commands.literal("heal")
      .executes(context -> {
        ServerPlayer player = context.getSource().getPlayerOrException();
        return heal(context, player, player.getHealth()*0.2f);
      })
      .then(
        Commands.argument("amount", FloatArgumentType.floatArg())
        .executes(context -> {
          ServerPlayer player = context.getSource().getPlayerOrException();
          float amount = FloatArgumentType.getFloat(context, "amount");
          return heal(context, player, amount);
        })
      )
      .then(
        Commands.argument("target", EntityArgument.players())
        .executes(context -> {
          Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");

          for(ServerPlayer player : players)
            heal(context, player, player.getHealth()*0.2f);

          return 1;
        })
        .then(
          Commands.argument("amount", FloatArgumentType.floatArg())
          .executes(context -> {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");
            float amount = FloatArgumentType.getFloat(context, "amount");

            for(ServerPlayer player : players)
              heal(context, player, amount);

            return 1;
          })
        )
      )
    );

    dispatcher.register(
      Commands.literal("feed")
        .then(
          Commands.argument("target", EntityArgument.players())
            .then(
              Commands.argument("amount", IntegerArgumentType.integer(1, 20))
                .executes(context -> {
                  Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");
                  int amount = IntegerArgumentType.getInteger(context, "amount");

                  for(ServerPlayer player : players)
                    feed(context, player, amount);

                  return 1;
                })
            )
        )
    );
  }

  private static int heal(CommandContext<CommandSourceStack> context, ServerPlayer player, float amount)
    throws CommandSyntaxException {
      float hpBefore = player.getHealth();

      player.setHealth(
        Math.min(hpBefore + amount, player.getMaxHealth())
      );

      context.getSource().sendSuccess(
        () -> Component.literal(
          player.getName().getString() + " telah dipulihkan sebanyak " + (player.getHealth()-hpBefore) + " HP!"
        ),
        false
      );

      return 1;
    }

  private static int feed(CommandContext<CommandSourceStack> context, ServerPlayer player, int amount)
    throws CommandSyntaxException {
      int hungerBefore = player.getFoodData().getFoodLevel();
      player.getFoodData().setFoodLevel(
        Math.min(player.getFoodData().getFoodLevel()+amount, 20)
      );

      context.getSource().sendSuccess(
        () -> Component.literal(
          player.getName().getString() + " telah dipulihkan sebanyak " + (player.getFoodData().getFoodLevel()-hungerBefore) + " hunger!"
        ),
        false
      );

      return 1;
    }
}
package com.example.examplemod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import javax.swing.*;

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
      Commands.literal("hai")
        .executes(context -> {
          context.getSource().sendSuccess(
            () -> Component.literal("Hai " + context.getSource().getTextName() + "!"),
            false
          );
          return 1;
        })
    );

    dispatcher.register(
      Commands.literal("bye")
        .executes(context -> {
          context.getSource().sendSuccess(
            () -> Component.literal("Sampai jumpa " + context.getSource().getTextName() + "!"),
            false
          );
          return 1;
        })
    );

    dispatcher.register(
      Commands.literal("halo2")
        .executes(context -> {
          context.getSource().sendSuccess(
            () -> Component.literal("Halo " + context.getSource().getTextName() + "!\nSelamat datang di mod pertamamu"),
            false
          );
          return 1;
        })
    );

    dispatcher.register(
      Commands.literal("get")
      .then(
        Commands.argument("attribut", StringArgumentType.word())
        .suggests((context, builder) -> {
          String[] attributes = {
            "health"
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
            case "health" -> "Darah: " + String.format("%.2f",player.getHealth());
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
        return heal(context, player.getHealth()*0.2f);
      })
      .then(
        Commands.argument("amount", FloatArgumentType.floatArg())
        .executes(context -> {
          ServerPlayer player = context.getSource().getPlayerOrException();
          float amount = FloatArgumentType.getFloat(context, "amount");
          return heal(context, amount);
        })
      )
    );
  }

  private static int heal(CommandContext<CommandSourceStack> context, float amount)
    throws CommandSyntaxException {

    ServerPlayer player = context.getSource().getPlayerOrException();

    float hpBefore = player.getHealth();

    player.setHealth(
      Math.min(hpBefore + amount, player.getMaxHealth())
    );

    context.getSource().sendSuccess(
      () -> Component.literal(
        "Halo " + context.getSource().getTextName()
          + "!\nDarahmu telah bertambah "
          + String.format("%.2f", player.getHealth() - hpBefore)
      ),
      false
    );

    return 1;
  }
}
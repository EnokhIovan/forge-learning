package com.example.examplemod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.level.ServerPlayer;

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
          player.setHealth(Math.min(player.getHealth()*1.2f, player.getMaxHealth()));

          context.getSource().sendSuccess(
            () -> Component.literal("Halo " + context.getSource().getTextName() + "!\nDarahmu telah bertambah " + String.format("%.2f",player. getHealth() - player.getHealth()/1.2)),
            false
          );
          return 1;
        })
    );
  }
}
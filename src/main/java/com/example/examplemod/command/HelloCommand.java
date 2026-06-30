package com.example.examplemod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.arguments.StringArgumentType;

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
  }
}
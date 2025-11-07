package com.craftvn.hitboxmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class Keybinds implements ClientModInitializer {

    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hitboxmod.toggle",
                GLFW.GLFW_KEY_P,
                "CraftVN Hitbox"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            while (toggleKey.wasPressed()) {
                HitboxMod.ENABLED = !HitboxMod.ENABLED;
                client.player.sendMessage(
                        Text.of("§aHitbox: " + (HitboxMod.ENABLED ? "§2BẬT" : "§cTẮT")),
                        true
                );
            }
        });
    }
}

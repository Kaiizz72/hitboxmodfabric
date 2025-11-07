package com.craftvn.hitboxmod;

import net.fabricmc.api.ModInitializer;

public class HitboxMod implements ModInitializer {

    public static boolean ENABLED = true;
    public static float SCALE = 1.05f; // +5%

    @Override
    public void onInitialize() {
        System.out.println("[HitboxMod] Loaded. Default ON.");
    }
}

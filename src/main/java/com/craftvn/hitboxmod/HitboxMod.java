package com.craftvn.hitboxmod;

import net.fabricmc.api.ModInitializer;

public class HitboxMod implements ModInitializer {

    // Trạng thái ON/OFF hitbox
    public static boolean ENABLED = true;

    // Tăng hitbox tí xíu (5%)
    public static float SCALE = 1.05f;

    @Override
    public void onInitialize() {
        System.out.println("[HitboxMod] Loaded. Default ON.");
    }
}

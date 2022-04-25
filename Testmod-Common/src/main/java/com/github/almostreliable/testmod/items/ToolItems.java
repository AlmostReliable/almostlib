package com.github.almostreliable.testmod.items;

import net.minecraft.world.item.Tier;

/**
 *
 */
public class ToolItems {
    public static class ShovelItem extends net.minecraft.world.item.ShovelItem {
        public ShovelItem(Tier tier, float atkDamage, float atkSpeed, Properties properties) {
            super(tier, atkDamage, atkSpeed, properties);
        }
    }

    public static class AxeItem extends net.minecraft.world.item.AxeItem {
        public AxeItem(Tier tier, float atkDamage, float atkSpeed, Properties properties) {
            super(tier, atkDamage, atkSpeed, properties);
        }
    }

    public static class HoeItem extends net.minecraft.world.item.HoeItem {
        public HoeItem(Tier tier, float atkDamage, float atkSpeed, Properties properties) {
            super(tier, (int) atkDamage, atkSpeed, properties);
        }
    }

    public static class PickaxeItem extends net.minecraft.world.item.PickaxeItem {
        public PickaxeItem(Tier tier, float atkDamage, float atkSpeed, Properties properties) {
            super(tier, (int) atkDamage, atkSpeed, properties);
        }
    }
}

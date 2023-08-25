package com.almostreliable.lib.forge;

import com.almostreliable.lib.AlmostLib;
import com.almostreliable.lib.BuildConfig;
import com.almostreliable.lib.component.ComponentHolder;
import com.almostreliable.lib.forge.component.ComponentCapabilityProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(BuildConfig.MOD_ID)
public class ForgeInitializer {

    public ForgeInitializer() {
        AlmostLib.initNetworkHandler();
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, ForgeInitializer::attachComponentApi);
    }

    private static void attachComponentApi(AttachCapabilitiesEvent<BlockEntity> event) {
        Object object = event.getObject();
        if (object instanceof ComponentHolder componentHolder) {
            event.addCapability(AlmostLib.getRL("components"), new ComponentCapabilityProvider(componentHolder));
        }
    }
}

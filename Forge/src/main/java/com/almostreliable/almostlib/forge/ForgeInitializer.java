package com.almostreliable.almostlib.forge;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.BuildConfig;
import com.almostreliable.almostlib.component.ComponentHolder;
import com.almostreliable.almostlib.forge.component.AlmostCapabilityProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(BuildConfig.MOD_ID)
public class ForgeInitializer {

    public ForgeInitializer() {
        AlmostLib.initNetworkHandler();
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, ForgeInitializer::attachBlockCapabilities);
    }

    private static void attachBlockCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        Object object = event.getObject();
        if (object instanceof ComponentHolder componentHolder) {
            event.addCapability(AlmostLib.getRL("components"), new AlmostCapabilityProvider(componentHolder));
        }
    }
}

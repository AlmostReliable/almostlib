package com.almostreliable.almostlib.item;

import com.almostreliable.almostlib.datagen.DataGenManager;
import com.almostreliable.almostlib.mixin.CreativeModeTabAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public abstract class AlmostCreativeTab extends CreativeModeTab {
    private final String translationKey;
    private final String defaultTranslation;

    public AlmostCreativeTab(ResourceLocation location, String defaultTranslation) {
        this(String.format("%s.%s", location.getNamespace(), location.getPath()), defaultTranslation);
    }

    public AlmostCreativeTab(String translationKey, String defaultTranslation) {
        super(expandTabsArray(), translationKey);
        this.translationKey = translationKey;
        this.defaultTranslation = defaultTranslation;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public String getDefaultTranslation() {
        return defaultTranslation;
    }

    public AlmostCreativeTab bindLang(DataGenManager dataGen) {
        dataGen.common().lang(lp -> lp.addLang("itemGroup." + getTranslationKey(), getDefaultTranslation()));
        return this;
    }

    private static int expandTabsArray() {
        int index = CreativeModeTab.TABS.length;
        CreativeModeTab[] newTabs = new CreativeModeTab[index + 1];
        System.arraycopy(CreativeModeTab.TABS, 0, newTabs, 0, CreativeModeTab.TABS.length);
        CreativeModeTabAccessor.setTabs(newTabs);
        return index;
    }
}

package com.almostreliable.almostlib;

import com.almostreliable.almostlib.component.ComponentLookup;

// TODO Move almostlib main platform service to here and maybe split it up. After component API is done and merged.
public class Services {

    public static final ComponentLookup COMPONENTS = AlmostLib.loadService(ComponentLookup.class);
}

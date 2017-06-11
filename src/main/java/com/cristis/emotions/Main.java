package com.cristis.emotions;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

/**
 * Created by chris on 6/11/2017.
 */
public class Main {
    public static void main(String[] args) {
        PlatformConfiguration   config  = PlatformConfiguration.getDefaultNoGui();

        config.addComponent("com.cristis.emotions.treasureisland.TreasureHunterA1BDI.class");
        Starter.createPlatform(config).get();
    }
}

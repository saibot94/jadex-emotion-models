package com.cristis.emotions.tutorial.goals;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

/**
 * Created by chris on 6/11/2017.
 */
public class MainGoals {
    public static void main(String[] args) {
        PlatformConfiguration config  = PlatformConfiguration.getDefaultNoGui();

        config.addComponent("com.cristis.emotions.tutorial.goals.TranslationBDI.class");
        Starter.createPlatform(config).get();
    }
}

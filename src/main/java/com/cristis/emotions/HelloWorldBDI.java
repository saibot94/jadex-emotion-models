package com.cristis.emotions;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;

/**
 * Created by chris on 6/11/2017.
 */
@Agent
@Description("The translation agent A1. <br> Empty agent that can be loaded and started.")
public class HelloWorldBDI {
    public HelloWorldBDI() {
        System.out.println("Hello, world!");
    }

}

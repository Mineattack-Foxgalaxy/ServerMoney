package io.github.skippyall.servermoney.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ServerMoneyConfig extends MidnightConfig {
    @Entry(category = "Money") public static String moneySymbol = "$";
    @Entry(category = "Money") public static double initialMoney = 100;
    @Entry(category = "Money") public static double moneyPerTick = 0;

    @Entry(category = "Shops") public static boolean protectShops = true;
}

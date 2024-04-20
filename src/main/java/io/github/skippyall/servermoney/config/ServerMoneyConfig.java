package io.github.skippyall.servermoney.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ServerMoneyConfig extends MidnightConfig {
    @Entry public static String moneySymbol = "$";
    @Entry public static boolean protectShops = true;
}

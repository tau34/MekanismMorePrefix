package io.github.tau34.mmp;

import mekanism.api.text.TextComponentUtil;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.network.chat.Component;

public enum MMPMeasurementUnit {
    QUECTO("Quecto", "q", 1e-30),
    RONTO("Ronto", "r", 1e-27),
    FEMTO("Femto", "f", 1e-15),
    PICO("Pico", "p", 1e-12),
    NANO("Nano", "n", 1e-9),
    MICRO("Micro", "µ", 1e-6),
    MILLI("Milli", "m", 1e-3),
    BASE("", "", 1),
    KILO("Kilo", "k", 1e3),
    MEGA("Mega", "M", 1e6),
    GIGA("Giga", "G", 1e9),
    TERA("Tera", "T", 1e12),
    PETA("Peta", "P", 1e15),
    EXA("Exa", "E", 1e18),
    ZETTA("Zetta", "Z", 1e21),
    YOTTA("Yotta", "Y", 1e24),
    RONNA("Ronna", "R", 1e27),
    QUETTA("Quetta", "Q", 1e30);

    private final String name;

    private final String symbol;

    public final double value;

    MMPMeasurementUnit(String name, String symbol, double value) {
        this.name = name;
        this.symbol = symbol;
        this.value = value;
    }

    public String getName(boolean isShort) {
        if (isShort) {
            return symbol;
        }
        return name;
    }

    public double process(double d) {
        return d / value;
    }

    public boolean aboveEqual(double d) {
        return d >= value;
    }

    public boolean below(double d) {
        return d < value;
    }

    public Component getRadiationDisplay(double value, UnitDisplayUtils.RadiationUnit unit, int decimalPlaces, boolean isShort, boolean spaceBetweenSymbol, boolean negative) {
        double rounded = UnitDisplayUtils.roundDecimals(negative, process(value), decimalPlaces);
        String name = getName(isShort);
        if (isShort) {
            if (spaceBetweenSymbol) {
                name = " " + name;
            }
            return TextComponentUtil.getString(rounded + name + unit.getSymbol());
        }
        return TextComponentUtil.build(rounded + " " + name, unit.getLabel());
    }

    public Component getTemperatureDisplay(double value, UnitDisplayUtils.TemperatureUnit unit, int decimalPlaces, boolean isShort, boolean spaceBetweenSymbol, boolean negative) {
        double rounded = UnitDisplayUtils.roundDecimals(negative, process(value), decimalPlaces);
        String name = getName(isShort);
        if (isShort) {
            if (spaceBetweenSymbol) {
                name = " " + name;
            }
            return TextComponentUtil.getString(rounded + name + unit.getSymbol());
        }
        return TextComponentUtil.build(rounded + " " + name, unit.getLabel());
    }
}
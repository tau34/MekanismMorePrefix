package io.github.tau34.mmp.mixin;

import io.github.tau34.mmp.MMPMeasurementUnit;
import mekanism.api.text.ILangEntry;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = UnitDisplayUtils.class, remap = false)
public abstract class UnitDisplayUtilsMixin {
    @Shadow
    public static double roundDecimals(double d, int decimalPlaces) {
        return 0;
    }

    @Inject(method = "getDisplay(DLmekanism/common/util/UnitDisplayUtils$TemperatureUnit;IZZZ)Lnet/minecraft/network/chat/Component;", at = @At("HEAD"), cancellable = true)
    private static void modifyTemperatureDisplay(double temp, UnitDisplayUtils.TemperatureUnit unit, int decimalPlaces, boolean shift, boolean isShort, boolean spaceBetweenSymbol, CallbackInfoReturnable<Component> cir) {
        cir.setReturnValue(getTemperatureDisplay(unit.convertFromK(temp, shift), unit, decimalPlaces, isShort, spaceBetweenSymbol));
    }

    @Inject(method = "getDisplayShort(DLmekanism/common/util/UnitDisplayUtils$RadiationUnit;I)Lnet/minecraft/network/chat/Component;", at = @At("HEAD"), cancellable = true)
    private static void modifyRadiationDisplay(double value, UnitDisplayUtils.RadiationUnit unit, int decimalPlaces, CallbackInfoReturnable<Component> cir) {
        cir.setReturnValue(getRadiationDisplay(value, unit, decimalPlaces, true, true));
    }

    @Inject(method = "roundDecimals(DI)D", at = @At("HEAD"), cancellable = true)
    private static void modifyRoundDecimals(double d, int decimalPlaces, CallbackInfoReturnable<Double> cir) {
        double multiplier = Math.pow(10, decimalPlaces);
        double multiplied = d * multiplier;
        if (multiplied > Long.MAX_VALUE || multiplied < Long.MIN_VALUE) {
            cir.setReturnValue(d);
        } else {
            cir.setReturnValue(Math.round(multiplied) / multiplier);
        }
    }

    @Unique
    private static Component getRadiationDisplay(double value, UnitDisplayUtils.RadiationUnit unit, int decimalPlaces, boolean isShort, boolean spaceBetweenSymbol) {
        if (value == 0) {
            if (isShort) {
                String spaceStr = spaceBetweenSymbol ? " " : "";
                return TextComponentUtil.getString(value + spaceStr + unit.getSymbol());
            }
            return TextComponentUtil.build(value, unit.getLabel());
        }
        boolean negative = value < 0;
        if (negative) {
            value = Math.abs(value);
        }
        MMPMeasurementUnit[] units = MMPMeasurementUnit.values();
        double min = units[0].value;
        double max = units[units.length - 1].value;
        if (value >= max * 1000 || value < min) {
            double signedValue = negative ? -value : value;
            String formatted = String.valueOf(roundDecimals(signedValue, decimalPlaces));
            return TextComponentUtil.getString(formatted + (spaceBetweenSymbol ? " " : "") + unit.getSymbol());
        }
        for (int i = 0; i < units.length; i++) {
            MMPMeasurementUnit lowerMeasure = units[i];
            if ((i == 0 && lowerMeasure.below(value)) ||
                    i + 1 >= units.length ||
                    (lowerMeasure.aboveEqual(value) && units[i + 1].below(value))) {
                return lowerMeasure.getRadiationDisplay(value, unit, decimalPlaces, isShort, spaceBetweenSymbol, negative);
            }
        }
        return units[units.length - 1].getRadiationDisplay(value, unit, decimalPlaces, isShort, spaceBetweenSymbol, negative);
    }

    @Unique
    private static Component getTemperatureDisplay(double value, UnitDisplayUtils.TemperatureUnit unit, int decimalPlaces, boolean isShort, boolean spaceBetweenSymbol) {
        if (value == 0) {
            if (isShort) {
                String spaceStr = spaceBetweenSymbol ? " " : "";
                return TextComponentUtil.getString(value + spaceStr + unit.getSymbol());
            }
            return TextComponentUtil.build(value, unit.getLabel());
        }
        boolean negative = value < 0;
        if (negative) {
            value = Math.abs(value);
        }
        MMPMeasurementUnit[] units = MMPMeasurementUnit.values();
        double min = units[0].value;
        double max = units[units.length - 1].value;
        if (value >= max * 1000 || value < min) {
            double signedValue = negative ? -value : value;
            String formatted = String.valueOf(roundDecimals(signedValue, decimalPlaces));
            return TextComponentUtil.getString(formatted + (spaceBetweenSymbol ? " " : "") + unit.getSymbol());
        }
        for (int i = 0; i < units.length; i++) {
            MMPMeasurementUnit lowerMeasure = units[i];
            if ((i == 0 && lowerMeasure.below(value)) ||
                    i + 1 >= units.length ||
                    (lowerMeasure.aboveEqual(value) && units[i + 1].below(value))) {
                return lowerMeasure.getTemperatureDisplay(value, unit, decimalPlaces, isShort, spaceBetweenSymbol, negative);
            }
        }
        return units[units.length - 1].getTemperatureDisplay(value, unit, decimalPlaces, isShort, spaceBetweenSymbol, negative);
    }
}

package carpetfixes.mixins.optimizations.random;

import carpetfixes.CFSettings;
import carpetfixes.helpers.XoroshiroCustomRandom;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(value = GeneratorOptions.class, priority = 1010)
public class GeneratorOptions_randomMixin {


    @Redirect(
            method = "*",
            require = 0,
            at = @At(
                    value = "NEW",
                    target = "java/util/Random"
            )
    )
    private static Random customRandomDefault() {
        return CFSettings.optimizedRandom ? new XoroshiroCustomRandom() : new Random();
    }
}

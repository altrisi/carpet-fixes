package carpetfixes.mixins.optimizations.random;

import carpetfixes.CFSettings;
import carpetfixes.helpers.XoroshiroCustomRandom;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(value = EnchantingTableBlockEntity.class, priority = 1010)
public class EnchantingTableBlockEntity_randomMixin {


    @Redirect(
            method = "<clinit>",
            require = 0,
            at = @At(
                    value = "NEW",
                    target = "java/util/Random"
            )
    )
    private static Random customRandom() {
        return CFSettings.optimizedRandom ? new XoroshiroCustomRandom() : new Random();
    }
}

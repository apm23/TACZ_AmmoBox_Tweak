package apm23.taczammoboxtweak.mixin.client;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.client.renderer.item.AmmoBoxStatueProperty;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = AmmoBoxStatueProperty.class, remap = false)
public abstract class AmmoBoxStatuePropertyMixin {
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void taczAmmoBoxTweak$diamondVisualForLevel2(ItemStack stack,
                                                         @Nullable ClientLevel level,
                                                         @Nullable LivingEntity entity,
                                                         int seed,
                                                         ItemDisplayContext displayContext,
                                                         CallbackInfoReturnable<Integer> cir) {
        if (!(stack.getItem() instanceof IAmmoBox box)) {
            return;
        }
        if (box.isCreative(stack) || box.isAllTypeCreative(stack) || box.getAmmoLevel(stack) < 3) {
            return;
        }
        boolean open = box.getAmmoId(stack).equals(DefaultAssets.EMPTY_AMMO_ID) || box.getAmmoCount(stack) <= 0;
        cir.setReturnValue(open ? 4 : 5);
    }
}

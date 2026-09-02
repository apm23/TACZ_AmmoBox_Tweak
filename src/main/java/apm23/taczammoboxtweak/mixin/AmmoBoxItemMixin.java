package apm23.taczammoboxtweak.mixin;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(value = AmmoBoxItem.class, remap = false)
public abstract class AmmoBoxItemMixin {
    private static final int IRON_CAPACITY = 1500;
    private static final int GOLD_CAPACITY = 4000;
    private static final int DIAMOND_CAPACITY = 6000;
    private static final int DIAMOND_LEVEL_2_CAPACITY = 10000;
    private static final int DIAMOND_LEVEL_2 = 3;

    private static int capacityFor(ItemStack stack, IAmmoBox box) {
        return switch (box.getAmmoLevel(stack)) {
            case 0 -> IRON_CAPACITY;
            case 1 -> GOLD_CAPACITY;
            case 2 -> DIAMOND_CAPACITY;
            default -> DIAMOND_LEVEL_2_CAPACITY;
        };
    }

    @Inject(method = "overrideStackedOnOther", at = @At("HEAD"), cancellable = true)
    private void taczAmmoBoxTweak$absoluteCapacityInsert(ItemStack ammoBox, Slot slot, ClickAction action,
                                                         Player player, CallbackInfoReturnable<Boolean> cir) {
        if (action != ClickAction.SECONDARY) {
            return;
        }
        if (!(ammoBox.getItem() instanceof IAmmoBox box)) {
            return;
        }

        ItemStack slotItem = slot.getItem();
        if (!(slotItem.getItem() instanceof IAmmo ammo)) {
            return;
        }

        if (box.isAllTypeCreative(ammoBox)) {
            cir.setReturnValue(false);
            return;
        }

        Identifier slotAmmoId = ammo.getAmmoId(slotItem);
        if (slotAmmoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
            cir.setReturnValue(false);
            return;
        }

        Identifier boxAmmoId = box.getAmmoId(ammoBox);
        if (boxAmmoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
            box.setAmmoId(ammoBox, slotAmmoId);
        } else if (!slotAmmoId.equals(boxAmmoId)) {
            cir.setReturnValue(false);
            return;
        }

        if (box.isCreative(ammoBox)) {
            box.setAmmoCount(ammoBox, Integer.MAX_VALUE);
            player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F,
                    0.8F + player.level().getRandom().nextFloat() * 0.4F);
            cir.setReturnValue(true);
            return;
        }

        int current = box.getAmmoCount(ammoBox);
        int capacity = capacityFor(ammoBox, box);
        int need = Math.max(0, capacity - current);
        if (need > 0) {
            ItemStack taken = slot.safeTake(slotItem.getCount(), need, player);
            box.setAmmoCount(ammoBox, Math.min(capacity, current + taken.getCount()));
        }

        player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F,
                0.8F + player.level().getRandom().nextFloat() * 0.4F);
        cir.setReturnValue(true);
    }

    @Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
    private void taczAmmoBoxTweak$absoluteBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!(stack.getItem() instanceof IAmmoBox box) || box.isCreative(stack) || box.isAllTypeCreative(stack)) {
            return;
        }
        int capacity = capacityFor(stack, box);
        double ratio = Math.max(0.0D, Math.min(1.0D, box.getAmmoCount(stack) / (double) capacity));
        cir.setReturnValue((int) Math.min(1 + 12 * ratio, 13));
    }

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void taczAmmoBoxTweak$level2Name(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        if (!(stack.getItem() instanceof IAmmoBox box)) {
            return;
        }
        if (!box.isCreative(stack) && !box.isAllTypeCreative(stack) && box.getAmmoLevel(stack) >= DIAMOND_LEVEL_2) {
            cir.setReturnValue(Component.translatable("item.tacz_ammobox_tweak.diamond_ammo_box_level_2")
                    .withStyle(style -> style.withColor(0x55FFFF)));
        }
    }

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void taczAmmoBoxTweak$capacityTooltip(ItemStack stack, Item.TooltipContext context,
                                                   TooltipDisplay display, Consumer<Component> adder,
                                                   TooltipFlag flag, CallbackInfo ci) {
        if (!(stack.getItem() instanceof IAmmoBox box) || box.isCreative(stack) || box.isAllTypeCreative(stack)) {
            return;
        }
        int current = Math.max(0, box.getAmmoCount(stack));
        int capacity = capacityFor(stack, box);
        adder.accept(Component.literal("Ammo: " + current + " / " + capacity)
                .withStyle(style -> style.withColor(0xAAAAAA)));
    }
}

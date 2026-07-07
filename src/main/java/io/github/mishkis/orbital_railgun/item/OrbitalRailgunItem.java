package io.github.mishkis.orbital_railgun.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class OrbitalRailgunItem extends Item implements GeoItem {
    private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);
    // The concrete renderer is client-only; it is injected from the client initializer.
    public final MutableObject<GeoRenderProvider> renderProviderHolder = new MutableObject<>();

    public OrbitalRailgunItem() {
        super(new Item.Settings().rarity(Rarity.EPIC).maxCount(1));
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 24000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getItemCooldownManager().isCoolingDown(this)) {
            return ItemUsage.consumeHeldItem(world, user, hand);
        }

        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    public void shoot(PlayerEntity user) {
        user.getItemCooldownManager().set(this, 2400);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(this.renderProviderHolder.getValue());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }
}

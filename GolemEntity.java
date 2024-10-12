package net.derek.tutorial.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class GolemEntity extends HostileEntity implements GeoEntity {

    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public GolemEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 45.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new MeleeAttackGoal(this, 2.4D, false));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.75f));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, MerchantEntity.class, true));
    }

    private <T extends GeoEntity> PlayState predicate(AnimationState<T> event) {
        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().then("animation.golem.walk", Animation.LoopType.LOOP));
        }
        return event.setAndContinue(RawAnimation.begin().then("animation.golem.idle_2", Animation.LoopType.LOOP));
    }

    private <T extends GeoEntity> PlayState attackPredicate(AnimationState<T> event) {
        if (this.handSwinging && event.getController().getAnimationState() == AnimationController.State.PAUSED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().then("animation.golem.attack", Animation.LoopType.PLAY_ONCE));
        }
        return PlayState.STOP;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 0, this::predicate));
        data.add(new AnimationController<>(this, "attackController", 0, this::attackPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public double getBoneResetTime() {
        return GeoEntity.super.getBoneResetTime();
    }

    @Override
    public boolean shouldPlayAnimsWhileGamePaused() {
        return GeoEntity.super.shouldPlayAnimsWhileGamePaused();
    }

    @Override
    public AnimatableInstanceCache animatableCacheOverride() {
        return GeoEntity.super.animatableCacheOverride();
    }
}

package net.derek.tutorial.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class GolemEntity extends HostileEntity implements GeoEntity {

    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private int attackCooldown = 0;

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
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new GolemAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, MerchantEntity.class, true));
    }

    private PlayState predicate(AnimationState<GolemEntity> event) {
        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().then("animation.golem.walk", Animation.LoopType.LOOP));
        }
        return event.setAndContinue(RawAnimation.begin().then("animation.golem.idle_2", Animation.LoopType.LOOP));
    }

    private PlayState attackPredicate(AnimationState<GolemEntity> event) {
        if (this.handSwinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            event.setAndContinue(RawAnimation.begin().then("animation.golem.attack", Animation.LoopType.PLAY_ONCE));
            this.handSwinging = false;
        }
        return PlayState.CONTINUE;
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
    public void tick() {
        super.tick();
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
    }

    private class GolemAttackGoal extends MeleeAttackGoal {
        public GolemAttackGoal(GolemEntity golem, double speed, boolean pauseWhenMobIdle) {
            super(golem, speed, pauseWhenMobIdle);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && GolemEntity.this.attackCooldown == 0;
        }

        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            double d = this.getSquaredMaxAttackDistance(target);
            if (squaredDistance <= d && this.getCooldown() <= 0) {
                this.resetCooldown();
                this.mob.swingHand(Hand.MAIN_HAND);
                this.mob.tryAttack(target);
                GolemEntity.this.attackCooldown = 20; // 1 segundo de cooldown
            }
        }
    }
}
package net.derek.tutorial.entity;

import net.derek.tutorial.TutorialMod;
import net.derek.tutorial.entity.custom.GolemEntity;
import net.derek.tutorial.entity.custom.Soldier_BeeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<GolemEntity> Golem = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(TutorialMod.MOD_ID, "golem"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, GolemEntity::new).
                    dimensions(EntityDimensions.fixed(2.0f, 2.0f)).build());

}

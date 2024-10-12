package net.derek.tutorial.entity.client;

import net.derek.tutorial.TutorialMod;
import net.derek.tutorial.entity.custom.GolemEntity;
import net.derek.tutorial.entity.custom.Soldier_BeeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GolemRenderer extends GeoEntityRenderer<GolemEntity> {

    public GolemRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new GolemModel());
    }

    @Override
    public Identifier getTextureLocation(GolemEntity animatable) {
        return new Identifier(TutorialMod.MOD_ID, "textures/entity/golem.png");
    }

    @Override
    public void render(GolemEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.isBaby()){
            poseStack.scale(0.4f,0.4f,0.4f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}

package me.dmillerw.remoteio.client.model.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import me.dmillerw.remoteio.block.BlockRemoteInterface;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by dmillerw
 */
public class MagicalBakedModel implements IBakedModel {

    private static final List<BakedQuad> EMPTY_LIST = Lists.newArrayList();

    private static BlockRendererDispatcher rendererDispatcher() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher();
    }

    private static IBlockState getMimickBlock(IBlockState oldState) {
        IExtendedBlockState state = (IExtendedBlockState) oldState;
        String mimickBlock = state.getValue(BlockRemoteInterface.MIMICK_BLOCK);
        if (mimickBlock == null || mimickBlock.isEmpty() || !mimickBlock.contains(":"))
            return null;

        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(mimickBlock));
        if (block == Blocks.AIR)
            return null;

        return block.getStateFromMeta(state.getValue(BlockRemoteInterface.MIMICK_VALUE));
    }

    private VertexFormat format;
    private final List<BakedQuad> CUBE_LIST = Lists.newArrayList();
    private TextureAtlasSprite remoteInterface;

    private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, Vec3d vertex, float u, float v, TextureAtlasSprite sprite) {
        for (int e = 0; e < format.getElementCount(); e++) {
            switch (format.getElement(e).getUsage()) {
                case POSITION:
                    builder.put(e, (float)vertex.xCoord, (float)vertex.yCoord, (float)vertex.zCoord, 1.0f);
                    break;
                case COLOR:
                    builder.put(e, 1.0f, 1.0f, 1.0f, 1.0f);
                    break;
                case UV:
                    if (format.getElement(e).getIndex() == 0) {
                        u = sprite.getInterpolatedU(u);
                        v = sprite.getInterpolatedV(v);
                        builder.put(e, u, v, 0f, 1f);
                        break;
                    }
                case NORMAL:
                    builder.put(e, (float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord, 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite) {
        return createQuad(v1, v2, v3, v4, sprite, 0);
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, int rotateUV) {
        Vec3d normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
        normal = normal.normalize().rotatePitch(180).rotateYaw(180);

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);

        switch (rotateUV) {
            case 3: {
                putVertex(builder, normal, v1, 16, 0, sprite);
                putVertex(builder, normal, v2, 16, 16, sprite);
                putVertex(builder, normal, v3, 0, 16, sprite);
                putVertex(builder, normal, v4, 0, 0, sprite);
                break;
            }
            case 2: {
                putVertex(builder, normal, v1, 16, 16, sprite);
                putVertex(builder, normal, v2, 0, 16, sprite);
                putVertex(builder, normal, v3, 0, 0, sprite);
                putVertex(builder, normal, v4, 16, 0, sprite);
                break;
            }
            case 1: {
                putVertex(builder, normal, v1, 0, 16, sprite);
                putVertex(builder, normal, v2, 0, 0, sprite);
                putVertex(builder, normal, v3, 16, 16, sprite);
                putVertex(builder, normal, v4, 16, 0, sprite);
                break;
            }
            default: {
                putVertex(builder, normal, v1, 0, 0, sprite);
                putVertex(builder, normal, v2, 0, 16, sprite);
                putVertex(builder, normal, v3, 16, 16, sprite);
                putVertex(builder, normal, v4, 16, 0, sprite);
                break;
            }
        }

        return builder.build();
    }

    private List<BakedQuad> getGenericCube() {
        if (CUBE_LIST.isEmpty()) {
            List<BakedQuad> list = Lists.newArrayList();

            list.add(createQuad(
                    new Vec3d(0, 0, 0),
                    new Vec3d(1, 0, 0),
                    new Vec3d(1, 0, 1),
                    new Vec3d(0, 0, 1),
                    remoteInterface
            ));

            list.add(createQuad(
                    new Vec3d(0, 1, 1),
                    new Vec3d(1, 1, 1),
                    new Vec3d(1, 1, 0),
                    new Vec3d(0, 1, 0),
                    remoteInterface
            ));

            list.add(createQuad(
                    new Vec3d(0, 1, 0),
                    new Vec3d(1, 1, 0),
                    new Vec3d(1, 0, 0),
                    new Vec3d(0, 0, 0),
                    remoteInterface
            ));

            list.add(createQuad(
                    new Vec3d(0, 0, 1),
                    new Vec3d(1, 0, 1),
                    new Vec3d(1, 1, 1),
                    new Vec3d(0, 1, 1),
                    remoteInterface
            ));

            list.add(createQuad(
                    new Vec3d(0, 0, 0),
                    new Vec3d(0, 0, 1),
                    new Vec3d(0, 1, 1),
                    new Vec3d(0, 1, 0),
                    remoteInterface
            ));

            list.add(createQuad(
                    new Vec3d(1, 1, 0),
                    new Vec3d(1, 1, 1),
                    new Vec3d(1, 0, 1),
                    new Vec3d(1, 0, 0),
                    remoteInterface
            ));

            CUBE_LIST.addAll(list);
        }
        return CUBE_LIST;
    }

    public MagicalBakedModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        this.format = format;
        this.remoteInterface = bakedTextureGetter.apply(new ResourceLocation("remoteio:blocks/blank"));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        IBlockState mimick = getMimickBlock(state);
        if (mimick == null)
            return getGenericCube();

        EnumBlockRenderType renderType = mimick.getRenderType();
        if (renderType == EnumBlockRenderType.LIQUID || renderType == EnumBlockRenderType.MODEL) {
            return rendererDispatcher().getModelForState(mimick).getQuads(getMimickBlock(state), side, rand);
        } else if (renderType == EnumBlockRenderType.ENTITYBLOCK_ANIMATED) {
            return EMPTY_LIST;
        } else {
            return getGenericCube();
        }
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return rendererDispatcher().getModelForState(Blocks.STONE.getDefaultState()).getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}

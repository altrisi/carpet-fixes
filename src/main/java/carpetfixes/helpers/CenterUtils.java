package carpetfixes.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CenterUtils {

    private static final double VELOCITY_AFFECTING_POS_Y_OFFSET = 0.5000001;

    //TODO: Change some of these to work with consumers, to minimize duplicate code

    public static void checkStepOnCollision(Entity entity) {
        Box box = entity.getBoundingBox();
        BlockPos blockPos = new BlockPos(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
        if (entity.world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, blockPos.getY(), k);
                    BlockState blockState = entity.world.getBlockState(mutable);
                    blockState.getBlock().onSteppedOn(entity.world, mutable, blockState, entity);
                }
            }
        }
    }

    public static void checkEntityLandOnCollision(Entity entity) {
        Box box = entity.getBoundingBox();
        BlockPos blockPos = new BlockPos(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
        if (entity.world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, blockPos.getY(), k);
                    BlockState blockState = entity.world.getBlockState(mutable);
                    if (!blockState.isAir()) blockState.getBlock().onEntityLand(entity.world, entity);
                }
            }
        }
    }

    public static void checkFallCollision(Entity entity, float fallDistance) {
        Box box = entity.getBoundingBox();
        BlockPos blockPos = new BlockPos(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
        if (entity.world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, blockPos.getY(), k);
                    BlockState blockState = entity.world.getBlockState(mutable);
                    blockState.getBlock().onLandedUpon(entity.world, blockState, mutable, entity, fallDistance);
                    if (!blockState.isAir() && !blockState.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS))
                        entity.emitGameEvent(GameEvent.HIT_GROUND);
                }
            }
        }
    }

    public static float checkJumpVelocityOnCollision(Box box, World world) {
        BlockPos blockPos = new BlockPos(box.minX + 0.001, box.minY, box.minZ + 0.001);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001, box.maxY, box.maxZ - 0.001);
        if (world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            float fastestBlock = 1.0F; //Highest value
            float slowestBlock = 1.0F; //Smallest value
            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, blockPos.getY(), k);
                    float topBlock = world.getBlockState(mutable).getBlock().getVelocityMultiplier();
                    if ((double)topBlock == 1.0D) {
                        mutable.set(i, box.minY-VELOCITY_AFFECTING_POS_Y_OFFSET, k);
                        float affectingBlock = world.getBlockState(mutable).getBlock().getVelocityMultiplier();
                        slowestBlock = Math.min(affectingBlock, slowestBlock);
                        fastestBlock = Math.max(affectingBlock, fastestBlock);
                    } else {
                        slowestBlock = Math.min(topBlock, slowestBlock);
                        fastestBlock = Math.max(topBlock, fastestBlock);
                    }
                }
            }
            return (double)slowestBlock < 1.0D ? slowestBlock : fastestBlock;
        }
        return 1.0F;
    }

    public static float checkVelocityOnCollision(Box box, World world) {
        BlockPos blockPos = new BlockPos(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
        if (world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            float fastestBlock = 1.0F; //Highest value
            float slowestBlock = 1.0F; //Smallest value
            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, blockPos.getY(), k);
                    BlockState blockState = world.getBlockState(mutable);
                    float topBlock = blockState.getBlock().getJumpVelocityMultiplier();
                    if ((double) topBlock == 1.0D &&
                            !blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.BUBBLE_COLUMN)) {
                        mutable.set(i, box.minY - VELOCITY_AFFECTING_POS_Y_OFFSET, k);
                        float affectingBlock = world.getBlockState(mutable).getBlock().getJumpVelocityMultiplier();
                        slowestBlock = Math.min(affectingBlock, slowestBlock);
                        fastestBlock = Math.max(affectingBlock, fastestBlock);
                    } else {
                        slowestBlock = Math.min(topBlock, slowestBlock);
                        fastestBlock = Math.max(topBlock, fastestBlock);
                    }
                }
            }
            return (double)slowestBlock < 1.0D ? slowestBlock : fastestBlock;
        }
        return 1.0F;
    }
}

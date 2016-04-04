package cz.genabitu.basestone;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public class BaseStoneWire extends Block {
    public final String name = "baseStoneWire";

    static enum EnumAttachPosition implements IStringSerializable {
        UP("up"),
        SIDE("side"),
        NONE("none");

        private final String name;

        private EnumAttachPosition(String name) {
            this.name = name;
        }

        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this.name;
        }
    }
    public static final PropertyEnum<BaseStoneWire.EnumAttachPosition> NORTH = PropertyEnum.create("north", BaseStoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BaseStoneWire.EnumAttachPosition> EAST = PropertyEnum.create("east", BaseStoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BaseStoneWire.EnumAttachPosition> SOUTH = PropertyEnum.create("south", BaseStoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BaseStoneWire.EnumAttachPosition> WEST = PropertyEnum.create("west", BaseStoneWire.EnumAttachPosition.class);
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 1024);
    protected static final AxisAlignedBB[] REDSTONE_WIRE_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D)};
    private boolean canProvidePower = true;
    private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();


    public BaseStoneWire()
    {
        super(Material.circuits);
        GameRegistry.registerBlock(this, name);
        setUnlocalizedName(name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, BaseStoneWire.EnumAttachPosition.NONE).withProperty(EAST, BaseStoneWire.EnumAttachPosition.NONE).withProperty(SOUTH, BaseStoneWire.EnumAttachPosition.NONE).withProperty(WEST, BaseStoneWire.EnumAttachPosition.NONE).withProperty(POWER, Integer.valueOf(0)));
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
        return REDSTONE_WIRE_AABB[getFacingBitList(state.getActualState(access, pos))];
    }

    private static int getFacingBitList(IBlockState state) {
        int i = 0;
        boolean flagN = state.getValue(NORTH) != BaseStoneWire.EnumAttachPosition.NONE;
        boolean flagE = state.getValue(EAST) != BaseStoneWire.EnumAttachPosition.NONE;
        boolean flagS = state.getValue(SOUTH) != BaseStoneWire.EnumAttachPosition.NONE;
        boolean flagW = state.getValue(WEST) != BaseStoneWire.EnumAttachPosition.NONE;
        if(flagN || (flagS && !flagN && !flagE && !flagW)) {
            i |= 1 << EnumFacing.NORTH.getHorizontalIndex(); // 2
        }

        if(flagE || flagW && !flagN && !flagE && !flagS) {
            i |= 1 << EnumFacing.EAST.getHorizontalIndex(); // 5
        }

        if(flagS || flagN && !flagE && !flagS && !flagW) {
            i |= 1 << EnumFacing.SOUTH.getHorizontalIndex(); // 3
        }

        if(flagW || flagE && !flagN && !flagS && !flagW) {
            i |= 1 << EnumFacing.WEST.getHorizontalIndex(); // 4
        }

        return i;
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess access, BlockPos pos) {
        state = state.withProperty(WEST, this.getAttachPosition(access, pos, EnumFacing.WEST));
        state = state.withProperty(EAST, this.getAttachPosition(access, pos, EnumFacing.EAST));
        state = state.withProperty(NORTH, this.getAttachPosition(access, pos, EnumFacing.NORTH));
        state = state.withProperty(SOUTH, this.getAttachPosition(access, pos, EnumFacing.SOUTH));
        return state;
    }

    private BaseStoneWire.EnumAttachPosition getAttachPosition(IBlockAccess access, BlockPos pos, EnumFacing facing) {
        BlockPos blockpos = pos.offset(facing);
        IBlockState iblockstate = access.getBlockState(pos.offset(facing));
        if(canConnectTo(access.getBlockState(blockpos), facing, access, blockpos) || !iblockstate.isNormalCube() && canConnectUpwardsTo(access, blockpos.down())) {
            return BaseStoneWire.EnumAttachPosition.SIDE;
        } else {
            IBlockState iblockstate1 = access.getBlockState(pos.up());
            if(!iblockstate1.isNormalCube()) {
                boolean flag = access.getBlockState(blockpos).isSideSolid(access, blockpos, EnumFacing.UP) || access.getBlockState(blockpos).getBlock() == Blocks.glowstone;
                if(flag && canConnectUpwardsTo(access, blockpos.up())) {
                    if(iblockstate.isBlockNormalCube()) {
                        return BaseStoneWire.EnumAttachPosition.UP;
                    }

                    return BaseStoneWire.EnumAttachPosition.SIDE;
                }
            }

            return BaseStoneWire.EnumAttachPosition.NONE;
        }
    }

    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        return NULL_AABB;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos.down()).isFullyOpaque() || world.getBlockState(pos.down()).getBlock() == Blocks.glowstone;
    }

    private IBlockState updateSurroundingBaseStone(World world, BlockPos pos, IBlockState state) {
        //state = this.calculateCurrentChanges(world, pos, pos, state);
        ArrayList list = Lists.newArrayList(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();
        Iterator i$ = list.iterator();

        while(i$.hasNext()) {
            BlockPos blockpos = (BlockPos)i$.next();
            world.notifyNeighborsOfStateChange(blockpos, this);
        }

        return state;
    }

    /*private IBlockState calculateCurrentChanges(World world, BlockPos pos, BlockPos pos1, IBlockState state) {
        IBlockState iblockstate = state;
        int i = ((Integer)state.getValue(POWER)).intValue();
        byte j = 0;
        int var14 = this.getMaxCurrentStrength(world, pos1, j);
        this.canProvidePower = false;
        int k = world.isBlockIndirectlyGettingPowered(pos);
        this.canProvidePower = true;
        if(k > 0 && k > var14 - 1) {
            var14 = k;
        }

        int l = 0;
        Iterator arr$ = Plane.HORIZONTAL.iterator();

        while(true) {
            while(arr$.hasNext()) {
                EnumFacing len$ = (EnumFacing)arr$.next();
                BlockPos i$ = pos.offset(len$);
                boolean enumfacing1 = i$.getX() != pos1.getX() || i$.getZ() != pos1.getZ();
                if(enumfacing1) {
                    l = this.getMaxCurrentStrength(world, i$, l);
                }

                if(world.getBlockState(i$).isNormalCube() && !world.getBlockState(pos.up()).isNormalCube()) {
                    if(enumfacing1 && pos.getY() >= pos1.getY()) {
                        l = this.getMaxCurrentStrength(world, i$.up(), l);
                    }
                } else if(!world.getBlockState(i$).isNormalCube() && enumfacing1 && pos.getY() <= pos1.getY()) {
                    l = this.getMaxCurrentStrength(world, i$.down(), l);
                }
            }

            if(l > var14) {
                var14 = l - 1;
            } else if(var14 > 0) {
                --var14;
            } else {
                var14 = 0;
            }

            if(k > var14 - 1) {
                var14 = k;
            }

            if(i != var14) {
                state = state.withProperty(POWER, Integer.valueOf(var14));
                if(world.getBlockState(pos) == iblockstate) {
                    world.setBlockState(pos, state, 2);
                }

                this.blocksNeedingUpdate.add(pos);
                EnumFacing[] var15 = EnumFacing.values();
                int var16 = var15.length;

                for(int var17 = 0; var17 < var16; ++var17) {
                    EnumFacing var18 = var15[var17];
                    this.blocksNeedingUpdate.add(pos.offset(var18));
                }
            }

            return state;
        }
    }*/

    private void notifyWireNeighborsOfStateChange(World world, BlockPos pos) {
        if(world.getBlockState(pos).getBlock() == this) {
            world.notifyNeighborsOfStateChange(pos, this);
            EnumFacing[] arr$ = EnumFacing.values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                EnumFacing enumfacing = arr$[i$];
                world.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }
        }
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if(!world.isRemote) {
            this.updateSurroundingBaseStone(world, pos, state);
            Iterator i$ = EnumFacing.Plane.VERTICAL.iterator();

            EnumFacing facing;
            while(i$.hasNext()) {
                facing = (EnumFacing)i$.next();
                world.notifyNeighborsOfStateChange(pos.offset(facing), this);
            }

            i$ = EnumFacing.Plane.HORIZONTAL.iterator();

            while(i$.hasNext()) {
                facing = (EnumFacing)i$.next();
                this.notifyWireNeighborsOfStateChange(world, pos.offset(facing));
            }

            i$ = EnumFacing.Plane.HORIZONTAL.iterator();

            while(i$.hasNext()) {
                facing = (EnumFacing)i$.next();
                BlockPos blockpos = pos.offset(facing);
                if(world.getBlockState(blockpos).isNormalCube()) {
                    this.notifyWireNeighborsOfStateChange(world, blockpos.up());
                } else {
                    this.notifyWireNeighborsOfStateChange(world, blockpos.down());
                }
            }
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        if(!world.isRemote) {
            EnumFacing[] i$ = EnumFacing.values();
            int facing = i$.length;

            for(int blockpos = 0; blockpos < facing; ++blockpos) {
                EnumFacing enumfacing = i$[blockpos];
                world.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }

            this.updateSurroundingBaseStone(world, pos, state);
            Iterator var8 = EnumFacing.Plane.HORIZONTAL.iterator();

            EnumFacing var9;
            while(var8.hasNext()) {
                var9 = (EnumFacing)var8.next();
                this.notifyWireNeighborsOfStateChange(world, pos.offset(var9));
            }

            var8 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(var8.hasNext()) {
                var9 = (EnumFacing)var8.next();
                BlockPos var10 = pos.offset(var9);
                if(world.getBlockState(var10).isNormalCube()) {
                    this.notifyWireNeighborsOfStateChange(world, var10.up());
                } else {
                    this.notifyWireNeighborsOfStateChange(world, var10.down());
                }
            }
        }
    }

    private int getMaxCurrentStrength(World world, BlockPos pos, int currentStrength) {
        if(world.getBlockState(pos).getBlock() != this) {
            return currentStrength;
        } else {
            int i = ((Integer)world.getBlockState(pos).getValue(POWER)).intValue();
            return i > currentStrength ?i: currentStrength;
        }
    }

    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
        if(!world.isRemote) {
            if(this.canPlaceBlockAt(world, pos)) {
                this.updateSurroundingBaseStone(world, pos, state);
            } else {
                this.dropBlockAsItem(world, pos, state, 0);
                world.setBlockToAir(pos);
            }
        }
    }

    public int getStrongPower(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing facing) {
        return !this.canProvidePower?0:state.getWeakPower(access, pos, facing);
    }

    public int getWeakPower(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing facing) {
        if(!this.canProvidePower) {
            return 0;
        } else {
            int i = ((Integer)state.getValue(POWER)).intValue();
            if(i == 0) {
                return 0;
            } else if(facing == EnumFacing.UP) {
                return i;
            } else {
                EnumSet enumset = EnumSet.noneOf(EnumFacing.class);
                Iterator i$ = EnumFacing.Plane.HORIZONTAL.iterator();

                while(i$.hasNext()) {
                    EnumFacing enumfacing = (EnumFacing)i$.next();
                    if(this.isProvidedStrongSignal(access, pos, enumfacing)) {
                        enumset.add(enumfacing);
                    }
                }

                if(facing.getAxis().isHorizontal() && enumset.isEmpty()) {
                    return i;
                } else if(enumset.contains(facing) && !enumset.contains(facing.rotateYCCW()) && !enumset.contains(facing.rotateY())) {
                    return i;
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean isProvidedStrongSignal(IBlockAccess access, BlockPos pos, EnumFacing facing) {
        BlockPos blockpos = pos.offset(facing);
        IBlockState iblockstate = access.getBlockState(blockpos);
        boolean flag = iblockstate.isNormalCube();
        boolean flag1 = access.getBlockState(pos.up()).isNormalCube();
        return false; // TODO
    }

    protected static boolean canConnectUpwardsTo(IBlockAccess access, BlockPos pos) {
        return canConnectTo(access.getBlockState(pos), (EnumFacing)null, access, pos);
    }

    protected static boolean canConnectTo(IBlockState state, EnumFacing facing, IBlockAccess access, BlockPos pos) {
        Block block = state.getBlock();
        if(block == BaseStone.baseStoneWire) {
            return true;
        }
        return false;
    }

    public boolean canProvidePower(IBlockState state) {
        return this.canProvidePower;
    }

    @SideOnly(Side.CLIENT)
    public static int colorMultiplier(int signal) { // TODO
        float f = (float) signal / 15.0F;
        float f1 = f * 0.6F + 0.4F;
        if(signal == 0) {
            f1 = 0.3F;
        }

        float f2 = f * f * 0.7F - 0.5F;
        float f3 = f * f * 0.6F - 0.7F;
        if(f2 < 0.0F) {
            f2 = 0.0F;
        }

        if(f3 < 0.0F) {
            f3 = 0.0F;
        }

        int i = MathHelper.clamp_int((int)(f1 * 255.0F), 0, 255);
        int j = MathHelper.clamp_int((int)(f2 * 255.0F), 0, 255);
        int k = MathHelper.clamp_int((int)(f3 * 255.0F), 0, 255);
        return -16777216 | i << 16 | j << 8 | k;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        int i = ((Integer)state.getValue(POWER)).intValue();
        if(i != 0) {
            double d0 = (double)pos.getX() + 0.5D + ((double)random.nextFloat() - 0.5D) * 0.2D;
            double d1 = (double)((float)pos.getY() + 0.0625F);
            double d2 = (double)pos.getZ() + 0.5D + ((double)random.nextFloat() - 0.5D) * 0.2D;
            float f = (float)i / 15.0F;
            float f1 = f * 0.6F + 0.4F;
            float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
            float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
            world.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, (double)f1, (double)f2, (double)f3, new int[0]);
        }   // TODO
    }

    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(BaseStone.baseStoneDust);
    }

    public IBlockState getStateFromMeta(int power) {
        return this.getDefaultState().withProperty(POWER, Integer.valueOf(power));
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public int getMetaFromState(IBlockState state) {
        return ((Integer)state.getValue(POWER)).intValue();
    }

    /*public IBlockState withRotation(IBlockState state, Rotation rotation) {
        switch(BaseStoneWire.SyntheticClass_1.$SwitchMap$net$minecraft$util$Rotation[rotation.ordinal()]) {
            case 1:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST));
            case 2:
                return state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH));
            case 3:
                return state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH));
            default:
                return state;
        }
    }

    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        switch(BaseStoneWire.SyntheticClass_1.$SwitchMap$net$minecraft$util$Mirror[mirror.ordinal()]) {
            case 1:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
            case 2:
                return state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
            default:
                return super.withMirror(state, mirror);
        }
    }*/

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{NORTH, EAST, SOUTH, WEST, POWER});
    }

    public Item getItemDropped(int metadata, Random random, int fortune)
    {
        return BaseStone.baseStoneDust;
    }
}

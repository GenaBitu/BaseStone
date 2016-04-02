package cz.genabitu.basestone;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemRedstone;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BaseStoneDust extends ItemRedstone {
    public final String name = "baseStoneDust";

    public BaseStoneDust()
    {
        GameRegistry.registerItem(this, name);
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.tabRedstone);
        setUnlocalizedName(name);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack tool, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float xFloat, float yFloat, float zFloat) {
        boolean canPlace = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
        BlockPos posPlace = canPlace ? pos : pos.offset(facing);
        if(player.canPlayerEdit(posPlace, facing, tool) && world.canBlockBePlaced(world.getBlockState(posPlace).getBlock(), posPlace, false, facing, (Entity)null, tool) && BaseStone.baseStoneWire.canPlaceBlockAt(world, posPlace)) {
            --tool.stackSize;
            world.setBlockState(posPlace, BaseStone.baseStoneWire.getDefaultState());
            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }
}

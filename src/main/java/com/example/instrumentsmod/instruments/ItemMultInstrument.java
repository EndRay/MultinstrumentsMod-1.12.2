package com.example.instrumentsmod.instruments;

import com.example.instrumentsmod.InstrumentsMod;
import com.example.instrumentsmod.utils.RelativePos;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.example.instrumentsmod.utils.PosUtils.getPlayerDirection;
import static com.example.instrumentsmod.utils.PosUtils.getRotatedShift;

public class ItemMultInstrument extends ItemPickaxe {
    public ItemMultInstrument(){
        super(ToolMaterial.DIAMOND);
        this.setRegistryName("multinstrument");
        this.setUnlocalizedName("multinstrument");
        this.setCreativeTab(InstrumentsMod.MULTINSTRUMENTS_TAB);
        this.setMaxDamage(1000);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {

        if(world.isRemote)
            return super.onItemRightClick(world, player, handIn);
        ItemStack itemInHand = player.getHeldItem(handIn);

        NBTTagCompound nbt;
        if(itemInHand.hasTagCompound()) {
            nbt = itemInHand.getTagCompound();
            assert nbt != null;
        }
        else nbt = new NBTTagCompound();

        BlockPos playerPos = player.getPosition().up();

        if(nbt.hasKey("blocks_cnt") &&
                nbt.hasKey("front_pos") &&
                nbt.hasKey("up_pos") &&
                nbt.hasKey("right_pos") &&
                nbt.hasKey("harvest_level")) {
            int blocks_cnt = nbt.getInteger("blocks_cnt");
            int[] front_pos = nbt.getIntArray("front_pos");
            int[] up_pos = nbt.getIntArray("up_pos");
            int[] right_pos = nbt.getIntArray("right_pos");
            int[] harvest_level = nbt.getIntArray("harvest_level");
            if(front_pos.length == blocks_cnt &&
                    up_pos.length == blocks_cnt &&
                    right_pos.length == blocks_cnt &&
                    harvest_level.length == blocks_cnt){
                InventoryPlayer inventory = player.inventory;
                int inventoryPointer = 9; //hotbar size
                for(int i = 0; i < blocks_cnt; ++i){
                    if(itemInHand.getItemDamage() > itemInHand.getMaxDamage())
                        break;
                    if(harvest_level[i] != -2)
                        continue;
                    while(inventoryPointer < inventory.getSizeInventory() && !(player.inventory.getStackInSlot(inventoryPointer).getItem() instanceof ItemBlock))
                        ++inventoryPointer;
                    if(inventoryPointer == inventory.getSizeInventory())
                        break;
                    RelativePos relativePos = new RelativePos(front_pos[i], up_pos[i], right_pos[i]);
                    BlockPos blockPos = playerPos.add(getRotatedShift(getPlayerDirection(player), relativePos));
                    IBlockState blockState = world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    if(block.isAir(blockState, world, blockPos)) {
                        ItemStack itemStack = player.inventory.getStackInSlot(inventoryPointer);
                        ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                        IBlockState createdBlockState = itemBlock.getBlock().getStateFromMeta(itemBlock.getMetadata(itemStack));
                        itemStack.shrink(1);
                        world.setBlockState(blockPos, createdBlockState);
                        itemInHand.damageItem(1, player);
                    }
                }
            }
        }
        return super.onItemRightClick(world, player, handIn);
    }
}

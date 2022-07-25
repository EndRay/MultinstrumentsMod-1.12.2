package com.example.instrumentsmod.instruments;

import com.example.instrumentsmod.utils.RelativePos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.example.instrumentsmod.utils.PosUtils.getPlayerDirection;
import static com.example.instrumentsmod.utils.PosUtils.getRotatedShift;

@Mod.EventBusSubscriber()
public class MultiBreakEvent {

    @SubscribeEvent
    public static void block_break(final BlockEvent.BreakEvent event)
    {
        World world = event.getWorld();
        EntityPlayer player = event.getPlayer();
        ItemStack itemInHand = player.getHeldItem(event.getPlayer().getActiveHand());
        System.out.println(itemInHand.getItemDamage());

        if(itemInHand.getItem() instanceof ItemMultInstrument)
        {
            NBTTagCompound nbt;
            if(itemInHand.hasTagCompound()) {
                nbt = itemInHand.getTagCompound();
                assert nbt != null;
            }
            else nbt = new NBTTagCompound();

            BlockPos brokenBlockPos = event.getPos();
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
                    for(int i = 0; i < blocks_cnt; ++i){
                        if(itemInHand.getItemDamage() >= itemInHand.getMaxDamage())
                            break;
                        if(harvest_level[i] == -2)
                            continue;
                        RelativePos relativePos = new RelativePos(front_pos[i], up_pos[i], right_pos[i]);
                        BlockPos blockPos = brokenBlockPos.add(getRotatedShift(getPlayerDirection(player), relativePos));
                        IBlockState blockState = world.getBlockState(blockPos);
                        int required_harvest = blockState.getBlock().getHarvestLevel(blockState);
                        if(required_harvest >= 0 && required_harvest <= harvest_level[i]) {
                            world.destroyBlock(blockPos, true);
                            itemInHand.damageItem(1, player);
                        }
                    }
                }
            }
        }
    }
}

package com.example.instrumentsmod.instruments;

import com.example.instrumentsmod.InstrumentsMod;
import com.example.instrumentsmod.ItemsRegistry;
import com.example.instrumentsmod.utils.RelativePos;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.*;

import static com.example.instrumentsmod.utils.PosUtils.getRotatedShift;
import static net.minecraft.init.Blocks.*;

public class BlockCoagulator extends BlockHorizontal {
    public BlockCoagulator() {
        super(Material.ROCK);
        this.setRegistryName("coagulator");
        this.setUnlocalizedName("coagulator");
        this.setCreativeTab(InstrumentsMod.MULTINSTRUMENTS_TAB);
    }

    static Vec3i getDirection(IBlockState state){
        switch (state.getValue(FACING).getOpposite()){
            case NORTH:
                return new Vec3i(0, 0, -1);

            case SOUTH:
                return new Vec3i(0, 0, 1);

            case EAST:
                return new Vec3i(1, 0, 0);

            case WEST:
                return new Vec3i(-1, 0, 0);
        }
        throw new RuntimeException();
    }

    static Vec3i getShift(IBlockState state, RelativePos pos){
        return getRotatedShift(getDirection(state), pos);
    }

    static class CoagulatedBlock{
        public RelativePos pos;
        public int harvest_level;
        public CoagulatedBlock(RelativePos pos, int harvest_level){
            this.pos = pos;
            this.harvest_level = harvest_level;
        }
    }

    private void searchForBlocks(World world, BlockPos initialBlockPos, IBlockState initialBlockState, List<CoagulatedBlock> coagulatedBlocks, Set<RelativePos> used, RelativePos relativePos){
        if(used.contains(relativePos))
            return;
        used.add(relativePos);
        boolean distribute = true;
        if(relativePos.front != 0 || relativePos.up != 0 || relativePos.right != 0) {
            boolean found = true;
            int harvest_level = -1;
            BlockPos pos = initialBlockPos.add(getShift(initialBlockState, relativePos));
            Block block = world.getBlockState(pos).getBlock();
            if (COBBLESTONE.equals(block)) {
                harvest_level = 0;
            } else if (IRON_BLOCK.equals(block)) {
                harvest_level = 1;
            } else if (DIAMOND_BLOCK.equals(block)) {
                harvest_level = 2;
            } else if (OBSIDIAN.equals(block)) {
                harvest_level = -2;
            } else if (GLASS.equals(block)) {
                found = false;
            } else {
                found = false;
                distribute = false;
            }
            if (found)
                coagulatedBlocks.add(new CoagulatedBlock(relativePos, harvest_level));
        }
        if(distribute){
            searchForBlocks(world, initialBlockPos, initialBlockState, coagulatedBlocks, used, relativePos.shifted(1, 0, 0));
            searchForBlocks(world, initialBlockPos, initialBlockState, coagulatedBlocks, used, relativePos.shifted(-1, 0, 0));
            searchForBlocks(world, initialBlockPos, initialBlockState, coagulatedBlocks, used, relativePos.shifted(0, 1, 0));
            searchForBlocks(world, initialBlockPos, initialBlockState, coagulatedBlocks, used, relativePos.shifted(0, -1, 0));
            searchForBlocks(world, initialBlockPos, initialBlockState, coagulatedBlocks, used, relativePos.shifted(0, 0, 1));
            searchForBlocks(world, initialBlockPos, initialBlockState, coagulatedBlocks, used, relativePos.shifted(0, 0, -1));
        }

    }

    private List<CoagulatedBlock> searchForBlocks(World world, BlockPos initialBlockPos, IBlockState initialBlockState) {
        List<CoagulatedBlock> coagulatedBlocks = new ArrayList<>();
        Set<RelativePos> used = new HashSet<>();
        searchForBlocks(world, initialBlockPos, initialBlockState, coagulatedBlocks, used, new RelativePos(0, 0, 0));
        return coagulatedBlocks;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if(world.isRemote)
            return false;
        List<CoagulatedBlock> coagulatedBlocks = searchForBlocks(world, pos, state);
        Collections.shuffle(coagulatedBlocks);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("blocks_cnt", coagulatedBlocks.size());
        int[] front_pos = new int[coagulatedBlocks.size()];
        int[] up_pos = new int[coagulatedBlocks.size()];
        int[] right_pos = new int[coagulatedBlocks.size()];
        int[] harvest_level = new int[coagulatedBlocks.size()];
        for (int i = 0; i < coagulatedBlocks.size(); ++i) {
            front_pos[i] = coagulatedBlocks.get(i).pos.front;
            up_pos[i] = coagulatedBlocks.get(i).pos.up;
            right_pos[i] = coagulatedBlocks.get(i).pos.right;
            harvest_level[i] = coagulatedBlocks.get(i).harvest_level;
        }
        nbt.setIntArray("front_pos", front_pos);
        nbt.setIntArray("up_pos", up_pos);
        nbt.setIntArray("right_pos", right_pos);
        nbt.setIntArray("harvest_level", harvest_level);


        world.setBlockToAir(pos);
        for (CoagulatedBlock coagulatedBlock : coagulatedBlocks)
            world.setBlockToAir(pos.add(getShift(state, coagulatedBlock.pos)));

        ItemStack itemStack = new ItemStack(ItemsRegistry.MULTINSTRUMENT, 1, 0);
        itemStack.setTagCompound(nbt);
        world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), itemStack));

        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));

    }

    @Override
    public int getMetaFromState(IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        return facing.getHorizontalIndex();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
}

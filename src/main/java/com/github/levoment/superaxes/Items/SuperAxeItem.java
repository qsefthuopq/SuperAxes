package com.github.levoment.superaxes.Items;

import com.github.levoment.superaxes.SuperAxesMaterialGenerator;
import com.github.levoment.superaxes.TreeChopper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SuperAxeItem extends AxeItem {

    // Constructor
    public SuperAxeItem(ToolMaterial material, Settings settings) {
        super(material, ((SuperAxesMaterialGenerator)material).getAxeAttackDamage(), ((SuperAxesMaterialGenerator) material).getAxeAttackSpeed(), settings);
    }


    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if(!world.isClient()) {
            // Check if the player is sneaking. If Sneaking, mine as normal
            if (miner.isSneaking()) return super.canMine(state, world, pos, miner);

            // Check if the tool is effective on the block and check for the LOGS tag
            if (state.getBlock().matches(BlockTags.LOGS)) {
                // Create an instance of TreeChopper
                TreeChopper treeChopper = new TreeChopper();
                // Create a new thread for chopping the tree
                new Thread(() -> {treeChopper.cutTree(state, world, pos, miner, miner.getMainHandStack());}).start();
            }
        }
        return true;
    }
}

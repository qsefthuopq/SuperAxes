package com.github.levoment.superaxes;

import com.github.levoment.superaxes.Items.ModItems;
import com.github.levoment.superaxes.Items.SuperAxeItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class TreeChopper {

    private Collection<BlockPos> listOfBlocksToBreak = new ArrayList<>();
    private boolean firstBlockBroken = false;
    private boolean axeBroken = false;
    private boolean itemInHand = true;


    public void cutTree(BlockState state, World world, BlockPos pos, PlayerEntity miner, ItemStack itemStack) {
        // Add our initial block to the queue
        listOfBlocksToBreak.add(pos);
        // Call the method to traverse through the tree
        traverseTree(world, pos);

        // Check if we don't want to harvest leaves
        if (!SuperAxesMod.harvestLeaves) {
            // Remove leaves from the list
            for(int i = 0; i < listOfBlocksToBreak.size(); i++) {
                listOfBlocksToBreak.removeIf(blockPos -> world.getBlockState(blockPos).getBlock().matches(BlockTags.LEAVES));
            }
        }

        // Iterate through the list containing the positions of the blocks we want to break
        for(BlockPos blockPos : listOfBlocksToBreak) {
            // Check if player can edit block
            if(world.canPlayerModifyAt(miner, blockPos)) {
                ModItems.mapOfIdentifiers.forEach((identifier, item) -> {
                    // Check if the item is in the player's main hand or if it broke while chopping the tree
                    if (Registry.ITEM.get(identifier).equals(miner.getMainHandStack().getItem()) || axeBroken) {
                        // Check if the item is about to break
                        if (miner.getMainHandStack().getMaxDamage() - miner.getMainHandStack().getDamage() == 1) axeBroken = true;
                        // Check if the superaxe hasn't broken
                        if (miner.getMainHandStack().getDamage() > 0) {
                            // Damage superaxe for each block that is broken
                            if (firstBlockBroken) miner.getMainHandStack().postMine(world, world.getBlockState(blockPos), blockPos, miner);
                            else this.firstBlockBroken = true;
                        }
                        // Harvest the block
                        world.breakBlock(blockPos, true, miner);
                    }
                });
            }
        }
    }

    public void traverseTree(World world, BlockPos pos) {
        // Stack that will contain the block positions that must be traversed
        Stack<BlockPos> stackOfBlockPositions = new Stack<>();
        // Put the root block to initiate the tree traversing
        stackOfBlockPositions.push(pos);
        // Last block position that will be used to continue searching for more blocks
        BlockPos lastBlockPosition = pos;
        // Assume first block is a log
        boolean CurrentIsLog = true;
        // The offset positions of the blocks to look for
        Stack<BlockPos> offsetsToCheck = new Stack<>();

        // TODO: limit breaking to configurable range to prevent deleafing a forest
        //
        // Loop while there are elements in the stack
        while (!stackOfBlockPositions.isEmpty()) {

            //This section could be moved to replace the while loop, but is seperated for readability
            // Get all block locations around current block in a 3x3 cube
            for (int y=-1;y<=1;y++){
                for (int x=-1;x<=1;x++){
                    for (int z=-1;z<=1;z++){
                        if (!(z==0 && x==0 && y==0)) {
                          // This is our current block, ignore it
                            BlockPos newPos = lastBlockPosition.add(x,y,z); 
                            if (newPos != null) {
                                offsetsToCheck.push(newPos);
                            }
                        }
                    }
                }
            }
            while (!offsetsToCheck.isEmpty()) {
                BlockPos checkPos = offsetsToCheck.pop();
                if (checkPos != null) {
                    if (
                            world.getBlockState(checkPos) != null && 
                            world.getBlockState(checkPos).getBlock() != null &&
                            ( 
                                // Dont process logs unless coming from another log, should alleviate most leaf->2nd tree issues
                                ( world.getBlockState(checkPos).getBlock().matches(BlockTags.LOGS) && CurrentIsLog)
                                || world.getBlockState(checkPos).getBlock().matches(BlockTags.LEAVES) 
                            ) 
                        ) {
                        if (!listOfBlocksToBreak.contains(checkPos)) {
                            listOfBlocksToBreak.add(checkPos);
                            stackOfBlockPositions.push(checkPos);
                        }
                    }
                }
            }

            // Make the next position to be used to search for other blocks be the one gotten from the top position in the stack
            // and remove that block from the list
            lastBlockPosition = stackOfBlockPositions.pop();
            CurrentIsLog = world.getBlockState(lastBlockPosition).getBlock().matches(BlockTags.LOGS);
        }
    }
}

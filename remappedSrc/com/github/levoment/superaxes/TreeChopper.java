package com.github.levoment.superaxes;

import com.github.levoment.superaxes.Items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class TreeChopper {

    private Collection<BlockPos> listOfBlocksToBreak = new ArrayList<>();
    private Collection<BlockPos> listOfLeavesToBreak = new ArrayList<>();
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
            // Break all the logs
            breakLogs(world, miner);
        } else {
            // Break the logs first
            breakLogs(world, miner);
            // Break leaves afterwards
            breakLeaves(world, miner);
        }
    }

    public void traverseTree(World world, BlockPos pos) {
        BlockPos originalBlockPos = pos;
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

        // Loop while there are elements in the stack
        while (!stackOfBlockPositions.isEmpty()) {

            // Get all block locations around current block in a 3x3 cube
            // y is for checking for blocks up and down
            for (int y = -1; y <= 1; y++) {
                // x is for checking for blocks East and West
                for (int x = -1; x <= 1; x++) {
                    // z is for checking for blocks North and South
                    for (int z = -1; z <= 1; z++) {
                        // This is our current block, ignore it
                        if (!(z == 0 && x == 0 && y == 0)) {
                            // Get the new block position
                            BlockPos newPos = lastBlockPosition.add(x, y, z);
                            if (newPos != null) {
                                // Check if we don't want to harvest leaves
                                if (!SuperAxesMod.harvestLeaves) {
                                    // Check if the blockstate is not null, the block is not null, and if the block is a log coming from another log, should alleviate most leaf->2nd tree issues
                                    if (world.getBlockState(newPos) != null && world.getBlockState(newPos).getBlock() != null && ((world.getBlockState(newPos).isIn(BlockTags.LOGS) && CurrentIsLog))) {
                                        // Check if the block has not been added to the list of blocks to break
                                        if (!listOfBlocksToBreak.contains(newPos)) {
                                            // Add the block to the list of blocks to break and also add it to the stack to continue searching for blocks to break
                                            listOfBlocksToBreak.add(newPos);
                                            stackOfBlockPositions.push(newPos);
                                        }
                                    }
                                } else {
                                    // We want to harvest leaves
                                    // Check if we are within the configured range
                                    if (Math.abs(newPos.getX() - originalBlockPos.getX()) < SuperAxesMod.range || Math.abs(newPos.getZ() - originalBlockPos.getZ()) < SuperAxesMod.range) {
                                        // Since we want to harvest leaves, let's add the leaves and the logs to search for
                                        // Check if the block is not blockstate is not null, the block is not null, and if the block is a log or leave coming from another log
                                        if (world.getBlockState(newPos) != null && world.getBlockState(newPos).getBlock() != null && ((world.getBlockState(newPos).isIn(BlockTags.LOGS) && CurrentIsLog) || world.getBlockState(newPos).isIn(BlockTags.LEAVES))) {
                                            // If it is a log add it to the list of logs to break
                                            if (world.getBlockState(newPos).isIn(BlockTags.LOGS)) {
                                                if (!listOfBlocksToBreak.contains(newPos)) {
                                                    listOfBlocksToBreak.add(newPos);
                                                    stackOfBlockPositions.push(newPos);
                                                }
                                            }
                                            // If it is a leave add it to the list of leaves to break
                                            else if (world.getBlockState(newPos).isIn(BlockTags.LEAVES)) {
                                                if (!listOfLeavesToBreak.contains(newPos)) {
                                                    listOfLeavesToBreak.add(newPos);
                                                    stackOfBlockPositions.push(newPos);
                                                }
                                            }
                                        }

                                    }
                                }

                            }
                        }
                    }
                }
            }

            // Make the next position to be used to search for other blocks be the one gotten from the top position in the stack
            // and remove that block from the list
            lastBlockPosition = stackOfBlockPositions.pop();
            CurrentIsLog = world.getBlockState(lastBlockPosition).isIn(BlockTags.LOGS);
        }
    }

    public void breakLogs(World world, PlayerEntity miner) {
        for (BlockPos blockPos : listOfBlocksToBreak) {
            // Check if player can edit block
            if (world.canPlayerModifyAt(miner, blockPos)) {
                ModItems.mapOfIdentifiers.forEach((identifier, item) -> {
                    // Check if the item is in the player's main hand or if it broke while chopping the tree
                    if (Registry.ITEM.get(identifier).equals(miner.getMainHandStack().getItem()) || axeBroken) {
                        // Check if the item is about to break
                        if (miner.getMainHandStack().getMaxDamage() - miner.getMainHandStack().getDamage() == 1)
                            axeBroken = true;
                        // Check if the superaxe hasn't broken
                        if (miner.getMainHandStack().getDamage() > 0) {
                            // Damage superaxe for each block that is broken
                            if (firstBlockBroken)
                                miner.getMainHandStack().postMine(world, world.getBlockState(blockPos), blockPos, miner);
                            else this.firstBlockBroken = true;
                        }
                        if (world.getBlockState(blockPos).isIn(BlockTags.LOGS)) {
                            // Harvest the block
                            world.breakBlock(blockPos, true, miner);
                        }
                    }
                });
            }
        }
    }

    public void breakLeaves(World world, PlayerEntity miner) {
        try {
            // Sleep to wait for the leaves to tick and be updated
            Thread.sleep(500);
            // Iterate through the list containing the positions of the blocks we want to break
            // Now break all of the leaves
            for (BlockPos blockPos : listOfLeavesToBreak) {
                // Check if player can edit block
                if (world.canPlayerModifyAt(miner, blockPos)) {
                    ModItems.mapOfIdentifiers.forEach((identifier, item) -> {
                        // Check if the item is in the player's main hand or if it broke while chopping the tree
                        if (Registry.ITEM.get(identifier).equals(miner.getMainHandStack().getItem()) || axeBroken) {
                            // Check if the item is about to break
                            if (miner.getMainHandStack().getMaxDamage() - miner.getMainHandStack().getDamage() == 1)
                                axeBroken = true;
                            // Check if the superaxe hasn't broken
                            if (miner.getMainHandStack().getDamage() > 0) {
                                // Damage superaxe for each block that is broken
                                if (firstBlockBroken)
                                    miner.getMainHandStack().postMine(world, world.getBlockState(blockPos), blockPos, miner);
                                else this.firstBlockBroken = true;
                            }
                            // Check if leaves are an instance of LeavesBlock
                            if (world.getBlockState(blockPos).isIn(BlockTags.LEAVES) && world.getBlockState(blockPos).getBlock() instanceof LeavesBlock) {
                                try {
                                    // Check if the leaves are a Distance of 7 from a log
                                    if (world.getBlockState(blockPos).get(LeavesBlock.DISTANCE) == 7) {
                                        // Harvest the block
                                        world.breakBlock(blockPos, true, miner);
                                    }
                                } catch (IllegalArgumentException illegalArgumentException) {
                                    // Don't do anything. Sometimes leaves are dropped before we harvest them
                                    // In those cases a block of air is left which doesn't have a DISTANCE property.
                                    // That will throw an exception. We can safely ignore the exception and harvest the actual leaves
                                }
                            }
                        }
                    });
                }
            }
        } catch (InterruptedException e) {
            // An exception happened when putting the thread to sleep
            // print the stack trace
            e.printStackTrace();
        }
    }
}

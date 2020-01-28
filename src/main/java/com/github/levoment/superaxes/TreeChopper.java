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

        // Remove leaves from the list
        for(int i = 0; i < listOfBlocksToBreak.size(); i++) {
            listOfBlocksToBreak.removeIf(blockPos -> world.getBlockState(blockPos).getBlock().matches(BlockTags.LEAVES));
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

        // The positions of the blocks to look for
        BlockPos positionToBreakNorth;
        BlockPos positionToBreakSouth;
        BlockPos positionToBreakEast;
        BlockPos positionToBreakWest;
        BlockPos positionToBreakUp;
        BlockPos positionToBreakDown;

        // Loop while there are elements in the stack
        while (!stackOfBlockPositions.isEmpty()) {
            // Set the blocks to look for
            positionToBreakNorth = lastBlockPosition.offset(Direction.NORTH);
            positionToBreakSouth = lastBlockPosition.offset(Direction.SOUTH);
            positionToBreakEast = lastBlockPosition.offset(Direction.EAST);
            positionToBreakWest = lastBlockPosition.offset(Direction.WEST);
            positionToBreakUp = lastBlockPosition.offset(Direction.UP);
            positionToBreakDown = lastBlockPosition.offset(Direction.DOWN);

            // Check that the block at the North of the lastBlockPosition is not null
            if (positionToBreakNorth != null) {
                // Check that there is an BlockState in the North offset, that there is a block in said position and that said block has tags: LOGS or LEAVES
                if (world.getBlockState(positionToBreakNorth) != null && world.getBlockState(positionToBreakNorth).getBlock() != null && (world.getBlockState(positionToBreakNorth).getBlock().matches(BlockTags.LOGS) || world.getBlockState(positionToBreakNorth).getBlock().matches(BlockTags.LEAVES))) {
                    // Check the the block has not already been added to the list
                    if (!listOfBlocksToBreak.contains(positionToBreakNorth)) {
                        // Add the block to the list
                        listOfBlocksToBreak.add(positionToBreakNorth);
                        // Add the block to the stack
                        stackOfBlockPositions.push(positionToBreakNorth);
                    }
                }
            }

            // Check that the block at the South of the lastBlockPosition is not null
            if (positionToBreakSouth != null) {
                // Check that there is an BlockState in the South offset, that there is a block in said position and that said block has tags: LOGS or LEAVES
                if (world.getBlockState(positionToBreakSouth) != null && world.getBlockState(positionToBreakSouth).getBlock() != null && (world.getBlockState(positionToBreakSouth).getBlock().matches(BlockTags.LOGS) || world.getBlockState(positionToBreakSouth).getBlock().matches(BlockTags.LEAVES))) {
                    // Check the the block has not already been added to the list
                    if (!listOfBlocksToBreak.contains(positionToBreakSouth)) {
                        // Add the block to the list
                        listOfBlocksToBreak.add(positionToBreakSouth);
                        // Add the block to the stack
                        stackOfBlockPositions.push(positionToBreakSouth);
                    }
                }
            }

            // Check that the block at the East of the lastBlockPosition is not null
            if (positionToBreakEast != null) {
                // Check that there is an BlockState in the East offset, that there is a block in said position and that said block has tags: LOGS or LEAVES
                if (world.getBlockState(positionToBreakEast) != null && world.getBlockState(positionToBreakEast).getBlock() != null && (world.getBlockState(positionToBreakEast).getBlock().matches(BlockTags.LOGS) || world.getBlockState(positionToBreakEast).getBlock().matches(BlockTags.LEAVES))) {
                    // Check the the block has not already been added to the list
                    if (!listOfBlocksToBreak.contains(positionToBreakEast)) {
                        // Add the block to the list
                        listOfBlocksToBreak.add(positionToBreakEast);
                        // Add the block to the stack
                        stackOfBlockPositions.push(positionToBreakEast);
                    }
                }
            }

            // Check that the block at the West of the lastBlockPosition is not null
            if (positionToBreakWest != null) {
                // Check that there is an BlockState in the West offset, that there is a block in said position and that said block has tags: LOGS or LEAVES
                if (world.getBlockState(positionToBreakWest) != null && world.getBlockState(positionToBreakWest).getBlock() != null && (world.getBlockState(positionToBreakWest).getBlock().matches(BlockTags.LOGS) || world.getBlockState(positionToBreakWest).getBlock().matches(BlockTags.LEAVES))) {
                    // Check the the block has not already been added to the list
                    if (!listOfBlocksToBreak.contains(positionToBreakWest)) {
                        // Add the block to the list
                        listOfBlocksToBreak.add(positionToBreakWest);
                        // Add the block to the stack
                        stackOfBlockPositions.push(positionToBreakWest);
                    }
                }
            }

            // Check that the block at the Up side of the lastBlockPosition is not null
            if (positionToBreakUp != null) {
                // Check that there is an BlockState in the Up offset, that there is a block in said position and that said block has tags: LOGS or LEAVES
                if (world.getBlockState(positionToBreakUp) != null && world.getBlockState(positionToBreakUp).getBlock() != null && (world.getBlockState(positionToBreakUp).getBlock().matches(BlockTags.LOGS) || world.getBlockState(positionToBreakUp).getBlock().matches(BlockTags.LEAVES))) {
                    // Check the the block has not already been added to the list
                    if (!listOfBlocksToBreak.contains(positionToBreakUp)) {
                        // Add the block to the list
                        listOfBlocksToBreak.add(positionToBreakUp);
                        // Add the block to the stack
                        stackOfBlockPositions.push(positionToBreakUp);
                    }
                }
            }

            // Check that the block at the Down side of the lastBlockPosition is not null
            if (positionToBreakDown != null) {
                // Check that there is an BlockState in the Down offset, that there is a block in said position and that said block has tags: LOGS or LEAVES
                if (world.getBlockState(positionToBreakDown) != null && world.getBlockState(positionToBreakDown).getBlock() != null && (world.getBlockState(positionToBreakDown).getBlock().matches(BlockTags.LOGS) || world.getBlockState(positionToBreakDown).getBlock().matches(BlockTags.LEAVES) )) {
                    // Check the the block has not already been added to the list
                    if (!listOfBlocksToBreak.contains(positionToBreakDown)) {
                        // Add the block to the list
                        listOfBlocksToBreak.add(positionToBreakDown);
                        // Add the block to the stack
                        stackOfBlockPositions.push(positionToBreakDown);
                    }
                }
            }

            // Make the next position to be used to search for other blocks be the one gotten from the top position in the stack
            lastBlockPosition = stackOfBlockPositions.peek();
            // Pop a BlockPos from the stack
            stackOfBlockPositions.pop();
        }
    }
}

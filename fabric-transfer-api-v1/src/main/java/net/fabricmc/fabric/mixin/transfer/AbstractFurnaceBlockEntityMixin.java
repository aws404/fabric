/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.transfer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.impl.transfer.TransferApiImpl;
import net.fabricmc.fabric.impl.transfer.item.SpecialLogicInventory;

/**
 * Defer cook time updates for furnaces, so that aborted transactions don't reset the cook time.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends LockableContainerBlockEntity implements SpecialLogicInventory {
	@Shadow
	protected DefaultedList<ItemStack> inventory;
	@Shadow
	int cookTime;
	@Shadow
	int cookTimeTotal;
	@Final
	@Shadow
	private RecipeType<? extends AbstractCookingRecipe> recipeType;

	protected AbstractFurnaceBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		throw new AssertionError();
	}

	@Inject(at = @At("HEAD"), method = "setStack", cancellable = true)
	public void setStackSuppressUpdate(int slot, ItemStack stack, CallbackInfo ci) {
		if (TransferApiImpl.SUPPRESS_SPECIAL_LOGIC.get() != null) {
			inventory.set(slot, stack);
			ci.cancel();
		}
	}

	@Override
	public void fabric_onFinalCommit(int slot, ItemStack oldStack, ItemStack newStack) {
		if (slot == 0) {
			ItemStack itemStack = oldStack;
			ItemStack stack = newStack;

			// Update cook time if needed. Code taken from AbstractFurnaceBlockEntity#setStack.
			boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areNbtEqual(stack, itemStack);

			if (!bl) {
				this.cookTimeTotal = getCookTime(this.world, this.recipeType, this);
				this.cookTime = 0;
			}
		}
	}

	@Shadow
	private static int getCookTime(World world, RecipeType<? extends AbstractCookingRecipe> recipeType, Inventory inventory) {
		throw new AssertionError();
	}
}

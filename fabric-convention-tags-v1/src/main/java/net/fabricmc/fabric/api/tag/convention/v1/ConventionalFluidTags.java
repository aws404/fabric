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

package net.fabricmc.fabric.api.tag.convention.v1;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.TagKey;

import net.fabricmc.fabric.impl.tag.convention.TagRegistration;

/**
 * See {@link net.minecraft.tag.FluidTags} for vanilla tags.
 * Note that addition to some vanilla tags implies having certain functionality.
 */
public class ConventionalFluidTags {
	public static final TagKey<Fluid> LAVA = register("lava");
	public static final TagKey<Fluid> WATER = register("water");
	public static final TagKey<Fluid> MILK = register("milk");
	public static final TagKey<Fluid> HONEY = register("honey");

	private static TagKey<Fluid> register(String tagID) {
		return TagRegistration.FLUID_TAG_REGISTRATION.registerCommon(tagID);
	}
}

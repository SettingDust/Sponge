/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.registry.type.item;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.AdditionalRegistration;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class EnchantmentRegistryModule implements AlternateCatalogRegistryModule<Enchantment> {

    @RegisterCatalog(Enchantments.class)
    private final Map<String, Enchantment> enchantmentMappings = new HashMap<>();

    @Override
    public Optional<Enchantment> getById(String id) {
        checkNotNull(id);
        if (!id.contains(":")) {
            id = "minecraft:" + id; // assume vanilla
        }
        return Optional.ofNullable(this.enchantmentMappings.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<Enchantment> getAll() {
        return ImmutableList.copyOf(this.enchantmentMappings.values());
    }

    @Override
    public Map<String, Enchantment> provideCatalogMap() {
        Map<String, Enchantment> newMap = new HashMap<>();
        for (Map.Entry<String, Enchantment> entry : this.enchantmentMappings.entrySet()) {
            newMap.put(entry.getKey().replace("minecraft:", ""), entry.getValue());
        }
        return newMap;
    }

    @Override
    public void registerDefaults() {
        for (ResourceLocation key: net.minecraft.enchantment.Enchantment.enchantmentRegistry.getKeys()) {
            this.enchantmentMappings.put(key.toString(), (Enchantment) net.minecraft.enchantment.Enchantment.enchantmentRegistry.getObject(key));
        }
    }

    @AdditionalRegistration
    public void registerAdditional() {
        for (ResourceLocation key: net.minecraft.enchantment.Enchantment.enchantmentRegistry.getKeys()) {
            net.minecraft.enchantment.Enchantment enchantment = net.minecraft.enchantment.Enchantment.enchantmentRegistry.getObject(key);
            if (enchantment == null) {
                continue;
            }
            if (!this.enchantmentMappings.containsValue((Enchantment) enchantment)) {
                final String name = enchantment.getName().replace("enchantment.", "");
                this.enchantmentMappings.put(name.toLowerCase(), (Enchantment) enchantment);
            }
        }

    }
}

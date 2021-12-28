/*******************************************************************************
 *     Copyright (C) 2018 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package io.github.wysohn.triggerreactor.bukkit.bridge;

import io.github.wysohn.triggerreactor.core.bridge.IItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BukkitItemStack implements IItemStack {
    private final ItemStack itemStack;

    public BukkitItemStack(ItemStack itemStack) {
        super();
        this.itemStack = itemStack;
    }

    @Override
    public IItemStack clone() {
        return new BukkitItemStack(itemStack.clone());
    }

    @Override
    public <T> T get() {
        return (T) itemStack;
    }

    /**
     * Returns the Material of the item
     */
    @Override
    public <T> T getType() {
        return (T) itemStack.getType();
    }

    @Override
    public boolean isAir() {
        return itemStack.getType() == Material.AIR;
    }

    @Override
    public boolean isInspectionTool() {
        return itemStack.getType() == INSPECTION_TOOL;
    }

    @Override
    public boolean isCutTool() {
        return itemStack.getType() == CUT_TOOL;
    }

    @Override
    public boolean isCopyTool() {
        return itemStack.getType() == COPY_TOOL;
    }

    public static final Material INSPECTION_TOOL = Material.BONE;
    public static final Material CUT_TOOL = Material.SHEARS;
    public static final Material COPY_TOOL = Material.PAPER;
}

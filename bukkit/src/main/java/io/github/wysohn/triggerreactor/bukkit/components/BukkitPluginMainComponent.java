/*
 *     Copyright (C) 2021 wysohn and contributors
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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.wysohn.triggerreactor.bukkit.components;

import dagger.Component;
import io.github.wysohn.triggerreactor.bukkit.modules.BukkitManagerModule;
import io.github.wysohn.triggerreactor.bukkit.modules.BukkitPluginMainModule;
import io.github.wysohn.triggerreactor.bukkit.modules.BukkitScriptEngineModule;
import io.github.wysohn.triggerreactor.core.main.PluginMainComponent;
import io.github.wysohn.triggerreactor.core.main.TriggerReactorMain;
import io.github.wysohn.triggerreactor.core.manager.Manager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Singleton;
import java.util.Set;

@Component(modules = {BukkitPluginMainModule.class, BukkitScriptEngineModule.class, BukkitManagerModule.class},
        dependencies = {PluginMainComponent.class})
@Singleton
public interface BukkitPluginMainComponent {
    void inject(JavaPlugin javaPlugin);

    Set<Manager> managers();

    TriggerReactorMain main();
}

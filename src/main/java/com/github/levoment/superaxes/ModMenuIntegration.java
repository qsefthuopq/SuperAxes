package com.github.levoment.superaxes;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

import java.util.Properties;
import java.util.function.Function;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public String getModId() {
        return SuperAxesMod.MODID;
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return screen -> {

            // Get the previous screen
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(MinecraftClient.getInstance().currentScreen)
                    .setTitle("configuration.superaxes.config");


            // Set category
            ConfigCategory general = builder.getOrCreateCategory("axebehaviour.superaxes.general");

            // Set an option for harvesting leaves
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            general.addEntry(entryBuilder.startBooleanToggle("option.superaxes.harvest_leaves", SuperAxesMod.harvestLeaves)
            .setDefaultValue(false)
            .setTooltip(new TranslatableText("option.superaxes.harvest_leaves.tooltip").asString())
            .setSaveConsumer(newValue -> SuperAxesMod.harvestLeaves = newValue)
            .build());

            // Save config
            builder.setSavingRunnable(() -> {
                // Create a Property
                Properties configProperties = new Properties();
                // Set the property
                configProperties.setProperty("harvestLeaves", String.valueOf(SuperAxesMod.harvestLeaves));
                // Save the properties
                SuperAxesMod.saveConfig(SuperAxesMod.configFile, configProperties);
            });


            return builder.build();
        };
    }
}

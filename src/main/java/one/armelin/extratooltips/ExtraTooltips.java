package one.armelin.extratooltips;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import one.armelin.extratooltips.providers.CaptureCrateProvider;
import one.armelin.extratooltips.providers.SwabTooltipProvider;
import org.herolias.tooltips.api.DynamicTooltipsApi;
import org.herolias.tooltips.api.DynamicTooltipsApiProvider;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class ExtraTooltips extends JavaPlugin {
    private static ExtraTooltips instance;

    public static final String NAME = "ExtraTooltips";
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static Configuration config;

    private static DynamicTooltipsApi tooltipsApi;

    private static HashMap<String, List<String>> modList = new HashMap<>();

    public ExtraTooltips(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        LOGGER.atInfo().log("%s v%s initializing...", NAME, this.getManifest().getVersion());

        LOGGER.atInfo().log(String.valueOf(getDataDirectory()));

        if(!Files.exists(getDataDirectory())){
            try {
                Files.createDirectories(getDataDirectory());
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log("Failed to create plugin data directory");
            }
        }
        config = Configuration.getConfig(getDataDirectory());

        LOGGER.atInfo().log("%s initialized successfully!", NAME);
    }

    @Override
    protected void setup() {
        super.setup();
        tooltipsApi = DynamicTooltipsApiProvider.get();
        if (tooltipsApi == null) {
            LOGGER.atSevere().log("DynamicTooltipsLib API not available! Is the library installed?");
            return;
        }
        getEventRegistry().register(LoadedAssetsEvent.class, Item.class, ExtraTooltips::onItemAssetLoad);
        tooltipsApi.registerProvider(new SwabTooltipProvider());
        tooltipsApi.registerProvider(new CaptureCrateProvider());
    }

    @Override
    protected void start() {
        super.start();
        LOGGER.atInfo().log("Registering mod labels for %d items", modList.size());
        LOGGER.atInfo().log("Silencing DynamicTooltipsLib logs during mod label registration to avoid log spam");
        Level originalLevel = HytaleLogger.get("DynamicTooltipsLib").getLevel();
        HytaleLogger.get("DynamicTooltipsLib").setLevel(Level.OFF);
        modList.forEach((id, mods) -> {
            if (!config.showOnlyLastMod){
                String modWord = mods.size() > 1 ? "Mods" : "Mod";
                String modName = String.join(" | ", mods);
                tooltipsApi.addGlobalLine(id, "<color is=\"#AAAAAA\"><b>" + modWord + ": </b> " + modName + "</color>");
            } else {
                String modName = mods.getLast();
                tooltipsApi.addGlobalLine(id, "<color is=\"#AAAAAA\"><b>Mod: </b> " + modName + "</color>");
            }
        });
        LOGGER.atInfo().log("Restoring DynamicTooltipsLib log level");
        HytaleLogger.get("DynamicTooltipsLib").setLevel(originalLevel);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    public static ExtraTooltips getInstance() {
        return instance;
    }

    private static void onItemAssetLoad(LoadedAssetsEvent<String, Item, DefaultAssetMap<String, Item>> event){
        event.getLoadedAssets().keySet().forEach(id -> {
            String assetPackId = event.getAssetMap().getAssetPack(id);
            if (assetPackId == null) return;
            AssetPack assetPack = AssetModule.get().getAssetPack(assetPackId);
            if  (assetPack == null) return;
            PluginManifest manifest = assetPack.getManifest();
            String modName = manifest.getName();
            if (modName.equals("Hytale") && !ExtraTooltips.config.showVanillaModLabel) {
                return; // Skip mod label for vanilla items if disabled in config
            }
            modList.computeIfAbsent(id, k -> new ArrayList<>()).add(modName);
        });
    }
}

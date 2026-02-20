package one.armelin.extratooltips;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import one.armelin.extratooltips.providers.CaptureCrateProvider;
import one.armelin.extratooltips.providers.ModTooltipProvider;
import one.armelin.extratooltips.providers.SwabTooltipProvider;
import org.herolias.tooltips.api.DynamicTooltipsApi;
import org.herolias.tooltips.api.DynamicTooltipsApiProvider;

import javax.annotation.Nonnull;
import java.nio.file.Files;

public class ExtraTooltips extends JavaPlugin {
    private static ExtraTooltips instance;

    public static final String NAME = "ExtraTooltips";
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static Configuration config;

    private DynamicTooltipsApi tooltipsApi;

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
        tooltipsApi.registerProvider(new ModTooltipProvider());
        tooltipsApi.registerProvider(new SwabTooltipProvider());
        tooltipsApi.registerProvider(new CaptureCrateProvider());
    }

    @Override
    protected void start() {
        super.start();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    public static ExtraTooltips getInstance() {
        return instance;
    }
}

package energyAddict;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@SpireInitializer
public class EnergyAddictMod implements PostInitializeSubscriber {

    public static final Logger logger = LogManager.getLogger(EnergyAddictMod.class.getName());

    //Mod metadata
    private static final String MOD_NAME = "Energy Addict Mod";
    private static final String AUTHOR = "JohnnyBazooka89";
    private static final String DESCRIPTION = "This mod guarantees that at least one relic in every boss chest will be an energy relic!";

    //Badge
    private static final String BADGE_IMG = "energyAddict/img/ModBadge.png";

    public static List<String> energyRelics = new ArrayList<>();

    public EnergyAddictMod() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        logger.info("======================== ENERGY ADDICT INIT ========================");

        new EnergyAddictMod();

        logger.info("====================================================================");
    }

    @Override
    public void receivePostInitialize() {
        // Mod badge
        Texture badgeTexture = ImageMaster.loadImage(BADGE_IMG);
        ModPanel settingsPanel = new ModPanel();
        BaseMod.registerModBadge(badgeTexture, MOD_NAME, AUTHOR, DESCRIPTION, settingsPanel);
    }

}

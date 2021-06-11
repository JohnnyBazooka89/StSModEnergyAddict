package energyAddict.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.RedCirclet;
import com.megacrit.cardcrawl.rewards.chests.BossChest;
import energyAddict.EnergyAddictMod;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(clz = BossChest.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {})
public class BossChestPatch {

    public static ExprEditor Instrument() {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("returnRandomRelic")) {
                    m.replace(
                            "if (i==0) { $_ = " + BossChestPatch.class.getName() + ".getFromEnergyRelicsList(); } else { $_ = $proceed($$); };"
                    );
                }
            }
        };
    }

    public static AbstractRelic getFromEnergyRelicsList() {
        EnergyAddictMod.energyRelics.retainAll(AbstractDungeon.bossRelicPool);
        if (EnergyAddictMod.energyRelics.size() == 0) {
            return new RedCirclet();
        }
        String randomEnergyRelicId = EnergyAddictMod.energyRelics.get(AbstractDungeon.relicRng.random(EnergyAddictMod.energyRelics.size() - 1));
        AbstractRelic result = RelicLibrary.getRelic(randomEnergyRelicId).makeCopy();
        AbstractDungeon.bossRelicPool.removeIf(relicId -> relicId.equals(randomEnergyRelicId));
        if (!result.canSpawn()) {
            return getFromEnergyRelicsList();
        }
        return result;
    }

}

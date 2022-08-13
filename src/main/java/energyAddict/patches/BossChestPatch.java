package energyAddict.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
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
                            AbstractRelic.class.getName() + " energyRelic = " + BossChestPatch.class.getName() + ".getFromEnergyRelicsList(1000); if (i==0 && energyRelic != null) { $_ = energyRelic; } else { $_ = $proceed($$); };"
                    );
                }
            }
        };
    }

    public static AbstractRelic getFromEnergyRelicsList(int tries) {
        EnergyAddictMod.energyRelics.retainAll(AbstractDungeon.bossRelicPool);
        if (EnergyAddictMod.energyRelics.size() == 0 || tries == 0) {
            return null;
        }
        String randomEnergyRelicId = EnergyAddictMod.energyRelics.get(AbstractDungeon.relicRng.random(EnergyAddictMod.energyRelics.size() - 1));
        AbstractRelic result = RelicLibrary.getRelic(randomEnergyRelicId).makeCopy();
        if (!result.canSpawn()) {
            return getFromEnergyRelicsList(tries - 1);
        }
        AbstractDungeon.bossRelicPool.removeIf(relicId -> relicId.equals(randomEnergyRelicId));
        return result;
    }

}

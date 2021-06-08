package energyAddict.patches;

import com.evacipated.cardcrawl.mod.stslib.relics.SuperRareRelic;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import energyAddict.EnergyAddictMod;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SpirePatch(clz = AbstractDungeon.class, method = "initializeRelicList", paramtypez = {})
public class InitializeRelicListPatch {

    public static List<String> HARDCODED_ENERGY_RELICS_LIST = Arrays.asList("SlaversCollar");
    public static List<String> HARDCODED_NON_ENERGY_RELICS_LIST = Arrays.asList("halation:Risk", "Honey Jar", "Replay:Drink Me");

    public static void Postfix(AbstractDungeon abstractDungeon) {
        ArrayList<String> bossRelicPool = AbstractDungeon.bossRelicPool;

        EnergyAddictMod.energyRelics = new ArrayList<>();

        for (String relicId : bossRelicPool) {
            AbstractRelic relic = RelicLibrary.getRelic(relicId).makeCopy();
            if (HARDCODED_NON_ENERGY_RELICS_LIST.contains(relicId) || (relic instanceof SuperRareRelic)) {
                continue;
            }
            if (HARDCODED_ENERGY_RELICS_LIST.contains(relicId) || isEnergyRelicBasedOnEnergyManagerFieldAccess(relicId, relic)) {
                EnergyAddictMod.energyRelics.add(relicId);
            }
        }

    }

    private static boolean isEnergyRelicBasedOnEnergyManagerFieldAccess(String relicId, AbstractRelic relic) {
        try {
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.getCtClass(relic.getClass().getName());
            CtMethod onEquip = ctClass.getDeclaredMethod("onEquip");
            CtMethod onUnequip = ctClass.getDeclaredMethod("onUnequip");
            AtomicBoolean onEquipAccess = new AtomicBoolean(false);
            AtomicBoolean onUnequipAccess = new AtomicBoolean(false);
            checkEnergyManagerFieldAccess(onEquip, onEquipAccess);
            checkEnergyManagerFieldAccess(onUnequip, onUnequipAccess);
            if (onEquipAccess.get() && onUnequipAccess.get()) {
                return true;
            }
        } catch (NotFoundException | CannotCompileException e) {
            //Ignore
        }
        return false;
    }

    private static void checkEnergyManagerFieldAccess(CtMethod onEquip, AtomicBoolean onEquipCheck) throws CannotCompileException {
        onEquip.instrument(
                new ExprEditor() {
                    @Override
                    public void edit(FieldAccess fieldAccess) {
                        try {
                            if (fieldAccess.getFieldName().equals("energyMaster") && fieldAccess.getField().getDeclaringClass().getName().equals("com.megacrit.cardcrawl.core.EnergyManager")) {
                                onEquipCheck.set(true);
                            }
                        } catch (NotFoundException e) {
                            //Ignore
                        }
                    }
                }
        );
    }

}
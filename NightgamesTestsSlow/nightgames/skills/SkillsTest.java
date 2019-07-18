package nightgames.skills;

import com.google.gson.JsonParseException;
import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.custom.CustomNPC;
import nightgames.characters.custom.JsonSourceNPCDataLoader;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.global.TestGameState;
import nightgames.gui.GUI;
import nightgames.gui.TestGUI;
import nightgames.stance.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SkillsTest {
	private List<NPC> npcs1;
	private List<NPC> npcs2;
	private List<Position> stances;
	private Area area;

	@Before
	public void prepare() throws JsonParseException {
		GUI.gui = new TestGUI();
		TestGameState gameState = new TestGameState();
		npcs1 = new ArrayList<>();
		npcs2 = new ArrayList<>();
		try {
			npcs1.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("hermtestnpc.js"))));
			npcs1.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("femaletestnpc.js"))));
			npcs1.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("maletestnpc.js"))));
			npcs1.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("asextestnpc.js"))));
			// don't set fake human right now because there are a lot of casts being done
			//npcs1.forEach(npc -> npc.setFakeHuman(true));

			npcs2.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("hermtestnpc.js"))));
			npcs2.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("femaletestnpc.js"))));
			npcs2.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("maletestnpc.js"))));
			npcs2.add(new CustomNPC(JsonSourceNPCDataLoader.load(SkillsTest.class.getResourceAsStream("asextestnpc.js"))));
		} catch (JsonParseException e) {
			e.printStackTrace();
			Assert.fail();
		}
		area = new Area("Test Area","Area for testing", Movement.quad);
		stances = new ArrayList<>();
		stances.add(new Anal(npcs1.get(0), npcs1.get(1)));
		stances.add(new AnalCowgirl(npcs1.get(0), npcs1.get(1)));
		stances.add(new AnalProne(npcs1.get(0), npcs1.get(1)));
		stances.add(new Behind(npcs1.get(0), npcs1.get(1)));
		stances.add(new BehindFootjob(npcs1.get(0), npcs1.get(1)));
		stances.add(new CoiledSex(npcs1.get(0), npcs1.get(1)));
		stances.add(new Cowgirl(npcs1.get(0), npcs1.get(1)));
		stances.add(new Doggy(npcs1.get(0), npcs1.get(1)));
		stances.add(new Engulfed(npcs1.get(0), npcs1.get(1)));
		stances.add(new FaceSitting(npcs1.get(0), npcs1.get(1)));
		stances.add(new FlowerSex(npcs1.get(0), npcs1.get(1)));
		stances.add(new FlyingCarry(npcs1.get(0), npcs1.get(1)));
		stances.add(new FlyingCowgirl(npcs1.get(0), npcs1.get(1)));
		stances.add(new HeldOral(npcs1.get(0), npcs1.get(1)));
		stances.add(new Jumped(npcs1.get(0), npcs1.get(1)));
		stances.add(new Missionary(npcs1.get(0), npcs1.get(1)));
		stances.add(new Mount(npcs1.get(0), npcs1.get(1)));
		stances.add(new Neutral(npcs1.get(0), npcs1.get(1)));
		stances.add(new NursingHold(npcs1.get(0), npcs1.get(1)));
		stances.add(new Pin(npcs1.get(0), npcs1.get(1)));
		stances.add(new ReverseCowgirl(npcs1.get(0), npcs1.get(1)));
		stances.add(new ReverseMount(npcs1.get(0), npcs1.get(1)));
		stances.add(new SixNine(npcs1.get(0), npcs1.get(1)));
		stances.add(new Standing(npcs1.get(0), npcs1.get(1)));
		stances.add(new StandingOver(npcs1.get(0), npcs1.get(1)));
		stances.add(new TribadismStance(npcs1.get(0), npcs1.get(1)));
		stances.add(new UpsideDownFemdom(npcs1.get(0), npcs1.get(1)));
        stances.add(new UpsideDownMaledom(npcs1.get(0), npcs1.get(1)));
        stances.add(new HeldOral(npcs1.get(0), npcs1.get(1)));
        stances.add(new HeldPaizuri(npcs1.get(0), npcs1.get(1)));
        gameState.makeMatch(new ArrayList<>(npcs1));
	}

	private void testSkill(Character npc1, Character npc2, Position pos) throws CloneNotSupportedException {
		Combat c = new Combat(npc1, npc2, area, pos);
		pos.checkOngoing(c);
		if (c.getStance() == pos) {
			for (Skill skill : SkillPool.skillPool) {
				Combat cloned = c.clone();
				Skill used = skill.copy(cloned.p1);
				if (Skill.skillIsUsable(cloned, used)) {
					System.out.println("["+cloned.getStance().getClass().getSimpleName()+"] Skill usable: " + used.getLabel(cloned) + ".");
					used.resolve(cloned, cloned.p2);
				}
			}
		} else {
			System.out.println("STANCE NOT EFFECTIVE: " + pos.getClass().getSimpleName() + " with top: " + pos.top.getTrueName() + " and bottom: " + pos.bottom.getTrueName());
		}
	}

	// TODO: May need to clone npc1 and npc2 here too, depending on how skills affect characters.
	private void testCombo(Character npc1, Character npc2, Position pos) throws CloneNotSupportedException {
		pos.top = npc1;
		pos.bottom = npc2;
		testSkill(npc1, npc2, pos);
		testSkill(npc2, npc1, pos);
	}

	@Test
	public void test() throws CloneNotSupportedException {
		for (int i = 0; i < npcs1.size(); i++) {
			for (int j = 0; j < npcs2.size(); j++) {
				System.out.println("i = " + i + ", j = " + j);
				for (Position pos : stances) {
					NPC npc1 = npcs1.get(i);
					NPC npc2 = npcs2.get(j);
					System.out.println("Testing [" + i + "]: " + npc1.getTrueName() + " with [" + j + "]: " + npc2.getTrueName() + " in Stance " + pos.getClass().getSimpleName());
					testCombo(npc1.clone(), npc2.clone(), pos);
					System.out.println("Testing [" + j + "]: " + npc2.getTrueName() + " with [" + i + "]: " + npc1.getTrueName() + " in Stance " + pos.getClass().getSimpleName());
					testCombo(npc2.clone(), npc1.clone(), pos);
				}
			}
		}
		System.out.println("test " + Random.random(100000) + " done");
	}
}

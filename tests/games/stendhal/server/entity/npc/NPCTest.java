package games.stendhal.server.entity.npc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.felinashouse.CatSellerNPC;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.CatTestHelper;

/**
 * Test NPC logic.
 *
 * @author Martin Fuchs
 */
public class NPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "int_ados_felinas_house";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CatTestHelper.generateRPClasses();
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME, new CatSellerNPC());

	}

	public NPCTest() throws Exception {
		super(ZONE_NAME, "Felina");
	}

	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Felina");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Felina"));
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void testLogic() {
		final SpeakerNPC npc = getNPC("Felina");
		final Engine en = npc.getEngine();

		npc.listenTo(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I sell cats. Well, really they are just little kittens when I sell them to you but if you #care for them well they grow into cats.", getReply(npc));

		assertNotNull(npc.getAttending());
		npc.preLogic();
		assertEquals("Bye.", getReply(npc));
		assertEquals(null, npc.getAttending());
	}

	@Test
	public void testIdea() {
		final SpeakerNPC npc = getNPC("Felina");

		assertEquals(null, npc.getIdea());
		npc.setIdea("walk");
		assertEquals("walk", npc.getIdea());

		npc.setIdea(null);
		assertEquals(null, npc.getIdea());
	}

	// players use _hi, _hello etc to avoid npcs answering when it's meant to 
	// other players
	@Test
	public void testUnderscore() {
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final SpeakerNPC npc = getNPC("Felina");
			final Engine en = npc.getEngine();
			
			assertEquals(ConversationStates.IDLE, en.getCurrentState());
			
			en.step(player, "_" + hello);
			assertEquals("npc should not answer to _" + hello, ConversationStates.IDLE, en.getCurrentState());
		}
	}
}

package Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import AI.AIManager;
import Network.MasteryData.UniqueType;
import Network.Spell.Effect;

import java.util.Calendar;

/** Player Data Structure */
public class Player {

	public boolean isAI = false;
	public boolean isFirstInGM = false;
	public boolean inGame = false;
	public boolean isConnected;
	public boolean DBDataReceived = false;

//	public Calendar nextChestOpenTime = Calendar.getInstance();

	public Map<Integer, Creature> creatures = new HashMap<Integer, Creature>();
	public Map<Integer, Spell> spells = new HashMap<Integer, Spell>();

	ArrayList<Creature> creaturesAI = new ArrayList<Creature>();

	ArrayList<Creature> creaturesToAddGM = new ArrayList<Creature>();
	ArrayList<Spell> spellsToAddGM = new ArrayList<Spell>();

	public Map<Long, Long> sentTimes = new HashMap<Long, Long>();
	public Map<Long, Long> clientsTimes = new HashMap<Long, Long>();
	public Map<Long, Long> deltaTimes = new HashMap<Long, Long>();

	public Map<Integer, Long> pingSentTimes = new HashMap<Integer, Long>();
	public int pingNum = 1;
	public long nextPingTime;

	public enum PlayerZone {
		LOADING, LOBBY, STORE, MASTERIES, GAME
	};

	// public PlayerZone playerZone = PlayerZone.LOADING;
	public GameManager gameManager;

	public PlayerData playerData;
	public ArrayList<Long> dataTimestamps = new ArrayList<>();
	public int playerIndex;

	CreatureList creaturesToAddTrans = new CreatureList(playerIndex);
	SpellList spellsToAddTrans = new SpellList(playerIndex);

	// public Socket clientSocket;
	public EventSocket eventSocket;
	// public DataOutputStream out;
	// public BufferedReader in;
	public String inputBuffer = "";
	public boolean initSent = false;
	public boolean parametersSent = false;
	public boolean gotParameters = false;

	public Creature king;
	public Creature miniKingLeft;
	public Creature miniKingRight;

	// public Deck deck;
	// public MasteryData[][][] masteriesData = new
	// MasteryData[MasteryData.MASTERY_TREES][MasteryData.MASTERY_MAX_FLOORS][MasteryData.MASTERY_INDEX];
	public HashMap<Integer, MasteryData> masteriesData = new HashMap<Integer, MasteryData>();

	// public float spellDmgModifier = 0;
	// public float spellDurationModifier = 0;

	public long effectiveTimeBetweenMinions = GameManager.TIME_BETWEEN_MINIONS;
	public long timeOfNextMinion;

	public long effectiveTimeBetweenDraws = GameManager.TIME_BETWEEN_DRAWS;
	public long timeOfNextDraw;
	public long timeLastAddedMana = 0;

	public long effectiveManaGrowthTime = GameManager.MANA_GROWTH_TIME;

	public ArrayList<GameCard> handAI = new ArrayList<GameCard>();
	public ArrayList<GameCard> deckAI = new ArrayList<GameCard>();

	/// Check AI input when 1. mana increased 2. card drawn 3. new active opp
	/// creature
	public boolean shouldCheckAiInput = false;

	// public ArrayList<String> cardsToDraw = new ArrayList<String>();
	// public ArrayList<Long> cardsToDrawTimes = new ArrayList<Long>();

	// public ArrayList<Long> ManaTimes = new ArrayList<Long>();
	// public ArrayList<Float> ManaValues = new ArrayList<Float>();

	public Map<Integer, Integer> manaDelayed = new HashMap<Integer, Integer>();
	public float mana = GameManager.MANA_INITIAL;
	public float manaForOpp = GameManager.MANA_INITIAL;
	public float aiCounter = GameManager.MANA_INITIAL;

	public String Data = "";
	public String cardData = "";

	public int cardsDrawn = 0;

	public boolean gotGameLoaded = false;
	public boolean gotTimingPing = false;
	public ArrayList<Long> pingDeltaTimes = new ArrayList<Long>();

	public long PingTimeAVG = 0;
	public long PingTimeAVGUse = 0;
	public boolean gotClock = false;
	public long clock = 0;
	public long ClockTime = 0;
	public boolean isVictorious = false;

	public String sendingBuffer = "";
	public long lastPackageSendingTime = 0;
	public long PackagesTimeInterval = 50;

	public Map<Long, Integer> confirmedTimes = new HashMap<Long, Integer>();
	public ArrayList<Long> nonConfirmedTimes = new ArrayList<Long>();

	private static Player delayedPlayer;

	/// Real player
	public Player(EventSocket eventSocket) {
		this.isConnected = true; // when created it connects to the server
		this.playerIndex = eventSocket.hashCode(); // TODO - consider removing
		this.eventSocket = eventSocket;
	}

	/// AI player
	public Player() {
		this.isAI = true;
		this.gotGameLoaded = true;
		this.isConnected = false; // not connected as it is a simulation player
		this.deckAI = AIManager.createDeck();
	}

	// Reads a single word from the player's buffer
	public String readPlayerBuffer() {
		String txt = "";
		if(inputBuffer.contains(Constants.SEMI_COLON_SEPERATOR)) {
			int ind = inputBuffer.indexOf(Constants.SEMI_COLON_SEPERATOR);
			if(ind == 0)
				txt = "";
			else
				txt = inputBuffer.substring(0, ind);
			inputBuffer = inputBuffer.substring(ind + 1);
		}
		return txt;
	}

	private static ArrayList<String> parseSegment(String str) {
		ArrayList<String> segments = new ArrayList<String>();

		while(str.contains(Constants.FIELD_SEPERATOR)) {
			int index = str.indexOf(Constants.FIELD_SEPERATOR);
			String playerDataSegment;

			if(index == 0)
				playerDataSegment = "";
			else
				playerDataSegment = str.substring(0, index);
			segments.add(playerDataSegment);

			if(str.length() > index + 1)
				str = str.substring(index + 1);
			else
				str = "";
		}

		if(str.length() > 0)
			segments.add(str);

		if(segments.size() == 0)
			segments.add("");

		return segments;
	}

	private static ArrayList<String> parseDataSegment(String str) {
		ArrayList<String> segments = new ArrayList<String>();

		while(str.contains(Constants.GAME_SEPERATOR)) {
			int index = str.indexOf(Constants.GAME_SEPERATOR);

			String playerDataSegment = str.substring(0, index);
			segments.add(playerDataSegment);
			if(str.length() > index + 1) {
				str = str.substring(index + 1);
			} else
				str = "";
		}
		if(str.length() > 0)
			segments.add(str);

		return segments;
	}

	// public void sendDataGradually(String _string) {
	// sendingBuffer += _string;
	// }

	public synchronized void sendData(String _string) {
		if(isAI)
			return;

		_string = _string.concat(Constants.SEMI_COLON_SEPERATOR);

		try {
			if(Constants.DALDebug || Constants.networkDebug)
				System.out.println("sendData: " + _string + " Servertime: " + ServerMain.serverTime);
			if(eventSocket.getRemote() == null) {
				if(Constants.networkDebug)
					System.out.println("RemoteEndPoint closed for player: " + playerIndex);
				return;
			}
			eventSocket.getRemote().sendString(_string);
		} catch(IOException e) {
			e.printStackTrace();
		}

		// try {
		// isConnected = true;
		// // Note: why check if the player is active after setting it active
		// if (isConnected) {
		// //System.out.println("Client #:" + playerIndex + ", Sending at: " +
		// Main.serverTime);
		// System.out.println(Main.serverTime + ": client #" + playerIndex + ",
		// sending message: " + _string);
		// for (long dataTimestamp : dataTimestamps) {
		// sentTimes.put(dataTimestamp, Main.serverTime);
		// // System.out.println("Client #:" + playerIndex + ",
		// // PUTTING: " + dataTimestamp + " " + Main.serverTime);
		// }
		// out.writeBytes(_string + Constants.SEMI_COLON_SEPERATOR);
		// out.flush();
		// dataTimestamps.clear();
		// // System.out.println("sending " + _player.playerIndex + " " +
		// // _string);
		// }
		// } catch (IOException e) {// wasn't able to receive the data from the
		// // player
		// isConnected = false;
		// // System.out.println("player " + _player.playerIndex + "
		// // disconnected");
		// }
	}

	// public synchronized void sendDataInPackages(String _string) {
	// try {
	// eventSocket.getRemote().sendString(_string);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	//// try {
	//// isConnected = true;
	//// // Note: why check if the player is active after setting it active
	//// if (isConnected) {
	//// //System.out.println("Client #:" + playerIndex + ", Sending at: " +
	// Main.serverTime);
	//// System.out.println(Main.serverTime + ": client #" + playerIndex + ",
	// sending message: " + _string);
	//// for (long dataTimestamp : dataTimestamps) {
	//// sentTimes.put(dataTimestamp, Main.serverTime);
	//// // System.out.println("Client #:" + playerIndex + ",
	//// // PUTTING: " + dataTimestamp + " " + Main.serverTime);
	//// }
	//// //out.writeBytes(_string + Constants.UPDATE_SEPERATOR);
	//// out.writeBytes(_string);
	//// out.flush();
	//// dataTimestamps.clear();
	//// // System.out.println("sending " + _player.playerIndex + " " +
	//// // _string);
	//// }
	//// } catch (IOException e) {// wasn't able to receive the data from the
	//// // player
	//// isConnected = false;
	//// // System.out.println("player " + _player.playerIndex + "
	//// // disconnected");
	//// }
	// }

	// public void sendPackage()
	// {
	// if((sendingBuffer.length()==0) ||
	// (ServerMain.serverTime < lastPackageSendingTime + PackagesTimeInterval))
	// return;
	// if(sendingBuffer.length()>Lobby.packageSize)
	// {
	// String MSG = sendingBuffer.substring(0, Lobby.packageSize);
	// sendingBuffer = sendingBuffer.substring(Lobby.packageSize);
	// sendDataInPackages(MSG);
	// lastPackageSendingTime = ServerMain.serverTime;
	// }
	// else
	// {
	// sendDataInPackages(sendingBuffer);
	// sendingBuffer = "";
	// }
	// }

	// Handles text data from the client
	public void handleData(String txt) {

		if(Constants.connectionDebug)
			System.out.println(
					"Client #: " + playerIndex + " Handling: " + txt + " Servertime: " + ServerMain.serverTime);

		ArrayList<String> segments = Player.parseSegment(txt);

		switch(segments.get(0)) {
		case Constants.GOT_PARAMETERS:
			gotParameters = true;
			break;
		case Constants.HAVE_INITIAL_DATA:
			requestInitPlayerData(segments);
			break;
		case Constants.REQUEST_INITIAL_DATA:
			requestNewInitPlayerData(segments);
			break;
		case Constants.GAME_TIME_KEY:
			// TM|2400,CR|ET|NoAcc|+02.70|+03.83|2308,MN|1.15|2308;
			handleGameTimeKey(txt);
			break;
		case Constants.GET_INIT_PLAYER_DATA_KEY:
			sendUserData();
			break;
		case Constants.GET_PLAYER_FIELD_KEY:
			sendUserField(segments);
			break;
		case Constants.POST_PLAYER_FIELD_KEY:
			postUserField(segments);
			break;
		case Constants.START_GAME:
			Lobby.addPlayerToWaitingToPlay(this);
			break;
		case Constants.CANCEL_START_GAME:
			Lobby.waitingToPlay.remove(this);
			break;
		case Constants.GAME_LOADED:
			gotGameLoaded = true;

			if(gameManager == null)
				System.out.println("gameManager==null!!!");
			else if(gameManager.players.size() != 2)
				System.out.println("players.size = " + gameManager.players.size() + " !!!");
			else if(gameManager.getOpp(this) == null)
				System.out.println("gameManager.getOpp(this)==null!!!");

			// if(gameManager.getOpp(this).gotGameLoaded)
			// {
			// sendPing();
			// gameManager.getOpp(this).sendPing();
			// }

			if(gameManager.getOpp(this).gotGameLoaded) {
				Player opp = gameManager.getOpp(this);

				if(PingTimeAVGUse > opp.PingTimeAVGUse) {
					sendData(Constants.START_CLOCK);
					delayedPlayer = opp;
				} else {
					opp.sendData(Constants.START_CLOCK);
					delayedPlayer = this;
				}

				long deltaTimePings = Math.abs(PingTimeAVGUse - opp.PingTimeAVGUse);
				if(Constants.clientTimingDebug)
					System.out.println("LOG - START_CLOCK: " + playerIndex);
				if(Constants.clientTimingDebug)
					System.out.println("LOG - deltaTimePings: " + deltaTimePings);

				if(!opp.isAI) {
					new java.util.Timer().schedule(new java.util.TimerTask() {
						@Override
						public void run() {
							delayedPlayer.sendData(Constants.START_CLOCK);
							if(Constants.clientTimingDebug)
								System.out.println("LOG - START_CLOCK: " + delayedPlayer.playerIndex);
						}
					}, deltaTimePings);
				}

				gameManager.startClockSent = true;
				gameManager.startGameTime = ServerMain.serverTime + GameManager.GAME_START_DELAY;
				// gameManager.timeAtClient = -GameManager.GAME_START_DELAY;
				gameManager.clientsTime = ServerMain.serverTime - gameManager.startGameTime;
				gameManager.gameTime = gameManager.clientsTime + GameManager.TIME_BUFFER;
				gameManager.sendDataToPlayers();
			}
			break;
		// case Constants.CLOCK:
		// gotClock = true;
		// clock = Long.parseLong(segments.get(1));
		// if(gameManager.getOpp(this).gotClock)
		// gameManager.sendStartClocks();
		// break;
		// case Constants.GAME_LOADED:
		// gotGameLoaded = true;
		// if(gameManager.getOpp(this).gotGameLoaded)
		// gameManager.AskClock();
		// break;
		// case Constants.CLOCK:
		// gotClock = true;
		// clock = Long.parseLong(segments.get(1));
		// if(gameManager.getOpp(this).gotClock)
		// gameManager.sendStartClocks();
		// break;
		case Constants.CLIENT_DISCONNECTED:
			isConnected = false;
			break;
		case Constants.CLIENT_RECONNECTED:
			isConnected = true;
			break;
		// case Constants.CREATURE_KEY:
		// gameManager.handleGameData(segments, this);
		// break;
		// case Constants.MANA_UPDATE_KEY:
		// updateMana(segments);
		// break;
		case Constants.BUY_GEMS_KEY:
			buyGems(segments);
			break;
		case Constants.BUY_GOLD_KEY:
			buyGold(segments);
			break;
		case Constants.TIMING_KEY:
			handleTimingKey(segments);
			break;
		case Constants.PING:
			pingDeltaTimes.add(handleGotPing(segments));
			// clock = Long.parseLong(segments.get(1));
			// ClockTime = Main.serverTime;
			// if(gotTimingPing && gameManager.getOpp(this).gotTimingPing)
			// {
			// Player player = this;
			// player.gotTimingPing = false;
			// player.PingTimeAVG = avgArray(player.pingDeltaTimes);
			// player.pingDeltaTimes.clear();
			// if(Constants.clientTimingDebug) System.out.println("PlayerID: " +
			// player.playerIndex +": PingTimeAVG:" + player.PingTimeAVG);
			// player = gameManager.getOpp(this);
			// player.gotTimingPing = false;
			// player.PingTimeAVG = avgArray(player.pingDeltaTimes);
			// player.pingDeltaTimes.clear();
			// if(Constants.clientTimingDebug) System.out.println("PlayerID: " +
			// player.playerIndex +": PingTimeAVG:" + player.PingTimeAVG);
			//
			// deltaTimePings = PingTimeAVG -
			// gameManager.getOpp(this).PingTimeAVG;
			// if(Constants.clientTimingDebug) System.out.println("LOG -
			// deltaTimePings: " + deltaTimePings);
			// sendData(Constants.START_CLOCK);
			// if(Constants.clientTimingDebug) System.out.println("LOG -
			// START_CLOCK: " + playerIndex);
			// tempPlayer = gameManager.getOpp(this);
			// new java.util.Timer().schedule(
			// new java.util.TimerTask() {
			// @Override
			// public void run() {
			// tempPlayer.sendData(Constants.START_CLOCK);
			// if(Constants.clientTimingDebug) System.out.println("LOG -
			// START_CLOCK: " + tempPlayer.playerIndex);
			// }
			// },
			// deltaTimePings
			// );
			// gameManager.startClockSent = true;
			// gameManager.startGameTime = ServerMain.serverTime +
			// gameManager.gameStartDelay + PingTimeAVG;
			// gameManager.timeAtClient = -GameManager.DEPLOY_TIME;
			// }
			break;
		}
	}

	private long avgArray(ArrayList<Long> myArray) {
		long sum = 0;
		int sumNumPings = 0;
		for(int i = 0; i < myArray.size(); i++) {
			if(myArray.get(i) < 1000) {
				sum += myArray.get(i);
				sumNumPings++;
			}
		}

		return(sum / sumNumPings);
	}

	public void sendPing() {
		// long time = (Main.serverTime - startGameTime);
		pingSentTimes.put(pingNum, ServerMain.serverTime);
		if(Constants.clientTimingDebug)
			System.out.println("--- pingSentTimes.put(" + pingNum + "," + ServerMain.serverTime);
		sendData(Constants.PING + Constants.FIELD_SEPERATOR + pingNum);
		pingNum++;
	}

	private void requestInitPlayerData(ArrayList<String> segments) {
		if(Constants.DALDebug)
			System.out.println("/// requestInitPlayerData" + " Servertime: " + ServerMain.serverTime);

		int DBIndex = Integer.parseInt(segments.get(1));
		playerData = new PlayerData(DBIndex);

		if(Constants.DALDebug)
			System.out.println("/// 1. playerData INITIALIZED" + " Servertime: " + ServerMain.serverTime);

		DBDataReceived = true;
		storeAndApplyMasteriesFromDB();
		sendParameters();
		parametersSent = true;

		// todo: check if gotInitialData still used

		if(Constants.DALDebug)
			System.out
					.println("client #" + playerIndex + ", got initial data" + " Servertime: " + ServerMain.serverTime);
	}

	private void requestNewInitPlayerData(ArrayList<String> segments) {
		if(Constants.DALDebug)
			System.out.println("/// requestNewInitPlayerData" + " Servertime: " + ServerMain.serverTime);

		String username = segments.get(1);
		String password = segments.get(2);
		playerData = new PlayerData();

		if(Constants.DALDebug)
			System.out.println("/// 2. playerData INITIALIZED" + " Servertime: " + ServerMain.serverTime);

		playerData.getDataForNewDBId(username, password);

		// sendDataGradually(Constants.USER_DB_ID + Constants.FIELD_SEPERATOR +
		// playerData.DBIndex + Constants.SEMI_COLON_SEPERATOR);
		sendData(
				Constants.USER_DB_ID + Constants.FIELD_SEPERATOR + playerData.DBIndex + Constants.SEMI_COLON_SEPERATOR);

		if(Constants.DALDebug)
			System.out.println("client #" + playerIndex + ", done getNewDBId for new user" + " Servertime: "
					+ ServerMain.serverTime);

		DBDataReceived = true;
		storeAndApplyMasteriesFromDB();
		sendParameters();
		parametersSent = true;

		if(Constants.DALDebug)
			System.out
					.println("client #" + playerIndex + ", got initial data" + " Servertime: " + ServerMain.serverTime);
	}

	// void handleGameTimeKey(ArrayList<String> segments) {
	private void handleGameTimeKey(String txt) {

		ArrayList<String> gameSegments = parseDataSegment(txt);
		// for(String gameSegment : gameSegments)
		// {
		// System.out.println("gameSegment: " + gameSegment);
		// }
		handlePlayerGameInput(gameSegments);

		// gameSegment.get.remove(0);
		// String timeKey = segments.get(0);
		//
		// long time = Long.parseLong(timeKey);
		// if(time >= gameManager.nextUpdateTime)
		// confirmedTimes.put(time, 1000);
		//
		// handlePlayerGameInput(segments, time);
	}

	// void handlePlayerGameInput(ArrayList<String> dataSegments, long
	// timeOfSending)
	private void handlePlayerGameInput(ArrayList<String> dataSegments) {
		int numCreated = 0;
		if(Constants.networkDebug)
			System.out.println("handlePlayerGameInput: dataSegments.size() = " + dataSegments.size() + " at:"
					+ gameManager.gameTime);

		for(String segment : dataSegments)
			System.out.println(segment);

		for(int i = 1; i < dataSegments.size(); i++) {
			ArrayList<String> segments = Player.parseSegment(dataSegments.get(i));
			// System.out.println("segments.size() = " + segments.size());
			// System.out.println("segments: " + segments);

			switch(segments.get(0)) {
			case Constants.CREATURE_KEY:
				if(Constants.creatureDebug)
					System.out.println("~~~ Creature received from client. Time: " + dataSegments.get(i) + " at:"
							+ gameManager.gameTime);
				numCreated += gameManager.createCreatureCopies(segments, this);
				break;
			case Constants.SPELL_KEY:
				// System.out.println("SPELL_KEY");
				numCreated += gameManager.handleSpellGameData(segments, this);
				break;
			// case Constants.MANA_UPDATE_KEY:
			//// System.out.println("MANA_UPDATE_KEY");
			// updateMana(segments, timeOfSendingLastInput);
			// break;
			}
		}
	}

	private void handleTimingKey(ArrayList<String> segments) {
		switch(segments.get(1)) {
		case Constants.GOT_MESSAGE_KEY:
			handleGotMessageKey(segments);
			break;
		case Constants.CREATURE_SPAWNED_KEY:
			handleCreatureSpawnedKey(segments);
			break;
		case Constants.CREATURE_ACTIVATE_KEY:
			handleCreatureActivateKey(segments);
		}
	}

	private void handleGotMessageKey(ArrayList<String> segments) {
		long key = Long.parseLong(segments.get(2));
		long value = ServerMain.serverTime;
		long clientsTime = Long.parseLong(segments.get(3));
		// long sent_value = 0;
		if(sentTimes.containsKey(key)) {
			long sent_value = sentTimes.get(key);
			sentTimes.remove(key);
			long delta = value - sent_value;
			// deltaTimes.put(key, delta);
			// clientsTimes.put(key, clientsTime);

			if(Constants.clientTimingDebug) {
				System.out.println(
						"Client #: " + playerIndex + ", getting key: " + key + " at clients time: " + clientsTime);
				// System.out.println("b. Client #: " + playerIndex + ", getting
				// key: " + key + " after time: " + delta +" from sending ");
			}
		}
	}

	private long handleGotPing(ArrayList<String> segments) {
		long delta = 0;
		int key = Integer.parseInt(segments.get(1));
		if(Constants.clientTimingDebug)
			System.out.println("--- key: " + key);
		if(Constants.clientTimingDebug)
			System.out.println("--- pingSentTimes.size(): " + pingSentTimes.size());
		long time = ServerMain.serverTime;
		// long sent_value = 0;
		if(pingSentTimes.containsKey(key)) {
			long sentTime = pingSentTimes.get(key);
			pingSentTimes.remove(key);
			delta = time - sentTime;
			// if(Constants.clientTimingDebug) System.out.println("--- sentTime:
			// " + sentTime);
			// if(Constants.clientTimingDebug) System.out.println("---
			// currentTime: " + time);
			if(Constants.clientTimingDebug)
				System.out.println("PlayerID: " + playerIndex + ": Got ping num: " + key + " with delta: " + delta);

			// if(Constants.clientTimingDebug)
			// {
			// System.out.println("Client #: " + playerIndex + ", getting ping
			// after: " + delta);
			// //System.out.println("b. Client #: " + playerIndex + ", getting
			// key: " + key + " after time: " + delta +" from sending ");
			// }
		}
		if(key == Lobby.NUM_SUCCESSIVE_PINGS) {
			// gotTimingPing = true;
			PingTimeAVG = avgArray(pingDeltaTimes);
			pingDeltaTimes.clear();
			pingNum = 1;
		} else
			sendPing();
		return delta;
	}

	private void handleCreatureActivateKey(ArrayList<String> segments) {
		int creatureID = Integer.parseInt(segments.get(2));
		long clientsTime = Long.parseLong(segments.get(3));
		// long sent_value = 0;

		if(creatures.containsKey(creatureID)) {
			Creature creature = creatures.get(creatureID);
			long delta = clientsTime - creature.timeOfCasting;

			if(Constants.clientTimingDebug) {
				// System.out.println("Client #: " + playerIndex + ", activating
				// creature: " + creatureID + " after time: " + delta);
				System.out.println("Timing: Client #: " + playerIndex + " activated creature: " + creature.id
						+ " at gameTime " + gameManager.gameTime);
			}
		}
	}

	private void handleCreatureSpawnedKey(ArrayList<String> segments) {
		int creatureID = Integer.parseInt(segments.get(2));
		long clientsTime = Long.parseLong(segments.get(3));
		// long sent_value = 0;

		if(creatures.containsKey(creatureID)) {
			Creature creature = creatures.get(creatureID);
			long delta = clientsTime - creature.timeOfCasting;

			if(Constants.clientTimingDebug) {
				// System.out.println("Client #: " + playerIndex + ", spawning
				// creature: " + creatureID + " after time: " + delta);
				System.out.println("Client #: " + playerIndex + ", spawning creature: " + creatureID
						+ " at clientsTime: " + clientsTime + " at gameTime: " + gameManager.gameTime);
			}
		}
	}

	// void updateMana(ArrayList<String> segments, long sendingTime) {
	// float mana = Float.parseFloat(segments.get(1));
	// long time = Long.parseLong(segments.get(2));
	// if(time >= gameManager.nextUpdateTime) {
	// gameManager.updateManaBuffer(this, time + GameManager.DEPLOY_TIME, mana);
	//
	// if(confirmedTimes.containsKey(gameManager.nextUpdateTime))
	// if (Constants.timeDebug) System.out.println("(player: " + playerIndex + "
	// confirmedTimes (MN): " + confirmedTimes.get(sendingTime) + " for
	// sendingTime " + sendingTime);
	// }
	// }

	private void sendUserData() {
		if(Constants.networkDebug)
			System.out.println("client #" + playerIndex + ", got getPlayerInitialDataKey");

		String newMessage = unParseSegment(new String[] { Constants.PLAYER_DATA_KEY,
				Constants.USERNAME + Constants.EQUALS + playerData.getUsername(),
				Constants.GOLD + Constants.EQUALS + playerData.getGold(),
				Constants.GEMS + Constants.EQUALS + playerData.getGems(),
				Constants.SCORE + Constants.EQUALS + playerData.getScore(),
				Constants.MUSIC + Constants.EQUALS + playerData.getMusic(),
				Constants.SOUND_FX + Constants.EQUALS + playerData.getSfx(),
				Constants.NOTIFICATIONS + Constants.EQUALS + playerData.getNotifications(),
				Constants.LANGUAGE + Constants.EQUALS + playerData.getLanguage(),
				Constants.COLLECTION + Constants.EQUALS + playerData.getCollection(),
				Constants.ACCESSORIES + Constants.EQUALS + playerData.getAccessories(),
				Constants.MASTERIES_KEYS + Constants.EQUALS + playerData.getMasteriesKeys(),
				Constants.MASTERIES_OFFENCE + Constants.EQUALS + playerData.getMasteriesOffence(),
				Constants.MASTERIES_DEFENCE + Constants.EQUALS + playerData.getMasteriesDefence(),
				Constants.MASTERIES_UTILITY + Constants.EQUALS + playerData.getMasteriesUtility(),
				Constants.DECK1 + Constants.EQUALS + playerData.getDeck1(),
				Constants.DECK2 + Constants.EQUALS + playerData.getDeck2(),
				Constants.DECK3 + Constants.EQUALS + playerData.getDeck3(),
				Constants.CURRENT_DECK + Constants.EQUALS + playerData.currentDeckIndex,
				Constants.NEXT_CHEST_TIME + Constants.EQUALS + playerData.nextChestTime, });

		sendData(newMessage);
	}

	private void sendUserField(ArrayList<String> segments) {
		if(Constants.networkDebug)
			System.out.println("client #" + playerIndex + ", got getPlayerFieldKey");

		String fieldName = segments.get(1), data = "";
		switch(fieldName) {
		case Constants.USERNAME:
			data = playerData.getUsername();
			break;
		case Constants.GOLD:
			data = String.valueOf(playerData.getGold());
			break;
		case Constants.GEMS:
			data = String.valueOf(playerData.getGems());
			break;
		case Constants.SCORE:
			data = String.valueOf(playerData.getScore());
			break;
		case Constants.MUSIC:
			data = String.valueOf(playerData.getMusic());
			break;
		case Constants.SOUND_FX:
			data = String.valueOf(playerData.getSfx());
			break;
		case Constants.NOTIFICATIONS:
			data = String.valueOf(playerData.getNotifications());
			break;
		case Constants.LANGUAGE:
			data = String.valueOf(playerData.getLanguage());
			break;
		case Constants.COLLECTION:
			data = playerData.getCollection();
			break;
		case Constants.ACCESSORIES:
			data = playerData.getAccessories();
			break;
		case Constants.MASTERIES_KEYS:
			data = playerData.getMasteriesKeys();
			break;
		case Constants.MASTERIES_OFFENCE:
			data = playerData.getMasteriesOffence();
			break;
		case Constants.MASTERIES_DEFENCE:
			data = playerData.getMasteriesDefence();
			break;
		case Constants.MASTERIES_UTILITY:
			data = playerData.getMasteriesUtility();
			break;
		case Constants.DECK1:
			data = playerData.getDeck1();
			break;
		case Constants.DECK2:
			data = playerData.getDeck2();
			break;
		case Constants.DECK3:
			data = playerData.getDeck3();
			break;
		case Constants.CURRENT_DECK:
			data = playerData.getCurrentDeck();
			break;
		case Constants.NEXT_CHEST_TIME:
			data = playerData.getNextChestTime();
			break;
		}

		String newMessage = unParseSegment(new String[] { Constants.GET_PLAYER_FIELD_KEY, fieldName + "=" + data });

		sendData(newMessage);
	}

	private void postUserField(ArrayList<String> segments) {
		if(Constants.DALDebug)
			System.out.println("client #" + playerIndex + ", got postPlayerFieldKey");

		String fieldName = segments.get(1);
		String data = segments.get(2);

		if(playerData == null)
			System.out.println("playerData == null!!");

		switch(fieldName) {
		case Constants.USERNAME:
			playerData.postUsername(data);
			break;
		case Constants.GOLD:
			playerData.postGold(Integer.parseInt(data));
			break;
		case Constants.GEMS:
			playerData.postGems(Integer.parseInt(data));
			break;
		case Constants.SCORE:
			playerData.postScore(Integer.parseInt(data));
			break;
		case Constants.MUSIC:
			playerData.postMusic(Integer.parseInt(data));
			break;
		case Constants.SOUND_FX:
			playerData.postSfx(Integer.parseInt(data));
			break;
		case Constants.NOTIFICATIONS:
			playerData.postNotifications(Integer.parseInt(data));
			break;
		case Constants.LANGUAGE:
			playerData.postLanguage(data);
			break;
		case Constants.COLLECTION:
			playerData.postCollection(data);
			break;
		case Constants.ACCESSORIES:
			playerData.postAccessories(data);
			break;
		case Constants.MASTERIES_KEYS:
			playerData.postMasteriesKeys(data);
			break;
		case Constants.MASTERIES_OFFENCE:
			playerData.postMasteriesOffence(data);
			storeAndApplyMasteries(data);
			break;
		case Constants.MASTERIES_DEFENCE:
			playerData.postMasteriesDefence(data);
			storeAndApplyMasteries(data);
			break;
		case Constants.MASTERIES_UTILITY:
			playerData.postMasteriesUtility(data);
			storeAndApplyMasteries(data);
			break;
		case Constants.DECK1:
			playerData.postDeck1(data);
			break;
		case Constants.DECK2:
			playerData.postDeck2(data);
			break;
		case Constants.DECK3:
			playerData.postDeck3(data);
			break;
		case Constants.CURRENT_DECK:
			playerData.postCurrentDeckIndex(data);
			break;
		case Constants.NEXT_CHEST_TIME:
			playerData.postNextChestTime(data);
			break;
		}
	}

	// void storeAndApplyAccessoriesFromDB()
	// {
	//
	// }

	private void storeAndApplyMasteriesFromDB() {
		storeMasteryData(playerData.getMasteriesOffence());
		storeMasteryData(playerData.getMasteriesDefence());
		storeMasteryData(playerData.getMasteriesUtility());
		applyGeneralMasteries();
	}

	private void storeAndApplyMasteries(String json) {
		storeMasteryData(json);
		applyGeneralMasteries();
	}

	private void storeMasteryData(String json) {
		if(Constants.DALDebug)
			System.out.println("storeMasteryData Servertime: " + ServerMain.serverTime);

		ArrayList<MasteryData> masteries = parseMasteryData(json);
		for(MasteryData mastery : masteries) {
			if(mastery != null)
				masteriesData.put(mastery.getUniqueType().getValue(), mastery);
			// masteriesData[mastery.Type.getValue()][mastery.Level][mastery.Index]
			// = mastery;
			// if(!masteriesData.containsValue(mastery))
			// {
			// masteriesData.put(mastery.getUniqueType().getValue(), mastery);
			// }
			if(Constants.DALDebug)
				System.out.println("Storing mastery: " + mastery.UniqueType + " Level: " + mastery.Level + " Index: "
						+ mastery.Index + " CurrentPoints: " + mastery.CurrentPoints);
		}
	}

	private void applyGeneralMasteries() {
		if(Constants.DALDebug)
			System.out.println("applyGeneralMasteries STARTS Servertime: " + ServerMain.serverTime);

		// float spellDmgBonus = getMasteryBonus(UniqueType.SPELL_DMG);
		// spellDmgModifier += Util.applyPercent(spellDmgBonus);
		//
		// float spellDurationBonus =
		// getMasteryBonus(UniqueType.SPELL_DURATION);
		// spellDurationModifier += Util.applyPercent(spellDurationBonus);

		float minionsSpawnTimeBonus = getMasteryBonus(UniqueType.CREEPS_SPAWN_TIME);
		effectiveTimeBetweenMinions *= Util.subtractPercent(minionsSpawnTimeBonus);
		timeOfNextMinion = effectiveTimeBetweenMinions;

		float manaRegenBonus = getMasteryBonus(UniqueType.MANA_REGEN);
		effectiveManaGrowthTime *= Util.subtractPercent(manaRegenBonus);
		// System.out.println("Player: " + playerIndex + " manaRegenBonus: " +
		// manaRegenBonus + " effectiveManaGrowthTime: " +
		// effectiveManaGrowthTime);

		float drawRegenBonus = getMasteryBonus(UniqueType.DRAW_REGEN);
		effectiveTimeBetweenDraws *= Util.subtractPercent(drawRegenBonus);

		if(Constants.DALDebug)
			System.out.println("applyGeneralMasteries ENDS Servertime: " + ServerMain.serverTime);
	}

	private float getMasteryBonus(UniqueType uniqueType) {
		int uniqueTypeValue = uniqueType.getValue();
		MasteryData mastery = masteriesData.get(uniqueTypeValue);
		return mastery.getTotalPercent();
	}

	private ArrayList<MasteryData> parseMasteryData(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			// MasteryData data = mapper.readValue(json, MasteryData.class);
			ArrayList<MasteryData> data = mapper.readValue(json, new TypeReference<ArrayList<MasteryData>>() {
			});
			return data;
			// JsonNode rootNode = mapper.readTree(json);
		} catch(JsonParseException e) {
			e.printStackTrace();
		} catch(JsonMappingException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null; // TODO - fix this?
	}

	private String unParseSegment(String[] strArray) {
		String newString = "";
		for(int i = 0; i < strArray.length; i++) {
			newString += strArray[i];
			if(i < (strArray.length - 1))
				newString += Constants.FIELD_SEPERATOR;

		}
		return newString;
	}

	// public void checkInputAddToBuffer() {
	// String data;
	// try {
	// if (in.ready()) {
	// data = in.readLine();
	// if(!inGame)
	// System.out.println("DATA from " + playerIndex + ": " + data);
	// else
	// System.out.println("DATA from " + playerIndex + ": " + data
	// + " at: " + (Main.serverTime - gameManager.startGameTime));
	// addDataToPlayerBuffer(data);
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	public void sendParameters() {
//		sendNextChestOpenTime();
		sendGameManagerParams();
		sendCreatureParams();
		sendSpellParams();
		sendAccessoryParams();
		if(Constants.DALDebug) System.out.println("sendData - END_OF_DATA at Servertime: " + ServerMain.serverTime);
		sendData(Constants.END_OF_DATA + Constants.SEMI_COLON_SEPERATOR);
	}

//	private void sendNextChestOpenTime() {
//		Calendar now = Calendar.getInstance();
//		if(now.after(nextChestOpenTime)) {			
//			/// Set next chest to be available 6 hours from now
//			nextChestOpenTime = now;
//			nextChestOpenTime.add(Calendar.HOUR, 6);
//		}
//		sendData(
//				Constants.NEXT_CHEST_TIME + 
//				Constants.FIELD_SEPERATOR +
//				nextChestOpenTime.getTimeInMillis()
//				);
//	}
	
	public void sendGameManagerParams() {

		sendGameManagerParam(Constants.END_GAME_TIME, String.valueOf(GameManager.END_GAME_TIME),
				Constants.MANA_GROWTH_TIME, String.valueOf(GameManager.MANA_GROWTH_TIME), Constants.DECK_SIZE,
				String.valueOf(GameManager.DECK_SIZE), Constants.SCALE_FACTOR,
				String.valueOf(GameManager.SPATIAL_SCALE_FACTOR), Constants.CARDS_LIMIT,
				String.valueOf(GameManager.NUM_CARDS_LIMIT), Constants.PACKAGE_UPDATE_DELTA_TIME,
				String.valueOf(GameManager.PACKAGE_UPDATE_DELTA_TIME), Constants.GAME_START_DELAY,
				String.valueOf(GameManager.GAME_START_DELAY), Constants.FIRST_UPDATE,
				String.valueOf(GameManager.FIRST_UPDATE_TIME));
	}

	private void sendGameManagerParam(String... params) {
		String data = Constants.GAME_MANAGER_DATA;

		for(int i = 0; i < params.length - 1; i += 2) {
			String fieldAndValue = Constants.FIELD_SEPERATOR + params[i] + Constants.EQUALS + params[i + 1];
			data = data.concat(fieldAndValue);
		}
		// sendDataGradually(data + Constants.SEMI_COLON_SEPERATOR);
		sendData(data + Constants.SEMI_COLON_SEPERATOR);
	}

	private void sendCreatureParams() {
		// TODO - check there are no concurrency issues with having everyone
		// iterating the same map
		if(Constants.networkDebug)
			System.out.println("&&& sendCreatureParams: creatureMap.size = " + CardsData.creatureMap.size());
		Iterator<Map.Entry<String, Creature>> it = CardsData.creatureMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Creature> pair = it.next();
			Creature creature = pair.getValue();
			sendCreatureParams(creature);
		}
	}

	private void sendCreatureParams(Creature creature) {
		sendCreatureParams(creature.acronym, Constants.MANA_COST, String.valueOf(creature.manaCost), Constants.RARITY,
				String.valueOf(creature.rarity), Constants.MAX_HP, String.valueOf((int) Math.floor(creature.maxHp)),
				Constants.ATTACK_TYPE, String.valueOf(creature.attackType), Constants.DEFENCE_TYPE,
				String.valueOf(creature.defenceType), Constants.DAMAGE,
				String.valueOf((int) Math.floor(creature.damage)), Constants.ATTACK_SPEED,
				String.valueOf(creature.attackSpeed), Constants.MOVEMENT_SPEED, String.valueOf(creature.ms.getValue()),
				Constants.RANGE_ATTACK, String.valueOf(creature.rangeAttack), Constants.RANGE,
				String.valueOf(creature.range), Constants.HARD_RADIUS, String.valueOf(creature.hardRadius),
				Constants.SOFT_RADIUS, String.valueOf(creature.softRadius), Constants.AREA_DAMAGE_RADIUS,
				String.valueOf(creature.areaDamageRadius), Constants.MASS,
				String.valueOf((int) Math.floor(creature.mass)), Constants.CLIP_POINT,
				String.valueOf(creature.clipPoint), Constants.CLIP_POINT_SA, String.valueOf(creature.clipPointSA),
				// Constants.ATTACK_CLIP_SPEED,
				// String.valueOf(creature.attackClipSpeed),
				// Constants.ATTACK_CLIP_LENGTH,
				// String.valueOf(creature.attackClipLength),
				Constants.ATTACK_PAUSE_TIME, String.valueOf(creature.attackPauseTimeSeconds),
				Constants.ATTACK_PAUSE_TIME_SA, String.valueOf(creature.attackPauseTimeSecondsSA),
				// Constants.ATTACK_TIME,
				// String.valueOf(creature.attackTime),
				Constants.X_START_SHOOT, String.valueOf(creature.xStartShoot), Constants.Y_START_SHOOT,
				String.valueOf(creature.yStartShoot), Constants.Y_HIT_SHOOT, String.valueOf(creature.yHitShoot),
				Constants.SPEED_SHOOT, String.valueOf(creature.speedShoot), Constants.PROJECTILE_LENGTH,
				String.valueOf(creature.projectileLength), Constants.GRAVITY, String.valueOf(creature.gravity));
	}

	private void sendCreatureParams(String acronym, String... params) {
		String data = Constants.CREATURE_DATA + Constants.FIELD_SEPERATOR + acronym;

		for(int i = 0; i < params.length - 1; i += 2) {
			String fieldAndValue = Constants.FIELD_SEPERATOR + params[i] + Constants.EQUALS + params[i + 1];
			data = data.concat(fieldAndValue);
		}
		// sendDataGradually(data + Constants.SEMI_COLON_SEPERATOR);
		sendData(data + Constants.SEMI_COLON_SEPERATOR);
	}

	private void sendSpellParams() {
		// TODO - check there are no concurrency issues with having everyone
		// iterating the same map
		Iterator<Map.Entry<String, Spell>> it = CardsData.spellMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Spell> pair = it.next();
			Spell spell = pair.getValue();
			sendSpellParams(spell);
		}
	}

	private void sendSpellParams(Spell spell) {
		String data = Constants.SPELL_DATA + Constants.FIELD_SEPERATOR + spell.acronym + Constants.FIELD_SEPERATOR
				+ Constants.MANA_COST + Constants.EQUALS + String.valueOf(spell.manaCost) + Constants.FIELD_SEPERATOR
				+ Constants.RARITY + Constants.EQUALS + String.valueOf(spell.rarity);

		Iterator<Map.Entry<Effect, Float>> it = spell.Effects.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Effect, Float> pair = it.next();
			Effect key = pair.getKey();
			float value = pair.getValue();

			data += Constants.FIELD_SEPERATOR + key + Constants.EQUALS + value;
		}

		data += Constants.FIELD_SEPERATOR + Constants.SPELL_RADIUS + Constants.EQUALS + String.valueOf(spell.radius)
				+ Constants.FIELD_SEPERATOR + Constants.SPELL_DURATION + Constants.EQUALS
				+ String.valueOf(spell.duration);

		// sendDataGradually(data + Constants.SEMI_COLON_SEPERATOR);
		sendData(data + Constants.SEMI_COLON_SEPERATOR);
	}

	// void sendSpellParams(String acronym, String... params) {
	// String data =
	// Constants.SPELL_DATA +
	// Constants.FIELD_SEPERATOR +
	// acronym;
	//
	// for(int i=0; i<params.length-1; i+=2){
	// String fieldAndValue =
	// Constants.FIELD_SEPERATOR +
	// params[i] +
	// Constants.EQUALS +
	// params[i+1];
	// data = data.concat(fieldAndValue);
	// }
	// sendDataGradually(data + Constants.SEMI_COLON_SEPERATOR);
	// }

	private void sendAccessoryParams() {
		// TODO - check there are no concurrency issues with having everyone
		// iterating the same map
		Iterator<Map.Entry<String, Accessory>> it = CardsData.accessoryMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Accessory> pair = it.next();
			Accessory accessory = pair.getValue();
			sendAccessoryParams(accessory);
		}
	}

	private void sendAccessoryParams(Accessory accessory) {
		sendAccessoryParams(accessory.acronym, Constants.AC_OWNER_NAME, String.valueOf(accessory.ownerName),
				Constants.AC_TYPE, String.valueOf(accessory.accessoryType.getValue()), Constants.AC_EFFECT,
				String.valueOf(accessory.accessoryEffect.getValue()), Constants.AC_BONUS_PERCENT,
				String.valueOf(accessory.bonusPercent));
	}

	private void sendAccessoryParams(String acronym, String... params) {
		String data = Constants.ACCESSORY_DATA + Constants.FIELD_SEPERATOR + acronym;

		for(int i = 0; i < params.length - 1; i += 2) {
			String fieldAndValue = Constants.FIELD_SEPERATOR + params[i] + Constants.EQUALS + params[i + 1];
			data = data.concat(fieldAndValue);
		}

		// sendDataGradually(data + Constants.SEMI_COLON_SEPERATOR);
		sendData(data + Constants.SEMI_COLON_SEPERATOR);
	}

	private void buyGold(ArrayList<String> segments) {
		synchronized(playerData) {
			int requestedGold = Integer.parseInt(segments.get(1));
			int playerGems = playerData.getGems();
			int playerGold = playerData.getGold();
			if(playerGems >= requestedGold / Constants.GOLD_GEMS_Ratio) {
				int newGems = playerGems - requestedGold / Constants.GOLD_GEMS_Ratio;
				int newGold = playerGold + requestedGold;

				sendData(Constants.PLAYER_DATA_KEY + Constants.FIELD_SEPERATOR + Constants.GEMS + Constants.EQUALS
						+ String.valueOf(newGems) + Constants.FIELD_SEPERATOR + Constants.GOLD + Constants.EQUALS
						+ String.valueOf(newGold));

				playerData.postGems(newGems);
				playerData.postGold(newGold);
			} else {
				sendData(Constants.NOT_ENOUGH_GEMS);
				if(Constants.debugPurchases)
					System.out.println("client #" + playerIndex + ", not enough gems to buy gold");
			}
		}
	}

	private void buyGems(ArrayList<String> segments) {
		synchronized(playerData) {
			// TODO: pay for gems with money, if not enough money...
			int requestedGems = Integer.parseInt(segments.get(1));
			int newGems = playerData.getGems() + requestedGems;
			playerData.postGems(playerData.getGems() + requestedGems);
			sendData(Constants.PLAYER_DATA_KEY + Constants.FIELD_SEPERATOR + "GEMS" + Constants.EQUALS
					+ String.valueOf(newGems));
		}
	}

	public void clearGameData() {
		inGame = false;
		creatures.clear();
		spells.clear();
		creaturesToAddGM.clear();
		spellsToAddGM.clear();
		sentTimes.clear();
		clientsTimes.clear();
		deltaTimes.clear();
		// playerZone = PlayerZone.LOBBY;
		gameManager = null;
		dataTimestamps.clear();
		creaturesToAddTrans.clear();
		spellsToAddTrans.clear();
		gotGameLoaded = false;
		gotClock = false;
		clock = 0;
		ClockTime = 0;
		isVictorious = false;
		// sendingBuffer = "";
		confirmedTimes.clear();
	}
}

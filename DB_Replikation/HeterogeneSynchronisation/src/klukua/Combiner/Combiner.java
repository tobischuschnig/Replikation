package klukua.Combiner;

import java.util.ArrayList;
import klukua.Config.ConfigLoader;

/**
 * Diese Klasse synchronisiert die logischen Datenbanken die von den Readern 
 * gelesen werden, dabei ist darauf zu achten welche Art der Synchronisation 
 * angewandt wird, also MM oder SM/MS
 * 
 * @author Alexander Klune, Kuanlun Huang
 * @version 1.0
 */
public class Combiner {

	// hier wird die synchronisierte Tabelle(Zeilen) gespeichert die auf
	// beide DBs gepeichert werden
	private ArrayList<String[][]> combinedList;

	/**
	 * Konstruktor welcher die combinedList initialisiert und die Synchronisationsart auswaehlt
	 * (MasterMaster oder MasterSlave)
	 * @param mySQLList	Tabellenliste von MySQL
	 * @param postGreSQLList	Tabellenlsite von PostgreSQL
	 * @param cl	ConfigLoader fuer zugangsdaten
	 */
	public Combiner(ArrayList<String[][]> mySQLList,
			ArrayList<String[][]> postGreSQLList, ConfigLoader cl) {
		// deklarieren der kombinierten Liste
		this.combinedList = new ArrayList<String[][]>();
		// Diese Schleife ueberprueft die Master/Slaves
		for (int i = 0; i < cl.getCountTable(); i++) {
			// wenn einer der beiden Tabellennamen in der config an der dritten Stelle steht wird MasterSlave angewendet
			//wenn erstes und drittes gleich
			if (cl.getTables()[i][2].equalsIgnoreCase(cl.getTables()[i][0]))
				combineMasterSlave(mySQLList.get(i), postGreSQLList.get(i), 0);
			//wenn zweites und drittes gleich
			else if (cl.getTables()[i][2]
					.equalsIgnoreCase(cl.getTables()[i][1]))
				combineMasterSlave(mySQLList.get(i), postGreSQLList.get(i), 1);
			// wenn eine Klammer am Anfag des ersten Eintrages erkannt wird muss
			// man MM aufrufen, herausfinden wo die PrimaryKeys sind und wo der
			// Timestamp
			else if (cl.getTables()[i][2].startsWith("(")) {
				combineMasters(
						mySQLList.get(i),
						postGreSQLList.get(i),
						searchForTimeStamp(
								cl.getTables()[i][0],
								cl.getTables()[i][2].split(",")[0].substring(1),
								cl.getVars()),
						searchForPrimary(cl.getTables()[i][0], cl.getVars()));
			} else
				System.err.println("Config-file fehlerhaft!");
		}
	}

	/**
	 * Diese Methode dient der MasterSlave/SlaveMaster synchronisation. Das beudeutet
	 * dass die komplette Master-Tabelle uebernommen wird
	 * 
	 * @param myTable MySQL Tabelleneintraege
	 * @param posTable	PostgreSQL Tabelleneintraege
	 * @param master	0/1		0 wenn MySQL Master ist,1 wenn POstgreSQL Master ist
	 */
	private void combineMasterSlave(String[][] myTable, String[][] posTable,
			int master) {
		if (master == 0) {
			combinedList.add(myTable);
		} else {
			combinedList.add(posTable);
		}
	}

	/**
	 * Diese Methode dient der Masermaster Synchronisation mit Timestamp
	 * 
	 * @param myTable MySQL Tabelleneintraege
	 * @param posTable	PostgreSQL Tabelleneintraege
	 * @param timeStampIndex	Index an dem sich der Timestamp befindet
	 * @param primaries		Indizes an denen sich die Primarykeys liegen
	 */
	private void combineMasters(String[][] myTable, String[][] posTable,
			int timeStampIndex, int[] primaries) {
		// In diesen array werden die richtigen Zeilen gespeichert
		String[][] newTable = new String[0][];
		// In dieser Schleife werden gleiche Primarykeys gesucht und anschliessend 
		// werden die Timestamps verglichen
		for (int i = 0; i < myTable.length; i++) {
			for (int j = 0; j < posTable.length; j++) {
				// Die Primarykeys in MySQL und PostgreSQL werden in je eine String var zusammenkopiert
				// um diese als ganzes vergleichen zu koennen
				String myprime = "";
				String posprime = "";
				for (int k = 0; k < primaries.length; k++) {
					myprime += myTable[i][primaries[k]];
					posprime += posTable[j][primaries[k]];
				}
				// ueberpruefung ob primarykeys ident sind
				if (myprime.equals(posprime)) {
					// nun wird unterschieden zwischen gleichen Timestamps
					// aelteren oder juengeren, jenachdem was herauskommt wird
					// entschieden welche gespeichert wird
					// wenn sie gleich sind
					if (Integer.parseInt(myTable[i][timeStampIndex]) == Integer
							.parseInt(posTable[j][timeStampIndex]))
						newTable = incrementStringStringArray(newTable,
								myTable[i]);
					// wenn Timestamp in PostgreSQL juenger ist
					else if (Integer.parseInt(myTable[i][timeStampIndex]) < Integer
							.parseInt(posTable[j][timeStampIndex]))
						newTable = incrementStringStringArray(newTable,
								posTable[j]);
					// wenn Timestamp in MySQL juenger ist
					else if (Integer.parseInt(myTable[i][timeStampIndex]) > Integer
							.parseInt(posTable[j][timeStampIndex]))
						newTable = incrementStringStringArray(newTable,
								myTable[i]);
				}
			}
		}
		// diese Schleife geht nocheinmal alle MySQL Eintraege durch, wenn die
		// Primarykey eintraege von denen noch nicht vorhanden sind wird dann
		// eine neue Zeile in das Array eingefuegt
		for (int i = 0; i < myTable.length; i++) {
			if (!searchForStringArray(myTable[i], newTable, primaries)) {
				newTable = incrementStringStringArray(newTable, myTable[i]);
				System.out.println("ich schreibe:" + myTable[i][0]);
			}
		}
		// diese Schleife geht nocheinmal alle PostgreSQL Eintraege durch, wenn
		// die Primarykey eintraege von denen noch nicht vorhanden sind wird dann
		// eine neue Zeile in das Array eingefuegt
		for (int i = 0; i < posTable.length; i++) {
			if (!searchForStringArray(posTable[i], newTable, primaries)) {
				newTable = incrementStringStringArray(newTable, posTable[i]);
				System.out.println("ich schreibe:" + posTable[i][0]);
			}
		}
		// am Ende der Methode wird dann noch das String[][] Array in die
		// ArrayList combinedList hinzugefuegt
		combinedList.add(newTable);
	}

	/**
	 * Diese Methode erhoeht ein 2dstring Array um einen Eintrag
	 * 
	 * @param array vorhandener array
	 * @param text das mit dem erhoeht wird
	 * @return neue Array
	 */
	private String[][] incrementStringStringArray(String[][] array,String[] text) {
		String[][] tempArray = new String[array.length + 1][];
		for (int i = 0; i < array.length; i++) {
			tempArray[i] = array[i];
		}
		tempArray[tempArray.length - 1] = text;
		array = tempArray;
		return array;
	}

	/**
	 * Diese Methode sucht in einem 2d Array nach einem eintrag und wenn dieser 
	 * gefunden wird, wird true zurueckgegeben
	 * 
	 * @param object
	 *            das Array nach dem gesucht wird
	 * @param searchPool
	 *            das 2d Array in dem nach object gesucht wird
	 * @param primaries
	 * @return true wenn es gefunden wurde
	 */
	private boolean searchForStringArray(String[] object,String[][] searchPool, int[] primaries) {
		boolean found = false;
		for (int i = 0; i < searchPool.length && !found; i++) {
			found = compareStringArray(object, searchPool[i], primaries);
		}
		return found;
	}

	/**
	 * Diese Methode ist dazu da 2 String Arrays zu vergleichen und 
	 * wenn sich diese beiden gleichen wird true zurueckgegeben
	 * wenn sie nicht gleich sind wird false zurueckgegeben
	 * 
	 * @param s1 das erste String Array
	 * @param s2 das zweite String Array
	 * @param primaries
	 * @return ob sich die beiden gleichen
	 */
	private boolean compareStringArray(String[] s1, String[] s2, int[] primaries) {
		boolean equal = true;
		if (s1.length != s2.length)
			equal = false;
		String temp1 = "";
		String temp2 = "";
		for (int i = 0; i < primaries.length && equal; i++) {
			temp1 += s1[primaries[i]];
			temp2 += s2[primaries[i]];
		}
		if (!temp1.equals(temp2))
			equal = false;
		return equal;
	}

	/**
	 * sucht nach dem Index der Timestamps in der momentanen Tabelle
	 
	 * @param myTableName name der momentanen Tabelle
	 * @param mytime name des Attributs auf der mysql seite
	 * @param tableInfo
	 * @return -1 wenn er nicht gefunden wurde Index wenn er gefunden wurde
	 */
	private int searchForTimeStamp(String myTableName, String mytime,
			String[][] tableInfo) {
		// man benoetigt ein Minus weil uns immer nur die Eintraege aus der
		// aktuellen Tabelle interessieren und nicht die aus allen
		int minus = 0;
		for (int j = 0; j < tableInfo.length; j++) {
			if (tableInfo[j][0].equals(myTableName)
					&& tableInfo[j][1].equals(mytime))
				return j - minus;
			if (!tableInfo[j][0].equals(myTableName))
				//wenn es nicht zutrifft wird minus erhoeht
				minus++;
		}
		return -1;
	}

	/**
	 * Sucht in der TableInfoTabelle nach eintraegen mit + (Inidkator fuer Primarkey) 
	 * am ende und gibt die Indizes dieser zurueck
	 * 
	 * @param myTableName zu durchsuchender Tablet
	 * @param tableInfo   hier wird gesucht
	 * @return indizes der primary keys
	 */
	private int[] searchForPrimary(String myTableName, String[][] tableInfo) {
		int[] indexes = new int[0];
		// man benoetigt ein Minus weil uns immer nur die Eintraege aus der
		// aktuellen Tabelle interessieren und nicht die aus allen
		int minus = 0;
		for (int i = 0; i < tableInfo.length; i++) {
			if (tableInfo[i][0].equals(myTableName)
					&& tableInfo[i][4].contains("+")) {
				indexes = incrementIntArray(indexes, i - minus);
			} else
				minus++;
		}
		return indexes;
	}

	/**
	 * Diese Methode erhoeht ein int[] um einen neuen Eintrag
	 * 
	 * @param array altes Array
	 * @param neu wert um den man das Array erhoeht
	 * @return das neue groessere Array
	 */
	private int[] incrementIntArray(int[] array, int neu) {
		int[] tempArray = new int[array.length + 1];
		for (int i = 0; i < array.length; i++) {
			tempArray[i] = array[i];
		}
		tempArray[tempArray.length - 1] = neu;
		return tempArray;
	}

	/**
	 * Diese Methode gibt die combinedList zurueck
	 * 
	 * @return combinedList
	 */
	public ArrayList<String[][]> getCombinedList() {
		return combinedList;
	}
}

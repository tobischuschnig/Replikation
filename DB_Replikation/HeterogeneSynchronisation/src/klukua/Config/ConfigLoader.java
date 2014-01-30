package klukua.Config;

/**
 * Diese Klasse dient dem interpretieren der config Datei.
 * Sie speichert die Daten aus dem config File in Variablen um diese
 * leicht weiterleiten zu koennen.
 * 
 * Im Protokoll dieser Arbeit findet man eine Anleitung wie man das config File schreibt
 * 
 * @author Kuanlun Huang
 * @version 1.0
 */
public class ConfigLoader {
	// Inhalt der Config Datei
	private String[] inhalt;

	// anmeldeinfos fuer MySQL
	private String myAddress;
	private String myUser;
	private String myPassword;
	private String myDatabase;

	// Anmeldeinfos fuer PostgreSQL
	private String posAddress;
	private String posUser;
	private String posPassword;
	private String posDatabase;

	// anzahl der Tabellen
	private int countTable;

	// hier werden alle Tabellennamen in einem String Array gespeichert
	// zusatzlich befinden sich hier Informationen wie synchronisiert werden
	// Eintrag 0 ist immer der Name der Tabelle in mySQL dann in PostgreSQL, und
	// danach die Art der Synchronisation
	private String[][] tables;

	// in diesem Array findet man Informationen ueber die Variablen und deren
	// Datentypen in spezifischen Tabellen
	// Hier ist der 0 Eintrag in welcher Tabelle man sich in mysql befindet dann
	// der Name aus MySQL
	// dann der Tabellenname in PostgreSQL und der Name dort und zuletzt der
	// Datentyp
	private String[][] vars;

	/**
	 * Kontruktor welcher sich den Inhalt des configFiles ueber eine Methode 
	 * als String array geben laesst, welcher spaeter interpretiert wird.
	 * 
	 * @param String Pfad zum config File
	 */
	public ConfigLoader(String file) {
		GetConfig getConfig = new GetConfig(file);
		inhalt = getConfig.getResult();
		countTable = 0;
		tables = new String[0][3];
		vars = new String[0][5];
		interpret();
	}

	/**
	 * Diese Methode dient dem interpretierten des ausgelesenen inhalts der 
	 * config datei. Die Daten werden in variablen gespeichert um diese infos leichter 
	 * zu uebergeben.
	 */
	private void interpret() {
		// erste Zeile muss [mysql] sein
		if (!inhalt[0].equalsIgnoreCase("[MySQL]"))
			System.err.println("Config File hat fehlerhaften Inhalt");

		// nun muessen die Anmeldeinformationen fuer mysql stehen
		String[] splitLine = inhalt[1].split(" ");
		myAddress = splitLine[0];
		myUser = splitLine[1];
		myPassword = splitLine[2];
		myDatabase = splitLine[3];

		// erste Zeile muss [postgreSQL] sein
		if (!inhalt[2].equalsIgnoreCase("[PostgreSQL]"))
			System.err.println("Config File hat fehlerhaften Inhalt");

		// nun muessen die AnmeldeInformation fuer postgresql stehen
		splitLine = inhalt[3].split(" ");
		posAddress = splitLine[0];
		posUser = splitLine[1];
		posPassword = splitLine[2];
		posDatabase = splitLine[3];

		// von der Anzahl der Strichpunkte kann man auf die Anzahl der Tabellen schliessen
		for (int i = 4; i < inhalt.length; i++) {
			if (inhalt[i].equals(";"))
				countTable++;
		}

		// nachdem man jetzt weiss wie viele Tabellen existieren kann man diese
		// trennen und danach durchgehen
		// in dieser Variable werden die TeilArrays gepsiechert
		String[][] tableSplit = new String[0][];
		String[] teilTable = new String[0];
		for (int j = 4; j < inhalt.length; j++) {
			// wenn ein ; gefunden wird dann kann man das als eine Tabelle
			// ansehen
			if (inhalt[j].equals(";")) {
				tableSplit = incrementStringStringArray(tableSplit, teilTable);
				teilTable = new String[0];
				// damit der ; uebersprungen wird
				j++;
			}
			if (j < inhalt.length)
				// in diesem Array wird immer eine Tabelle zusammengebastelt
				teilTable = incrementStringArray(teilTable, inhalt[j]);
		}
		for (int i = 0; i < tableSplit.length; i++) {
			// hier befinden sich die Namen der Tabelle in den verschiedenen
			// Datenbanken und danach steht die sync art
			String[] namen = tableSplit[i][0].split(" ");
			String[] tableInfo = { namen[0], namen[1], namen[2] };
			tables = incrementStringStringArray(tables, tableInfo);

			for (int j = 1; j < tableSplit[i].length; j++) {
				String[] columns = tableSplit[i][j].split(" ");
				String[] varInfo = { tableInfo[0], columns[0], tableInfo[1],
						columns[1], columns[2] };
				vars = incrementStringStringArray(vars, varInfo);
			}
		}
	}

	/**
	 * Diese Methode erhoeht ein string Array um einen Eintrag
	 * 
	 * @param array Vorhandener Array
	 * @param text mit dem wird erhoeht
	 * @return den neuen,vergroesserten String
	 */
	public String[] incrementStringArray(String[] array, String text) {
		// erstellen eines um 1 groesseren array
		String[] tempArray = new String[array.length + 1];
		// uebertragen des inhalts auf den neuen array
		for (int i = 0; i < array.length; i++) {
			tempArray[i] = array[i];
		}
		// neue Zeile wird hinzugefuegt
		tempArray[tempArray.length - 1] = text;
		// alter array wird mit dem neuen ueberschrieben
		array = tempArray;
		return array;
	}

	/**
	 * Diese Methode erhoeht ein 2dstring Array um einen Eintrag
	 * 
	 * @param array Vorhandener Array
	 * @param text mit dem wird erhoeht
	 * @return den neuen,vergroesserten String
	 */
	public String[][] incrementStringStringArray(String[][] array, String[] text) {
		// erstellen eines um [1][0] groesseren array
		String[][] tempArray = new String[array.length + 1][];
		// uebertragen der daten
		for (int i = 0; i < array.length; i++) {
			tempArray[i] = array[i];
		}
		// neue Zeile hinzufuegen
		tempArray[tempArray.length-1] = text;
		// ueberschreiben mit neu auf alt
		array = tempArray;
		return array;
	}

	/**
	 * @return the myAddress
	 */
	public String getMyAddress() {
		return myAddress;
	}

	/**
	 * @return the myUser
	 */
	public String getMyUser() {
		return myUser;
	}

	/**
	 * @return the myPassword
	 */
	public String getMyPassword() {
		return myPassword;
	}

	/**
	 * @return the myDatabase
	 */
	public String getMyDatabase() {
		return myDatabase;
	}

	/**
	 * @return the posAddress
	 */
	public String getPosAddress() {
		return posAddress;
	}

	/**
	 * @return the posUser
	 */
	public String getPosUser() {
		return posUser;
	}

	/**
	 * @return the posPassword
	 */
	public String getPosPassword() {
		return posPassword;
	}

	/**
	 * @return the posDatabase
	 */
	public String getPosDatabase() {
		return posDatabase;
	}

	/**
	 * @return the countTable
	 */
	public int getCountTable() {
		return countTable;
	}

	/**
	 * @return the tables
	 */
	public String[][] getTables() {
		return tables;
	}

	/**
	 * @return the vars
	 */
	public String[][] getVars() {
		return vars;
	}
}

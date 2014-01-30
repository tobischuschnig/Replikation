package klukua.Writer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import klukua.Config.ConfigLoader;

/**
 * Diese Klasse kann sowohl fuer postgesql, als auch fuer mysql konfiguriert<br>
 * werden und loescht zuerst alle Daten aus allen durch den ConfigLoader<br>
 * spezifizierten Tabellen. Anschliessend werden alle in der ArrayList fullData<br>
 * beschriebenen Daten in die leeren Tabellen und wird mit dem Reader insgesamt<br>
 * zum Ausfuerhen der eigentlichen Synchronisation benutzt.
 * 
 * @author Klune Alexander,Kuanlun Huang
 * @version 1.0
 */
public class Writer {
	
	private ConfigLoader cl;

	// Die Treiber fuer die Verbindung zu postgresql und mysql
	static final String JDBC_DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	static final String JDBC_DRIVER_POSTGRESQL = "org.postgresql.Driver";

	// ob verbunden mit der DB
	private boolean connectedToDatabase = false;

	private Connection connection;
	private Statement statement;

	// Hierin sind die kompletten Daten gespeichert, die in alle Tabellen
	// gespeichert werden sollen
	// hier sind alle Daten gespeichert, die in alle Tabellen gespeichert gehoeren
	private ArrayList<String[][]> fullData;

	/**
	 * Konstruktor, der die Parameter speichert und je nach dbms
	 * die Verbindung aufbaut
	 * 
	 * @param cl	ConfigLoader der die Daten enthaelt
	 * @param dbms	spezifiziertes DBMS.  "postgresql" oder "mysql"
	 * @param fullData	die zu schreibenden Daten
	 * @throws ClassNotFoundException	wenn die Treiberklasse nicht gefunden werden kann
	 * @throws SQLException	wenn die DB Verbindung nicht aufgebaut werden kann
	 */
	public Writer(ConfigLoader cl, String dbms, ArrayList<String[][]> fullData)
			throws ClassNotFoundException, SQLException {
		// Speichern des Paramters, um Daten aus dem Config-File buntzen zu
		// koennen
		this.cl = cl;

		this.fullData = fullData;

		// verbindung herstellen
		if (dbms.equalsIgnoreCase("mysql"))
			writeData(JDBC_DRIVER_MYSQL, "jdbc:mysql://" + cl.getMyAddress()
					+ "/" + cl.getMyDatabase(), cl.getMyUser(),
					cl.getMyPassword(), 0);
		else if (dbms.equalsIgnoreCase("postgresql"))
			writeData(
					JDBC_DRIVER_POSTGRESQL,
					"jdbc:postgresql://" + cl.getPosAddress() + "/"
							+ cl.getPosDatabase(), cl.getPosUser(),
					cl.getPosPassword(), 1);
	}

	/**
	 * Diese Methode schreibt alle uebergebenen Daten(fullData) in eine Datenbankwird.
	 * Ausserdem wird vor dem schreiben die tabelle geleert und danach alle neuen Daten eingefuegt.
	 * 
	 * @param driver	 Treiberklassenbezeichnung DBMS
	 * @param url	url zur Datenbank
	 * @param username	Usernamen auf der Datenbank
	 * @param password	Passwort zu Username
	 * @param postgresqlIncrementor		fuer postgreDB 1, fuer MySQL 0, weil im configfile der index um 1 groesser ist als in psql
	 * @throws ClassNotFoundException	wenn Treiberklasse nicht gefunden wurde
	 * @throws SQLException		wenn die Db verbindung nicht aufgebaut werden konnte
	 */
	private void writeData(String driver, String url, String username,
			String password, int postgresqlIncrementor)
			throws ClassNotFoundException, SQLException {
		// Gibt die Klasse des Treibers zurueck
		Class.forName(driver);

		// Erstellen der Datenbankverbindung
		connection = DriverManager.getConnection(url, username, password);

		statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// ob man zur Db verbunden ist
		connectedToDatabase = true;

		// Itteriert durch jede Tabelle und selected deren relevante Inhalte
		for (int i = 0; i < cl.getCountTable(); i++) {
			// Delete
			setUpdateQuery("DELETE FROM "
					+ cl.getTables()[i][0 + postgresqlIncrementor]);

			// Speichert mit Beistrich getrennt alle Spalten die gelesen werden
			// sollen
			String columnsToBeInserted = "";
			// In dieser Liste werden bei jedem Wert noch jeweils die richtigen
			// Datentypen mit gespeichert.
			ArrayList<String> datatype = new ArrayList<String>();
			for (int j = 0; j < cl.getVars().length; j++) {
				if (cl.getVars()[j][0 + postgresqlIncrementor * 2].equals(cl.getTables()[i][0 + postgresqlIncrementor])
						&& !cl.getVars()[j][1].equalsIgnoreCase("-")
						&& !cl.getVars()[j][3].equalsIgnoreCase("-")) {
					datatype.add(cl.getVars()[j][4]);
					columnsToBeInserted += cl.getVars()[j][1 + postgresqlIncrementor * 2];
					columnsToBeInserted += ",";
				}
			}
			// entfernen des letzten beistrichs
			columnsToBeInserted = columnsToBeInserted.substring(0,
					columnsToBeInserted.length() - 1);

			// In dieser Schleife werden alle Inserts einer Tabelle
			// durchgefuehrt
			for (int j = 0; j < fullData.get(i).length; j++) {
				String values = "";
				// Diese Schleife sorgt dafuer, alle Werte, die zu Inserten sind
				// mit dem richtigen Datentyp zu assoziieren
				// und somit die richtigen Schreibweise z.B. beoi string mit ''
				// zu waehlen
				for (int j2 = 0; j2 < fullData.get(i)[j].length; j2++) {
					//System.out.println(fullData.get(i)[j][j2]);
					if ((fullData.get(i)[j][j2]) == null)
						values += fullData.get(i)[j][j2] + ",";
					else if (datatype.get(j2).startsWith("number")) {
						values += fullData.get(i)[j][j2] + ",";
					} else if (datatype.get(j2).startsWith("string")) {
						values += "'" + fullData.get(i)[j][j2] + "',";
					}
				}
				// entfernen des letzten Beistrichs
				values = values.substring(0, values.length() - 1);

				// Insert durchfuehren
				System.out.println("INSERT INTO "
						+ cl.getTables()[i][0 + postgresqlIncrementor] + " ("
						+ columnsToBeInserted + ") VALUES(" + values + ")");
				setUpdateQuery("INSERT INTO "
						+ cl.getTables()[i][0 + postgresqlIncrementor] + " ("
						+ columnsToBeInserted + ") VALUES(" + values + ")");

			}
		}
		statement.close();
	}

	/**
	 * Diese Methode fuehrt eine DB- veraendernde Query durch
	 * 
	 * @param updateQuery 	auszufuehrende Query
	 * @throws SQLException		wenn SQL Fehler
	 * @throws IllegalStateException	wenn DB nicht verbunden ist
	 */
	public void setUpdateQuery(String updateQuery) throws SQLException,
			IllegalStateException {
		if (!connectedToDatabase)
			throw new IllegalStateException("Not Connected to Database");
		int affectedRows = statement.executeUpdate(updateQuery);
		System.out.println(affectedRows + " Row/s are affected!");
	}
}
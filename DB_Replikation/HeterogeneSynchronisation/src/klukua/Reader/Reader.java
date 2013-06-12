package klukua.Reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import klukua.Config.ConfigLoader;

/**
 * Diese Klasse kann sowohl fuer postgesql, als auch fuer mysql konfiguriert
 * werden und liest alle in dem uebergebenen ConfigLoader durch die
 * Konfigurationsdatei spezifizierten Spalten und Tabellen aus und speichert
 * diese in eine ArrayList aus String[][], wobei jedes String[][] eine Tabelle
 * repraesentiert.
 * 
 * @author Klune Alexander
 * @version 1.0
 * 
 */
public class Reader {

	private ConfigLoader cl;

	// Die Treiber fuer die Verbindung zu postgresql und mysql
	static final String JDBC_DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	static final String JDBC_DRIVER_POSTGRESQL = "org.postgresql.Driver";

	// bestehende verbindung zur DB
	private boolean connectedToDatabase = false;

	// Verbindung und Statement f�r die DB-Verbindung
	private Connection connection;
	private Statement statement;

	// beinhaltet alle ausgelesenen Daten
	private ArrayList<String[][]> fullData;
	
	private String dbms;

	/**
	 * Konstruktor, der die Parameter speichert und je nach spezifiziertem dbms
	 * die Lesenoperation mit den richtigen Daten startet.
	 * 
	 * @param cl
	 *            ConfigLoader der die Daten enthaelt
	 * @param dbms
	 *            spezifiziertes DBMS. Moeglich sind "postgresql" und "mysql"
	 * @throws ClassNotFoundException
	 *             Exception wird ausgeloest, wenn die Treiberklasse nicht
	 *             gefunden werden kann
	 * @throws SQLException
	 *             wird ausgeloest, wenn die DB-Verbindung nicht ordnungsgemaess
	 *             aufgebaut werden kann
	 */
	public Reader(ConfigLoader cl, String dbms) throws ClassNotFoundException,
			SQLException {
		// ConfigLoader speichern um auf Daten zugreifen zu koennen
		this.cl = cl;
		this.dbms = dbms;
		this.fullData = new ArrayList<String[][]>();

		// anmelden an der db je nachdem welches dbms verwendet wird
		if (dbms.equalsIgnoreCase("mysql"))
			readData(JDBC_DRIVER_MYSQL, "jdbc:mysql://" + cl.getMyAddress()
					+ "/" + cl.getMyDatabase(), cl.getMyUser(),
					cl.getMyPassword(), 0);
		else if (dbms.equalsIgnoreCase("postgresql"))
			readData(
					JDBC_DRIVER_POSTGRESQL,
					"jdbc:postgresql://" + cl.getPosAddress() + "/"
							+ cl.getPosDatabase(), cl.getPosUser(),
					cl.getPosPassword(), 1);
		else if(dbms.equalsIgnoreCase("mysql2"))
			readData(JDBC_DRIVER_MYSQL, "jdbc:mysql://" + cl.getPosAddress()
					+ "/" + cl.getPosDatabase(), cl.getPosUser(),
					cl.getPosPassword(), 0);
	}

	/**
	 * Diese Methode liest alle uebergebenen Daten aus der DB aus 
	 * und speichert diese in fullData
	 *  
	 * @param driver
	 *            Treiberklassenbezeichnung fuer den Treiber des DBMS
	 * @param url
	 *            url zur DB
	 * @param username
	 *            Usernamen auf der DB
	 * @param password
	 *            Passwort zum Usernamen auf der DB
	 * @param postgresqlIncrementor
	 *            fuer postgreDB 1, fuer MySQL 0, weil im configfile der index um 1 groesser ist als in psql
	 * @throws ClassNotFoundException
	 *             wenn Treiberklasse nicht gefunden wurde
	 * @throws SQLException
	 *            wenn verbindung nicht aufgebaut werden konnte
	 */
	private void readData(String driver, String url, String username,
			String password, int postgresqlIncrementor)
			throws ClassNotFoundException, SQLException {
		// Gibt die Klasse des Treibers zurueck
		Class.forName(driver);

		// DB verbindung herstellen
		connection = DriverManager.getConnection(url, username, password);

		statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		// bei fehlerfreien aufbau der verbindung wird der wert auf true gesetzt
		connectedToDatabase = true;

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String fn = "./logs/reader/" + sdf.format(date) + "_" + dbms + "_LOG.txt";
		PrintWriter out = null;
		try {
			out = new PrintWriter(fn);
		} catch (FileNotFoundException e) {
			System.err.println("Kein File fuer logging gefunden!");
		}
		System.out.println(fn);

		new File(fn);
		// Itteriert durch jede Tabelle und selected deren relevante Inhalte
		for (int i = 0; i < cl.getCountTable(); i++) {
			// Speichert mit Beistrich getrennt alle Spalten die gelesen werden
			// sollen
			String columnsToBeSelected = "";
			for (int j = 0; j < cl.getVars().length; j++) {
				if (cl.getVars()[j][0 + postgresqlIncrementor * 2].equals(cl.getTables()[i][0 + postgresqlIncrementor])
						&& !cl.getVars()[j][1].equalsIgnoreCase("-")
						&& !cl.getVars()[j][3].equalsIgnoreCase("-")) {
					columnsToBeSelected += cl.getVars()[j][1 + postgresqlIncrementor * 2];
					columnsToBeSelected += ",";
				}
			}
			// beim letzten eintrag soll der letzte beistrich entfernt werden
			columnsToBeSelected = columnsToBeSelected.substring(0,
					columnsToBeSelected.length() - 1);
			// Ausfuehren der entstehenden Query, die die Inhalte richtigen
			// Spalten jeder Tabelle einzeln
			// bei den Schleifendurchlaufen ermitteln soll
			out.println("SELECT "
					+ columnsToBeSelected + " FROM "
					+ cl.getTables()[i][0 + postgresqlIncrementor]);
			
			fullData.add(convertResultSetToArray(setQuery("SELECT "
					+ columnsToBeSelected + " FROM "
					+ cl.getTables()[i][0 + postgresqlIncrementor])));
		}
		statement.close();
		connection.close();
		out.close();
	}

	/**
	 * Diese Methode dient dem ausfuehren einer Query auf eine DB
	 * 
	 * @param query 	die auszufuehrende Query
	 * @throws SQLException		Wird bei SQL Fehlern geworfen
	 * @throws IllegalStateException	Wird geworfen wenn man nicht auf die DB verbunden ist
	 */
	private ResultSet setQuery(String query) throws SQLException,
			IllegalStateException {

		if (!connectedToDatabase)
			throw new IllegalStateException("Nicht mit der Datenbank verbunden");

		// Ausfuehren der Query
		ResultSet resultSet = statement.executeQuery(query);

		// Setzen des Cursors auf das letzte Elemement
		// resultSet.last();

		return resultSet;
	}

	/**
	 * @return  fullData
	 */
	public ArrayList<String[][]> getFullData() {
		return fullData;
	}

	/**
	 * Diese Methode dient der konvertierung eines ResultSet zu einem String[][] Array
	 * um es in die uebergeordnete ArrayList speichern zu koennen
	 * 
	 * @param rs	zu konvertierendes ResultSet
	 * @return ein Array fuer das Gesamtergebnis
	 * @throws SQLException
	 */
	public String[][] convertResultSetToArray(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		//anzahl der zeilen fuer array
		int columnCount = rsmd.getColumnCount();
		List rows = new ArrayList();
		while (rs.next()) {
			String[] row = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				row[i - 1] = rs.getString(i);
			}
			rows.add(row);
		}

		rs.close();

		return (String[][]) rows.toArray(new String[rows.size()][columnCount]);
	}
}
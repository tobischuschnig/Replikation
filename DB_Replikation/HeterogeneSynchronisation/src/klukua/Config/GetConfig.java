package klukua.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * Diese Klasse dient dem auslesen des Files welches die config Anweisungen
 * vom Benutzer beinhaltet. Danach wird die config an einen Interpreter uebergeben.
 * 
 * @author Kuanlun Huang
 * @version 1.0
 */
public class GetConfig {
	// File in das das config File gespeichert wird
	private File settingsFile;
	// Jeder eintrag in diesem array beinhaltet eine Zeile aus dem config File
	private String[] lines;

	/**
	 * Konstruktor welcher dem File einen Namen gibt und kontrolliert ob ein solches
	 * config File ueberhaupt existiert. Danach wird das auslesen gestartet
	 * 
	 * @param String Pfad zur config datei
	 */
	public GetConfig(String file) {
		if(file==null||file.isEmpty())
			file = "config";
		// Das config File liegt immer neben der .jar Datei 
		// deswegen kann hier hard gecoded werden
		settingsFile = new File(file);
		// array wird mit 0 initialisiert und muss erhoet werden wenn eine zusaetzliche
		// Zeile eingelesen wird.
		lines = new String[0];
		// ob File existiert
		if (!settingsFile.isFile())
			System.err
					.println("Es existiert kein config- File!");
		startRead();
	}

	/**
	 * Diese Methode liest das File Zeile fuer Zeile aus und speichert jede
	 * in einen eigenen array index
	 */
	private void startRead() {
		// um File auszulesen
		LineNumberReader r = null;
		try {
			r = new LineNumberReader(new FileReader(settingsFile));
			// schleife in der ausgelesen wird. 
			// abbruch wenn keine Zeilen mehr vorhanden sind
			while (true) {
				// naechste Zeile
				String zwischenSpeicher = r.readLine();
				// abbruch der schleife
				if (zwischenSpeicher == null)
					break;
				else {
					incrementArray(zwischenSpeicher);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("File wurde nicht gefunden");
		} catch (IOException e) {
			System.err
					.println("Beim Lesevorgang ist ein Fehler aufgetreten");
		} finally {
			// natuerlich muss man am Ende der Methode auch noch die Streams
			// schliessen
			try {
				r.close();
			} catch (IOException e) {
				System.err.println("Der Stream zum auslesen der Datei konnte nicht geschlossen werden!");
			}
		}
	}

	/**
	 * Diese Methode speichert einen String in einen leeren index des arrays lines
	 * somit wird der array vergoessert.
	 * 
	 * @param String	Eintrag welcher eingefuegt werden soll
	 */
	public void incrementArray(String newText) {
		// erstellen eines um 1 groesseren array
		String[] tempArray = new String[lines.length + 1];
		// Werte des alten arrays in den neuen speichern 
		for (int i = 0; i < lines.length; i++) {
			tempArray[i] = lines[i];
		}
		// neuer eintrag wird eingfuegt
		tempArray[tempArray.length-1] = newText;
		// lines wird mit dem Zwischenspeicher ueberschrieben
		lines = tempArray;
	}

	/**
	 * Diese Methode gibt den gesammten Inhalt in einem String array zurueck
	 * 
	 * @return alle gelesenen Lines
	 */
	public String[] getResult() {
		return lines;
	}
}

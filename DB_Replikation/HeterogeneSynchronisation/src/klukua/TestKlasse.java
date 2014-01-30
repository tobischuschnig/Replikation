package klukua;

import klukua.Combiner.Combiner;
import klukua.Config.ConfigLoader;
import klukua.Reader.Reader;
import klukua.Writer.Writer;

/**
 * Die Klasse "belebt" den Programmablauf.
 * 
 * @author Klune Alexander
 * @version 1.0
 */
public class TestKlasse {
	public static void main(String[] args) {
		try{
			// Testen des Interpreters
			ConfigLoader cl = new ConfigLoader(args[0]);
	
			// Starten der Reader fuer MySQL und PostgreSQL
			Reader readerMySQL = new Reader(cl, "mysql");
			Reader readerPostGreSQL = new Reader(cl, "postgresql");
	
			// Aufrufen der Klasse die fuer das Kombinieren der beiden Datenbanken
			// zustaendig ist
			Combiner combiner = new Combiner(readerMySQL.getFullData(),
					readerPostGreSQL.getFullData(), cl);
			// Starten der beiden Klassen die in beide Datenbanken alle Daten
			// schreiben
			Writer myWriter = new Writer(cl, "mysql", combiner.getCombinedList());
			Writer posWriter = new Writer(cl, "postgresql",
					combiner.getCombinedList());
		} catch(Exception e){
			System.err.println("Es ist ein Fehler aufgetreten! "+e.getMessage());
		}
	}
}
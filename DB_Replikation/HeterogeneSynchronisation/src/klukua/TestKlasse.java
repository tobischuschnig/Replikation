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
		if(args.length != 2){
			System.err.println("Fehler bei Programmparameter\nAufruf: .jar PfadZuConfig anzWiederholungen");
			System.exit(0);
		}
			
		int wh = 1;
		try{
		wh = Integer.parseInt(args[1]);
		}catch(NumberFormatException e){}
		for(int i=0;i < wh;i++){
		try{
			// Testen des Interpreters
			ConfigLoader cl = new ConfigLoader(args[0]);
	
			// Starten der Reader fuer MySQL und PostgreSQL
			Reader readerMySQL = new Reader(cl, "mysql");
			Reader readerPostGreSQL = new Reader(cl, "mysql2");
	
			// Aufrufen der Klasse die fuer das Kombinieren der beiden Datenbanken
			// zustaendig ist
			Combiner combiner = new Combiner(readerMySQL.getFullData(),
					readerPostGreSQL.getFullData(), cl);
			// Starten der beiden Klassen die in beide Datenbanken alle Daten
			// schreiben
			Writer myWriter = new Writer(cl, "mysql", combiner.getCombinedList());
			Writer posWriter = new Writer(cl, "mysql2", combiner.getCombinedList());
		} catch(Exception e){
			System.err.println("Es ist ein Fehler aufgetreten! "+e.getMessage());
		}
		try {
			//Synchronisation startet in ca. 30 sek von neu 
			Thread.sleep(30000);
			System.out.println("restart replication");
		} catch (InterruptedException e) {
			System.err.println("Fehler beim warten aufgetreten!");
		}
		}
	}
}
package files;


import java.io.IOException;
import java.net.URI;import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

import java.nio.file.*;
import java.nio.file.WatchService.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.FileVorlage;

import util.Utill;
import receiversender.Sender;

/**
 * Dies ist ein Watchdog der auf Aenderungen schaut
 * In diesem Fall wird auf Erstellungen
 * Der Code des Watchdoges ist im wesentlichen von
 * http//jaxenter.de/artikel/javaniofile-Zeitgemaesses-Arbeiten-mit-Dateien-166848
 * @author Tobias Schuschnig
 */
public class WatchdogCreate implements Runnable {
	private String ordner;
	private Server server;
	private Client client;
	private Sender sender;

	
	/**
	 * Erzeugt einen neuen Watchdog
	 * @param ordner der Ordner der ueberwacht werden soll
	 * @param sender der Sender auf dem das File rausgesendet wird
	 */
	public WatchdogCreate(String ordner,Sender sender) {
		this.ordner = ordner;
		this.sender = sender;
	}
	
	/**
	 * Die run Methode von Thread ueberprueft laufend die aenderungen des ordners
	 */
	public void run() {
		try {
			Path path = Paths.get(URI.create("file:"+ordner));
			WatchService watchService = path.getFileSystem().newWatchService();
			WatchKey watchKey;
			watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
			
			while(true) {
				for(WatchEvent<?> event : watchKey.pollEvents()) {
					if(!(event.context()).toString().contains(".DS_Store")) {
					//Path newPath = (Path)event.context();
					
					Path newPath =  Paths.get(URI.create("file:"+ordner+"/"+event.context()));
					FileVorlage file = Utill.packing(newPath,false);
					sender.sendMessage(file);
					Logger.getLogger(WatchdogCreate.class.getName()).log(Level.SEVERE, null, "New file: "+ newPath);
						//Filesynchro.senderneu();
						//server.created(newPath);
					System.out.println("New file: " + newPath);
					//break;
					} 
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.err.println("Fail by reading the File. Please check the File and try again.");
			Logger.getLogger(WatchdogCreate.class.getName()).log(Level.SEVERE, null, e);
		} catch (Exception e) {
			System.err.println("Invalid File! Please check the Path and the File and then Restart the Service." +
					"\nThe Path may not consists spaces. ");
			Logger.getLogger(WatchdogCreate.class.getName()).log(Level.SEVERE, null, e);
			System.exit(1);
		}
	}

}

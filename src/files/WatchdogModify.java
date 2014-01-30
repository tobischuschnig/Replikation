package files;


import java.io.IOException;
import java.net.URI;import java.nio.file.Path;
import java.nio.file.WatchEvent;

import java.nio.file.*;

public class WatchdogModify implements Runnable {
	private String ordner;
	private Server server;
	private Client client;
	
	
	public WatchdogModify(String ordner) {
		this.ordner = ordner;
	}
	
	
	public void run() {
		try {
			Path path = Paths.get(URI.create("file:"+ordner));
			WatchService watchService = path.getFileSystem().newWatchService();
			WatchKey watchKey;
			watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
			
			while(true) {
				for(WatchEvent<?> event : watchKey.pollEvents()) {
					if(!event.context().equals(".DS_Store")) {
						Path newPath = (Path)event.context();
						
						System.out.println("Modify file: " + newPath);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

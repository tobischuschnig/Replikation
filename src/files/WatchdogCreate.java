package files;


import java.io.IOException;
import java.net.URI;import java.nio.file.Path;
import java.nio.file.WatchEvent;

import java.nio.file.*;
import java.nio.file.WatchService.*;
import java.util.List;

public class WatchdogCreate implements Runnable {
private String ordner;
	
	public WatchdogCreate(String ordner) {
		this.ordner = ordner;
	}
	
	
	public void run() {
		try {
			Path path = Paths.get(URI.create("file:"+ordner));
			WatchService watchService = path.getFileSystem().newWatchService();
			WatchKey watchKey;
			watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
			
			while(true) {
				for(WatchEvent<?> event : watchKey.pollEvents()) {
					Path newPath = (Path)event.context();
					System.out.println("New file: " + newPath);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package files;


import java.io.IOException;
import java.net.URI;import java.nio.file.Path;
import java.nio.file.WatchEvent;

import java.nio.file.*;

import receiversender.Sender;

import util.Utill;

public class WatchdogModify implements Runnable {
	private String ordner;
	private Server server;
	private Client client;
	private Sender sender;
	
	
	public WatchdogModify(String ordner, Sender sender) {
		this.ordner = ordner;
		this.sender = sender;
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
						//sender.sendMessage(Utill.packing(newPath));
						//server.created(newPath);
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

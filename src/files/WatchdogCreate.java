package files;


import java.io.IOException;
import java.net.URI;import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

import java.nio.file.*;
import java.nio.file.WatchService.*;
import java.util.List;

import model.FileVorlage;

import util.Utill;
import receiversender.Sender;


public class WatchdogCreate implements Runnable {
	private String ordner;
	private Server server;
	private Client client;
	private Sender sender;

	public WatchdogCreate(String ordner,Sender sender) {
		this.ordner = ordner;
		this.sender = sender;
	}
	
	
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
					
						//Filesynchro.senderneu();
						//server.created(newPath);
					System.out.println("New file: " + newPath);
					//break;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

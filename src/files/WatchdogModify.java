package files;


import java.io.IOException;
import java.net.URI;import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;

import java.nio.file.*;

import model.FileVorlage;

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
					if(!(event.context()).toString().contains(".DS_Store")) {
						Path newPath =  Paths.get(URI.create("file:"+ordner+"/"+event.context()));
						FileVorlage file = Utill.packing(newPath,false);
						sender.sendMessage(file);
						//sender.sendMessage(Utill.packing(newPath,));
						//server.created(newPath);
						System.out.println("Modify file: " + newPath);
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

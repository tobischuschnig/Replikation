package files;

import java.nio.file.Path;
import java.util.ArrayList;

public class Server {
	private ArrayList<String> ownfiles; //alle Datenelemente die man selbst besitzt 
	private ArrayList<String> files; //alle Datenelemente die ein anderer besitzt 
	private int port;
	
	public Server(ArrayList<String> ownfiles, int port) {
		this.ownfiles = ownfiles;
		this.port = port;
		
	}
	
	public boolean created(Path newPath) {
		send(newPath);
		ownfiles.add(newPath.toString());
		return false;
	}
	
	public boolean send(Path newPath) {
		return false;
	}
}

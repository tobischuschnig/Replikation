package files;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import receiversender.Receiver;
import receiversender.Sender;

public class FilesynchroReceiver {
	private static int portServer;
	private static String ip;
	
	public static void main (String args[]) {
		///Users/tobi/Desktop/Neuer //Test verzeichnis
		try {
			int portClient = Integer.parseInt(args[1]); //receiver
//			Server server = new Server(files,args[0],portServer);
//			Client client = new Client(files,args[0],portServer);
			
			
			Path path = Paths.get(URI.create("file:"+args[0]));
			//    System.out.println(path.toString());
			
			
			if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
//			    System.out.println(path.toString());
				Thread thread1 = new Thread(new Receiver(path,portClient));
				thread1.start();
				System.out.println("Server is now receiving!");
			}
			else {
				System.err.println("Invalid Input! \nThe Path musst exist!");
				System.exit(1);
			}
		}catch(Exception e) {
			System.err.println("Invalid Input! \nPlease Enter Path like this exampel: path, Client Port" +
					"Ports musst be numeric. Path like this: /Users/tobi/Desktop/Neuer");
			//e.printStackTrace();
			Logger.getLogger(FilesynchroReceiver.class.getName()).log(Level.SEVERE, null, e);
			System.exit(1);
		}
	}
	public static ArrayList<String> fileList(String directory) {
        ArrayList<String> fileNames = new ArrayList();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
                System.out.println(path.toString());
            }
        } catch (IOException ex) {Logger.getLogger(FilesynchroReceiver.class.getName()).log(Level.SEVERE, null, ex);
}
        return fileNames;
    }
	
	public static void senderneu() {
		Sender sender = new Sender(ip,portServer);
		Thread thread2 = new Thread(sender);
		thread2.start();
		System.out.println("Server is now send able!");
	} 
}

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

import receiversender.Receiver;
import receiversender.Sender;

public class Filesynchro {
	private static int portServer;
	private static String ip;
	
	public static void main (String args[]) {
		///Users/tobi/Desktop/Neuer //Test verzeichnis
		try {
			portServer = Integer.parseInt(args[1]); //sender
			int portClient = Integer.parseInt(args[2]); //receiver
//			Server server = new Server(files,args[0],portServer);
//			Client client = new Client(files,args[0],portServer);
			
			
			Path path = Paths.get(URI.create("file:"+args[0]));
			//    System.out.println(path.toString());
//
//			Thread thread1 = new Thread(new Receiver(path,portClient));
//			thread1.start();
//			System.out.println("Server is now receiving!");
			
			//Sender sender = new Sender(portServer);
			Sender sender = new Sender(args[3],portServer);
			Thread thread2 = new Thread(sender);
			thread2.start();
			System.out.println("Server is now send able!");
			
			if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
				ArrayList<String> files = Filesynchro.fileList(args[0]);
//				int portServer = Integer.parseInt(args[1]);
//				int portClient = Integer.parseInt(args[2]);
				//Server server = new Server(files,args[0],portServer);
				//Client client = new Client(files,args[0],portServer);
				Thread wert1 = new Thread(new WatchdogCreate(args[0],sender));
				Thread wert2 = new Thread(new WatchdogDelete(args[0],sender));
				Thread wert3 = new Thread(new WatchdogModify(args[0],sender));
				wert1.start();
				wert2.start();
				wert3.start();	
			}
			else {
				System.err.println("Invalid Input! \nThe Path musst exist!");
				System.exit(1);
			}
			ip = args[3];
		}catch(Exception e) {
			System.err.println("Invalid Input! \nPlease Enter Path like this exampel: path, Server Port, Client Port, IP Server" +
					"Ports musst be numeric. Path like this: /Users/tobi/Desktop/Neuer");
			e.printStackTrace();
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
        } catch (IOException ex) {}
        return fileNames;
    }
	
	public static void senderneu() {
		Sender sender = new Sender(ip,portServer);
		Thread thread2 = new Thread(sender);
		thread2.start();
		System.out.println("Server is now send able!");
	}
}

package files;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Filesynchro {
	public static void main (String args[]) {
		try {
			Path path = Paths.get(URI.create("file:"+args[0]));
			if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
				Thread wert1 = new Thread(new WatchdogCreate(args[0]));
				Thread wert2 = new Thread(new WatchdogDelete(args[0]));
				Thread wert3 = new Thread(new WatchdogModify(args[0]));
				wert1.start();
				wert2.start();
				wert3.start();	
			}
			else {
				System.err.println("Invalid Input! \nThe Path musst exist!");
				System.exit(1);
			}
		}catch(Exception e) {
			System.err.println("Invalid Input! \nPlease Enter Path like this exampel: /Users/tobi/Desktop/Neuer");
			//e.printStackTrace();
			System.exit(1);
		}
	}
}

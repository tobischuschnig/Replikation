package receiversender;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import model.FileVorlage;


public class FileReceiver {
	public static void main(String[] argv) throws Exception {
	    Socket sock = new Socket("localhost", 54321);
//	    byte[] mybytearray = new byte[1024];
	    ObjectInputStream is = new ObjectInputStream(sock.getInputStream());
//	    ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
	    FileVorlage fc = (FileVorlage) is.readObject();
	    FileOutputStream fos = new FileOutputStream("c"+fc.getName());
	    BufferedOutputStream bos = new BufferedOutputStream(fos);
	    
	    bos.write(fc.getF());
//	    out.writeObject(new Long(2));
	    bos.close();
	    sock.close();
	  }
}

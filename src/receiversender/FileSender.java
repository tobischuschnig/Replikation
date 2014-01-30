package receiversender;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import model.FileVorlage;

public class FileSender{
	Socket s;

	ObjectOutputStream bf;
	ObjectInputStream in;
	public FileSender(){

	}
	public void send(String file) {
		ServerSocket servsock;

		while(true){
			try {
				servsock = new ServerSocket(1234);

				s = servsock.accept();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}



			File f = new File(file);
			FileInputStream fis =null;
			try {
				bf = new ObjectOutputStream( new BufferedOutputStream(s.getOutputStream()));
				//			in = new ObjectInputStream ( s.getInputStream());
				fis = new FileInputStream(f);
			} catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {

				System.out.println("Write");
				byte [] size = new byte[(int) f.length()];

				fis.read(size);
				FileVorlage fc = new FileVorlage(size, f.getName(), 0);



				bf.writeObject(fc);
				System.out.println("Bla");
				//				in.readByte();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

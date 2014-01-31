package receiversender;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Path;

import model.FileVorlage;



/**

 * Das Prinzipt des Empfang Algorithmus aus Task07 (Auction System) aus dem Unterrichtsfach 
 * APR uebernommen.
 * Der Code fuer das Empfangen stammt von Danie Reichmann und wurde hirfuer wiederverwendet und 
 * von Tobias Schuschnig veraendert.
 * 
 * 
 * @author Daniel Reichmann <dreichmann@student.tgm.ac.at>, Tobias Schuschnig <tschuschnig@student.tgm.ac.at>
 * @version 10-12-2013
 *
 */
public class Receiver implements Runnable{

	private Socket client; //Socket-Verbindung mit Client
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private FileVorlage fc;
	private Thread executor; //Fuerht die Aktionen durch.
	private Path path;
	private int port;
	
//	public static void main(String[] args) {
//		new Receiver();
//	}
//	
//	public Receiver() {
//		
//	}
	/**
	 * Creates the UserHandler and starts the Connection
	 * 
	 * @param c 	Socket which has the connection
	 * @param s		Server which shall handle the requests
	 */
	public Receiver(Path path, int receive){
		this.path = path;
		this.port = receive;
//		executor = new Thread(this);
//		executor.start();
	}
	@Override
	public void run() {
		ServerSocket s;
		try {
			s = new ServerSocket(port);
			client = s.accept();
			
			in = new ObjectInputStream( client.getInputStream());
			out = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			System.err.println("Invalid Stream! Please Restart with correct Configs");
			return;
		}catch(Exception e){
			e.printStackTrace();
			return ;
		}
		while(true){
			Object o = null;
			try {				
				o = in.readObject();
				fc = (FileVorlage) o;
				System.out.println("blabal");
			} catch(SocketException e){
				System.out.println("Connection to Client lost.");
				break;
			}
			catch (IOException|ClassNotFoundException e) {
				System.err.println("Invalid Stream! Please Restart with correct Configs");

			}
			
			if(o instanceof FileVorlage){
				System.out.println("if");
				fc = (FileVorlage) o;
				String ret;
				 FileOutputStream fos;
				try {
					System.out.println(path.toString()+"bla"+fc.getName());
					fos = new FileOutputStream(path.toString()+"2/"+fc.getName());
					BufferedOutputStream bos = new BufferedOutputStream(fos);
				    
				    bos.write(fc.getF());
				    bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		try {
			client.close();
			System.out.println("fail");
		} catch (IOException e) {
			System.out.println("Could not close client");
		}
	}
	
}
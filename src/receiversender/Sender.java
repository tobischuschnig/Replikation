package receiversender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
public class Sender implements Runnable{
	
	int tcpPort;
	FileVorlage  fc;
	private Socket s; //Connection to Server
	private ReentrantLock lock = new ReentrantLock(); //To lock specific actions
	private Condition con; //Thread wait until message is set
//	private Thread t; //Thread in which program is running
	//Fehlt Objekt fr Ausgabe
	private ObjectOutputStream objectOutput; //Stream for Output
	private ObjectInputStream input; //Stream for Input

	
	public Sender(int port){		
		try {
			s = new Socket("localhost",port);
			objectOutput = new ObjectOutputStream(s.getOutputStream());
			input = new ObjectInputStream(s.getInputStream());
			objectOutput.writeObject(null); //Initialize stream
			con = lock.newCondition();
//			t  = new Thread(this);
		} catch (UnknownHostException e) {
			System.err.println("Could not Connect to Server");
			return;
		} catch (IOException e) {
			System.err.println("Could not open Connection\nCheck server and restart");
			return;
		}
//		t.start();
	}
	/**
	 * Gives the Connector a message to send to the server
	 * Notifies the Thread that it has to work
	 */
	public void sendMessage(FileVorlage m){
		lock.lock();
		fc = m;
		con.signal();
		lock.unlock();
	}

	@Override
	public void run() {
		try {			
			while(true){
				try{
					lock.lock();
					//Wait if message is null
					if(fc==null){
						try {
							//Wait until sendMessage is called
							con.await();						
//							if(!client.isActive())
//								break;
						} catch (InterruptedException e) {
							//Could not wait
						}
					}
					//System.out.println("Funkt");
					objectOutput.writeObject(fc);
					//System.out.println("Funkt2");
					String s="";
//					try {
//						System.out.println("Funkt3");
//						s = (String)input.readObject();
//						System.out.println("Funkt4");
//						System.out.println(s+"asdfasdf");
//						
//					} catch (ClassNotFoundException e) {
//						System.err.println("Fehler");
//					}
					//System.out.println("Funkt4");
					fc=null;
					//lock.unlock();
					
				}finally{
					lock.unlock();
				}
			}
//			s.close();
			
		} catch (IOException e) {
			System.err.println("Server unreachable. Check configs and restart");
		}
	}

}

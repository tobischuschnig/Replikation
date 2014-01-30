package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException; 
import java.net.URI;
import java.nio.file.*; 

import model.FileVorlage;

public class Utill {
	public static FileVorlage packing(Path path) {
		while(true){
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			File f = path.toFile();
			FileInputStream fis =null;

			try {
				fis = new FileInputStream(f);
			
				System.out.println("Write");
				byte [] size = new byte[(int) f.length()];

				fis.read(size);
				System.out.println(size);
				FileVorlage fc = new FileVorlage(size, f.getName(), 0);
				return fc;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException; 
import java.net.URI;
import java.nio.file.*; 

import model.FileVorlage;

public class Utill {
	public static FileVorlage packing(Path path,boolean delete) {
		while(true){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			File f = path.toFile();
			FileInputStream fis =null;
			if (delete == false) {
				try {
					fis = new FileInputStream(f);

					System.out.println("Write");
					byte [] size = new byte[(int) f.length()];

					fis.read(size);
					for (int i =0; i < size.length;i++) {
						System.out.println(size[i]);
					}

					FileVorlage fc = new FileVorlage(size, f.getName(), 0);
					System.out.println("End");
					return fc;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				//System.out.println("asdfasdfasdfasdfasdf");
				FileVorlage file = new FileVorlage(null,f.getName(),1);
				//file.setMethod(1);
				System.out.println(file.getMethod());
				return file;
			}
		}
	}
}

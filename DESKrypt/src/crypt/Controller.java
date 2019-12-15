package crypt;

import javax.swing.JOptionPane;

public class Controller {
	private Manager manager; 
	private String pathIn = "in.txt";
	private String pathEncoded = "encoded.txt";
	private String pathOut = "out.txt";
	
	Controller(){
		manager = new Manager();
	}

	

	public boolean encryptFromFile(String key) {
		boolean fadd = manager.addFile();
		if(fadd) {
		 return manager.saveToFile1(manager.encrypt1(key));
		}
		else return fadd;
		
	}

	public boolean decryptFromFile(String key) {
		boolean fadd = manager.addFile();
		if(fadd) {
		return manager.saveToFile1(manager.decrypt1(key));
		} else return fadd;
		
	}

}

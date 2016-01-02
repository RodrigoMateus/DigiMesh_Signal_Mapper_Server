package com.maykot.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.maykot.radiolibrary.IProcessMessage;

public class ProcessMessage implements IProcessMessage {

	@Override
	public void textMessageReceived(byte[] message) {
		String fileName = (new String(new SimpleDateFormat("yyyy-MM-dd_HHmmss_").format(new Date()))) + ".txt";
		try {
			FileOutputStream fileChannel = new FileOutputStream(fileName);
			fileChannel.write(message);
			fileChannel.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERRO FileChannel");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

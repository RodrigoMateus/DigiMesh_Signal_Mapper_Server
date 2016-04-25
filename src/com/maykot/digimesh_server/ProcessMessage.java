package com.maykot.digimesh_server;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.radiolibrary.RadioRouter;
import com.maykot.radiolibrary.interfaces.IProcessMessage;
import com.maykot.radiolibrary.model.MessageParameter;

public class ProcessMessage implements IProcessMessage {

	private RadioRouter radioRouter;

	public ProcessMessage() {
		this(RadioRouter.getInstance());
	}

	public ProcessMessage(RadioRouter radioRouter) {
		this.radioRouter = radioRouter;
	}

	@Override
	public void clientConnectionReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {
		System.out.println(new String(message));
		try {
			radioRouter.sendMessage(MainApp.myDevice, sourceDeviceAddress, MessageParameter.CONFIRM_CLIENT_CONNECTION,
					new String("Successfully connected to server!").getBytes());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void clientConnectionConfirm(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void textFileReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {
		treatTextFile(sourceDeviceAddress, message);
	}

	private void treatTextFile(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {
		String fileName = (new String(new SimpleDateFormat("yyyy-MM-dd_HHmmss_").format(new Date()))) + ".txt";
		try {
			FileOutputStream fileChannel = new FileOutputStream(fileName);
			fileChannel.write(message);
			fileChannel.close();
			radioRouter.sendMessage(MainApp.myDevice, sourceDeviceAddress, MessageParameter.CONFIRM_TXT_FILE,
					new String("Texto enviado com SUCESSO!").getBytes());
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: ERRO ao criar arquivo texto");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void textFileConfirm(byte[] message) {
		System.out.println(new String(message));
	}

	@Override
	public void localPostReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {
		new TreatRequest().localRequest(sourceDeviceAddress, message);
	}

	@Override
	public void localPostConfirm(byte[] message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mobilePostReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {
		new TreatRequest().mobileRequest(sourceDeviceAddress, message);
	}

	@Override
	public void mobilePostConfirm(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void packetTransferReceived(RemoteXBeeDevice sourceDeviceAddress, String md5, byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void packetTransferConfirm(byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void telemetryTransferReceived(RemoteXBeeDevice sourceDeviceAddress, String md5, byte[] message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void textFileReceived(RemoteXBeeDevice sourceDeviceAddress, String md5, byte[] message) {
		// TODO Auto-generated method stub

	}
}

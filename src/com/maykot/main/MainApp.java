package com.maykot.main;

import java.io.IOException;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.utils.DeviceConfig;
import com.digi.xbee.api.utils.LogRecord;
import com.maykot.radiolibrary.OpenMyDevice;
import com.maykot.radiolibrary.RouterRadio;

public class MainApp {

	/* Arquivo de configurações do sistema */
	static DeviceConfig deviceConfig;

	/* XTends */
	public static DigiMeshDevice myDevice;
	public static RemoteXBeeDevice remoteDevice;

	public static void main(String[] args) {
		System.out.println(" +-------------------+");
		System.out.println(" |  DigiMesh Server  |");
		System.out.println(" +-------------------+\n");

		try {
			deviceConfig = new DeviceConfig();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		new LogRecord();

		myDevice = OpenMyDevice.open(deviceConfig);

		System.out.println("\n>> Waiting messages ...");

		// Registra listener para processar mensagens recebidas
		RouterRadio.getInstance().processMyMessage(new ProcessMessage());
	}
}
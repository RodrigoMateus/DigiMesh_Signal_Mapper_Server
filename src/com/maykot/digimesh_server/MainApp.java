package com.maykot.digimesh_server;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.maykot.radiolibrary.RadioRouter;
import com.maykot.radiolibrary.utils.DeviceConfig;
import com.maykot.radiolibrary.utils.LogRecord;
import com.maykot.radiolibrary.utils.OpenMyDevice;

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

		new LogRecord();

		deviceConfig = DeviceConfig.getInstance();
		myDevice = OpenMyDevice.open(deviceConfig);

		System.out.println("\n>> Waiting messages ...");

		// Registra listener para processar mensagens recebidas
		RadioRouter.getInstance().addProcessMessageListener(new ProcessMessage());
	}
}
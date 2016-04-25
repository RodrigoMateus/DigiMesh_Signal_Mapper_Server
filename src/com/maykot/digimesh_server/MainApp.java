package com.maykot.digimesh_server;

import com.digi.xbee.api.ZigBeeDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.ZigBeeDevice;
import com.maykot.radiolibrary.RadioRouter;
import com.maykot.radiolibrary.utils.DeviceConfig;
import com.maykot.radiolibrary.utils.LogRecord;
import com.maykot.radiolibrary.utils.OpenMyDevice;

@SuppressWarnings("unused")
public class MainApp {

	/* Arquivo de configurações do sistema */
	static DeviceConfig deviceConfig;

	/* Se o rádio for um XTend */
	public static ZigBeeDevice myDevice = null;

	/* Se o rádio for um XBee */
//	public static ZigBeeDevice myDevice = null;

	public static RemoteXBeeDevice remoteDevice;

	public static void main(String[] args) {
		System.out.println(" +---------------------------------+");
		System.out.println(" |  DigiMesh Signal Mapper Server  |");
		System.out.println(" +---------------------------------+\n");

		new LogRecord();

		deviceConfig = DeviceConfig.getInstance();
		myDevice = OpenMyDevice.open(deviceConfig);

		// verifica se o rádio está conectado e
		// tenta reconectar se rádio não foi encontrado.
		new Thread(new RadioMonitor()).start();

		System.out.println("\n>> Waiting messages ...");

		// Registra listener para processar mensagens recebidas
		RadioRouter.getInstance().addProcessMessageListener(new ProcessMessage());
	}
}
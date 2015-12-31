package com.maykot.main;

import java.io.IOException;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.models.APIOutputMode;
import com.digi.xbee.api.utils.DeviceConfig;
import com.digi.xbee.api.utils.LogRecord;
import com.digi.xbee.api.utils.SerialPorts;
import com.maykot.radiolibrary.Router;

public class MainApp {

	/* XTends */
	public static DigiMeshDevice myDevice;
	public static DeviceConfig deviceConfig;
	static String XTEND_PORT = null;
	static int XTEND_BAUD_RATE;
	static int TIMEOUT_FOR_SYNC_OPERATIONS = 10000; // 10 seconds

	public static void main(String[] args) {
		System.out.println(" +-------------------+");
		System.out.println(" |  DigiMesh Server  |");
		System.out.println(" +-------------------+\n");

		try {
			deviceConfig = new DeviceConfig();
			XTEND_PORT = deviceConfig.getXTendPort();
			XTEND_BAUD_RATE = deviceConfig.getXTendBaudRate();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		new LogRecord();

		openMyDevice();
	}

	public static void openMyDevice() {
		try {
			XTEND_PORT = deviceConfig.getXTendPort();
			myDevice = openDevice(XTEND_PORT, XTEND_BAUD_RATE);
			myDevice.addExplicitDataListener(Router.getInstance());
			System.out.println("Was found LOCAL radio " + myDevice.getNodeID() + " (PowerLevel "
					+ myDevice.getPowerLevel() + ").");
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String port : SerialPorts.getSerialPortList()) {
			try {
				System.out.println("Try " + port);
				myDevice = openDevice(port, XTEND_BAUD_RATE);
				myDevice.addExplicitDataListener(Router.getInstance());
				System.out.println("Was found LOCAL radio " + myDevice.getNodeID() + " (PowerLevel: "
						+ myDevice.getPowerLevel() + ").");
				return;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("openDevice() ERROR");
			}
		}
		System.out.println("LOCAL Radio not found! Try openDevice() again.");
		openMyDevice();
	}

	public static DigiMeshDevice openDevice(String port, int bd) throws Exception {
		DigiMeshDevice device = new DigiMeshDevice(port, bd);
		device.open();
		device.setAPIOutputMode(APIOutputMode.MODE_EXPLICIT);
		return device;
	}
}
package com.maykot.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.SerializationUtils;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.radiolibrary.ErrorMessage;
import com.maykot.radiolibrary.MessageParameter;
import com.maykot.radiolibrary.ProxyRequest;
import com.maykot.radiolibrary.ProxyResponse;
import com.maykot.radiolibrary.Router;

public class TreatRequest {

	String mqttClientId = null;
	String mqttMessageId = null;

	public TreatRequest(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {

		ProxyRequest proxyRequest = (ProxyRequest) SerializationUtils.deserialize(message);

		System.out.println(proxyRequest.getIdMessage());
		System.out.println(proxyRequest.getVerb());

		ProxyResponse response = processRequest(proxyRequest, sourceDeviceAddress);

		byte[] responseToSourceDevice = SerializationUtils.serialize(response);

		try {
			Router.getInstance().sendMessage(MainApp.myDevice, sourceDeviceAddress, MessageParameter.CONFIRM_HTTP_POST,
					responseToSourceDevice);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ProxyResponse processRequest(ProxyRequest proxyRequest, RemoteXBeeDevice sourceDeviceAddress) {
		ProxyResponse response = null;

		HashMap<String, String> requestHeader = new HashMap<String, String>();
		requestHeader = proxyRequest.getHeader();

		byte[] tempByteArray = proxyRequest.getBody();
		String fileName = (new String(new SimpleDateFormat("yyyy-MM-dd_HHmmss_").format(new Date()))) + "image.jpg";
		try {
			FileOutputStream fileChannel = new FileOutputStream(fileName);
			fileChannel.write(tempByteArray);
			fileChannel.close();
			response = new ProxyResponse(ErrorMessage.OK.value(), requestHeader.get("content-type"),
					ErrorMessage.OK.description().getBytes());
		} catch (FileNotFoundException e) {
			System.out.println("ERRO FileChannel");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (response == null) {
			response = new ProxyResponse(ErrorMessage.TRANSMIT_EXCEPTION.value(), requestHeader.get("content-type"),
					ErrorMessage.TRANSMIT_EXCEPTION.description().getBytes());
		}
		response.setIdMessage(proxyRequest.getIdMessage());
		return response;
	}
}

package com.maykot.digimesh_server;

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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.maykot.radiolibrary.RadioRouter;
import com.maykot.radiolibrary.http.ProxyHttp;
import com.maykot.radiolibrary.model.ErrorMessage;
import com.maykot.radiolibrary.model.MessageParameter;
import com.maykot.radiolibrary.model.Point;
import com.maykot.radiolibrary.model.ProxyRequest;
import com.maykot.radiolibrary.model.ProxyResponse;
import com.maykot.radiolibrary.utils.LogRecord;

public class TreatRequest {

	private RadioRouter radioRouter;

	public TreatRequest() {
		this(RadioRouter.getInstance());
	}

	public TreatRequest(RadioRouter radioRouter) {
		this.radioRouter = radioRouter;
	}

	public void mobileRequest(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {
		ProxyRequest proxyRequest = (ProxyRequest) SerializationUtils.deserialize(message);
		
		LogRecord.insertLog("MobileRequest_ServerLog",
				new String(proxyRequest.getMqttClientId() + ";" + proxyRequest.getIdMessage() + ";"
						+ new String(new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss:SSS").format(new Date())) + ";"
						+ new String(proxyRequest.getBody())));

		System.out.println(proxyRequest.getIdMessage());
		System.out.println(proxyRequest.getVerb());

		ProxyResponse response = processRequest(proxyRequest, sourceDeviceAddress);

		byte[] responseToSourceDevice = SerializationUtils.serialize(response);

		try {
			radioRouter.sendMessage(MainApp.myDevice, sourceDeviceAddress, MessageParameter.CONFIRM_MOBILE_POST,
					responseToSourceDevice);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void localRequest(RemoteXBeeDevice sourceDeviceAddress, byte[] message) {
		ProxyRequest proxyRequest = (ProxyRequest) SerializationUtils.deserialize(message);

		System.out.println(proxyRequest.getIdMessage());
		System.out.println(proxyRequest.getVerb());

		ProxyResponse response = processRequest(proxyRequest, sourceDeviceAddress);

		byte[] responseToSourceDevice = SerializationUtils.serialize(response);

		try {
			radioRouter.sendMessage(MainApp.myDevice, sourceDeviceAddress, MessageParameter.CONFIRM_LOCAL_POST,
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

		try {
			if (proxyRequest.getVerb().contains("get")) {
				response = ProxyHttp.getFile(proxyRequest);
			} else if (proxyRequest.getVerb().contains("post")) {
				response = postFile(proxyRequest);
			} else {
				response = new ProxyResponse(ErrorMessage.NOT_VERB.value(),
						ErrorMessage.NOT_VERB.description().getBytes());
			}
		} catch (Exception e) {
			response = new ProxyResponse(ErrorMessage.INVALID_PROXY_REQUEST.value(),
					ErrorMessage.INVALID_PROXY_REQUEST.description().getBytes());
		}

		HashMap<String, String> requestHeader = new HashMap<String, String>();
		requestHeader = proxyRequest.getHeader();
		String fileName = null;
		byte[] tempByteArray = proxyRequest.getBody();

		String contentType = requestHeader.get("content-type");
		switch (contentType) {
		case "application/json":
			fileName = (new String(new SimpleDateFormat("yyyy-MM-dd_HHmmss_").format(new Date()))) + "coordinates.json";
			// response = new ProxyResponse(ErrorMessage.OK.value(),
			// ErrorMessage.OK.description().getBytes());

			saveCoordinatesToLog(tempByteArray);

			break;
		case "image/jpg":
			fileName = (new String(new SimpleDateFormat("yyyy-MM-dd_HHmmss_").format(new Date()))) + "imagem.jpg";
			break;

		default:
			break;
		}
		try {
			FileOutputStream fileChannel = new FileOutputStream(fileName);
			fileChannel.write(tempByteArray);
			fileChannel.close();
			// response = new ProxyResponse(ErrorMessage.OK.value(),
			// ErrorMessage.OK.description().getBytes());
		} catch (FileNotFoundException e) {
			System.out.println("ERRO FileChannel");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (response == null) {
			response = new ProxyResponse(ErrorMessage.TRANSMIT_EXCEPTION.value(),
					ErrorMessage.TRANSMIT_EXCEPTION.description().getBytes());
		}
		response.setMqttClientId(proxyRequest.getMqttClientId());
		response.setIdMessage(proxyRequest.getIdMessage());
		return response;
	}

	private void saveCoordinatesToLog(byte[] tempByteArray) {
		Gson gson = new Gson();
		try {
			Point point = gson.fromJson(new String(tempByteArray), Point.class);
			LogRecord.insertCoordinates("CoordinatesFromRouter", point.latitude + "," + point.longitude);
		} catch (JsonSyntaxException e1) {
			System.out.println("Mensagem não contém um JSON válido.");
		}
	}

	public static ProxyResponse postFile(ProxyRequest proxyRequest) {

		ProxyResponse proxyResponse = null;
		String response = null;

		response = "Log recebido!!!";

		proxyResponse = new ProxyResponse(200, response.getBytes());
		proxyResponse.setIdMessage(proxyRequest.getIdMessage());
		// getHeader(proxyResponse, httpResponse);

		System.out.println(proxyResponse.toString());

		return proxyResponse;
	}

}

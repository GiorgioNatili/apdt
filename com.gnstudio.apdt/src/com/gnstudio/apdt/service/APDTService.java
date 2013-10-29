/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.gnstudio.apdt.APDTLog;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class APDTService {

	private static final String SERVICE_APDT_FIRST_RUN = "apdt.first_run";
	private static final String SERVICE_APDT_TRIAL_EXPIRE = "apdt.trial_expire";
	private static final String SERVICE_APDT_INSERT_LICENSE = "apdt.insert_license";
	private static final String SERVICE_APDT_LICENCE_VERIFICATION = "apdt.licence_verification";

	private static final String PARAM_PLUGIN_VERSION = "plugin_version";
	private static final String PARAM_MAC_ADDRESS = "mac_address";
	private static final String PARAM_EMAIL = "email";
	private static final String PARAM_USERNAME = "username";
	private static final String PARAM_SERIAL_NUMBER = "serial_number";

	private static final String METHOD = "method";

	private static final String APDT_GNSTUDIO_BIZ_SERVICES_URL = "http://www.modeling.io/services/json";
	private static final String PARM_EQ = "=";
	private static final String PARM_SP = "&";
	private static final String UTF_8 = "UTF-8";

	private APDTService() {
		throw new AssertionError();
	}

	private static String[] getFingerprints() {
		Set<String> macs = new HashSet<String>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				if (!ni.isVirtual() && !ni.isLoopback() && !ni.isPointToPoint()
						&& ni.getHardwareAddress() != null) {
					byte[] mac = ni.getHardwareAddress();
					StringBuilder macID = new StringBuilder();
					for (int i = 0; i < mac.length; i++) {
						macID.append(String.format("%02X%s", mac[i],
								(i < mac.length - 1) ? ":" : ""));

					}
					macs.add(macID.toString());
				}

			}
		} catch (SocketException e) {
			APDTLog.log(e);
		}
		return macs.toArray(new String[0]);
	}

	public static JSONObject firstRun(String macids, String version) {
		// apdt.trial_expire
		JSONObject jsonObject = null;

		Parameter[] parms = new Parameter[] {
				new Parameter(METHOD, SERVICE_APDT_FIRST_RUN),
				new Parameter(PARAM_MAC_ADDRESS, macids),
				new Parameter(PARAM_PLUGIN_VERSION, version) };
		final String data = toParameterData(parms);
		jsonObject = callService(data);

		return jsonObject;
	}

	public static JSONObject insertLicense(String username, String email,
			String serial, String macids, String version) {
		// apdt.insert_license
		JSONObject jsonObject = null;

		Parameter[] parms = new Parameter[] {
				new Parameter(METHOD, SERVICE_APDT_INSERT_LICENSE),
				new Parameter(PARAM_USERNAME, username),
				new Parameter(PARAM_EMAIL, email),
				new Parameter(PARAM_SERIAL_NUMBER, serial),
				new Parameter(PARAM_MAC_ADDRESS, macids),
				new Parameter(PARAM_PLUGIN_VERSION, version) };
		final String data = toParameterData(parms);
		jsonObject = callService(data);

		return jsonObject;
	}

	public static JSONObject licenceVerification(String username,String serial, String macids,
			String version) {
		// apdt.insert_license
		JSONObject jsonObject = null;

		Parameter[] parms = new Parameter[] {
				new Parameter(METHOD, SERVICE_APDT_LICENCE_VERIFICATION),
				new Parameter(PARAM_USERNAME, username),
				new Parameter(PARAM_SERIAL_NUMBER, serial),
				new Parameter(PARAM_MAC_ADDRESS, macids),
				new Parameter(PARAM_PLUGIN_VERSION, version) };
		final String data = toParameterData(parms);
		jsonObject = callService(data);

		return jsonObject;
	}

	public static JSONObject trialExpire(String macids, String version) {
		// apdt.trial_expire
		JSONObject jsonObject = null;

		Parameter[] parms = new Parameter[] {
				new Parameter(METHOD, SERVICE_APDT_TRIAL_EXPIRE),
				new Parameter(PARAM_MAC_ADDRESS, macids),
		// new Parameter("plugin_version", version)
		};
		final String data = toParameterData(parms);
		jsonObject = callService(data);

		return jsonObject;
	}

	private static String toParameterData(Parameter... parameters) {
		final StringBuilder data = new StringBuilder();
		for (Parameter parameter : parameters) {
			try {
				data.append(URLEncoder.encode(parameter.key, UTF_8))
						.append(PARM_EQ)
						.append(URLEncoder.encode(
								toParameterValue(parameter.value), UTF_8))
						.append(PARM_SP);
			} catch (UnsupportedEncodingException e) {
				APDTLog.log(e);
			}
		}

		return data.toString();
	}

	private static String toParameterValue(String value) {
		return new StringBuilder().append("\"").append(value).append("\"")
				.toString();
	}

	private static class Parameter {
		private final String key;
		private final String value;

		public Parameter(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

	}

	private static JSONObject callService(final String data) {
		JSONObject jsonObject = null;
		try {
			URL url = new URL(APDT_GNSTUDIO_BIZ_SERVICES_URL);
			URLConnection conn = url.openConnection();

			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());

			try {

				// Send data
				wr.write(data);
				wr.flush();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				try {
					JSONTokener parser = new JSONTokener(rd);
					jsonObject = new JSONObject(parser);

				} finally {
					rd.close();

				}
			} catch (UnsupportedEncodingException e) {
				APDTLog.log(e);
			} catch (IOException e) {
				APDTLog.log(e);
			} catch (JSONException e) {
				APDTLog.log(e);
			} finally {
				wr.close();
			}
		} catch (IOException ex) {
			APDTLog.log(ex);
		}
		return jsonObject;
	}

	public static String getFPIds() {
		String[] fingerprints = getFingerprints();
		// pass ',' separated fingerprints
		StringBuilder macs = new StringBuilder();
		for (int i = 0; i < fingerprints.length; i++) {
			String mac = fingerprints[i];
			if (i > 0)
				macs.append(",");
			macs.append(mac);

		}
		String macids = macs.toString();
		return macids;
	}

	public static void main(String[] arg) {
		String macids = getFPIds();
	    // macids = "00:1c:3f:sd:2d:d4";
		// macids = "00:26:82:A9:73:12";
		 macids = "00:1f:5b:d5:e2:bt";
		System.out.println(macids);
		JSONObject jsonObject;
		jsonObject = firstRun(macids, "1.0");
		System.out.println(jsonObject);

		
		 jsonObject = trialExpire(macids, "1.0");
		System.out.println(jsonObject);
		
		
		jsonObject = insertLicense("coach", "m.fusetti@gnstudio.com",
				"1a2b3c4d5e6f7t8g9h1y0j1u1m1j3k1l", macids, "1.0");
		System.out.println(jsonObject);
		
		jsonObject = licenceVerification("coach","1a2b3c4d5e6f7t8g9h1y0j1u1m1j3k1l",
				macids, "1.0");
        System.out.println(jsonObject);

	}
}

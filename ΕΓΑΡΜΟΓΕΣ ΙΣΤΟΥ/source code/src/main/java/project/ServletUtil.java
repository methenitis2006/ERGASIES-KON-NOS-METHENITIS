package hua.dit.web.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServletUtil {

	private ServletUtil() {
	}

	public static <T> T getRequestData(Class<T> cls, HttpServletRequest request) throws IOException {
		final StringBuilder sb = new StringBuilder();
		try(BufferedReader reader = request.getReader()){
			String line;
			while ((line = reader.readLine()) != null){
				sb.append(line);
			}
		}
		final String payload = sb.toString();
		return jsonStrToObj(cls, payload);
	}

	public static void sendResponseData(Object obj, HttpServletResponse response) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(objToJsonStr(obj));
		out.flush();
	}

	public static String getPathFirstParam(HttpServletRequest request) {
		final String path = request.getPathInfo();
		if (path == null) return null;
		String[] tokens = path.split("/");
		for(String token : tokens) {
			if (token.trim().equals("")) continue;
			return token;
		}
		return null;
	}

	private static <T> T jsonStrToObj(Class<T> cls, String str) {
		return new Gson().fromJson(str, cls);
	}

	private static String objToJsonStr(Object obj) {
		return new Gson().toJson(obj);
	}
}

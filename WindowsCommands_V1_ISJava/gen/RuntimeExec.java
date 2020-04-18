package gen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RuntimeExec {

	public static String exec(String command, String parameter) throws IOException, InterruptedException {
		// Execute the command
		Process executed;
		executed = Runtime.getRuntime().exec("cmd /c " + command + " " + parameter);

		InputStream error = null;
		InputStream output = null;
		BufferedReader in = null;
		BufferedReader errorStream = null;
		// check the result
		output = executed.getInputStream();
		in = new BufferedReader(new InputStreamReader(output));

		String result = "";
		String execLine;
		while ((execLine = in.readLine()) != null) {
			result += execLine;
		}
		error = executed.getErrorStream();
		errorStream = new BufferedReader(new InputStreamReader(error));
		String errorLine;
		while ((errorLine = errorStream.readLine()) != null) {
			result += errorLine;
		}

		// return result
		int returnCode = executed.waitFor();
		if (returnCode != 0) {
			throw new RuntimeException("Return code = " + returnCode + " Error message = " + result);
		}
		error.close();
		output.close();
		in.close();
		errorStream.close();
		return "Command executed successfully. " + result;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
//		RuntimeExec.exec("notepad", "");
		RuntimeExec.exec("copy C:\\vishnu\\workspace\\data\\iib-WindowsCommands\\input.png C:\\vishnu\\workspace\\data\\iib-WindowsCommands\\output.png ","");
	}
}

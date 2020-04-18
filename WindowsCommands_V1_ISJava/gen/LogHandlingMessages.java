package gen;

import java.util.ListResourceBundle;

public class LogHandlingMessages extends ListResourceBundle {

	public static final String MESSAGE_SOURCE = LogHandlingMessages.class.getName();
	public static final String sysLogDemo01 = "sysLogDemo01";
	public static final String sysLogDemo02 = "sysLogDemo02";

	private Object[][] messages = { { sysLogDemo01, "sysLogDemo03" },
			{ sysLogDemo02, "sysLogDemo04 {1}, sysLogDemo {2}, sysLogDemo {3}" } };

	@Override
	protected Object[][] getContents() {
		return messages;
	}

}

package com.patrickanker.isay.formatters;

import com.patrickanker.lib.util.Formatter;
import com.patrickanker.isay.ISMain;

public class ConsoleMessageFormatter extends Formatter {

    @Override
    public String formatMessage(String in, Object... otherArgs)
    {
        String master = ISMain.getConfigData().getString("console-format");

        if (master == null) {
            master = ISMain.getDefaultConsoleFormat();
            ISMain.getConfigData().setString("console-format", ISMain.getDefaultConsoleFormat());
        }

        master = Formatter.encodeColors(master);
        master = master.replace("$message", in);
        master = master.replace("$m", in);

        return master;
    }

    @Override
    public String[] formatMessages(String in, Object... otherArgs)
    {
        return new String[]{formatMessage(in, otherArgs)};
    }
}
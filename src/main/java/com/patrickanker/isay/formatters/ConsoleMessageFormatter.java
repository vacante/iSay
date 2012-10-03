/*
 * ConsoleMessageFormatter.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 * 
 * iSay by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.formatters;

import com.patrickanker.isay.ISMain;
import com.patrickanker.lib.util.Formatter;

public class ConsoleMessageFormatter extends Formatter {

    @Override
    public String formatMessage(String in, Object... otherArgs) {
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
    public String[] formatMessages(String in, Object... otherArgs) {
        return new String[] {formatMessage(in)};
    }
}

/*
 * SingleLineBroadcastFormatter.java
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

public class SingleLineBroadcastFormatter extends Formatter {

    @Override
    public String formatMessage(String in, Object... otherArgs) {
        String master = ISMain.getConfigData().getString("broadcast-format");
        
        if (master == null) {
            master = ISMain.getDefaultBroadcastFormat();
            ISMain.getConfigData().setString("broadcast-format", ISMain.getDefaultBroadcastFormat());
        }
        
        master = master.replace("$message", in);
        master = master.replace("$m", in);
        
        master = Formatter.encodeColors(master);
        
        return master;
    }

    @Override
    public String[] formatMessages(String in, Object... otherArgs) {
        return new String[] {formatMessage(in)};
    }   
}

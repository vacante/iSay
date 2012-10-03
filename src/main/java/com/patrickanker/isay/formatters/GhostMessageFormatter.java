/*
 * GhostMessageFormatter.java
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

import com.patrickanker.isay.ChatPlayer;
import com.patrickanker.isay.channels.ChatChannel;
import com.patrickanker.lib.permissions.PermissionsManager;
import com.patrickanker.lib.util.Formatter;

public class GhostMessageFormatter extends Formatter {

    @Override
    public String formatMessage(String in, Object... otherArgs) {
        ChatPlayer cp = (ChatPlayer) otherArgs[0];
        ChatChannel destination = (ChatChannel) otherArgs[1];
        
        String master = destination.getGhostFormat();
        String idFormat = cp.getFormat();
        String groupIdFormat = cp.getGroupFormat();

        idFormat = idFormat.replace("$name", cp.getPlayer().getName());
        idFormat = idFormat.replace("$n", cp.getPlayer().getName());
        idFormat = Formatter.encodeColors(idFormat);

        groupIdFormat = groupIdFormat.replace("$name", cp.getPlayer().getName());
        groupIdFormat = groupIdFormat.replace("$n", cp.getPlayer().getName());
        groupIdFormat = groupIdFormat.replace("\u0026", "\u00A7");

        master = master.replace("$id", idFormat);
        master = master.replace("$group", groupIdFormat);
        master = master.replace("$g", groupIdFormat);
        
        master = master.replace("$channel", destination.getName());
        master = master.replace("$ch", destination.getName());

        if (PermissionsManager.getHandler().hasPermission(cp.getPlayer().getWorld().getName(), cp.getPlayer().getName(), "isay.chat.color"))
            in = Formatter.encodeColors(in);

        master = master.replace("\u0026", "\u00A7");
        master = master.replace("$name", cp.getPlayer().getName());
        master = master.replace("$n", cp.getPlayer().getName());

        master = master.replace("$message", in);
        master = master.replace("$m", in);
        return master;
    }

    @Override
    public String[] formatMessages(String in, Object... otherArgs) {
        ChatPlayer cp = (ChatPlayer) otherArgs[0];
        ChatChannel destination = (ChatChannel) otherArgs[1];
        return new String[] {formatMessage(in, cp, destination)};
    }   
}

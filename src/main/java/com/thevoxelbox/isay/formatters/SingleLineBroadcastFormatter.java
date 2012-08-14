package com.thevoxelbox.isay.formatters;

import com.patrickanker.lib.util.Formatter;
import com.thevoxelbox.isay.ISMain;

public class SingleLineBroadcastFormatter extends Formatter {

    @Override
    public String formatMessage(String in, Object... otherArgs)
    {
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
    public String[] formatMessages(String in, Object... otherArgs)
    {
        return new String[]{formatMessage(in, otherArgs)};
    }
}
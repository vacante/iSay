package com.patrickanker.isay;

import com.patrickanker.lib.permissions.PermissionsManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

public class PingManager {

    private final HashMap<String, Long> lastUsed = new HashMap();
    private final HashMap<String, Integer> pingCount = new HashMap();
    private int pingTask = -1;

    public PingManager()
    {
        this.pingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(ISMain.getInstance(), new Runnable() {

            @Override
            public void run()
            {
                List<String> removeList = new ArrayList<String>();

                for (Map.Entry<String, Long> entry : PingManager.this.lastUsed.entrySet()) {
                    if (System.currentTimeMillis() - ((Long) entry.getValue()).longValue() > 30000L) {
                        removeList.add(entry.getKey());
                        PingManager.this.pingCount.remove(entry.getKey());
                    }
                }

                for (String str : removeList) {
                    PingManager.this.lastUsed.remove(str);
                }
            }
        }, 0L, 600L);
    }

    public ChatPlayer[] getPingeesFromString(String str)
    {
        List l = new ArrayList();

        Pattern pattern = Pattern.compile("@\\w+?\\b");
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            String get = matcher.group();
            get = get.replace("@", "");

            List match = Bukkit.matchPlayer(get);

            if ((!match.isEmpty()) && (match.size() <= 1)) {
                ChatPlayer toPing = ISMain.getRegisteredPlayer((Player) match.get(0));
                l.add(toPing);
            }
        }

        ChatPlayer[] ret = new ChatPlayer[l.size()];
        return (ChatPlayer[]) l.toArray(ret);
    }

    public boolean canPing(ChatPlayer pinger, ChatPlayer pingee)
    {
        if ((PermissionsManager.getHandler().hasPermission(pinger.getPlayer().getName(), "isay.ping.ping"))
                && (pingee.isPingEnabled())) {
            if (this.lastUsed.containsKey(pinger.getPlayer().getName())) {
                long last = ((Long) this.lastUsed.get(pinger.getPlayer().getName())).longValue();
                int count = ((Integer) this.pingCount.get(pinger.getPlayer().getName())).intValue();

                return (count <= 10) || (System.currentTimeMillis() - last >= 30000L);
            }

            return true;
        }

        return false;
    }

    public void doPing(ChatPlayer pinger, ChatPlayer pingee)
    {
        pingee.getPlayer().playEffect(pingee.getPlayer().getLocation(), Effect.GHAST_SHRIEK, null);

        this.lastUsed.put(pinger.getPlayer().getName(), Long.valueOf(System.currentTimeMillis()));

        if (this.pingCount.containsKey(pinger.getPlayer().getName())) {
            int last = ((Integer) this.pingCount.get(pinger.getPlayer().getName())).intValue();
            last++;
            this.pingCount.put(pinger.getPlayer().getName(), Integer.valueOf(last));
        } else {
            this.pingCount.put(pinger.getPlayer().getName(), Integer.valueOf(1));
        }
    }

    public void shutdownPingTask()
    {
        if (this.pingTask != -1) {
            Bukkit.getScheduler().cancelTask(this.pingTask);
        }
    }
}
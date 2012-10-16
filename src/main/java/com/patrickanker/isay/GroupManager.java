package com.patrickanker.isay;

import com.patrickanker.lib.config.PropertyConfiguration;
import com.patrickanker.lib.permissions.PermissionsManager;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import org.bukkit.entity.Player;

public class GroupManager {

    protected static HashMap<String, PropertyConfiguration> groupMap = new HashMap<String, PropertyConfiguration>();
    protected static HashMap<String, List<String>> playerMap = new HashMap();
    private final String defaultGroupName = "Group";
    private final String defaultGroupFormat = "&f$name:";
    private final PropertyConfiguration defaultConfig = new PropertyConfiguration("Group", "/iSay/groups");
    
    public void load()
    {
        File dir = new File("plugins/iSay/groups/");

        if (!dir.isDirectory()) {
            dir.mkdirs();
            return;
        }

        String[] files = dir.list();

        for (String file : files) {
            if (file.endsWith(".properties")) {
                String f = file.replace(".properties", "");
                PropertyConfiguration config = new PropertyConfiguration(f, "/iSay/groups");
                groupMap.put(f, config);
            }
        }

        this.defaultConfig.setString("format", "&f$name:");
    }

    public PropertyConfiguration getGroupConfiguration(String name)
    {
        if (groupMap.containsKey(name)) {
            return (PropertyConfiguration) groupMap.get(name);
        }
        PropertyConfiguration config = new PropertyConfiguration(name, "/iSay/groups");
        setGroupConfiguration(name, config);
        return config;
    }

    public void setGroupConfiguration(String name, PropertyConfiguration config)
    {
        groupMap.put(name, config);
    }

    public String findGroup(String key, Object value) throws GroupNotFoundException
    {
        for (Map.Entry<String, PropertyConfiguration> entry : groupMap.entrySet()) {
            PropertyConfiguration config = (PropertyConfiguration) entry.getValue();

            if ((config.getEntry(key) != null) && (value.equals(config.getEntry(key)))) {
                return (String) entry.getKey();
            }
        }
        throw new GroupNotFoundException();
    }

    public void saveGroupConfigurations()
    {
        for (Map.Entry entry : groupMap.entrySet()) {
            saveGroupConfiguration((String) entry.getKey());
        }
    }

    public void saveGroupConfiguration(String name)
    {
        PropertyConfiguration config = (PropertyConfiguration) groupMap.get(name);
        config.save();
    }

    public void verifyPlayerGroupExistence(Player p)
    {
        String[] groups = PermissionsManager.getHandler().getGroups(p.getName());

        if ((groups != null) && (groups.length > 0)) {
            String group = groups[0];

            if (!groupMap.containsKey(group)) {
                groupMap.put(group, this.defaultConfig);

                getGroupConfiguration(group).assignTarget(group);
            }
        }
    }

    public List<String> getPlayerListForGroup(String group)
    {
        return (List) playerMap.get(group);
    }

    public List<String> getRegisteredGroups()
    {
        List<String> l = new ArrayList<String>();
        
        for (Iterator<Entry<String, PropertyConfiguration>> it = groupMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, PropertyConfiguration> entry = it.next();
            
            if (!l.contains(entry.getKey())) {
                l.add(entry.getKey());
            }
        }
        
        return l;
    }

    public PropertyConfiguration getDefaultConfiguration()
    {
        return this.defaultConfig;
    }
}
/*
   Copyright 2009 NEERC team

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
// $Id$
/**
 * Date: 25.10.2004
 */
package ru.ifmo.neerc.chat;

import ru.ifmo.ips.config.Config;

/**
 * @author Matvey Kazakov
 */
public class UserEntry implements Comparable {
    private static final String ATTR_ID = "@id";
    private static final String ATTR_NAME = "@name";
    private static final String ATTR_GROUP = "@group";
    private static final String ATTR_POWER = "@power";
    private static final String USER_NODE = "user";
    private static final String ATTR_ONLINE = "@online";
    
    
    private int id;
    private String name;
    private boolean power;
    private String group;
    private boolean online = false;
    
    private static int LAST_ID = 0;
    
    public void genId() {
        id = LAST_ID++;
    }

    public UserEntry() {
    }

    public UserEntry(int id, String name, boolean power) {
        this.id = id;
        this.name = name;
        this.power = power;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserEntry)) {
            return false;
        }

        final UserEntry userEntry = (UserEntry)o;

		return id == userEntry.id;
	}

    public int hashCode() {
        return id;
    }

    public void serialize(Config config) {
        Config node = config.createNode(USER_NODE + "#" + id);
        node.setProperty(ATTR_NAME, name);
        node.setProperty(ATTR_ONLINE, Boolean.toString(online));
        node.setProperty(ATTR_POWER, Boolean.toString(power));
        node.setProperty(ATTR_GROUP, group);
    }

    public void deserialize(Config config) {
        Config node = config.getNode(USER_NODE);
        id = node.getInt(ATTR_ID);
        name = node.getProperty(ATTR_NAME);
        group = node.getProperty(ATTR_GROUP);
        online = Boolean.valueOf(node.getProperty(ATTR_ONLINE));
        power = Boolean.valueOf(node.getProperty(ATTR_POWER));
    }

    public String toString() {
        String result = name;
        if (!online) {
            result = "-" + result;
        }
        if (power) {
            result += "(!)";
        }
        return result;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int compareTo(Object o) {
        UserEntry userEntry = (UserEntry)o;
        String name = getName();
        if (name == null) {
            name = "";
        }
        String name1 = userEntry.getName();
        if (name1 == null) {
            name1 = "";
        }
        return name.compareTo(name1);
    }
}

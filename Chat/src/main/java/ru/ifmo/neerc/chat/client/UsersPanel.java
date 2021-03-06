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
 * Date: 27.10.2004
 */
package ru.ifmo.neerc.chat.client;

import ru.ifmo.neerc.chat.user.UserEntry;
import ru.ifmo.neerc.chat.user.UserRegistry;
import ru.ifmo.neerc.chat.user.UserRegistryListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Matvey Kazakov
 */
public class UsersPanel extends JPanel {
    public static final ImageIcon iconUserNormal = new ImageIcon(
            UsersPanel.class.getResource("res/user_normal.gif"));
    public static final ImageIcon iconUserPower = new ImageIcon(
            UsersPanel.class.getResource("res/user_power.gif"));
    public static final ImageIcon iconUserNormalOffline = new ImageIcon(
            UsersPanel.class.getResource("res/user_normal_offline.gif"));
    public static final ImageIcon iconUserPowerOffline = new ImageIcon(
            UsersPanel.class.getResource("res/user_power_offline.gif"));
    public static final ImageIcon iconChannel = new ImageIcon(
            UsersPanel.class.getResource("res/user_channel.gif"));
    private static final int USER_ITEM_HEIGHT = 26;

    private UserEntry user;
    private JSplitPane splitter;
    private ArrayList<UserPickListener> listeners = new ArrayList<UserPickListener>();
    private NameColorizer colorizer;

    public UsersPanel(UserEntry user, NameColorizer colorizer) {
        this.user = user;
        this.colorizer = colorizer;

        setLayout(new BorderLayout());
        ListData model = new ListData();
        final JList<UserEntry> userList = new JList<UserEntry>();
        userList.setModel(model);
        userList.setCellRenderer(new UserListCellRenderer());
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UserRegistry.getInstance().addListener(model);
        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                UserEntry user = (UserEntry)userList.getSelectedValue();
                if (user != null && e.getClickCount() % 2 == 0) {
                    for (UserPickListener listener : listeners) {
                        listener.userPicked(user);
                    }
                }
            }
        });
        add(new JScrollPane(userList), BorderLayout.CENTER);
    }
    
    public void setSplitter(JSplitPane splitter) {
        this.splitter = splitter;
    }
    
    private void resize(int users) {
        if (splitter == null) return;
        splitter.setDividerLocation(users * USER_ITEM_HEIGHT + 5);
    }
    
    public void addListener(UserPickListener listener) {
        listeners.add(listener);
    }

    private class ListData extends AbstractListModel<UserEntry> implements UserRegistryListener {

        private UserEntry[] userEntries;

        public ListData() {
            init();
        }

        private void init() {
            userEntries = UserRegistry.getInstance().serialize();
            Arrays.sort(userEntries);
            resize(userEntries.length);
        }

        public synchronized int getSize() {
            return userEntries.length;
        }

        public synchronized UserEntry getElementAt(int index) {
            return userEntries[index];
        }

        public void userChanged(UserEntry userEntry) {
            update();
        }

        public void userPresenceChanged(UserEntry userEntry) {
            update();
        }

        private void update() {
            init();
            fireContentsChanged(this, 0, userEntries.length);
        }
    }

    private class UserListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            UserEntry entry = (UserEntry) value;
            setBackground(list.getBackground());
            setIcon(entry.isPower() ?
                    (entry.isOnline() ? iconUserPower : iconUserPowerOffline) :
                    (entry.isOnline() ? iconUserNormal : iconUserNormalOffline)
            );

            setEnabled(list.isEnabled());
            Font font = list.getFont();
            if (entry.getId() == user.getId()) {
                font = font.deriveFont(Font.BOLD);
            } else {
                font = font.deriveFont(Font.PLAIN);
            }
            setFont(font);
            setText(entry.getName());

            if (entry.isOnline()) {
                setForeground(colorizer.generateColor(entry));
            } else {
                setForeground(Color.lightGray);
            }

            setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

            return this;
        }
    }

}

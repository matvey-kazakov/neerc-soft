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
package ru.ifmo.neerc.chat.client;

import ru.ifmo.neerc.chat.message.Message;
import ru.ifmo.neerc.chat.message.MessageListener;
import ru.ifmo.neerc.chat.message.ServerMessage;
import ru.ifmo.neerc.chat.message.UserMessage;
import ru.ifmo.neerc.chat.user.UserEntry;
import ru.ifmo.neerc.chat.user.UserRegistry;
import ru.ifmo.neerc.chat.utils.ChatLogger;
import ru.ifmo.neerc.task.Task;
import ru.ifmo.neerc.task.TaskActions;
import ru.ifmo.neerc.task.TaskRegistry;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Log file
 * TODO: List instead of Text area for chat log
 *
 * @author Matvey Kazakov
 */
public abstract class AbstractChatClient extends JFrame implements MessageListener {

    public ChatArea outputArea;
    public ChatArea outputAreaJury;
    public JTextArea inputArea;
    public JLabel neercTimer = new JLabel();
    protected JLabel connectionStatus = new JLabel();
    private JLabel subscriptionsList = new JLabel();
    protected JButton resetButton;
    protected TaskRegistry taskRegistry = TaskRegistry.getInstance();
    protected UserEntry user;
    UsersPanel usersPanel;
    protected int localHistorySize;
    private static final int MAX_MESSAGE_LENGTH = 500;

    ChannelList channelsSubscription = new ChannelList();

    private JSplitPane powerSplitter;

    protected boolean isBeepOn = false;

    protected boolean sendOnEnter = false;

    protected TimerTicker ticker = new TimerTicker(neercTimer);
    protected NameColorizer colorizer = new NameColorizer();

    protected Chat chat;

    public AbstractChatClient() {
    }

    protected AdminTaskPanel taskPanel;

    protected void setupUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = createMainPanel();
        taskPanel = new AdminTaskPanel(this, taskRegistry, chat, user.getName());

        powerSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        powerSplitter.setTopComponent(mainPanel);
        powerSplitter.setBottomComponent(taskPanel);
        powerSplitter.setResizeWeight(1.0);
        powerSplitter.setDividerLocation(600);
        setContentPane(powerSplitter);
        setTitle("NEERC chat: " + user.getName());
        setSize(800, 800);
        setLocationRelativeTo(null);
    }

    private JPanel createMainPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());
        outputArea = new ChatArea(user, colorizer);
        outputAreaJury = new ChatArea();
        inputArea = createInputArea();
        JScrollPane outputAreaScroller = new JScrollPane(outputArea);
        JScrollPane outputAreaScrollerJury = new JScrollPane(outputAreaJury);
        JSplitPane outputSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, outputAreaScrollerJury,
                outputAreaScroller);
        setupSplitter(outputSplitter);
        outputAreaScrollerJury.setMinimumSize(new Dimension(300, 100));
        JSplitPane chatSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, outputSplitter,
                new JScrollPane(inputArea));
        setupSplitter(chatSplitter);
        chatSplitter.setResizeWeight(1);
        chatSplitter.setDividerLocation(526);
        chatPanel.add(chatSplitter, BorderLayout.CENTER);

        UserPickListener setPrivateAddresseesListener = new UserPickListener() {
            public void userPicked(UserEntry user) {
                setPrivateAddressees(user.getName());
            }
        };

//        JPanel controlPanel = new JPanel(new BorderLayout());
        usersPanel = new UsersPanel(user, colorizer);
        usersPanel.addListener(setPrivateAddresseesListener);

        outputArea.addUserPickListener(setPrivateAddresseesListener);
//        TaskPanel personalTasks = new TaskPanel(taskRegistry, user, chat);
//        JSplitPane controlSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, users, personalTasks);
//        setupSplitter(controlSplitter);
//        controlSplitter.setResizeWeight(1);
//        controlSplitter.setDividerLocation(300);
//        controlPanel.add(controlSplitter, BorderLayout.CENTER);
//        users.setSplitter(controlSplitter);

        JPanel topPanel = new JPanel(new BorderLayout());
        JSplitPane mainSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, usersPanel, chatPanel);
        setupSplitter(mainSplitter);
        mainSplitter.setDividerLocation(100);
        topPanel.add(mainSplitter);
        topPanel.add(createToolBar(), BorderLayout.NORTH);
        return topPanel;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        JButton tasks = new JButton(new ImageIcon(AbstractChatClient.class.getResource("res/btn_tasks.gif")));
        tasks.setToolTipText("Change task list position");
        tasks.setFocusable(false);
        tasks.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                powerSplitter.setOrientation(1 - powerSplitter.getOrientation());
            }
        });
        toolBar.add(tasks);

        JButton about = new JButton(new ImageIcon(AbstractChatClient.class.getResource("res/btn_about.gif")));
        about.setToolTipText("About");
        about.setFocusable(false);
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AboutBox(AbstractChatClient.this).setVisible(true);
            }
        });
        toolBar.add(about);

        final ImageIcon beepOnImage = new ImageIcon(AbstractChatClient.class.getResource("res/btn_beep_on.png"));
        final ImageIcon beepOffImage = new ImageIcon(AbstractChatClient.class.getResource("res/btn_beep_off.png"));
        final JButton mute = new JButton(beepOffImage);
        mute.setFocusable(false);
        mute.setToolTipText(isBeepOn ? "Turn beep off" : "Turn beep on");
        mute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isBeepOn = !isBeepOn;
                mute.setIcon(isBeepOn ? beepOnImage : beepOffImage);
                mute.setToolTipText(isBeepOn ? "Turn beep off" : "Turn beep on");
            }
        });
        toolBar.add(mute);

        final ImageIcon enterImage = new ImageIcon(AbstractChatClient.class.getResource("res/btn_enter.png"));
        final ImageIcon ctrlEnterImage = new ImageIcon(AbstractChatClient.class.getResource("res/btn_ctrl_enter.png"));
        final JButton sendModeSwitch = new JButton(ctrlEnterImage);
        sendModeSwitch.setFocusable(false);
        final String sendModeEnter = "Messages are sent on Enter";
        final String sendModeCtrlEnter = "Messages are sent on Ctrl+Enter";
        sendModeSwitch.setToolTipText(sendOnEnter ? sendModeEnter : sendModeCtrlEnter);
        sendModeSwitch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendOnEnter = !sendOnEnter;
                sendModeSwitch.setIcon(sendOnEnter ? enterImage : ctrlEnterImage);
                sendModeSwitch.setToolTipText(sendOnEnter ? sendModeEnter : sendModeCtrlEnter);
            }
        });
        toolBar.add(sendModeSwitch);

        final ImageIcon coloredImage = new ImageIcon(AbstractChatClient.class.getResource("res/btn_colored.png"));
        final ImageIcon blackAndWhiteImage = new ImageIcon(AbstractChatClient.class.getResource("res/btn_black_and_white.png"));
        final JButton chatColorSwitch = new JButton(coloredImage);
        chatColorSwitch.setFocusable(false);
        final String chatColored = "Colored names in chat";
        final String chatBlackAndWhite = "Black names in chat";
        chatColorSwitch.setToolTipText(chatColored);
        chatColorSwitch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                colorizer.setColored(!colorizer.isColored());
                chatColorSwitch.setIcon(colorizer.isColored() ? coloredImage : blackAndWhiteImage);
                chatColorSwitch.setToolTipText(colorizer.isColored() ? chatColored : chatBlackAndWhite);

                outputArea.repaint();
                usersPanel.repaint();

            }
        });
        toolBar.add(chatColorSwitch);

        resetButton = new JButton("Reconnect");
        resetButton.setFocusable(false);

        subscriptionsList.setText(channelsSubscription.toString());

        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(subscriptionsList);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(connectionStatus);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(neercTimer);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(resetButton);
        return toolBar;
    }

    private void setupSplitter(JSplitPane chatSplitter) {
        chatSplitter.setDividerSize(2);
        chatSplitter.setOneTouchExpandable(false);
    }

    private JTextArea createInputArea() {
        final JTextArea inputArea = new JTextArea(2, 45);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        if (!user.isPower()) {
            inputArea.setDocument(new PlainDocument() {
                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                    if (getLength() + str.length() > MAX_MESSAGE_LENGTH) {
                        str = str.substring(0, MAX_MESSAGE_LENGTH - getLength());
                    }
                    super.insertString(offs, str, a);
                }
            });
        }
        final MessageLocalHistory messageLocalHistory = new MessageLocalHistory(localHistorySize);
        inputArea.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e1) {
                if (e1.getKeyChar() == KeyEvent.VK_ENTER) {
                    boolean hasCtrl = (e1.getModifiers() & KeyEvent.CTRL_MASK) > 0;
                    if (hasCtrl != sendOnEnter) {
                        String text = inputArea.getText().trim();
                        if (text.equals("")) return;

                        Matcher privateMatcher = Pattern.compile(ChatMessage.PRIVATE_FIND_REGEX, Pattern.DOTALL).matcher(text);
                        if (privateMatcher.find() && privateMatcher.groupCount() > 0) {
                            messageLocalHistory.setLastPrivateAddressees(privateMatcher.group(1));
                        }

                        messageLocalHistory.add(text);
                        send(text);
                    } else {
                        if (hasCtrl) {
                            inputArea.setText(inputArea.getText() + "\n");
                        }
                    }
                }
            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP && (e.getModifiers() & KeyEvent.CTRL_MASK) == 0) {
                    try {
                        int caretPosition = inputArea.getCaretPosition();
                        int lineNum = inputArea.getLineOfOffset(caretPosition);
                        if (lineNum == 0) {
                            setPrivateAddressees(messageLocalHistory.getLastPrivateAddressees());
                        }

                    } catch (BadLocationException e1) {
                        // it's not like anything can be done here
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && (e.getModifiers() & KeyEvent.CTRL_MASK) > 0) {
                    String message = messageLocalHistory.moveDown();
                    if (message != null) {
                        inputArea.setText(message);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP && (e.getModifiers() & KeyEvent.CTRL_MASK) > 0) {
                    String message = messageLocalHistory.moveUp();
                    if (message != null) {
                        inputArea.setText(message);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    inputArea.setText("");
                }
            }
        });
        return inputArea;
    }

    protected void setPrivateAddressees(String addressees) {
        if (!"".equals(addressees)) {
            String text = inputArea.getText();
            text = text.replaceAll("\\A[a-zA-Z0-9%]+>\\s*", "");
            text = addressees + "> " + text;
            inputArea.setText(text);
        }
        inputArea.requestFocus();
    }

    protected void send(String text) {
        // ensure that null won't be here
        text = String.valueOf(text);
        String pattern = "^@(todo|task|confirm|ok|okfail|reason|question|q)( [\\w,]+)? (.*)$";
        Matcher matcher = Pattern.compile(pattern, Pattern.MULTILINE).matcher(text);
        while (matcher.find()) {
            String type = TaskActions.getTypeByAlias(matcher.group(1));
            String to = matcher.group(2) == null ? "" : matcher.group(2).substring(1);
            String title = matcher.group(3);
            Task task = new Task(type, title);
            for (UserEntry user : UserRegistry.getInstance().findMatchingUsers(to)) {
                String username = user.getName();
                if (task.getStatus(username) == null) {
                    task.setStatus(username, "none", "");
                }
            }
            chat.write(task);
        }

        // channels support
        Matcher channelMatches = Pattern.compile("^/s\\s+" + ChatMessage.CHANNEL_MATCH_REGEX + "\\s*$", Pattern.DOTALL).matcher(text);
        if (channelMatches.find()) {
            channelsSubscription.subscribeTo(channelMatches.group(1));
            subscriptionsList.setText(channelsSubscription.toString());
        }

        channelMatches = Pattern.compile("^/d\\s+" + ChatMessage.CHANNEL_MATCH_REGEX + "\\s*$", Pattern.DOTALL).matcher(text);
        if (channelMatches.find()) {
            channelsSubscription.unsubscribeFrom(channelMatches.group(1));
            subscriptionsList.setText(channelsSubscription.toString());
        }

        int destination = -1;
        // do not echo commands (including mistyped) to chat
        if (!Pattern.compile("^(@|/)\\w+ .*", Pattern.DOTALL).matcher(text).matches()) {
            chat.write(new UserMessage(user.getJid(), destination, text));
        }
        inputArea.setText("");
    }

    public void processMessage(Message message) {
        ChatMessage chatMessage = null;
        if (message instanceof ServerMessage) {
            ServerMessage serverMessage = (ServerMessage) message;
            chatMessage = ChatMessage.createServerMessage(
                    serverMessage.getText()
            );
        } else if (message instanceof UserMessage) {
            chatMessage = ChatMessage.createUserMessage((UserMessage) message);
            String jid = user.getJid();

            if (chatMessage.isPrivate()
                    && !jid.equals(chatMessage.getUser().getJid())
                    && !jid.equals(chatMessage.getTo())
                    && !channelsSubscription.isSubscribed(chatMessage.getTo())
                    ) {
                // foreign private message
                return;
            }
        }
        processMessage(chatMessage);
    }

    public void processMessage(ChatMessage chatMessage) {
        if (chatMessage != null) {
            if (outputArea == null || outputAreaJury == null) {
                addMessage(chatMessage);
            } else {
                synchronized (messagesToShow) {
                    for (ChatMessage chatMessage1 : messagesToShow) {
                        showMessage(chatMessage1);
                    }
                    messagesToShow.clear();
                    showMessage(chatMessage);
                }
            }
        }
    }

    private void showMessage(ChatMessage chatMessage) {
        outputArea.addMessage(chatMessage);
        if (chatMessage.isSpecial()) {
            outputAreaJury.addMessage(chatMessage);
        }

        ChatLogger.logChat(chatMessage.log());
    }

    private final ArrayList<ChatMessage> messagesToShow = new ArrayList<ChatMessage>();

    private void addMessage(ChatMessage msg) {
        synchronized (messagesToShow) {
            messagesToShow.add(msg);
        }
    }

    protected void addToModel(ChatMessage message) {
        outputArea.addToModel(message);
        if (message.isSpecial()) {
            outputAreaJury.addToModel(message);
        }
    }
}

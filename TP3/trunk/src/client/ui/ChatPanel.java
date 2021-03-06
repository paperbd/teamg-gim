package client.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import util.Html;

import client.Model;
import client.GimClient;
import client.Smiley;
import client.User;

/**
 * General class for a chat panel.
 */
public class ChatPanel extends JPanel {

	protected Model model = Model.getInstance();

	protected String id;
	protected JTextArea chatBox;
	protected JEditorPane messages;
	protected JButton send;

	public boolean showTimestamps = model.getOptions().showTimestamps;
	public boolean enableLogging = model.getOptions().enableLogging;

	public BufferedWriter out;
	public String logfile;

	private static final long serialVersionUID = 1L;
	private LinkedList<String> messageQueue;

	private Smiley[] smilies = { new Smiley(":)", "Happy_smiley.png"), new Smiley(":-)", "Happy_smiley.png"),
			new Smiley(":(", "Sad_smiley.png"), new Smiley(":-(", "Sad_smiley.png"),
			new Smiley(":P", "Tonque_out_smiley.png"), new Smiley(":-P", "Tonque_out_smiley.png"),
			new Smiley(";(", "Crying_smiley.png"), new Smiley(";-(", "Crying_smiley.png"),
			new Smiley(":'(", "Crying_smiley.png"), new Smiley(";)", "Winking_smiley.png"),
			new Smiley(";-)", "Winking_smiley.png"), new Smiley(":D", "Very_happy_smiley.png"),
			new Smiley(":-D", "Very_happy_smiley.png"), new Smiley(":S", "Confused_smiley.png"),
			new Smiley(":-S", "Confused_smiley.png"), new Smiley("(X)", "Xbox.png"), new Smiley("(@)", "Cat.png"),
			new Smiley("CALEF13", "calef13.png") };

	private int messageCount;
	protected User user;

	private Boolean inProgress = false;
	private boolean isFocused;

	Font f = (Font) UIManager.get("Label.font");
	Pattern pattern = Pattern
			.compile(
					"(?<![=\"/>])(www\\.|(http|https|ftp|news)://)(\\w+?\\.\\w+)+([a-zA-Z0-9~!@#$%^&*()_\\-=+\\/?.:;',]*)?([^.'# !])",
					Pattern.DOTALL | Pattern.UNIX_LINES | Pattern.CASE_INSENSITIVE);

	/**
	 * Constructor for a chat box. Creates styles, smiles and everything else we
	 * need
	 */
	public ChatPanel(User user, String roomID) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		this.user = user;
		id = roomID;
		messageQueue = new LinkedList<String>();

		// Create a new document for the messages
		messages = new JEditorPane();
		messages.setEditable(false);
		messages.setEditorKit(new HTMLEditorKit());
		messages.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent evt) {
				if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						java.awt.Desktop.getDesktop().browse(evt.getURL().toURI());
					} catch (IOException e) {
					} catch (URISyntaxException e) {
					}
				}
			}
		});

		isFocused = false;

		chatBox = new JTextArea(new LimitedDocument(1500));
		chatBox.setEditable(true);
		chatBox.setLineWrap(true);
		chatBox.setWrapStyleWord(true);
		chatBox.setText("");

		EnterListener enterListener = new EnterListener();
		chatBox.addKeyListener(enterListener);

		Date d = new Date();
		String timestamp = d.toString();

		String filename;
		boolean dirExists = false;

		File dir = new File(model.getPath() + "/logs/");
		if (!dir.exists())
			dir.mkdir();

		dirExists = dir.exists();

		if (dirExists) {
			if (this instanceof SingleChatPanel)
				filename = model.getPath() + "/logs/" + user.getEmail() + ".html";
			else
				filename = model.getPath() + "/logs/" + "Group Chat " + timestamp + ".html";

			File f = new File(filename);
			boolean exists = f.exists();

			try {
				out = new BufferedWriter(new FileWriter(filename, true));
				this.logfile = filename;
			} catch (IOException e) {
				this.enableLogging = false;
			}

			if (!exists && this.enableLogging) {
				if (this instanceof SingleChatPanel && user != null) {
					try {
						out.write("<html><head><title>Chat with " + user.getEmail() + "</title><head><body>");
					} catch (IOException e) {
					}
				} else {
					try {
						out.write("<html><head><title>Group chat on " + timestamp.replaceAll(":", "-")
								+ "</title><head><body>");
					} catch (IOException e) {
					}
				}

			}

		} else {
			this.enableLogging = false;
		}

	}

	public Boolean getInProgress() {
		return inProgress;
	}

	public void setInProgress(Boolean inProgress) {
		if (inProgress.equals(true)) {
			sendMessageQueue();
		}
		this.inProgress = inProgress;
	}

	public void chatBoxEnabled(final Boolean b) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				chatBox.setEnabled(b);
			}
		});
	}

	/**
	 * Case where the other user has closed their window. Don't want to concern
	 * the user with having to wait for room creation
	 */
	private void sendMessageQueue() {
		if (!messageQueue.isEmpty()) {
			System.out.println("The queue is " + messageQueue.toArray().toString());
		} else {
			System.out.println("Empty Queue");
		}

		while (!messageQueue.isEmpty()) {
			model.getServer().message(id, messageQueue.removeLast());
		}
	}

	public void setIsFocused(Boolean focused) {
		this.isFocused = focused;
	}

	public Boolean getIsFocused() {
		return isFocused;
	}

	public String getID() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getChatWith() {
		return this.user;
	}

	/**
	 * Set the focus of the mouse to the input text box
	 */
	public void setFocus() {
		chatBox.requestFocusInWindow();
	}

	/**
	 * Sends a message to the message log
	 */
	private void sendMessage() {
		if (chatBox.getText().trim().length() > 0) {

			receiveMessage(model.getSelf(), chatBox.getText().trim());

			if (getInProgress()) {
				model.getServer().message(id, chatBox.getText().trim());
			} else {
				messageQueue.push(chatBox.getText().trim());

				if (id.equals("-1")) {
					model.createRoom(user);
				}
			}
		}
	}

	/**
	 * Sends a received message to the message log
	 * 
	 * @param sender
	 *            The id of the person who sent the message
	 * @param message
	 *            The message itself
	 */
	public void receiveMessage(final User sender, final String message) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// If the user is not focused on this window, alert them a
				// message has been received.
				if (isFocused == false) {
					GimClient.alertMessage(sender.getNickname(), message);
				}

				String color = "#cc0000";
				if (sender == model.getSelf())
					color = "#000066";

				String msg = new String(util.Html.escape(message));
				for (Smiley s : smilies) {
					msg = Pattern.compile(Pattern.quote(s.getText()), Pattern.CASE_INSENSITIVE).matcher(msg)
							.replaceAll(
									"<img align=bottom src=\"file:///" + model.getPath() + "smiles/" + s.getIcon()
											+ "\" alt='" + s.getText() + "'>");
				}

				Matcher matcher = pattern.matcher(msg);
				msg = matcher.replaceAll("<a href=\"$0\">$0</a>");
				msg = msg.replaceAll(Pattern.quote("<a href=\"www."), "<a href=\"http://wwww.");
				msg = msg.replaceAll(Pattern.quote("\n"), "<br>");

				String timestamp = "";
				if (showTimestamps) {
					Calendar c = new GregorianCalendar();
					timestamp = String.format("(%02d:%02d:%02d) ", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
							c.get(Calendar.SECOND));
				}

				StringBuffer sb = new StringBuffer();
				sb.append("<table width=100% cellpadding=0 border=0 style='padding-top: 2px; padding-bottom: 2px;'>");
				sb.append("<tr><td>");
				sb.append("<font face='" + f.getFontName() + ", Arial, sans-serif' color=" + color + ">" + timestamp
						+ "<b>");
				sb.append(Html.escape(sender.getNickname()) + ": </b></font>");
				sb.append("<font face='" + f.getFontName() + ", Arial, sans-serif'> " + msg);
				sb.append("</font></table>\n");
				msg = sb.toString();
				msg = msg.replace("  ", "&nbsp ");

				Document doc = (Document) messages.getDocument();
				try {
					((HTMLEditorKit) messages.getEditorKit()).read(new java.io.StringReader(msg), doc, doc.getLength());
					if (ChatPanel.this.enableLogging) {
						ChatPanel.this.out.write(msg);
						ChatPanel.this.out.flush();
					}
				} catch (IOException e) {
				} catch (BadLocationException e) {
				}

				messageCount += 1;
				if (messageCount == 1)
					showChat();

				messages.setCaretPosition(doc.getLength());

			}

		});

	}

	/**
	 * Show the chat window
	 */
	private void showChat() {
		GimClient.getWindowIdentifierFromId(id).getWindow().setVisible(true);
	}

	/**
	 * Action listeners
	 */
	public class SendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (chatBox.getText().trim().length() != 0) {
				sendMessage();
				chatBox.setText("");
			}
			chatBox.requestFocusInWindow();
		}

	}

	/**
	 * A Key listener to send messages upon pressing "Enter"
	 */
	public class EnterListener implements KeyListener {

		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == KeyEvent.VK_ENTER && e.getModifiers() != 1) {
				if (chatBox.getText().trim().length() != 0) {
					sendMessage();
				}
				chatBox.setText("");
			} else if (e.getKeyChar() == KeyEvent.VK_ENTER && e.getModifiers() == 1) {
				chatBox.append("\n");
			}
		}

		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
		}
	}

	public void systemMessage(String msg) {
		StringBuffer sb = new StringBuffer();
		sb.append("<table width=100% cellpadding=5 cellspacing=0 border=0><tr><td valign=bottom align=center>");
		sb.append("<b><font face='" + f.getFontName() + ", sans-serif' color=#888888>- " + msg);
		sb.append(" -</font></tr></td></table>\n");
		msg = sb.toString();

		Document doc = (Document) messages.getDocument();
		try {
			((HTMLEditorKit) messages.getEditorKit()).read(new java.io.StringReader(msg), doc, doc.getLength());
			if (ChatPanel.this.enableLogging) {
				ChatPanel.this.out.write(msg);
				ChatPanel.this.out.flush();
			}
		} catch (IOException e) {
		} catch (BadLocationException e) {
		}

		messages.setCaretPosition(doc.getLength());
	}

}

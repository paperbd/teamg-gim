package client.net;

import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import client.FriendList;
import client.Model;
import client.GimClient;
import client.User;
import client.ui.ChatWindow;
import client.ui.ChatWindowIdentifier;
import client.ui.ContactPanel;
import client.ui.GroupChatPanel;
import client.ui.LoginPanel;
import client.ui.SingleChatPanel;

/**
 * Contacts all the methods which should be called upon receipt of a command
 * from the server.
 */
public class ServerConnection implements NetworkingIn {

	private Model model = Model.getInstance();
	private ClientConnection server;

	/**
	 * Default constructor
	 */
	public ServerConnection() {
	}

	public void setServer(ClientConnection s) {
		this.server = s;
	}

	/**
	 * Download all the information needed to populate the model.
	 */
	public void authorised() {
		Thread t = new Thread(new Authorised());
		t.start();
	}

	/**
	 * Download all the information needed then changes to the new view
	 */
	class Authorised implements Runnable {
		@Override
		public void run() {
			server.friendlist();

			model.getFriendList().waitForInitilisation();

			server.updateAll();
			model.waitForInitilisation();

			ContactPanel panel = new ContactPanel();
			GimClient.getMainWindow().setMainPanel(panel);
			GimClient.getMainWindow().canLogout(true);
			model.getSelf().setStatus("online");
			server.setStatus("online");

			// TODO: Ask James if this should be here
			GimClient.setChatBoxes(true);
		}
	}

	/**
	 * Display a broadcast message sent by the server
	 * 
	 * @param message
	 */
	public void broadcast(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, "Broadcast from server: " + message);
			}
		});

	}

	public void emailInuseError(final String message) {
		// JOptionPane.showMessageDialog(GimClient.getMainWindow(),
		// "E-Mail is already in use.");

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JOptionPane.showMessageDialog(GimClient.getMainWindow(), message);
			}
		});

	}

	public void registered() {
		LoginPanel panel = new LoginPanel();
		panel.setParent(GimClient.getMainWindow());
		GimClient.getMainWindow().setMainPanel(panel);
	}

	/**
	 * Update the friend list
	 * 
	 * @param onlinelist
	 *            A list of online users
	 * @param offlinelist
	 *            A list of offline users
	 * @param blockedlist
	 *            A list of blocked users
	 */
	public void friendlist(LinkedList<String> onlinelist, LinkedList<String> offlinelist, LinkedList<String> blockedlist) {
		FriendList friendList = model.getFriendList();

		for (String e : onlinelist) {
			User u = model.getUser(e);

			if (u == null) {
				u = new User(e);
				model.addUser(u);
			}

			friendList.addUser(u);

			// We don't want to set their status to online if they're already
			// online, otherwise we will overwrite their actual status
			if (u.getStatus().equalsIgnoreCase("offline"))
				u.setStatus("ONLINE");
		}

		for (String e : offlinelist) {
			User u = model.getUser(e);

			if (u == null) {
				u = new User(e);
				model.addUser(u);
			}

			friendList.addUser(u);
			u.setStatus("OFFLINE");
		}

		for (String e : blockedlist) {
			if (!friendList.isBlocked(e)) {
				User u;
				if ((u = model.getUser(e)) == null)
					u = new User(e);

				model.addUser(u);
				friendList.addBlockedUser(u);
			}
		}

		model.getFriendList().setInitilised(true);

	}

	public void friendrequest(final String user, final String nickname) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				Object[] options = { "Accept", "Decline", };
				int n = JOptionPane.showOptionDialog(GimClient.getMainWindow(),
						"You have received a friend request from " + user + "(nickname :" + nickname
								+ ".) Would you like to accept?", "Freind Request", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

				if (n == 0) {
					model.getServer().accept(user);
				}
			}
		});
	}

	public void invalidArgumentError(String message) {
	}

	public void invalidEmailError(final String message) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				String error = "";
				if (!message.equals("")) {
					error += ":\n\n server reported:\n " + message;
				}

				JOptionPane.showMessageDialog(null, "Invalid user." + error);
			}
		});

	}

	public void kill(String message) {
	}

	public void logInDetailsIncorrectError(final String message) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				String error = "";
				if (!message.equals("")) {
					error += ":\n\n server reported:\n " + message;
				}

				JOptionPane.showMessageDialog(GimClient.getMainWindow(), "Log in Details Incorrect." + error);
			}
		});
	}

	public void loggedInFromAnotherLocationError(final String message) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				String error = "";
				if (!message.equals("")) {
					error += ":\n\n server reported:\n " + message;
				}

				JOptionPane.showMessageDialog(GimClient.getMainWindow(), "Logged in from another location." + error);
			}
		});
	}

	public void message(final String roomid, final User sender, final String message) {

		// This wasn't added to the event queue, so message was being routed
		// before window
		// created
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				GimClient.routeMessage(roomid, sender, message);

			}
		});

	}

	public void missingArgumentsError(String message) {

	}

	public void notifyDisplayPicture(String user) {
		model.getServer().getDisplayPicture(user);
	}

	/**
	 * Someone's nickname has changed, go get the new one.
	 * 
	 * @param user
	 *            The user who's nickname has changed
	 */
	public void notifyNickname(String user) {
		server.getNickname(user);
	}

	public void notifyPersonalMessage(String user) {
		server.getPersonalMessage(user);
	}

	public void notifyStatus(String user) {
		server.getStatus(user);
	}

	public void okay() {
	}

	public void passwordTooShortError(final String message) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				String error = "";
				if (!message.equals("")) {
					error += ":\n\n server reported :\n " + message;
				}

				JOptionPane.showMessageDialog(GimClient.getMainWindow(), "Password too short error." + error);

			}
		});
	}

	public void servertime(String servertime) {
	}

	public void tooManyArgumentsError(String message) {
	}

	public void unauthorised() {
	}

	public void unauthorisedError(String message) {
	}

	/**
	 * Update the users display picture
	 * 
	 * @param user
	 *            The user whos display picture is to be updated
	 * @param displayPicture
	 *            The base 64 encoded string over the display picture
	 */
	public void updateDisplayPicture(String user, String displayPicture) {
		User u = model.getUser(user);
		if (u != null)
			u.setDisplayPic(displayPicture);
	}

	/**
	 * Update the users nickname
	 * 
	 * @param user
	 *            The use whos nickname to update
	 * @param nickname
	 *            The new nickname
	 */
	public void updateNickname(String user, String nickname) {
		User u = model.getUser(user);
		if (u != null)
			u.setNickname(nickname);
	}

	/**
	 * Update the users personal message
	 * 
	 * @param user
	 *            The use who has a new personal message
	 * @param personalMessage
	 *            The new personal message
	 */
	public void updatePersonalMessage(String user, String personalMessage) {
		User u = model.getUser(user);
		if (u != null)
			u.setPersonalMessage(personalMessage);
	}

	/**
	 * Update the users status
	 * 
	 * @param user
	 *            The user who's status has changed
	 * @param status
	 *            The new status
	 */
	public void updateStatus(String user, String status) {
		User u = model.getUser(user);
		System.out.println("Update Status");
		if (u != null)
			u.setStatus(status);
	}

	public void uptime(String uptime) {
	}

	public void userDoesNotExistError(String message) {
		String error = "";
		if (!message.equals("")) {
			error += ":\n\n server reported :\n " + message;
		}

		JOptionPane.showMessageDialog(GimClient.getMainWindow(), "User does not exist error. " + error);
	}

	public void userAlreadyinFriendlistError(String message) {
		String error = "";
		if (!message.equals("")) {
			error += ":\n\n server reported :\n " + message;
		}

		JOptionPane.showMessageDialog(GimClient.getMainWindow(), "User already in friendslist. " + error);

	}

	public void usercount(String usercount) {

	}

	public void created(final String roomid) {
		// get next list of users

		final LinkedList<User> contacts = model.getNextRoom();
		final Boolean isGroup = model.getNextType();

		// open new chat window
		if (isGroup) {

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					ChatWindowIdentifier l = GimClient.getWindowIdentifierFromId(roomid);
					if (l == null) {

						GroupChatPanel gcp = new GroupChatPanel(roomid);
						// GimClient.addRoom(gcp);
						gcp.setInProgress(true);

						ChatWindow ui = new ChatWindow("GIM - Group Chat", gcp);
						GimClient.addWindow(null, ui, gcp);

						ui.setLocationRelativeTo(null);// center new chat window
						model.getServer().roomusers(roomid);
						ui.setVisible(true);

					}

				}
			});

		} else {

			System.out.println("(created method) creating single chat win, room id " + roomid);

			// if we already have a window...
			final ChatWindowIdentifier l = GimClient.getWindowIdentifierFromUser(contacts.getFirst());
			
			System.out.println("contacts.getFirst() is " + contacts.getFirst());
			
			if (l != null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						l.getChatPanel().setId(roomid);

					}
				});

			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SingleChatPanel scp = new SingleChatPanel(contacts.getFirst(), roomid);

						ChatWindow ui = new ChatWindow("Chat with " + contacts.getFirst().getEmail() + " - GIM", scp);
						GimClient.addWindow(contacts.getFirst(), ui, scp);

						ui.setLocationRelativeTo(null);// center new chat window
						ui.setVisible(true);
					}
				});
			}

		}

		// invite contacts
		for (User u : contacts) {
			model.getServer().invite(roomid, u.getEmail());
		}

	}

	public void invited(User user, String roomid) {
		System.out.println("invited to room " + roomid + "by user " + user);

		model.getServer().type(roomid);

		// Store who gave the invitation, for when we ask the user if they want
		// to join
		model.addInvitation(user);

		/*
		 * Gordon: it could be possible to display the list of users already in
		 * the room with this invitation... but that would be a bit of work.
		 * discuss? could be a low priority...
		 */
	}

	public void joined(final String user, final String roomid) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				System.out.println("user: " + user + "joined " + roomid);

				ChatWindowIdentifier l = GimClient.getWindowIdentifierFromId(roomid);

				// The other person has joined the personal chat, it is safe to
				// send messages
				if (l != null) {
					if (l.getChatPanel() instanceof SingleChatPanel) {
						l.getChatPanel().setInProgress(true);
					} else { // group chat
						model.getServer().roomusers(roomid);
						l.getChatPanel().systemMessage(user + " has joined the chat\n");

						if (model.getUser(user) == null) {
							User newUser = new User(user);
							model.addUser(newUser);
						}

					}

				}

			}

		});

	}

	public void left(String user, String roomid) {

		// this used to be FromUser... this change shouldn't screw anything up
		// as far as i know
		// but this is a reminder to myself...
		ChatWindowIdentifier l = GimClient.getWindowIdentifierFromId(roomid);
		if (l != null) {
			if (l.getChatPanel() instanceof SingleChatPanel) {
				l.getChatPanel().setInProgress(false);
				l.getChatPanel().setId("-1");
				model.getServer().leave(roomid);
			} else if (l.getChatPanel() instanceof GroupChatPanel) {
				// do this later...
				model.getServer().roomusers(roomid);
			}

		}

	}

	public void users(String[] users, String roomid) {
		User[] participants = new User[users.length];

		// add any new users we don't have a record of
		for (String user : users) {
			if (model.getUser(user) == null) {
				model.addUser(new User(user));
				System.out.println("Creating users in group chat, but we didn't download their infromation.");
			}
		}

		// create the participant list of the room
		for (int i = 0; i < users.length; i++) {
			if (model.getUser(users[i]) != null) {
				participants[i] = model.getUser(users[i]);
			}

		}

		// update group chat list
		((GroupChatPanel) GimClient.getWindowIdentifierFromId(roomid).getChatPanel()).updateUserList(participants);

	}

	public void group(final String roomid) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				User invitedBy = model.getNextInvitation();

				Object[] options = { "Accept", "Decline" };
				int n = JOptionPane.showOptionDialog(GimClient.getMainWindow(),
						"You have been invited to a group chat by " + invitedBy + ". Would you like to accept?",
						"Group chat invitation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						options, options[0]);

				if (n == 0) {
					model.getServer().join(roomid);

					GroupChatPanel gcp = new GroupChatPanel(roomid);

					ChatWindow ui = new ChatWindow("Group Chat - GIM", gcp);

					GimClient.addWindow(null, ui, gcp);
					ui.setVisible(true);
					gcp.setInProgress(true);

					ui.setLocationRelativeTo(null);// center new chat window
					model.getServer().roomusers(roomid);
				}
			}
		});
	}

	public void personal(final String roomid) {
		System.out.println("the room we were invited to is a personal chat, roomid: " + roomid);

		final User invitedBy = model.getNextInvitation();

		model.getServer().join(roomid);

		final ChatWindowIdentifier l = GimClient.getWindowIdentifierFromUser(invitedBy);
		if (l != null) { // already window open

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					l.getChatPanel().setId(roomid);
					l.getChatPanel().setInProgress(true); // find a better way
					// later
				}
			});
		}

		else { // there is no window for this user
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					SingleChatPanel scp = new SingleChatPanel(invitedBy, roomid);

					ChatWindow ui = new ChatWindow("GIM - Chat with " + invitedBy.getEmail(), scp);
					GimClient.addWindow(invitedBy, ui, scp);
					ui.setLocationRelativeTo(null);

					scp.setInProgress(true);
					// ui.setVisible(true);

					System.out.println("there was no window for the personal chat. finished making it");
					System.out.println("roomid: " + roomid + "chat with: " + invitedBy);
				}
			});
		}
	}

	public void notifyFriendsList() {
		server.friendlist();
	}

	public void invalidUserError(String message) {
		// the invitatiation we queued up was invalid
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, "Invalid user.");
			}
		});

	}

	public void userOfflineError(String message) {
		// the invitation we queued up was invalid
		model.getNextRoom();
	}

	public void connectionDroppedError() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JOptionPane.showMessageDialog(GimClient.getMainWindow(),
						"Connection to server lost. Try logging in again. ");
			}
		});
	}
}

package client.net;

import javax.swing.*;

import client.*;
import client.ui.*;

public class ServerConnection implements NetworkingIn {

	public void authorised() {
		ContactPanel panel = GimClient.getContactPanel();
		panel.setParent(GimClient.getMainWindow());
		GimClient.getMainWindow().setMainPanel(panel);
		GimClient.getMainWindow().canLogout(true);
		GimClient.getClient().setContactList();
		
		// Do this properly later...
		// may want to sign in appearing offline
		
		GimClient.getClient().setStatus("ONLINE");
		
	}

	public void broadcast(String message) {
		// TODO (heather): how to implement this? all chat windows or dialog
		// box?
	}

	public void emailInuseError(String message) {
		// JOptionPane.showMessageDialog(GimClient.getMainWindow(),
		// "E-Mail is already in use.");
		JOptionPane.showMessageDialog(GimClient.getMainWindow(), message);
	}

	public void registered() {
		LoginPanel panel = new LoginPanel();
		panel.setParent(GimClient.getMainWindow());
		GimClient.getMainWindow().setMainPanel(panel);
	}

	public void friendlist(String[] onlinelist, String[] offlinelist,
			String[] blockedlist) {
		GimClient.getClient().setOnlinefriends(onlinelist);
		GimClient.getClient().setOfflinefriends(offlinelist);
		GimClient.getClient().setBlockedfriends(blockedlist);

		// update the interface

		for (int i = 0; i < onlinelist.length; i++) {
			if (GimClient.getClient().getUser(onlinelist[i]) == null) {
				GimClient.getClient().addUser(onlinelist[i]);
				GimClient.getClient().getUser(onlinelist[i]).setStatus("Online");
			}
		}
		GimClient.getContactPanel().createNodes(onlinelist, offlinelist);

	}

	public void friendrequest(String user, String nickname) {

	}

	public void invalidArgumentError(String message) {

	}

	public void invalidEmailError(String message) {

	}

	public void kill(String message) {

	}

	public void logInDetailsIncorrectError(String message) {
		String error = "";
		if (!message.equals("")) {
			error += ":\n\n server reported:\n " + message;
		}

		JOptionPane.showMessageDialog(GimClient.getMainWindow(),
				"Log in Details Incorrect." + error);
	}

	public void loggedInFromAnotherLocationError(String message) {
		String error = "";
		if (!message.equals("")) {
			error += ":\n\n server reported:\n " + message;
		}

		JOptionPane.showMessageDialog(GimClient.getMainWindow(),
				"Logged in from another location." + error);
	}

	public void message(String roomid, String sender, String message) {
		GimClient.routeMessage(roomid, sender, message);
	}

	public void missingArgumentsError(String message) {

	}

	public void notifyDisplayPicture(String user) {
		GimClient.getClient().getDisplayPicture(user);
	}

	public void notifyNickname(String user) {
		GimClient.getClient().getNickName(user);

	}

	public void notifyPersonalMessage(String user) {
		GimClient.getClient().getPersonalMessage(user);

	}

	public void notifyStatus(String user) {
		// the user might have gone offline, tell the server
		// to send an updated buddy list
		// (this may change if the server sends it anyway...
		// need to talk to james on that one)
		GimClient.getClient().getFriendList();

		GimClient.getClient().getStatus(user);

	}

	public void okay() {

	}

	public void passwordTooShortError(String message) {
		String error = "";
		if (!message.equals("")) {
			error += ":\n\n server reported :\n " + message;
		}

		JOptionPane.showMessageDialog(GimClient.getMainWindow(),
				"Password too short error." + error);
	}

	public void servertime(String servertime) {

	}

	// don't think this one will exist... will need to ask james!
	/*
	 * @Override public void status(String status) {
	 * 
	 * }
	 */

	public void tooManyArgumentsError(String message) {

	}

	public void unauthorised() {

	}

	public void unauthorisedError(String message) {

	}

	public void updateDisplayPicture(String user, String displayPicture) {
		if (GimClient.getClient().getUser(user) != null) {
			User l = GimClient.getClient().getUser(user);
			if (l != null) {
				l.setDisplayPic(displayPicture);
				// lul'z: dunno how to get this into an imageIcon
			}

		} else {
			// uhm...
		}

	}

	public void updateNickname(String user, String nickname) {
		User l = GimClient.getClient().getUser(nickname);
		if (l != null) {
			l.setNickname(nickname);

			chatWindowIdentifier s = GimClient
					.getWindowIdentifierFromUser(user);

			if (s != null) {
				((SingleChatPanel) s.getCp()).setNickname(nickname);
			}
		}

	}

	public void updatePersonalMessage(String user, String personalmessage) {
		User l = GimClient.getClient().getUser(user);
		if (l != null) {
			l.setPersonalMessage(personalmessage);

			chatWindowIdentifier s = GimClient
					.getWindowIdentifierFromUser(user);

			if (s != null) {
				((SingleChatPanel) s.getCp())
						.setPersonalMessage(personalmessage);
			}
		}

	}

	public void updateStatus(String user, String status) {
		User l = GimClient.getClient().getUser(user);
		if (l != null) {
			l.setStatus(status);

			chatWindowIdentifier s = GimClient
					.getWindowIdentifierFromUser(user);

			if (s != null) {
				((SingleChatPanel) s.getCp())
						.setStatus(status);
			}
		}

	}

	public void uptime(String uptime) {

	}

	public void userDoesNotExistError(String message) {
		String error = "";
		if (!message.equals("")) {
			error += ":\n\n server reported :\n " + message;
		}

		JOptionPane.showMessageDialog(GimClient.getMainWindow(),
				"User does not exist error. " + error);
	}

	public void usercount(String usercount) {

	}

	public void created(final String roomid) {
		// get next list of users

		final String[] contacts = GimClient.getClient().getNextRoom();

		// TODO (heather): this if/else will not work! (presumably) gordon: why
		// not? :S
		// open new chat window
		if (contacts.length > 1) {

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					GroupChatPanel gcp = new GroupChatPanel(roomid);
					// GimClient.addRoom(gcp);

					GimUI ui = new GimUI("GIM - Group Chat", gcp);
					GimClient.addWindow(contacts[0], ui, gcp);

					ui.setLocationRelativeTo(null);// center new chat window
				}
			});
		} else {

			// if we already have a window...
			chatWindowIdentifier l = GimClient
					.getWindowIdentifierFromUser(contacts[0]);
			if (l != null) {
				l.getCp().setId(roomid);
				// l.getCp().setInProgress(true); // find a better way

			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SingleChatPanel scp = new SingleChatPanel(roomid);

						// set the chat to be with the user we invited to chat
						scp.setChatWith(contacts[0]);
						// scp.setInProgress(true);
						// </Gordon>

						// GimClient.addRoom(scp);
						
						// get contact info
						User l = GimClient.getClient().getUser(contacts[0]);
						if (l != null ) {
							scp.setNickname(l.getNickname());
							scp.setStatus(l.getStatus());
							scp.setPersonalMessage(l.getPersonalMessage());
						}
						

						GimUI ui = new GimUI("GIM - Chat with " + contacts[0],
								scp);
						GimClient.addWindow(contacts[0], ui, scp);

						ui.setLocationRelativeTo(null);// center new chat window
						ui.setVisible(true);
					}
				});
			}

		}

		// invite contacts
		for (String s : contacts) {
			GimClient.getClient().invite(roomid, s);
		}

	}

	public void invited(String user, String roomid) {
		GimClient.getClient().type(roomid);

		// Store who gave the invitation, for when we ask the user if they want
		// to join
		GimClient.getClient().addInvitation(user);

		/*
		 * Gordon: it could be possible to display the list of users already in
		 * the room with this invitation... but that would be a bit of work.
		 * discuss? could be a low priority...
		 */

	}

	public void joined(String user, final String roomid) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				System.out.println("got to the joined method");
				chatWindowIdentifier l = GimClient
						.getWindowIdentifierFromId(roomid);

				// The other person has joined the personal chat, it is safe to
				// send
				// messages
				if (l != null) {
					System.out.println("got to the if block");
					l.getCp().setInProgress(true);
				}

			}
		});

	}

	public void left(String user, String roomid) {
		chatWindowIdentifier l = GimClient.getWindowIdentifierFromUser(user);
		if (l != null) {
			if (l.getCp() instanceof SingleChatPanel) {
				l.getCp().setInProgress(false);
				l.getCp().setId("-1");
				GimClient.getClient().leave(roomid);
			} else if (l.getCp() instanceof GroupChatPanel) {
				// do this later...
			}

		}

	}

	public void users(String[] users, String roomid) {

	}

	public void group(String roomid) {
		String invitedBy = GimClient.getClient().getNextInvitation();

		// spawn a new window notifying the user they've been invited to a group
		// chat

	}

	public void personal(final String roomid) {
		System.out.println("someone invited us to a personal chat");

		/*
		 * gordon: spawn a personal chat window immediately ... oh wait, what if
		 * no message has been sent yet... ... we might need to keep a log of
		 * messages, and make the IM window visable ... only when a message has
		 * been received. This should only be a few lines in the chat window
		 * handler class. Like, when the "write message to window" method is
		 * invoked, if messagecount == 0, spawn a window...
		 */
		final String invitedBy = GimClient.getClient().getNextInvitation();

		GimClient.getClient().join(roomid);

		chatWindowIdentifier l = GimClient
				.getWindowIdentifierFromUser(invitedBy);
		if (l != null) {
			l.getCp().setId(roomid);
			l.getCp().setInProgress(true); // find a better way later
		} else { // there is no window for this user
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					SingleChatPanel scp = new SingleChatPanel(roomid);
					// gordon
					scp.setChatWith(invitedBy);
					scp.setInProgress(true);
					
					// set contact info
					
					User l = GimClient.getClient().getUser(invitedBy);
					if (l != null ) {
						scp.setNickname(l.getNickname());
						scp.setStatus(l.getStatus());
						scp.setPersonalMessage(l.getPersonalMessage());
					}
					// </gordon>
					// GimClient.addRoom(scp);
					GimUI ui = new GimUI("GIM - Chat with " + invitedBy, scp);
					GimClient.addWindow(invitedBy, ui, scp);
					ui.setLocationRelativeTo(null);// center new chat window
				}
			});
		}
	}

	public void notifyFriendsList() {
		GimClient.getClient().getFriendList();
	}

	@Override
	public void invalidUserError(String message) {
		// FIX THIS LATER TO PARSE FOR CONTEXT. FOR NOW, ASSUME
		// USER HAS DOUBLE CLICKED 'ONLINE' on buddy list

		// the invitatiation we queued up was invalid
		GimClient.getClient().getNextRoom();

	}

	@Override
	public void userOfflineError(String message) {
		// FIX THIS LATER TO PARSE FOR CONTEXT. FOR NOW, ASSUME
		// USER HAS DOUBLE CLICKED AN OFFLINE USER on buddy list

		// the invitatiation we queued up was invalid
		GimClient.getClient().getNextRoom();
	}
}

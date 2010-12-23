package client.net;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import client.*;
import client.ui.*;

public class ServerConnection implements NetworkingIn {

	public void authorised() {
		ContactPanel panel = new ContactPanel();
		panel.setParent(GimClient.getMainWindow());
		GimClient.getMainWindow().setMainPanel(panel);
		GimClient.getMainWindow().canLogout(true);
	}

	public void broadcast(String message) {
		// TODO how to implement this? all chat windows or dialog box?
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
		// JOptionPane.showMessageDialog(GimClient.getMainWindow(),
		// "Login details incorrect.");
		
		String error = "";
		if (!message.equals("") ) {
			error += ":\n\n server reported:\n " + message;
		}
		
		JOptionPane.showMessageDialog(GimClient.getMainWindow(),
				"Log in Details Incorrect." + error);
	}

	public void loggedInFromAnotherLocationError(String message) {
		// JOptionPane.showMessageDialog(GimClient.getMainWindow(),
		// "Already logged in at another location.");
		
		String error = "";
		if (!message.equals("") ) {
			error += ":\n\n server reported:\n " + message;
		}
		
		JOptionPane.showMessageDialog(GimClient.getMainWindow(),
				"Logged in from another location."
						+ error);
	}

	public void message(String roomid, String sender, String message) {

	}

	public void missingArgumentsError(String message) {

	}

	public void notifyDisplayPicture(String user) {

	}

	public void notifyNickname(String user) {

	}

	public void notifyPersonalMessage(String user) {

	}

	public void notifyStatus(String user) {

	}

	public void okay() {

	}

	public void passwordTooShortError(String message) {
		// JOptionPane.showMessageDialog(GimClient.getMainWindow(),
		// "Password is too short.");
		
		String error = "";
		if (!message.equals("") ) {
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

	}

	public void updateNickname(String user, String nickname) {

	}

	public void updatePersonalMessage(String user, String personalmessage) {

	}

	public void updateStatus(String user, String status) {

	}

	public void uptime(String uptime) {

	}

	public void userDoesNotExistError(String message) {
		// JOptionPane.showMessageDialog(GimClient.getMainWindow(),
		// "User does not exist.");
		String error = "";
		if (!message.equals("") ) {
			error += ":\n\n server reported :\n " + message;
		}
		
		JOptionPane.showMessageDialog(GimClient.getMainWindow(),
				"User does not exist error. "
						+ error);
	}

	public void usercount(String usercount) {

	}

	public void created(final String roomid) {
		// get next list of users

		// TODO: James Change back...
		// GimClient.getClient().getRoomList().remove();
		// Gordon: I've been working on it, so maybe not now...
		String[] contacts = GimClient.getClient().getNextRoom();

		// TODO this if/else will not work! (presumably) gordon: why not? :S
		// open new chat window
		if (contacts.length > 1) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					GroupChatPanel gcp = new GroupChatPanel(roomid);
					GimClient.addRoom(gcp);
					GimUI ui = new GimUI("GIM - Group Chat", gcp);
					ui.setLocationRelativeTo(null);// center new chat window
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					SingleChatPanel scp = new SingleChatPanel(roomid);
					GimClient.addRoom(scp);
					GimUI ui = new GimUI("GIM - Chat with Contact ", scp);
					ui.setLocationRelativeTo(null);// center new chat window
				}
			});
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

	public void joined(String user, String roomid) {

	}

	public void left(String user, String roomid) {

	}

	public void users(String[] users, String roomid) {

	}

	public void group(String roomid) {
		String invitedBy = GimClient.getClient().getNextInvitation();

		// spawn a new window notifying the user they've been invited to a group
		// chat

	}

	public void personal(String roomid) {
		/*
		 * gordon: spawn a personal chat window immediately ... oh wait, what if
		 * no message has been sent yet... ... we might need to keep a log of
		 * messages, and make the IM window visable ... only when a message has
		 * been received. This should only be a few lines in the chat window
		 * handler class. Like, when the "write message to window" method is
		 * invoked, if messagecount == 0, spawn a window...
		 */

	}
}

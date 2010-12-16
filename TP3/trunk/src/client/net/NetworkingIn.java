package client.net;

import java.util.ArrayList;

public interface NetworkingIn {
	
	// prelogin
	
	// :OKAY:;
	
	/** The last command was send successfully (this is sent where there is no other response) */
	void okay();
	
	// :SERVERSTATUS { USERS | TIME | UPTIME }:;
	
	/** The server has sent information about the global status of the server
	 * @param status
	 * 		The string with global information */
	void status(String status);
	
	/** The server has sent information about the server's time 
	 * @param servertime
	 * 		The server's time*/
	void servertime(String servertime);
	
	/** The server has sent the number of users connected to the server
	 * @param usercount
	 * 		The amount of user's connected */
	void usercount(String usercount);
	
	/** The server has sent information about its uptime
	 * @param uptime
	 * 		The uptime of the server */
	void uptime(String uptime);
	
	// :SERVERSTATUS ends
	
	/** The server has indicated that it wants to force the client to disconnect gracefully.
	 * @param message
	 * 		The reason why the server wants the client to disconnect gracefully. */
	void kill(String message);
	
	// :BROADCAST: <message>;
	
	/** The server has sent a global message to all connected user. This is likely to contain critical information
	 * and should be clearly displayed to the user.
	 * @param message
	 * 		The message from the server.  */
	void broadcast(String message);
	
	
	// :AUTH [ LOGGEDIN | UNAUTHORIZED ]:;
	
	// NOT SURE ABOUT THESE TWO... WILL TALK TO JAMES
	
	/** The server indicated the user is authorised */
	void authorised();
	
	/** The server indicated the user is unauthorised */
	void unauthorised();
	
	// end :AUTH
	
	// postlogin
	
	//:MESSAGE: <roomid> <sender> <message>;
	
	/** The server has indicated that the user has received a message
	 * @param roomid
	 * 		The id of the room the message was sent to
	 * @param sender
	 * 		The person the message was received from
	 * @param message
	 * 		The message the sender sent */
	void message(String roomid, String sender, String message);
	
	// need james to clarify on these:
	
	// :ROOM [ CREATED | JOINED | LEFT | INVITED | USERS ]: <roomid> {<user>};
	
	void created();
	
	void joined();
	
	void left();
	
	void invited();
	
	void users();
	
	// end :ROOM
	
	// :p feel free to tell me if you don't think this is a sensible way of dealing with this: 
	
	// :FRIENDLIST: ONLINE <user>{,<user>} OFFLINE <user>{,<user>} BLOCKED
	
	/** The server has sent the online status of all the users on the buddy list
	 * @param onlinelist
	 * 		An arraylist of the users online on the buddy list
	 * @param offlinelist
	 * 		An arraylist of the users offline on the buddy list
	 * @param blockedlist
	 * 		An arraylist of the users who are blocked on the buddy list
	 *  */
	
	void friendlist(ArrayList<String> onlinelist, ArrayList<String> offlinelist, ArrayList<String> blockedlist);
	
	// :FRIENDREQUEST: <user> <nickname>;
	
	/** Indicates the user has received a friend request */
	void friendrequest();
	
	// :UPDATE [ NICKNAME| STATUS | PERSONAL_MESSAGE | DISPLAY_PIC ]: <user>;
	
	/** The server has notified that a user has changed their nickname
	 * @param user
	 * 		The user that changed their nickname */
	void notifyNickname(String user);
	
	/** The server has notified that a user has changed their status 
	 * @param user
	 * 		The user that has changed their status*/
	void notifyStatus(String user);
	
	/** The server has notified that a user has changed their personal message
	 * @param user
	 * 		The user that has changed their personal message*/
	void notifyPersonalMessage(String user);
	
	/** The server has notified that a user has changed their display picture
	 * @param user
	 * 		The user that has changed their display picture*/
	void notifyDisplayPicture();
	
	// end :UPDATE
	
	// LET ME KNOW IF YOU THINK THIS IS A SILLY WAY TO GIVE YOU THIS STUFF: :P
	// I was considering processing it first... but maybe it's better i give you it raw, and you can process
	// it. DISCUSS Team G! :P
	
	
	// :INFO { NICKNAME | STATUS | PERSONAL_MESSAGE | DISPLAY_PIC }: <user> <data> {<user data>};
	
	/** The server has notified the user of  */
	void info(ArrayList<String> Attributes, ArrayList<String> Data);
	
	// errors
	
	

}

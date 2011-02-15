package uk.ac.glasgow.minder.uistate.test;

import java.io.File;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.minder.event.impl.Controller;
import uk.ac.glasgow.minder.recipient.Privilege;
import uk.ac.glasgow.minder.recipient.Recipient;
import uk.ac.glasgow.minder.recipient.User;
import uk.ac.glasgow.minder.recipient.impl.MailingList;
import uk.ac.glasgow.minder.recipient.impl.RecipientStoreImpl;
import uk.ac.glasgow.minder.uistate.impl.UIStateImpl;

public class AddUserToMLTest {

	private RecipientStoreImpl rs;
	private UIStateImpl s;
	
	@Before
	public void setUp() throws Exception {
		rs = new RecipientStoreImpl();
		rs.addUser("Administrator", "admin", "monkey",
		new InternetAddress("monkey.me@glasgow.ac.uk"), Privilege.ADMINISTRATOR);
		
		s = new UIStateImpl(rs, new Controller(rs));
		s.login("admin", "monkey");
	}
	
	@After
	public void tearDown() throws Exception {
		File target = new File("mailing.obj");
		target.delete();
		target = new File("users.obj");
		target.delete();
	}
	
	@Test
	public void userDoesntExist() {
		s.createMailingList("Level 4");
		s.addUserToMailingList("Frank Newton", "Level 4");
		
		Set<Recipient> recipients = s.searchRecipients("Level 4");
		Object[] recipAry = recipients.toArray();
		Set<User> u = ((MailingList) recipAry[0]).getMembers();
		
		Assert.assertEquals(0, u.size());
	}

	@Test
	public void noUserSpecified() {
		s.createMailingList("Level 4");
		s.addUserToMailingList("", "Level 4");
		
		Set<Recipient> recipients = s.searchRecipients("Level 4");
		Object[] recipAry = recipients.toArray();
		Set<User> u = ((MailingList) recipAry[0]).getMembers();
		
		Assert.assertEquals(0, u.size());
	}
	
	@Test
	public void userAlreadyInML() {
		s.createMailingList("Level 4");
		try {
			s.createUser("Mark Jones", "mark", "pineapple", new InternetAddress("mark@gmail.com"), Privilege.RECIPIENT);
		} catch (AddressException e) {}
		s.addUserToMailingList("mark", "Level 4");
		s.addUserToMailingList("mark", "Level 4");
		
		Set<Recipient> recipients = s.searchRecipients("Level 4");
		Object[] recipAry = recipients.toArray();
		Set<User> u = ((MailingList) recipAry[0]).getMembers();
		
		//TODO: does this overwrite users in mailing lists? does it matter?
		Assert.assertEquals(1, u.size());
	}
	
	@Test
	public void MLDoesNotExist() {
		try {
			s.createUser("John Smith", "John", "banana", new InternetAddress("jsmith@gmail.com"), Privilege.RECIPIENT);
		} catch (AddressException e) {}
		s.addUserToMailingList("John", "Level 7");
		
		Set<Recipient> recipients = s.searchRecipients("Level 7");
		
		Assert.assertEquals(0, recipients.size());
	}
	
	@Test
	public void noMLSpecified() {
		try {
			s.createUser("John Smith", "John", "banana", new InternetAddress("jsmith@gmail.com"), Privilege.RECIPIENT);
		} catch (AddressException e) {}
		
		s.addUserToMailingList("John", "");
		
		//TODO: How to test whether John was added to a mailing list?
		//Assert.
	}
	
	@Test
	public void correct() {
		s.createMailingList("Level 4");
		try {
			s.createUser("John Smith", "John", "banana", new InternetAddress("jsmith@gmail.com"), Privilege.RECIPIENT);
		} catch (AddressException e) {}
		s.addUserToMailingList("John", "Level 4");
		
		Set<Recipient> recipients = s.searchRecipients("Level 4");
		Object[] recipAry = recipients.toArray();
		Set<User> u = ((MailingList) recipAry[0]).getMembers();
		
		Assert.assertEquals(1, u.size());
	}
}

package uk.ac.glasgow.minder.uistate.test;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.minder.event.Event;
import uk.ac.glasgow.minder.event.impl.Controller;
import uk.ac.glasgow.minder.recipient.Privilege;
import uk.ac.glasgow.minder.recipient.impl.RecipientStoreImpl;
import uk.ac.glasgow.minder.uistate.impl.UIStateImpl;

public class AttachReminderTest {

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
	public void badEventID() {
		try {
			s.createUser("John Smith", "John", "banana", new InternetAddress("jsmith@gmail.com"), Privilege.RECIPIENT);
		} catch (AddressException e) {}
		
		s.attachReminderToEvent("GE2", "John", 7200000);
		Set<Event> events = s.searchEvents("GE2");
		
		Assert.assertEquals(0, events.size());
	}
	
	@Test
	public void badRecipID() {
		Calendar cal = new GregorianCalendar(2011, 4, 30);
		Date date = cal.getTime();
		s.createDeadlineEvent(date, "Class Test", "PSD3");
		Set<Event> events = s.searchEvents("Class Test");
		
		s.attachReminderToEvent(events.toArray(new Event[0])[0].getUid(), "Greg", 7200000);

		Object[] eventsAry = events.toArray();
		Properties eventProp = ((Event) eventsAry[0]).getEventProperties();
		
		Assert.assertEquals("0", eventProp.getProperty("numberReminders"));
	}
	
	@Test
	public void badTimeBefore() {
		try {
			s.createUser("John Smith", "John", "banana", new InternetAddress("jsmith@gmail.com"), Privilege.RECIPIENT);
		} catch (AddressException e) {}
		
		Calendar cal = new GregorianCalendar(2011, 4, 30);
		Date date = cal.getTime();
		s.createDeadlineEvent(date, "Class Test", "PSD3");
		Set<Event> events = s.searchEvents("Class Test");
		
		s.attachReminderToEvent(events.toArray(new Event[0])[0].getUid(), "John", -1);
		
		Object[] eventsAry = events.toArray();
		Properties eventProp = ((Event) eventsAry[0]).getEventProperties();
		
		Assert.assertEquals("0", eventProp.getProperty("numberReminders"));
	}
	
	@Test
	public void correct() {
		try {
			s.createUser("John Smith", "John", "banana", new InternetAddress("jsmith@gmail.com"), Privilege.RECIPIENT);
		} catch (AddressException e) {}
		
		Calendar cal = new GregorianCalendar(2011, 4, 30);
		Date date = cal.getTime();
		s.createDeadlineEvent(date, "Class Test", "PSD3");
		
		Set<Event> events = s.searchEvents("Class Test");
		s.attachReminderToEvent(events.toArray(new Event[0])[0].getUid(), "John", 7200000);
		
		Object[] eventsAry = events.toArray();
		Properties eventProp = ((Event) eventsAry[0]).getEventProperties();
		
		Assert.assertEquals("1", eventProp.getProperty("numberReminders"));
	}

}

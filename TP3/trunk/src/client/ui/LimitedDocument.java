package client.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Inspired by:
 * 
 * http://blogdieu.com/tutorial/code/java/swing/jtextfield/limit-jtextfield
 * -input-to-a-maximum-length.html
 * 
 */
public class LimitedDocument extends PlainDocument {

	private static final long serialVersionUID = 1L;
	private int limit;
	private boolean toUppercase = false;

	LimitedDocument(int limit) {
		super();
		this.limit = limit;
		System.out.println(limit);
	}

	LimitedDocument(int limit, boolean upper) {
		super();
		this.limit = limit;
		toUppercase = upper;
	}

	public void insertString(int offset, String str, AttributeSet attr)
			throws BadLocationException {
		if (str == null)
			return;

		if ((getLength() + str.length()) <= limit) {
			if (toUppercase)
				str = str.toUpperCase();
			super.insertString(offset, str, attr);
		}
	}
}

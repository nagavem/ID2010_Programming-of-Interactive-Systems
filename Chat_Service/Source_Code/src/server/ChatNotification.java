// ChatNotification.java
// 2018-08-22/fki Refactored for lab version 7
// 18-mar-2004/FK First version


package chat.server;

import java.io.Serializable;
import net.jini.core.event.RemoteEvent;


/**
 * This class implements the notification that is sent to the ChatClients
 * as a new piece of text in the discussion is distributed. The clients
 * can obtain the serial number by calling method getSequenceNumber ()
 * (defined in the superclass RemoteEvent) and the message text by calling
 * method getText () defined below.
 */
public class ChatNotification
  extends
    RemoteEvent implements Serializable 
{

  /**
   * The text of the message.
   */

 protected String text;

  /**
   * Creates a new ChatNotification instance.
   * @param source The object from which this instance originates.
   * @param msg    The message to the client.
   * @param serial The serial number of the message in the server's sequence.
   */
  public ChatNotification (Object source, String msg, int serial) {
    // Call the constructor of the superclass (RemoteEvent) explicitly
    // so that its fields can be initialized to what we want. Actually,
    // we are only putting the serial number in as the sequence nr, but
    // the other arguments could be there as well if we had use for them.
    super (source,		// Source
	   0,			// ID
	   serial,		// sequence number
	   null);		// handback
    this.text = msg;
  }

  /**
   * Returns the text message in the notification.
   * @return The text message.
   */
  public String getText () {
    return text;
  }
}

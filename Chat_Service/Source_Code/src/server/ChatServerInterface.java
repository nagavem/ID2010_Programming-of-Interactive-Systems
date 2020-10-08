// ChatServerInterface.java
// 2018-08-22/fki Refactored for lab version 7
// 14-oct-2004/FK New package.
// 25-mar-2004/FK New package.
// 18-mar-2004/FK First version

package chat.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Set;
import java.util.List;

import net.jini.core.event.RemoteEventListener;

/**
 * This interface is implemented by the ChatServer, and is used by ChatClient
 * to place requests. It must therefore be known to both implementations.
 */
public interface ChatServerInterface
  extends
    java.rmi.Remote
{
  /**
   * Used by ChatClient instances to inject a text message to be
   * distributed to registered ChatClientNotificationInterfaces.
   * @param msg The message.
   */
 	//----------------------//
public void say (String msg, RemoteEventListener rel)
 throws java.rmi.RemoteException;

  /**
   * Returns the server's user-friendly name.
   * @return The server's user-friendly name.
   */
  public String getName () throws java.rmi.RemoteException;

  /**
   * Used by ChatClient instances to register themselves as receivers of
   * remote notifications.
   * @param rel An object that implements net.jini.core.event.RemoteEvent
   *            interface.
   */
  public void register (RemoteEventListener rel)
    throws java.rmi.RemoteException;

  /**
   * Used by ChatClient instances to unregister themselves as receivers of
   * remote notifications.
   * @param rel An object that implements net.jini.core.event.RemoteEvent
   *            interface. This should be the same object as was originally
   *            used to register.
   */
  public void unregister (RemoteEventListener rel)
    throws java.rmi.RemoteException;

  
  public void addActiveUsers( String name)
		throws java.rmi.RemoteException;
		 

public void removeDummyUser(String name)
		throws java.rmi.RemoteException;

public List<String> sendActiveUsers()
		throws java.rmi.RemoteException;

public void swapUserName(String oldName, String newName)
		throws java.rmi.RemoteException;

public void AFKRequiredUsers(String name, final boolean status, final RemoteEventListener currentListner) 
		throws java.rmi.RemoteException;

public void AFK(String name) 
		throws java.rmi.RemoteException;
}

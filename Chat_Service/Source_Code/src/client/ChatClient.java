// ChatClient.java
// 2018-08-22/fki Refactored for lab version 7
// 26-mar-2004/FK Small fixes
// 25-mar-2004/FK Given to its own package.
// 18-mar-2004/FK First version

package chat.client;


// Standard JDK

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Jini

import net.jini.core.entry.Entry;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;

import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;

import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;

import net.jini.lookup.entry.Name;

// Chatserver

import chat.server.ChatServerInterface;
import chat.server.ChatNotification;

/**
 * This class implements the ChatClient application.
 */
public class ChatClient
  extends
    UnicastRemoteObject		// Since we accept remote calls
  implements
    ServiceDiscoveryListener,	// So we can receive service notifications
    RemoteEventListener		// So we can receive chat notifications
{
        protected ScheduledExecutorService persist;
        protected Deque<Long> check = new ArrayDeque<Long>();
	protected long counter =0;
        protected final long awayTime = 10;
	protected boolean AFKstatus = true;
  /**
   * Information string for the user. Printed by the help command.
   */
  protected static final String versionString = "fki-7.0";

  /**
   * Holds the Jini ServiceItems of found ChatServers.
   */
  protected Vector<ServiceItem> servers = new Vector<ServiceItem> ();

  /**
   * Maps from Jini ServiceID to ServiceItem.
   */
  protected HashMap<ServiceID,ServiceItem> serverIDs =
    new HashMap<ServiceID,ServiceItem> ();

  /**
   * Refers to the service object of the currently connected chat-service.
   */
  protected ChatServerInterface myServer = null;

  /**
   * The name the user has choosen to present itself as.
   */
  protected String myName = null;

  /**
   * Jini helper object (actually, a kind of service) that automates
   * the finding of interesting services.
   */
  protected ServiceDiscoveryManager sdm;

  /**
   * Refers to an object obtained from the Jini Service Discovery
   * Manager to locate a specific kind of service.
   */
  protected LookupCache luc;

  /**
   * Refers to a Jini matching template used to identify interesting
   * services.
   */
  protected ServiceTemplate chatServiceTemplate;

  /**
   * The class name of the interface implemented by Jini services we
   * want to find.
   */
  protected static final String csi = "chat.server.ChatServerInterface";

  /* *** Constructor ** */
  
  /**
   * Creates a new ChatClient instance.
   */
  public ChatClient ()
    throws
      ClassNotFoundException,
      IOException,
      RemoteException
  {
    // Create a service template so the lookup cache will have
    // something matching against discovered services. There are three
    // ways of matching against a service; the service id, an array of
    // service types (interface classes) or an array of attributes
    // (Entry). Here we just use the interface implemented by the
    // service.

    chatServiceTemplate =
      new ServiceTemplate (null,
			   new Class [] {java.lang.Class.forName (csi)},
			   null);

    // Next we create a Jini service discovery manager to manage all
    // interaction with Jini lookup servers for us.

    sdm = new ServiceDiscoveryManager (null, null);

    // Then we ask the SDM for a Jini lookup cache to manage lookups
    // against chat-services found. The arguments are the template for
    // matching, and ourselves so the cache can notify us via the
    // interface ServiceDiscoveryListener (which we implement below).

    luc = sdm.createLookupCache (chatServiceTemplate, null, this);
  }

  /* ***** Interface ServiceDiscoveryListener ***** */

  // The next three methods, serviceAdded, serviceChanged and
  // serviceRemoved, may be called at any time by the Jini lookup
  // cache object. The lookup cache has its own thread, talking to
  // Jini lookup servers and looking for services that match the
  // service template we gave it. When it has some news, it reports in
  // using the three callback methods.

  /**
   * The Jini lookup cache calls this method when it has found a new
   * service. The found service is in the post-event service item.
   * @param e The discovery event.
   */
  public void serviceAdded (ServiceDiscoveryEvent e) {
    ServiceItem sit = e.getPostEventServiceItem ();
    if (sit.service instanceof ChatServerInterface) {
      servers.add (sit);
      serverIDs.put (sit.serviceID, sit);
      System.out.println ("[Added server " + sit.toString () + "]");
    }
  }

  /**
   * The Jini lookup cache calls this method when a previously found
   * service has changed its registration. The old service item is in
   * the pre-event service item and the changed service item is in the
   * post-event service item.
   * @param e The discovery event.
   */
  public void serviceChanged (ServiceDiscoveryEvent e) {
    ServiceItem preSit  = e.getPreEventServiceItem ();
    ServiceItem postSit = e.getPostEventServiceItem ();
    if (postSit.service instanceof ChatServerInterface) {
      servers.remove (serverIDs.get (preSit.serviceID));
      servers.add (postSit);
      serverIDs.put (postSit.serviceID, postSit);
      System.out.println ("[Changed server " + postSit.toString () + "]");
    }
  }

  /**
   * The Jini lookup cache calls this method when a found service has
   * removed its registration. The removed service is found in the
   * pre-event service item.
   * @param e The discovery event.
   */
  public void serviceRemoved (ServiceDiscoveryEvent e) {
    ServiceItem sit = e.getPreEventServiceItem ();
    if (sit.service instanceof ChatServerInterface) {
      servers.remove (serverIDs.get (sit.serviceID));
      System.out.println ("[Removed server " + sit.toString () + "]");
    }
  }

  /* ***** Interface net.jini.core.event.RemoteEventListener ***** */

  /**
   * The ChatServer we are registered with (connected to) calls this
   * method to notify us of a new chat message.
   * @param rev  The remote event that is the notification.
   */
  public void notify (RemoteEvent rev)
    throws
      UnknownEventException,
      RemoteException
  {
    if (rev instanceof ChatNotification) {
      ChatNotification chat = (ChatNotification) rev;
      System.out.println (chat.getSequenceNumber () + " : " +
			  chat.getText ());
    }
  }
 //////////////////////////////////////////////////////////////////////////////
	public void removeUserName(ChatServerInterface server) throws RemoteException {
		if (server != null) {
			String removeData = myName;
			/// ...................///
			heartBeat(awayTime);
			/// ...................///
			server.removeDummyUser(removeData);
		}
	}

	public void registerUserName(ChatServerInterface server) throws RemoteException {
		if (server != null) {
			String data = myName;
			/// ...................///
			heartBeat(awayTime);
			/// ...................///
			server.addActiveUsers(data);
		}
	}

	public void receiveActiveUsers(ChatServerInterface server) throws RemoteException {
		if (server != null) {
			/// ...................///
			heartBeat(awayTime);
			/// ...................///
			System.out.println(server.sendActiveUsers());
		}
	}

	public void swapName(ChatServerInterface server, String oldName) throws RemoteException {
		if (server != null) {
			String data = myName;
			/// ...................///
			heartBeat(awayTime);
			/// ...................///
			server.swapUserName(data, oldName);
		}
	}

	public void disableAFK(ChatServerInterface server, boolean status) throws RemoteException {
		if (server != null) {
			/// ...................///
			AFKstatus = status;
			server.AFKRequiredUsers(myName, status, this);
			/// ...................///
		}
	}
	
	public void enableAFK(ChatServerInterface server, boolean status) throws RemoteException {
		if (server != null) {
			/// ...................///
			AFKstatus = status;
			server.AFKRequiredUsers(myName, status, this);
			/// ...................///
		}
	} 
//////////////////////////////////////////////////////////////////////////////

  /* *** ChatClient *** */

  /**
   * This method disconnects the chat client from a chat server. It
   * unregisters the client and supresses any exceptions generated by the
   * communication.
   * @param server The chat server to disconnect from.
   */
  public void disconnect(ChatServerInterface server) {
		if (server != null) {
			try {
				String serverName = server.getName();
				
///////////////////////////////////
				try {
					removeUserName(myServer);
				} catch (RemoteException e) {
// TODO Auto-generated catch block
					e.printStackTrace();
				}
				server.unregister(this);
////////////////////////////////////
				System.out.println("[Disconnected from " + serverName + "]");
			} catch (RemoteException rex) {
			}
		}
	}

  /**
   * This method implements the '.disconnect' user command.
   */
  public void userDisconnect () {
    if (myServer != null) {
      disconnect (myServer);
      myServer = null;
    }
    else {
      System.out.println ("[Client is not currently connected]");
    }
  }

  /**
   * This method implements the '.connect' user command. If a
   * servername pattern is supplied, the known chat services are
   * scanned for names in which the pattern is a prefix. If a null or
   * empty pattern is supplied, the connection attempt is directed at
   * the first known server (regardless of whether it is answering or
   * not). Upon a successful connect, any current service is disconnected.
   * @param serverNamePattern The substring to match against the server name.
   */
  public void connectToChat (String serverNamePattern)
  {
    // See if we know any servers at all.

    if (0 < servers.size ()) {
      ServiceItem selectedSit = null;
      String selectedName = "";

      // See if we can select a server by its name

      if (serverNamePattern == null || serverNamePattern.isEmpty()) {
	selectedSit = servers.elementAt(0); // The first one is the default
	for (Entry e : selectedSit.attributeSets)
	  if (e instanceof Name) {
	    selectedName = ((Name) e).name;
	    break;
	  }
      }
      else {
	// Build a map from server name to server
	HashMap<String,ServiceItem> nsmap = new HashMap<>();
	
	// Avoid concurrent modification of the Vector servers
	synchronized (servers) {
	  for (ServiceItem sit : servers) { // each known server
	    for (Entry e : sit.attributeSets) { // each attribute of the server
	      if (e instanceof Name) {
		String svname = ((Name) e).name;
		nsmap.put(svname, sit);
		break;		// out of for each attribute entry
	      }
	    }
	  }
	}

	// Scan the server names for a match against the given server
	// name pattern.
	for (String svname : nsmap.keySet()) {
	  if (svname.startsWith(serverNamePattern)) {
	    selectedSit = nsmap.get(svname);
	    selectedName = svname;
	    break;		// out of for each name string
	  }
	}
      }

      if (selectedSit == null) {
	System.out.printf("No servers found matching '%s'%n",
			  serverNamePattern);
      }
      else {
	// Connect to the selected server
	ChatServerInterface nextServer = 
	  (ChatServerInterface) selectedSit.service;

	// Check that we are not already connected
	if (myServer == nextServer) {
	  try {
	    System.out.printf("[Already connected to %s%n", myServer.getName());
	    return;
	  }
	  catch (RemoteException rex) {
	    disconnect(myServer);
	  }
	}
	
	System.out.printf("[Connecting to %s...", selectedName);
	System.out.flush();

	try {
	  nextServer.register(this);
	  System.out.println("ok]");
	}
	catch (RemoteException rex) {
	  nextServer = null;
	  System.out.println("failed]");
	}

	// Upon a succesful connect to the next server, disconnect
	// from the current one (if any), then make the next server
	// the current server.
	
	if (nextServer != null) {
	  if (myServer != null)
	    disconnect(myServer);
         
	  myServer = nextServer;
           ////////////////////////////////////////////////////////////////////
		 
		try {
			registerUserName(myServer);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	///////////////////////////////////////////////////////////////////////
	}
      }

    }
    else {
      System.out.println ("[There are no known servers]");
    }
  } // method connectToChat


  /**
   * This method implements the '.name' user command. It sets the name
   * the user has choosen for herself on the chat. If the name is null
   * or the empty string, the &quot;user.name&quot; system property is
   * used as a substitute.
   *
   * @param newName  The user's name.
   */
  public void setName(String newName) {
//////////////////////////////////////////////
		 if (myName != newName) {
			String oldName = myName;
			myName = newName;

			if (myName != null) {
				myName = myName.trim();
				//////////////////////////////////////////
				try {
					swapName(myServer, oldName);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//////////////////////////////////////////
				if (myName.length() == 0) {
					myName = null;
				}
			}
		}

		if (myName == null) {
			myName = System.getProperty("user.name");
		}
	}
  
  /**
   * This method implements the send command which is implicit in the
   * command interpreter (the input line does not start with a period).
   * @param text  The text to send to the currently connected server.
   */
  public void sendToChat(String text) throws RemoteException {
		if (myServer != null) {
			try {
				myServer.say(text, this);
				/// ...................///
				heartBeat(awayTime);
				/// ...................///
			} catch (RemoteException rex) {
				System.out.println("[Sending to server failed]");
			}
		} else {
			System.out.println("[Cannot send chat text: not connected to a server]");
		}
	}
	///.................///	
	public void heartBeat(long number) {
		ScheduledExecutorService scheduler=
				  Executors.newSingleThreadScheduledExecutor();
		Executors.newSingleThreadScheduledExecutor(); 
		Runnable task = new Runnable(){ 
			public void run() {
				try {
					myServer.AFK(myName);
					check.clear();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		};
		
		if(!check.isEmpty())
			persist.shutdownNow();
		

		
		long delay = number; 
		check.add(counter++);
		persist = scheduler;
		scheduler.schedule(task,delay, TimeUnit.SECONDS);
					
	}
    ///.................///

  /**
   * This method implements the '.list' and '.purge' user commands.
   * All known servers are listed and a call attempt is made with each.
   * Non-reachable servers are listed and if the purge parameter is true,
   * also removed from the list of known servers. Note that intermittent
   * network failures (not uncommon for wireless and mobile users) may
   * cause a service to appear down when it really is not.
   *
   * @param purge  False to just list the servers, true to remove the
   *               service objects of services that does not respond.
   */
  public void listServers (boolean purge) {
    if (servers.isEmpty ()) {
      System.out.println ("[There are no known servers at this time]");
      return;
    }

    synchronized (servers) {
      for (int i = 0; i < servers.size (); i++) {

	ServiceItem sit = servers.elementAt (i);
	ChatServerInterface chat = (ChatServerInterface) sit.service;
	String remoteName = null;
	String localName = null;

	for (int j = 0; j < sit.attributeSets.length; j++) {
	  Entry e = sit.attributeSets[j];
	  if (e instanceof Name) {
	    localName = ((Name) e).name;
	    break;
	  }
	}

	if (localName == null) {
	  localName = sit.service.toString ();
	}

	System.out.print ("[");
	try {
	  remoteName = chat.getName ();
	  System.out.print (remoteName);
	  if (chat == myServer) {
	    System.out.print (": connected");
	  }
	}
	catch (RemoteException rex) {
	  System.out.print (localName + ": not responding");
	  if (purge) {
	    servers.remove (i);
	    System.out.print (": PURGED");
	    i = i - 1;		// Compensate for for-loop increment
	  }
	}

	System.out.println ("]");
      }
    }
    
  }

  /**
   * This array holds the strings of the user command help text.
   */
  protected String [] cmdHelp = {
    "Commands (can be abbreviated):",
    ".list              List the currently known chat servers",
    ".purge             As list, but also forget non-responding servers",
    ".name <name>       Set the username presented by the chat client",
    ".c                 Connect to the default server",
    ".connect <string>  Connect to a server with a matching string",
    ".disconnect        Break the connection to the server",
    ".quit              Exit the client",
    ".help              This text",
    ".activeusers       To Get Active Users",
    ".disable AFK        To Disable AFK messages",
    ".enable AFK        To Enable AFK messages"
  };

  /**
   * Implements the '.help' user command.
   * @param argv Reserved for future used (e.g. '.help purge').
   */
  protected void showHelp (String [] argv) {
    System.out.println ("[" + versionString + "]");
    for (int i = 0; i < cmdHelp.length; i++) {
      System.out.println ("[" + cmdHelp[i] + "]");
    }
  }

  /**
   * Creates a new string which is the concatenation of the elements
   * in a string array, joined around a given delimiter string.
   *
   * @param sa         The string array to join together.
   * @param firstIndex The index of the first element in sa to consider.
   * @param delimiter  Delimiter string between elements in sa, or null.
   *
   * @return The concatenated result or at least the empty string.
   */
  protected String stringJoin (String [] sa, int firstIndex, String delimiter) {
    StringBuilder sb = new StringBuilder();
    String delim = (delimiter == null) ? "" : delimiter;

    if (sa != null) {
      if (firstIndex < sa.length) {
	sb.append(sa[firstIndex]);
	for (int i = firstIndex + 1; i < sa.length; i++) 
	  sb.append(delim).append(sa[i]);
      }
    }

    return sb.toString();
  }

  /**
   * The user command interpreter. Commands are read from standard input,
   * parsed and dispatched methods that either alter the client or sends
   * the text to the ChatServer (when connected).
   */
  public void readLoop () {
    boolean halted = false;
    BufferedReader d = new BufferedReader(new InputStreamReader(System.in));
    
    System.out.println ("[Output from the client is in square brackets]");
    System.out.println ("[Commands start with '.' (period). Try .help]");
    System.out.println ("[When connected, type text and hit return to send]");
 
    setName(myName);		// set default name

    while (!halted) {
      System.out.print ("Client> ");
      System.out.flush ();
      String buf = null;

      try {
	buf = d.readLine ();
      }
      catch (IOException iox) {
	iox.printStackTrace ();
	System.out.println ("\nI/O error in command interface.");
	halted = true;
	continue;
      }

      if (buf == null) {	// EOF in command input.
	halted = true;
	continue;
      }

      // Trim away leading and trailing space from the raw input.

      String arg = buf.trim ();

      // Check if the input starts with a period.

      if (arg.startsWith (".")) {

	// Skip the leading period and split the string into
	// fragments, separated by whitespace.
	String [] argv = arg.substring(1).split ("\\s++");

	// We treat the first word as a command verb and makes it lowercase
	// for easier matching.
	String verb = argv[0].toLowerCase ();

	// We accept any leading abbreviation and this is fine while
	// the number of commands is so small that their first
	// character is sufficiently distinctive.

	if ("quit".startsWith (verb)) {
	  halted = true;
	}
	else if ("connect".startsWith (verb)) {
	  connectToChat (stringJoin (argv, 1, " "));
	}
	else if ("disconnect".startsWith (verb)) {
	  userDisconnect ();
	}
	else if ("list".startsWith (verb)) {
	  listServers (false);
	}
	else if ("purge".startsWith (verb)) {
	  listServers (true);
	}
	else if ("name".startsWith (verb)) {
	  setName (stringJoin (argv, 1, " "));
	}
	else if ("help".startsWith (verb)) {
	  showHelp (argv);
	}
      //////////////////////////////////////////////
				else if ("activeusers".startsWith(verb)) {
					try {
						receiveActiveUsers(myServer);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// ***************//
				else if ("disableAFK".startsWith(verb)) {
					try {
						if (AFKstatus == true) {
							disableAFK(myServer, false);
							heartBeat(awayTime);
							System.out.println("AFK is now disabled");
						} else {
							heartBeat(awayTime);
							System.out.println("AFK is already disabled");
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if ("enableAFK".startsWith(verb)) {
					if (AFKstatus == true) {
						heartBeat(awayTime);
						System.out.println("AFK's already enabled");
					} else {
						try {
						heartBeat(awayTime);
						enableAFK(myServer,true);
						System.out.println("AFK is now enabled");
						} catch(RemoteException e) {
							e.printStackTrace();
						}
					}

				}
				///////////////////////////////////////////////
	else {
	  System.out.println ("[" + verb + ": unknown command]");
	}
      }
      else if (0 < arg.length ()) {
	
	 try {
				sendToChat(myName + ": " + arg);
                }
                catch(Exception e) {
                	e.printStackTrace();
                }
				
      }

    } // while not halted

    System.out.println ("[Quitting, please wait...]");

    disconnect (myServer);

    luc.terminate();
    System.out.println("[Lookup Cache terminated]");

    // Shut down the service discovery manager.

    sdm.terminate ();
    System.out.println("[System Discovery Manager terminated]");
  }

  // The main method.

  public static void main (String [] argv)
    throws
      ClassNotFoundException,
      IOException,
      RemoteException
  {

    if (System.getSecurityManager() == null)
      System.setSecurityManager(new SecurityManager());

    ChatClient cc = new ChatClient ();
    cc.readLoop ();
    System.exit(0);
  }
}

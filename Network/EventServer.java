package Network;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class EventServer
{
	public static void main(String[] args) {		
    	PrintStream output;
		try {
			output = new PrintStream(new FileOutputStream("output.txt"));
			System.setOut(output);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        
        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/events/*");
        
        System.out.println("Starting ServerMain thread");
        new Thread(new ServerMain()).start();
        
        System.out.println("Starting Server");
        try {
            server.start();
            System.out.println("Server Started");
            //server.dump(System.err);            
            server.join();
            System.out.println("Server Joined");                        
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
        }
        
    }
}

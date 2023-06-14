package client.main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import client.control.Control;
import client.exceptions.ResponseEnvelopeException;

public class MainClient {
	static Logger logger = LoggerFactory.getLogger(MainClient.class);
	private Control control;

	public MainClient(Control control) {
		this.control = control;
	}
	
	public static void main(String[] args) {

		logger.info("Info: client game started");

		String serverBaseUrl = args[1];
		String gameId = args[2];
		
		Control control = new Control(serverBaseUrl, gameId);
		MainClient client = new MainClient(control);
			
		try {
			client.control.playerRegistration();
			client.control.sendMap();
			client.control.saveMap();
			client.control.play();
	    } catch (ResponseEnvelopeException e) {
	        logger.error("Error with network communication: " + e.getMessage());
	    } catch (Exception e) {
	        logger.error("Unexpected Error: " + e.getMessage());
		}
		
		logger.info("Info: client game ended");
	}
}
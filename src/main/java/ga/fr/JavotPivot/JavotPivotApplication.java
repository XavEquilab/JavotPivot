package ga.fr.JavotPivot;

import java.net.InetAddress;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestClient;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class JavotPivotApplication {	
	private static final Logger _logger = LoggerFactory.getLogger(JavotPivotApplication.class);

	public static void main(String[] args) {
		try {
			ApplicationContext context = SpringApplication.run(JavotPivotApplication.class, args);
			_logger.info("Starting JavotPyvoooooooot");
	
			ThreadsSharedData.HyperviseurApiUrl = getHyperviseurApiUrl(context.getEnvironment());
			_logger.info("Hyperviseur API url = " + ThreadsSharedData.HyperviseurApiUrl);
	
			ThreadsSharedData.PyliersDictionnary = getPyliersDictionnary();
	
			// Run Command routine every second
			CommandThread commandThread = new CommandThread();
			commandThread.start();

			// Run Data routine every second
			DataThread dataThread = new DataThread();
			dataThread.start();
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
	}

	private static String getHyperviseurApiUrl(Environment p_environment) throws Exception{
		String hyperviseurApiUrl = p_environment.getProperty("javotPivot.hyperviseurApi.url");
		if(hyperviseurApiUrl == null ||hyperviseurApiUrl.isBlank() || hyperviseurApiUrl.isEmpty())
			hyperviseurApiUrl = getHyperviseurApiUrlFromNetwork();	
		return hyperviseurApiUrl;
	}
	private static String getHyperviseurApiUrlFromNetwork() throws Exception{
		List<String> reachableAddresses = getReachableAddresses();
		for (String reachableAddress : reachableAddresses) {
			//TODO: l'algo ici
			String configPylierResponse = ThreadsSharedData.HttpClient.get()
				.uri("http://" +  reachableAddress + "/api/isHyperviseur") // essayer avec https aussi ?
				.header("Accept", "application/json")
				.retrieve()
				.body(String.class);
			if(true) // jsp
			return reachableAddress;
		}
		return "127.0.0.1"; // local par defaut
	}
	private static List<String> getReachableAddresses() throws Exception{
		List<String> reachableAddresses = new ArrayList<String>(); 
		
		InetAddress localHost = InetAddress.getLocalHost();
		String localIpAddress = localHost.getHostAddress();
		String subnet = getSubnet(localIpAddress);
		
		// Scan the subnet
		for (int i = 1; i < 255; i++) {
			String host = subnet + i;
			if (isReachable(host)) {
				reachableAddresses.add(host);
			}
		}
		
		return reachableAddresses;
	}
	private static String getSubnet(String p_ipAddress) {
		// Assuming a subnet mask of 255.255.255.0
        return p_ipAddress.substring(0, p_ipAddress.lastIndexOf('.') + 1);
    }
	private static boolean isReachable(String p_ipAddress) {
		try {
			InetAddress inet = InetAddress.getByName(p_ipAddress);
            return inet.isReachable(100); // Timeout in milliseconds
        } catch (Exception e) {
			//e.printStackTrace();
            return false;
        }
    }

	private static ConcurrentHashMap<Integer, String> getPyliersDictionnary() throws Exception{
		ConcurrentHashMap<Integer, String> pyliersDictionnary = new ConcurrentHashMap<Integer, String>();
		
		// get json pyliers config from hyperviseur API
		PylierDB[] pylierDBs = ThreadsSharedData.HttpClient.get()
			.uri(ThreadsSharedData.HyperviseurApiUrl + "/api/config/pylier.json")
			.header("Accept", "application/json")
			.retrieve()
			.body(PylierDB[].class);
		
		_logger.info("pyliersDictionnary paires :");
		for (PylierDB pylierDB : pylierDBs) {
			_logger.info(pylierDB.pylier_id + " -> " + pylierDB.pylier_url);			
			pyliersDictionnary.put(pylierDB.pylier_id, pylierDB.pylier_url);
		}

		return pyliersDictionnary;
	}
}
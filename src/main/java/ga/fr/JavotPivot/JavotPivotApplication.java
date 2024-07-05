package ga.fr.JavotPivot;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import ga.fr.ModelsDB.PylierDB;

@SpringBootApplication
public class JavotPivotApplication {	
	public static void main(String[] args) {
		try {
			Environment environment = SpringApplication.run(JavotPivotApplication.class, args).getEnvironment();
			ThreadsSharedData.Logger.info("Starting JavotPyvot");
	
			ThreadsSharedData.HyperviseurApiUrl = getHyperviseurApiUrl(environment);
			ThreadsSharedData.Logger.info("Hyperviseur API url = " + ThreadsSharedData.HyperviseurApiUrl);
	
			ThreadsSharedData.PyliersDictionnary = getPyliersDictionnary();
			ThreadsSharedData.Logger.info("Pyliers = " + ThreadsSharedData.PyliersDictionnary);

			ThreadsSharedData.BuildingId = getBuildingId(environment);
			ThreadsSharedData.Logger.info("Building Id = " + ThreadsSharedData.BuildingId);

			// Run Command routine every second
			CommandThread commandThread = new CommandThread();
			//commandThread.start();

			// Run Data routine every second
			DataThread dataThread = new DataThread();
			//dataThread.start();
		} catch (Exception e) {
			ThreadsSharedData.Logger.error(e.getMessage());
		}
	}

	private static String getHyperviseurApiUrl(Environment p_environment) throws Exception{
		String hyperviseurApiUrl = p_environment.getProperty("javotPivot.hyperviseurApi.url");
		if(hyperviseurApiUrl == null ||hyperviseurApiUrl.isBlank() || hyperviseurApiUrl.isEmpty())
			ThreadsSharedData.Logger.info("hyperviseurApiUrl invalide");
			hyperviseurApiUrl = getHyperviseurApiUrlFromNetwork();	
		return hyperviseurApiUrl;
	}
	private static String getHyperviseurApiUrlFromNetwork() throws Exception{
		List<String> reachableAddresses = getReachableAddresses();
		ThreadsSharedData.Logger.info("reachableAddresses = " + reachableAddresses);
		for (String reachableAddress : reachableAddresses) {
			//TODO: l'algo ici
			String isHyperviseurResponse = ThreadsSharedData.HttpClient.get()
				.uri("http://" +  reachableAddress + ":8000/api/isHyperviseur") // essayer avec https aussi ?
				.header("Accept", "application/json")
				.retrieve()
				.body(String.class);

			ThreadsSharedData.Logger.info("isHyperviseurResponse = " + isHyperviseurResponse);

			//if(true) // jsp
			return reachableAddress;
		}
		return "127.0.0.1"; // local par defaut
	}
	private static List<String> getReachableAddresses() throws Exception{
		List<String> reachableAddresses = new ArrayList<String>(); 
		
		InetAddress localHost = InetAddress.getLocalHost();
		String localIpAddress = localHost.getHostAddress();
		String subnet = getSubnet(localIpAddress);
		
		ThreadsSharedData.Logger.info("subnet = " + subnet);
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
			ThreadsSharedData.Logger.info("isReachable(" + p_ipAddress + ")");
			InetAddress inet = InetAddress.getByName(p_ipAddress);
            return inet.isReachable(10); // Timeout in milliseconds
        } catch (Exception e) {
			//e.printStackTrace();
            return false;
        }
    }

	private static ConcurrentHashMap<Integer, String> getPyliersDictionnary() throws Exception{
		ConcurrentHashMap<Integer, String> pyliersDictionnary = new ConcurrentHashMap<Integer, String>();
		
		// get json pyliers config from hyperviseur API
		PylierDB[] pylierDBs = ThreadsSharedData.HttpClient.get()
			.uri(ThreadsSharedData.HyperviseurApiUrl + "/api/config/pylier")
			.header("Accept", "application/json")
			.retrieve()
			.body(PylierDB[].class);
		
		for (PylierDB pylierDB : pylierDBs) {	
			pyliersDictionnary.put(pylierDB.pylier_id, pylierDB.pylier_url);
		}
		return pyliersDictionnary;
	}

	private static int getBuildingId(Environment p_environment) throws Exception{
		try {
			int buildingId = p_environment.getProperty("javotPivot.batiment.id", int.class);
			return buildingId;
		} catch (Exception e) {
			throw new Exception("L'ID du batiment n'est pas renseigne dans le fichier application.properties");
		}
	}
}
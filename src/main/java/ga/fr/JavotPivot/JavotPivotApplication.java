package ga.fr.JavotPivot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class JavotPivotApplication {	
	public static void main(String[] args) {
		try {
			Environment environment = SpringApplication.run(JavotPivotApplication.class, args).getEnvironment();
			ThreadsSharedData.Logger.info("Starting JavotPyvot");
	
			ThreadsSharedData.HyperviseurApiUrl = getHyperviseurApiUrl(environment);
			ThreadsSharedData.Logger.info("Hyperviseur API url = " + ThreadsSharedData.HyperviseurApiUrl);
	
			ThreadsSharedData.BuildingId = getBuildingId(environment);
			ThreadsSharedData.Logger.info("Building Id = " + ThreadsSharedData.BuildingId);

			// Run Config routine every 10 minutes
			ConfigThread configThread = new ConfigThread();
			configThread.start();

			// Run Command routine every second
			CommandThread commandThread = new CommandThread();
			commandThread.start();

			// Run Data routine every second
			DataThread dataThread = new DataThread();
			dataThread.start();
		} catch (Exception e) {
			ThreadsSharedData.Logger.error(e.getMessage());
		}
	}

	private static String getHyperviseurApiUrl(Environment p_environment) throws Exception{
		String hyperviseurApiUrl = p_environment.getProperty("javotPivot.hyperviseurApi.url");
		if(hyperviseurApiUrl == null ||hyperviseurApiUrl.isBlank() || hyperviseurApiUrl.isEmpty())
			throw new Exception("L'URL de l'hyperviseur n'est pas renseigne dans le fichier application.properties");
		return hyperviseurApiUrl;
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
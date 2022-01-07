package agencesVoyages.launch;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import jade.core.Runtime;

/**
 * launch the simulation of travelers and travel agencies
 * 
 * @author emmanueladam
 */
public class LaunchSimu {

	public static final Logger logger = Logger.getLogger("simu");

	/**
	 * @param args
	 */
	public static void main(String... args) {

		logger.setLevel(Level.ALL);
		Handler fh;
		try {
			fh = new FileHandler("./simuAgences.xml", false);
			logger.addHandler(fh);
		} 
		catch (SecurityException | IOException e) { e.printStackTrace(); }

		// ******************JADE******************
		
		// allow to send arguments to the JADE launcher
		var pp = new ExtendedProperties();
		// add the gui
		pp.setProperty(Profile.GUI, "true");
		// add the Topic Management Service
		pp.setProperty(Profile.SERVICES, "jade.core.messaging.TopicManagementService;jade.core.event.NotificationService");		 
		
		var lesAgents = new StringBuilder();
		lesAgents.append("client1:agencesVoyages.agents.TravellerAgent;");
		lesAgents.append("agentCar:agencesVoyages.agents.AgenceAgent(agentsVoyage/car.csv);");
		//ajout de car autre
		lesAgents.append("agentCarAutre:agencesVoyages.agents.AgenceAgent(carAutre.csv);");

		lesAgents.append("agentBus:agencesVoyages.agents.AgenceAgent(agentsVoyage/bus.csv);");
		//ajout de bus autre
		lesAgents.append("agentBusAutre:agencesVoyages.agents.AgenceAgent(busAutre.csv);");

		lesAgents.append("agentTrain:agencesVoyages.agents.AgenceAgent(agentsVoyage/train.csv);");

		// portail Bus
		//Creez un agent PortailBus qui sert d'intermédiaire entre les clients et les bus
		lesAgents.append("portailBus:agencesVoyages.agents.PortailAgence(bus);");

		//portail Car
		// un agent PortailCar qui fait de même pour les agences de voiture.
		lesAgents.append("portailCar:agencesVoyages.agents.PortailAgence(car);");

		lesAgents.append("alert1:agencesVoyages.agents.AlertAgent");
		pp.setProperty(Profile.AGENTS, lesAgents.toString());
		// create a default Profile
		var pMain = new ProfileImpl(pp);

		// launch the main jade container
		Runtime.instance().createMainContainer(pMain);

	}

}

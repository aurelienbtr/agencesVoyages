package agencesVoyages.agents;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Stream;

import agencesVoyages.comportements.ContractNetAchat;
import agencesVoyages.data.ComposedJourney;
import agencesVoyages.data.Journey;
import agencesVoyages.data.JourneysList;
import agencesVoyages.gui.TravellerGui;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionInitiator;

/**
 * Journey searcher
 * 
 * @author Emmanuel ADAM
 */
@SuppressWarnings("serial")
public abstract class TravellerAgent extends GuiAgent {
	/** code pour ajout de livre par la gui */
	public static final int EXIT = 0;
	/** code pour achat de livre par la gui */
	public static final int BUY_TRAVEL = 1;

	/** liste des vendeurs */
	private ArrayList<AID> vendeurs;

	// liste d'enchere
	private ArrayList<AID> enchere;

	//critere
	private String critere;




	/** catalog received by the sellers */
	private JourneysList catalogs;

	/** the journey chosen by the agent*/
	private ComposedJourney myJourney;

	/** topic from which the alert will be received */
	private AID topic;

	/** gui */
	private TravellerGui window;

	/** Initialisation de l'agent */
	@Override
	protected void setup() {
		this.window = new TravellerGui(this);
		window.setColor(Color.cyan);
		window.println("Hello! AgentAcheteurCN " + this.getLocalName() + " est pret. ");
		window.setVisible(true);

		vendeurs = new ArrayList<>();
		detectAgences();

		topic = AgentToolsEA.generateTopicAID(this,"TRAFFIC NEWS");
		//ecoute des messages radio
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				var msg = myAgent.receive(MessageTemplate.MatchTopic(topic));
				if (msg != null) {
					println("Message recu sur le topic " + topic.getLocalName() + ". Contenu " + msg.getContent()
							+ " émis par " + msg.getSender().getLocalName());
				} else { block();}
			}
		});
		
	}
// on effectue des modifs sur le catalogue en cas d'alerte
	public void getAlertMessage(ACLMessage msg) {

		// On split le message pour récupérer le point de départ et le point d'arriver
		String[] splitMsg = msg.getContent().split(",");
		String start = splitMsg[0].toUpperCase();
		String end = splitMsg[1].toUpperCase();

		// Si l'on a un trajet en cours
		if (myJourney != null) {
			// On recherche un nouveau trajet pour pallier à l'alerte reçure
			Journey journey = myJourney.getJourneyFromStartAndStop(start, end);
			// Si l'on en trouve un nouveau on refait une demande d'achat pour pouvoir réaliser le trajet
			if (journey != null) {
				addBehaviour(
						new ContractNetAchat(
								this,
								new ACLMessage(ACLMessage.CFP),
								start,
								end,
								journey.getDepartureDate(),
								critere
						)
				);
			}
		}

	}



	/**ecoute des evenement de type enregistrement en tant qu'agence aupres des pages jaunes*/
	private void detectAgences()
	{
		var model = AgentToolsEA.createAgentDescription("travel agency", "seller");
		var msg = DFService.createSubscriptionMessage(this, getDefaultDF(), model, null);
		vendeurs = new ArrayList<>();
		addBehaviour(new SubscriptionInitiator(this, msg) {
			@Override
			protected void handleInform(ACLMessage inform) {
				window.println("Agent "+getLocalName()+": information recues de DF");
				try {
					var results = DFService.decodeNotification(inform.getContent());
					if (results.length > 0) {
						for (DFAgentDescription dfd:results) {
							vendeurs.add(dfd.getName());
							window.println(dfd.getName().getName() + " s'est inscrit en tant qu'agence");
						}
					}	
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		} );		
	}
	
	// 'Nettoyage' de l'agent
	@Override
	protected void takeDown() {
		window.println("Je quitte la plateforme. ");
	}

	///// SETTERS AND GETTERS
	/**
	 * @return agent gui
	 */
	public TravellerGui getWindow() {
		return window;
	}

	public void computeComposedJourney(final String from, final String to, final int departure,
			final String preference) {
		final List<ComposedJourney> journeys = new ArrayList<>();
		//recherche de trajets avecc des temps d'attentes entre via = 60mn
		final boolean result = catalogs.findIndirectJourney(from, to, departure, 60, new ArrayList<>(),
				new ArrayList<>(), journeys);

		if (!result) {
			println("no journey found !!!");
		}
		if (result) {
			//oter les voyages demarrant trop tard (1h30 apres la date de depart souhaitee)
			journeys.removeIf(j->j.getJourneys().get(0).getDepartureDate()-departure>90);
			switch (preference) {
			case "duration":
				Stream<ComposedJourney> strCJ = journeys.stream();
				OptionalDouble moy = strCJ.mapToInt(ComposedJourney::getDuration).average();
				final double avg = moy.getAsDouble();
				println("duree moyenne = " + avg );//+ ", moy au carre = " + avg * avg);
				journeys.sort(Comparator.comparingInt(ComposedJourney::getDuration));
				break;
			case "confort":
				journeys.sort(Comparator.comparingInt(ComposedJourney::getConfort).reversed());
				break;
			case "cost":
				journeys.sort(Comparator.comparingDouble(ComposedJourney::getCost));
				break;
			case "duration-cost":
				journeys.sort(Comparator.comparingDouble(ComposedJourney::getCost));
				break;
			default:
				journeys.sort(Comparator.comparingDouble(ComposedJourney::getCost));
				break;
			}
			myJourney = journeys.get(0);
			println("I choose this journey : " + myJourney);
		}
	}

	/** get event from the GUI */
	@Override
	protected void onGuiEvent(final GuiEvent eventFromGui) {
		if (eventFromGui.getType() == TravellerAgent.EXIT) {
			doDelete();
		}
		if (eventFromGui.getType() == TravellerAgent.BUY_TRAVEL) {
			this.critere = (String) eventFromGui.getParameter(3);
			addBehaviour(new ContractNetAchat(this, new ACLMessage(ACLMessage.CFP),
					(String) eventFromGui.getParameter(0), (String) eventFromGui.getParameter(1),
					(Integer) eventFromGui.getParameter(2), (String) eventFromGui.getParameter(3)));
		}
	}

	/**
	 * @return the vendeurs
	 */
	public List<AID> getVendeurs() {
		return (ArrayList<AID>)vendeurs.clone();
	}

	//calcul de moyenne
	public double avg(double min, double max, double toTest) {
		return (toTest - min) / (max - toTest);
	}

	/**
	 * print a message on the window lined to the agent
	 * 
	 * @param msg
	 *            text to display in th window
	 */
	public void println(final String msg) {
		window.println(msg);
	}

	/** set the list of journeys */
	public void setCatalogs(final JourneysList catalogs) {
		this.catalogs = catalogs;
	}


	public ComposedJourney getMyJourney() {
		return myJourney;
	}

	public abstract ComposedJourney chooseJourneyByNature(String preference, List<ComposedJourney> journeys);
}


package agencesVoyages.agents;

/**
 * Créez une classe PortailAgence.
 * Une classe portail agence se comporte comme une agence, mais ne dispose pas de moyens de locomotion
 * Le client n'envoie plus de demande auprès des agences, mais auprès des portails.
 */

import jade.core.AID;


    public class PortailAgence extends AgenceAgent {

        private AID topic_sellers;

    }


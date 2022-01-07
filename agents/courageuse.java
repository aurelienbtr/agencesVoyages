package agencesVoyages.agents;

import agencesVoyages.data.ComposedJourney;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

public class courageuse extends TravellerAgent{
    // on sait que la nature courageuse ne tient pas compte des avis, eet prend l'offre la plus interessante
    public ComposedJourney chooseJourneyByNature(String preference, List<ComposedJourney> journeys) {
        switch (preference) {
            case "duration":
                OptionalDouble moy = journeys.stream().mapToInt(ComposedJourney::getDuration).average();
                final double avg = moy.getAsDouble();
                println("duree moyenne = " + avg);//+ ", moy au carre = " + avg * avg);
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
        return journeys.get(0);
    }
}

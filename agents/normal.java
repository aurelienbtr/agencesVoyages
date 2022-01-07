package agencesVoyages.agents;

import agencesVoyages.data.ComposedJourney;

import java.util.List;
import java.util.OptionalDouble;

public class normal extends TravellerAgent {

    @Override
    public ComposedJourney chooseJourneyByNature(String preference, List<ComposedJourney> journeys) {
        int minInt = 0;
        int maxInt = 10;
        double min = 0.0;
        double max = 10.0;
        int finalMin;
        int finalMax;
        double finalMax1;
        double finalMin1;
        switch (preference) {
            case "duration":
                OptionalDouble moy = journeys.stream().mapToInt(ComposedJourney::getDuration).average();
                final double avg = moy.getAsDouble();
                println("duree moyenne = " + avg);//+ ", moy au carre = " + avg * avg);
                if (journeys.stream().mapToInt(ComposedJourney::getDuration).min().isPresent()) {
                    minInt = journeys.stream().mapToInt(ComposedJourney::getDuration).min().getAsInt();
                }

                if (journeys.stream().mapToInt(ComposedJourney::getDuration).max().isPresent()) {
                    maxInt = journeys.stream().mapToInt(ComposedJourney::getDuration).max().getAsInt();
                }

                finalMin = minInt;
                finalMax = maxInt;

                journeys.sort(
                        (j1, j2) -> {
                            double durationJ1 = avg(finalMin, finalMax, j1.getDuration());
                            double durationJ2 = avg(finalMin, finalMax, j2.getDuration());
                            double confianceAgenceJ1 = avg(0, 10, j1.getConfiance());
                            double confianceAgenceJ2 = avg(0, 10, j2.getConfiance());
                            int resultJ1 = (int) ((durationJ1 + confianceAgenceJ1) * 100);
                            int resultJ2 = (int) ((durationJ2 + confianceAgenceJ2) * 100);

                            return resultJ1 - resultJ2;
                        }
                );
                break;
            case "confort":

                if (journeys.stream().mapToInt(ComposedJourney::getConfort).max().isPresent()) {
                    max = journeys.stream().mapToInt(ComposedJourney::getConfort).max().getAsInt();
                }


                finalMax1 = max;
                finalMin1 = min;

                journeys.sort(
                        (j1, j2) -> {
                            double confortJ1 = avg(finalMin1, finalMax1, j1.getConfort());
                            double confortJ2 = avg(finalMin1, finalMax1, j2.getConfort());
                            double confianceAgenceJ1 = avg(0, 10, j1.getConfiance());
                            double confianceAgenceJ2 = avg(0, 10, j2.getConfiance());
                            int resultJ1 = (int) ((confortJ1 + confianceAgenceJ1) * 100);
                            int resultJ2 = (int) ((confortJ2 + confianceAgenceJ2) * 100);

                            return resultJ1 - resultJ2;
                        }
                );
                break;
            case "cost":

                min = 0.0;
                max = 10.0;
                if (journeys.stream().mapToDouble(ComposedJourney::getCost).max().isPresent()) {
                    max = journeys.stream().mapToDouble(ComposedJourney::getCost).max().getAsDouble();
                }

                finalMax1 = max;
                finalMin1 = min;

                journeys.sort(
                        (j1, j2) -> {
                            double costJ1 = avg(finalMin1, finalMax1, j1.getCost());
                            double costJ2 = avg(finalMin1, finalMax1, j2.getCost());
                            double confianceAgenceJ1 = avg(0, 10, j1.getConfiance());
                            double confianceAgenceJ2 = avg(0, 10, j2.getConfiance());
                            int resultJ1 = (int) ((costJ1 + confianceAgenceJ1) * 100);
                            int resultJ2 = (int) ((costJ2 + confianceAgenceJ2) * 100);

                            return resultJ1 - resultJ2;
                        }
                );
                break;
            case "duration-cost":
                min = 0.0;
                max = 10.0;
                if (journeys.stream().mapToDouble(ComposedJourney::getCost).max().isPresent()) {
                    max = journeys.stream().mapToDouble(ComposedJourney::getCost).max().getAsDouble();
                }

                if (journeys.stream().mapToInt(ComposedJourney::getDuration).min().isPresent()) {
                    minInt = journeys.stream().mapToInt(ComposedJourney::getDuration).min().getAsInt();
                }

                if (journeys.stream().mapToInt(ComposedJourney::getDuration).max().isPresent()) {
                    maxInt = journeys.stream().mapToInt(ComposedJourney::getDuration).max().getAsInt();
                }

                finalMin = minInt;
                finalMax = maxInt;

                finalMin1 = min;
                finalMax1 = max;

                journeys.sort(
                        (j1, j2) -> {
                            double costJ1 = avg(finalMin1, finalMax1, j1.getCost());
                            double costJ2 = avg(finalMin1, finalMax1, j2.getCost());
                            double durationJ1 = avg(finalMin, finalMax, j1.getDuration());
                            double durationJ2 = avg(finalMin, finalMax, j2.getDuration());
                            double confianceAgenceJ1 = avg(0, 10, j1.getConfiance());
                            double confianceAgenceJ2 = avg(0, 10, j2.getConfiance());
                            int resultJ1 = (int) ((costJ1 + durationJ1 + confianceAgenceJ1) * 100);
                            int resultJ2 = (int) ((costJ2 + durationJ2 + confianceAgenceJ2) * 100);

                            return resultJ1 - resultJ2;
                        }
                );
                break;
            default:
                min = 0.0;
                max = 10.0;
                if (journeys.stream().mapToDouble(ComposedJourney::getCost).max().isPresent()) {
                    max = journeys.stream().mapToDouble(ComposedJourney::getCost).max().getAsDouble();
                }

                finalMax1 = max;
                finalMin1 = min;

                journeys.sort(
                        (j1, j2) -> {
                            double durationJ1 = avg(finalMin1, finalMax1, j1.getCost());
                            double durationJ2 = avg(finalMin1, finalMax1, j2.getCost());
                            double confianceAgenceJ1 = avg(0, 10, j1.getConfiance());
                            double confianceAgenceJ2 = avg(0, 10, j2.getConfiance());
                            int resultJ1 = (int) ((durationJ1 + confianceAgenceJ1) * 100);
                            int resultJ2 = (int) ((durationJ2 + confianceAgenceJ2) * 100);

                            return resultJ1 - resultJ2;
                        }
                );
                break;
        }
        return journeys.get(0);
    }
}

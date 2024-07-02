/*
@author: Patrick Wiedenmann
*/

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Netzplan {
    private List<Arbeitspaket> arbeitspakete;

    public Netzplan() {

        arbeitspakete = new ArrayList<>();
    }

    public static void main(String[] args) throws IOException {
        Netzplan netzplan = new Netzplan();
        Scanner scanner = new Scanner(System.in);

        netzplan.testMethode(netzplan);

        while (true) {
            System.out.println("Wählen Sie eine Option: ");
            System.out.println("1. Arbeitspaket hinzufügen");
            System.out.println("2. Arbeitspaket löschen");
            System.out.println("3. Werte eines Arbeitspakets anzeigen");
            System.out.println("4. Werte aller Arbeitspakete anzeigen");
            System.out.println("5. Beenden");
            System.out.println("6. Projektdauer anzeigen");
            System.out.println("7. Kritischer Pfad anzeigen");
            System.out.println("8. Werte bearbeiten");
            System.out.println("9. Werte in Textfile speichern");
            System.out.println("10. Werte aus Textfile laden");

            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 1) {
                System.out.println("Nummer des Arbeitspakets: ");
                int nummer = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Name des Arbeitspakets: ");
                String name = scanner.nextLine();
                System.out.println("Dauer des Arbeitspakets: ");
                int dauer = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Anzahl der Vorgänger: ");
                int anzahlVorgaenger = scanner.nextInt();
                scanner.nextLine();

                List<Integer> vorgaenger = new ArrayList<>();
                for (int i = 0; i < anzahlVorgaenger; i++) {
                    System.out.println("Vorgänger " + (i + 1) + ": ");
                    int vorgaengerNummer = scanner.nextInt();
                    scanner.nextLine();
                    vorgaenger.add(vorgaengerNummer);
                }

                Arbeitspaket arbeitspaket = new Arbeitspaket(nummer, name, dauer, vorgaenger);
                netzplan.addArbeitspaket(arbeitspaket);
            } else if (option == 2) {
                System.out.println("Nummer des zu löschenden Arbeitspakets: ");
                int nummer = scanner.nextInt();
                scanner.nextLine();
                netzplan.removeArbeitspaket(nummer);
            } else if (option == 3) {
                System.out.println("Nummer des anzuzeigenden Arbeitspakets: ");
                int nummer = scanner.nextInt();
                scanner.nextLine();
                netzplan.einzelwerteAusgeben(nummer);
            } else if (option == 4) {
                netzplan.alleWerteAusgeben();
            } else if (option == 5) {
                break;
            } else if (option == 6) {
                netzplan.projektDauerAusgeben();
            } else if (option == 7) {
                netzplan.kritischerPfadAusgeben(netzplan);
            } else if (option == 8) {
                netzplan.knotenBearbeiten(netzplan);
            } else if (option == 9) {
                netzplan.inTextFileSpeichern(netzplan);
            } else if (option == 10) {
                netzplan.textFileAuslesen(netzplan);
            } else {
                System.out.println("Ungültige Option. Bitte erneut versuchen.");
            }
        }

        scanner.close();
    }

    public void addArbeitspaket(Arbeitspaket arbeitspaket) {

        arbeitspakete.add(arbeitspaket);
    }

    public void removeArbeitspaket(int nummer) {

        arbeitspakete.removeIf(ap -> ap.getNummer() == nummer);
    }

    private Arbeitspaket findeArbeitspaket(int nummer) {
        for (Arbeitspaket ap : arbeitspakete) {
            if (ap.getNummer() == nummer) {
                return ap;
            }
        }
        return null;
    }

    private List<Integer> findeNachfolger(int arbeitspaketNummer) {
        List<Integer> nachfolger = new ArrayList<>();
        for (Arbeitspaket paket : arbeitspakete) {
            if (paket.getVorgaenger().contains(arbeitspaketNummer)) {
                nachfolger.add(paket.getNummer());
            }
        }
        return nachfolger;
    }

    public int berechneFaz(int arbeitspaketNummer) {
        Arbeitspaket paket = findeArbeitspaket(arbeitspaketNummer);
        int maxFez = 0;
        for (int vorgaengerNummer : paket.getVorgaenger()) {
            int fez = berechneFez(vorgaengerNummer);
            if (fez > maxFez) {
                maxFez = fez;
            }
        }
        return maxFez;
    }

    public int berechneFez(int arbeitspaketNummer) {
        Arbeitspaket paket = findeArbeitspaket(arbeitspaketNummer);
        int faz = berechneFaz(arbeitspaketNummer);
        return faz + paket.getDauer();
    }

    public int berechneSaz(int arbeitspaketNummer) {
        int sez = berechneSez(arbeitspaketNummer);
        Arbeitspaket paket = findeArbeitspaket(arbeitspaketNummer);
        return sez - paket.getDauer();
    }

    public int berechneSez(int arbeitspaketNummer) {
        List<Integer> nachfolger = findeNachfolger(arbeitspaketNummer);
        if (nachfolger.isEmpty()) {
            return berechneFez(arbeitspaketNummer);
        }
        int minSaz = Integer.MAX_VALUE;
        for (int nachfolgerNummer : nachfolger) {
            int saz = berechneSaz(nachfolgerNummer);
            if (saz < minSaz) {
                minSaz = saz;
            }
        }
        return minSaz;
    }

    public int berechneFP(int arbeitspaketNummer) {
        return berechneSaz(arbeitspaketNummer) - berechneFaz(arbeitspaketNummer);
    }

    public void alleWerteAusgeben() {
        for (Arbeitspaket paket : arbeitspakete) {
            int nummer = paket.getNummer();
            System.out.println("Arbeitspaket " + nummer + ": " + paket.getName());
            System.out.println("FAZ: " + berechneFaz(nummer));
            System.out.println("FEZ: " + berechneFez(nummer));
            System.out.println("SAZ: " + berechneSaz(nummer));
            System.out.println("SEZ: " + berechneSez(nummer));
            System.out.println("FP: " + berechneFP(nummer));
            System.out.println();
        }
    }

    public void einzelwerteAusgeben(int nummer) {
        Arbeitspaket paket = findeArbeitspaket(nummer);
        if (paket != null) {
            System.out.println("Arbeitspaket " + nummer + ": " + paket.getName());
            System.out.println("FAZ: " + berechneFaz(nummer));
            System.out.println("FEZ: " + berechneFez(nummer));
            System.out.println("SAZ: " + berechneSaz(nummer));
            System.out.println("SEZ: " + berechneSez(nummer));
            System.out.println("FP: " + berechneFP(nummer));
            System.out.println();
        } else {
            System.out.println("Arbeitspaket " + nummer + " nicht gefunden.");
        }
    }

    public void projektDauerAusgeben() {
        int letzteArbeitspaket = arbeitspakete.size();
        int dauer = berechneFez(letzteArbeitspaket);
        System.out.println("Die Dauer des Projekts ist: " + dauer);
        System.out.println();
    }

    public void kritischerPfadAusgeben(Netzplan netzplan) {

        for (Arbeitspaket paket : arbeitspakete) {
            if (netzplan.berechneFP(paket.getNummer()) == 0) {
                System.out.println("Kritischer Pfad: " + paket.getNummer());
            }
        }
    }

    public void knotenBearbeiten(Netzplan netzplan) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcher Knoten wollen Sie bearbeiten? Geben Sie die Nummer ein: ");
        int nummer = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Was möchten Sie bearbeiten? Geben Sie 'name' oder 'dauer' ein: ");
        String option = scanner.nextLine();
        if (option.equals("name")) {
            System.out.println("Neuer Name: ");
            String name = scanner.nextLine();
            arbeitspakete.get(nummer).setName(name);
        } else if (option.equals("dauer")) {
            System.out.println("Neue Dauer: ");
            int dauer = scanner.nextInt();
            scanner.nextLine();
            arbeitspakete.get(nummer).setDauer(dauer);
        } else {
            System.out.println("Ungültige Eingabe");
        }
    }

    public void inTextFileSpeichern(Netzplan netzplan) {

        try (PrintWriter writer = new PrintWriter("arbeitspakete.txt")) {
            for (Arbeitspaket paket : arbeitspakete) {
                writer.println(paket.getNummer() + "," + paket.getName() + "," + paket.getDauer() + "," + paket.getVorgaenger());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void textFileAuslesen(Netzplan netzplan) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("arbeitspakete.txt"));

        String regex = "[\\[\\]]";

        Pattern pattern = Pattern.compile(regex);

        String line;
        while ((line = reader.readLine()) != null) {

            Matcher matcher = pattern.matcher(line);
            String processedLine = matcher.replaceAll("");
            System.out.println(processedLine);

            String[] values = processedLine.split(",");
            int nummer = Integer.parseInt(values[0]);
            String name = values[1];
            int dauer = Integer.parseInt(values[2]);
            List<Integer> vorgaenger;
            if (values.length > 3) {
                vorgaenger = Arrays.stream(values[3].split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            } else {
                vorgaenger = new ArrayList<>();
            }
            netzplan.addArbeitspaket(new Arbeitspaket(nummer, name, dauer, vorgaenger));
        }
        reader.close();
    }

    public void testMethode(Netzplan netzplan) {

        // Beispiel Arbeitspakete hinzufügen
        netzplan.addArbeitspaket(new Arbeitspaket(1, "Server einbauen", 1, new ArrayList<>()));
        netzplan.addArbeitspaket(new Arbeitspaket(2, "Betriebssystem installieren", 3, Arrays.asList(1)));
        netzplan.addArbeitspaket(new Arbeitspaket(3, "Updates installieren", 3, Arrays.asList(2)));
        netzplan.addArbeitspaket(new Arbeitspaket(4, "Rolle installieren", 1, Arrays.asList(2)));
        netzplan.addArbeitspaket(new Arbeitspaket(5, "Rolle konfigurieren", 5, Arrays.asList(4)));
        netzplan.addArbeitspaket(new Arbeitspaket(6, "Clients installieren", 3, new ArrayList<>()));
        netzplan.addArbeitspaket(new Arbeitspaket(7, "Updates auf Client installieren", 3, Arrays.asList(6)));
        netzplan.addArbeitspaket(new Arbeitspaket(8, "SW auf Clients installieren", 4, Arrays.asList(6)));
        netzplan.addArbeitspaket(new Arbeitspaket(9, "Modultests", 3, Arrays.asList(3, 5)));
        netzplan.addArbeitspaket(new Arbeitspaket(10, "Integrationstest", 3, Arrays.asList(7, 8, 9)));
        netzplan.addArbeitspaket(new Arbeitspaket(11, "Systemtest", 3, Arrays.asList(10)));

        // Netzplan berechnen und Ergebnisse ausgeben
        netzplan.alleWerteAusgeben();

    }
}
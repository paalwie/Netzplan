import java.util.List;

class Arbeitspaket {
    private int nummer;
    private String name;
    private int dauer;
    private List<Integer> vorgaenger;

    public Arbeitspaket(int nummer, String name, int dauer, List<Integer> vorgaenger) {
        this.nummer = nummer;
        this.name = name;
        this.dauer = dauer;
        this.vorgaenger = vorgaenger;
    }

    public int getNummer() {

        return nummer;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDauer() {

        return dauer;
    }

    public void setDauer(int dauer) {
        this.dauer = dauer;
    }

    public List<Integer> getVorgaenger() {

        return vorgaenger;
    }

}
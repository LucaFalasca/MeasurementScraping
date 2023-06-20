package it.lucafalasca.util;

public class Count {

    public static int contaOccorrenze(String testo, String sottostringa) {
        int contatore = 0;
        int indice = testo.indexOf(sottostringa);
        while (indice != -1) {
            contatore++;
            indice = testo.indexOf(sottostringa, indice + 1);
        }
        return contatore;
    }

}

package it.lucafalasca;

import it.lucafalasca.enumerations.Project;
import it.lucafalasca.populate.PopulateLocalFiles;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Project project = Project.BOOKKEEPER;
        //PopulateLocalFiles.populateLocalJsonClasses(project);
        PopulateLocalFiles.populateCSVfile(project);
        //PopulateLocalFiles.tickets();
    }
}
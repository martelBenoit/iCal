package ical.database.entity;

import ical.database.DAOFactory;
import ical.database.dao.ProfessorDAO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class Professor {

    private int id;

    private final String name;

    private final String url;


    public Professor(int id, @Nonnull String name, @Nonnull String url){
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public Professor(@Nonnull String name){
        this.name = name;

        this.id = -1;

        ProfessorDAO professorDAO = (ProfessorDAO) DAOFactory.getProfessorDAO();
        Professor professor = professorDAO.find(name);
        if(professor == null)
            this.url = "https://www.unamur.be/en/sci/chemistry/rco/membres-images/inconnu/image";
        else
            this.url = professor.getUrl();

    }

    public int getId(){
        return this.id;
    }


    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setId(int id){
        this.id = id;
    }

    @Override
    public String toString() {
        return "professor[id="+this.id+", name="+this.name+", url="+this.url+"]\n";
    }
}

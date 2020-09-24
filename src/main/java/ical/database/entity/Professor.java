package ical.database.entity;

import ical.database.DAOFactory;
import ical.database.dao.ProfessorDAO;

import javax.annotation.Nonnull;

/**
 * Professor class.
 *
 * <br>The Professor entity.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class Professor extends Entity{

    /**
     * the id of the professor.
     */
    private int id;

    /**
     * the name of the professor.
     */
    private final String name;

    /**
     * the url of the image of the professor.
     */
    private final String url;

    /**
     * Constructor.
     *
     * @param id the id of the professor
     * @param name the name of the professor
     * @param url the url of the image of the professor
     */
    public Professor(int id, @Nonnull String name, @Nonnull String url){
        this.id = id;
        this.name = name;
        this.url = url;
    }

    /**
     * Constructor.
     *
     * <br>This constructor is used to create a teacher by knowing neither his id in the database nor the url
     * of an image.
     * <br>If the professor already exists in the database then we recover the image otherwise it is one by default.
     *
     * @param name the name of the professor
     */
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

    /**
     * Get the id of the professor.
     *
     * @return the id of the professor
     */
    public int getId(){
        return this.id;
    }

    /**
     * Get the name of the professor.
     *
     * @return the name of the professor
     */
    public String getName() {
        return name;
    }

    /**
     * Get the url image of the professor.
     *
     * @return the url image of the professor
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the id of the professor.
     *
     * @param id the id of the professor
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     * Display the parameters of this professor.
     *
     * @return the string contains the parameters of this professor
     */
    @Override
    public String toString() {
        return "professor[id="+this.id+", name="+this.name+", url="+this.url+"]\n";
    }
}

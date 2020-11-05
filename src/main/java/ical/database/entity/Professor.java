package ical.database.entity;

import ical.database.DAOFactory;
import ical.database.dao.ProfessorDAO;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Professor class.
 *
 * <br>The Professor entity.
 *
 * @author Benoît Martel
 * @version 1.1
 */
public class Professor extends Entity implements Comparable{

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
    private String url;

    /**
     * the displayed professor name
     */
    private String displayName;

    /**
     * Constructor.
     *
     * @param id            the id of the professor
     * @param name          the name of the professor
     * @param url           the url of the image of the professor
     * @param displayName  the embellished professor name
     */
    public Professor(int id, @Nonnull String name, @Nonnull String url, @Nonnull String displayName){
        this.id = id;
        this.name = name;
        this.url = url;
        this.displayName = displayName;
    }

    /**
     * Constructor.
     *
     * <br>This constructor is used to create a teacher by knowing neither his id in the database nor the url
     * of an image.
     * <br>If the professor already exists in the database then we recover the image otherwise it is one by default.
     *
     * @param name          the name of the professor
     * @param displayName   the embellished professor name
     */
    public Professor(@Nonnull String name, @Nonnull String displayName){
        this.name = name;
        this.displayName = displayName;

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
     * Get the embellished professor name
     *
     * @return the embellished professor name
     */
    public String getDisplayName() {
        return displayName;
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
     * Set the url of the professor.
     *
     * @param id the url of the professor
     */
    public void setUrl(String url){
        this.url = url;
    }

    /**
     * Set the embellished professor name
     *
     * @param displayName  the embellished professor name
     */
    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }

    /**
     * Display the parameters of this professor.
     *
     * @return the string contains the parameters of this professor
     */
    @Override
    public String toString() {
        return "professor[id="+this.id+", name="+this.name+", url="+this.url+", name="+this.displayName +"]\n";
    }

    @Override
    public int compareTo(@NotNull Object professor) {
        if(professor instanceof Professor)
            return ((Professor) professor).name.toUpperCase().compareTo(this.name.toUpperCase());
        else
            return -1;


    }


}

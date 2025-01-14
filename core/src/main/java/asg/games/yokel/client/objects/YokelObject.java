package asg.games.yokel.client.objects;

public interface YokelObject {
    /**
     * Sets the Object ID
     *
     * @param id String
     */
    void setId(String id);

    /**
     * @return Id String
     */
    String getId();

    /**
     * Sets the Object Name
     *
     * @param name String
     */
    void setName(String name);

    /**
     * Returns the Object Name
     *
     * @return name String
     */
    String getName();

    /**
     * Sets the Object created date
     *
     * @param dateTime long string
     */
    void setCreated(long dateTime);

    /**
     * Returns the Object created date
     *
     * @return date created long
     */
    long getCreated();

    /**
     * Sets the Object modified date
     *
     * @param dateTime long string
     */
    void setModified(long dateTime);

    /**
     * Returns the Object modified date
     *
     * @return date modified long string
     */
    long getModified();
}
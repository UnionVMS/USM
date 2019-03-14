package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

/**
 * Application details.
 */
public class Application implements Serializable {

  private static final long serialVersionUID = 1L;
  private String name;
  private String description;
  private String parent;

  /**
   * Creates a new instance.
   */
  public Application() {
  }

  /**
   * Get the value of application's name
   *
   * @return the value of application's name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the value of application's name
   *
   * @param name new value of application's name
   */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	   * Get the value of application's description
	   *
	   * @return the value of application's description
	   */
	public String getDescription() {
		return description;
	}


	/**
	   * Set the value of application's description
	   *
	   * @param description new value of application's description
	   */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	   * Get the value of application's parent
	   *
	   * @return the value of application's parent
	   */
	public String getParent() {
		return parent;
	}

	/**
	   * Set the value of application's parent
	   *
	   * @param parent new value of application's parent
	   */
	public void setParent(String parent) {
		this.parent = parent;
	}


  /**
   * Formats a human-readable view of this instance.
   * 
   * @return a human-readable view
   */
  @Override
  public String toString() 
  {
    return "Application{" + 
            "name=" + name + 
            ", description=" + description +
            ", parent=" + parent +
            '}';
  }
  
}

package net.dynamic_tools.model;

/**
 * Created by IntelliJ IDEA.
 * User: Peter
 * Date: 4/11/11
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class NamedResource {
    private String name;

    public NamedResource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(this.getClass()) && name.equals(((NamedResource) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

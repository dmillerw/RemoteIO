package com.dmillerw.remoteIO.api.documentation;

/**
 * Created by Dylan Miller on 2/15/14
 */
public class Documentation {

    private String name;

    private String documentation;

    public Documentation(String name, String documentation) {
        this.name = name;
        this.documentation = documentation;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return documentation;
    }

}

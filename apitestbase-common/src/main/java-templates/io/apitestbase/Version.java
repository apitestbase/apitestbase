package io.apitestbase;

public class Version {
    //  project.version is a Maven built-in property, and it will be filtered during build
    public static final String VERSION = "${project.version}";
}
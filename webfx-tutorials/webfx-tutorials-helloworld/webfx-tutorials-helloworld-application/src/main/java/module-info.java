// Generated by WebFx

module webfx.tutorials.helloworld.application {

    // Direct dependencies modules
    requires javafx.controls;
    requires javafx.graphics;

    // Exported packages
    exports webfx.tutorials.helloworld;

    // Provided services
    provides javafx.application.Application with webfx.tutorials.helloworld.HelloWorldApplication;

}